import pandas as pd
import psycopg2
import os
import math
import unicodedata
from datetime import datetime
from dotenv import load_dotenv

load_dotenv()

EXCEL_PATH = os.getenv("EXCEL_PATH", "documentos.xlsx")

DB_HOST = os.getenv("DB_HOST", "localhost")
DB_PORT = os.getenv("DB_PORT", "5432")
DB_NAME = os.getenv("DB_NAME", "sgd_db")
DB_USER = os.getenv("DB_USER", "postgres")
DB_PASS = os.getenv("DB_PASSWORD", "postgres")

def normalizar_cabecalho(texto):
    """Remove acentos, espaços extras e deixa em minúsculo para evitar erros de casamento no Pandas."""
    if pd.isna(texto):
        return ""
    texto = str(texto).strip().lower()
    # Remove acentos (Ex: código -> codigo, situação -> situacao)
    texto = "".join(c for c in unicodedata.normalize('NFD', texto) if unicodedata.category(c) != 'Mn')
    # Substitui quebras de linha e hifens por espaços simples
    texto = texto.replace('\n', ' ').replace('-', ' ').replace('/', ' ')
    # Remove espaços duplicados
    return " ".join(texto.split())

def limpar_texto(valor, max_len=None):
    if pd.isna(valor) or str(valor).strip().lower() in ['nan', 'none', '']:
        return None
    texto = str(valor).strip()
    if max_len and len(texto) > max_len:
        return texto[:max_len]
    return texto

def limpar_data(valor):
    if pd.isna(valor):
        return None
    try:
        return pd.to_datetime(valor).to_pydatetime()
    except:
        return None

def migrar_dados():
    if not os.path.exists(EXCEL_PATH):
        print(f"❌ Erro: Arquivo {EXCEL_PATH} não encontrado.")
        return

    print("📊 Lendo arquivo Excel...")
    # ATENÇÃO: Se os cabeçalhos estiverem na linha 5 do Excel, mantenha header=4.
    # Se estiverem na primeira linha da planilha, mude para header=0.
    df = pd.read_excel(EXCEL_PATH, sheet_name="FILA AQ-TI", header=4)
    
    # Aplica a normalização rigorosa nos cabeçalhos da tabela do Excel
    df.columns = [normalizar_cabecalho(col) for col in df.columns]
    
    print("📋 Colunas identificadas e mapeadas no Excel:")
    print(list(df.columns))

    print("🔌 Conectando ao banco de dados...")
    try:
        conexao = psycopg2.connect(
            host=DB_HOST, port=DB_PORT, dbname=DB_NAME, user=DB_USER, password=DB_PASS
        )
        cursor = conexao.cursor()
        
        cursor.execute("SELECT acronimo, id FROM orgao WHERE acronimo IS NOT NULL")
        mapa_orgaos = {row[0].strip().upper().replace(" ", ""): row[1] for row in cursor.fetchall()}
        
        cursor.execute("SELECT nome, id FROM usuarios")
        mapa_usuarios = {row[0].strip().lower(): row[1] for row in cursor.fetchall()}
        
        print(f"🏢 {len(mapa_orgaos)} órgãos carregados.")
        print(f"👤 {len(mapa_usuarios)} usuários carregados.")
        
    except Exception as e:
        print(f"❌ Erro ao conectar no Postgres: {e}")
        return

    sucessos = 0
    erros = 0
    ignorados = 0

    print("🚀 Iniciando migração de registros...")
    
    for index, row in df.iterrows():
        # Mapeamento usando as chaves normalizadas (sem acento, minúsculo, sem barras/hifens)
        sigdoc = limpar_texto(row.get('codigo sigadoc'))
        
        # Trava de segurança contra linhas vazias no fim do Excel
        if not sigdoc:
            ignorados += 1
            continue
            
        sigdoc_upper = sigdoc.upper()
        prefixo_raw = sigdoc.split('-')[0].upper().strip().replace(" ", "")
        
        # Regras de correspondência de Órgãos
        if "SEFAZ" in sigdoc_upper:
            prefixo_raw = "SEFAZ"
        elif "DETRAN" in sigdoc_upper:
            prefixo_raw = "DETRAN"
        elif "SEMA" in sigdoc_upper:
            prefixo_raw = "SEMA"
        elif prefixo_raw == "INDEAMT":
            prefixo_raw = "INDEA"
        elif prefixo_raw in ["BOMBEIROS", "CBMMT"]:
            prefixo_raw = "CBM"
        elif prefixo_raw == "GOV":
            prefixo_raw = "CASAMILITAR"
            
        orgao_id = mapa_orgaos.get(prefixo_raw, mapa_orgaos.get("SEPLAG"))

        # Busca de usuários (Analistas)
        nome_excel = limpar_texto(row.get('analista')) 
        usuarios_ids_encontrados = []
        if nome_excel:
            nome_excel_limpo = nome_excel.lower()
            for nome_db, u_id in mapa_usuarios.items():
                if nome_db in nome_excel_limpo:
                    usuarios_ids_encontrados.append(u_id)

        # Captura de Valores (Prioriza a coluna VALOR, senão tenta VALOR DA PROPOSTA)
        valor_bruto = row.get('valor') if row.get('valor') is not None else row.get('valor da proposta')
        try:
            valor = float(valor_bruto)
            if math.isnan(valor): valor = 0.0
        except (ValueError, TypeError):
            valor = 0.0

        # Tratamento de Datas usando os novos cabeçalhos da sua lista
        chegou_em = limpar_data(row.get('data entrada sigadoc'))
        concluiu_em = limpar_data(row.get('data de conclusao'))

        # Cálculo dinâmico dos Dias em Espera
        em_espera = 0
        if chegou_em:
            data_referencia = concluiu_em if concluiu_em else datetime.now()
            # Zera a informação de horas/minutos para focar apenas no intervalo de dias corridos
            diferenca = data_referencia.date() - chegou_em.date()
            em_espera = max(0, diferenca.days)

        # Regras booleanas baseadas nas suas colunas
        iniciado_bruto = limpar_texto(row.get('prioridade')) # Ajuste se houver outra lógica para 'iniciado'
        iniciado = True if iniciado_bruto in ['1', '1.0', 'SIM', 'sim'] else False

        condes_bruto = limpar_texto(row.get('autorizacao condes'))
        condes = True if (condes_bruto and condes_bruto.lower() == 'sim') or (valor >= 400000) else False

        # Demais campos de texto baseados nas suas colunas
        resumo = limpar_texto(row.get('apelido'))
        caracterizacao_ti = limpar_texto(row.get('caracterizacao de ti'))
        objeto = limpar_texto(row.get('objeto'))
        recomendacao = limpar_texto(row.get('observacao'))
        parecer_final = limpar_texto(row.get('parecer final'))
        tipo_contratacao = limpar_texto(row.get('tipo contratacao'), 255)
        
        situacao = limpar_texto(row.get('situacao'), 255)
        if not situacao: 
            situacao = "CONCLUIDO" if concluiu_em else "EM_ANALISE"

        try:
            # A. Inserção do Documento no Banco
            query_doc = """
                INSERT INTO documentos 
                (orgao_id, sigdoc, chegou_em, concluiu_em, em_espera, valor, situacao, 
                caracterizacao_ti, iniciado, condes, resumo, tipo_contratacao, 
                objeto, recomendacao, parecer_final)
                VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
                RETURNING id;
            """
            cursor.execute(query_doc, (
                orgao_id, sigdoc, chegou_em, concluiu_em, em_espera, valor, situacao,
                caracterizacao_ti, iniciado, condes, resumo, tipo_contratacao,
                objeto, recomendacao, parecer_final
            ))
            
            doc_id = cursor.fetchone()[0]

            # B. Vinculação de Analistas
            if usuarios_ids_encontrados:
                for u_id in usuarios_ids_encontrados:
                    query_relacao = """
                        INSERT INTO documento_usuarios (documento_id, usuario_id, cargo)
                        VALUES (%s, %s, %s)
                    """
                    cursor.execute(query_relacao, (doc_id, u_id, "ANALISTA"))
            
            conexao.commit()
            sucessos += 1
            print(f"✅ {sigdoc} inserido. Dias em espera: {em_espera}. Valor: R$ {valor:.2f}")

        except Exception as e:
            conexao.rollback()
            erros += 1
            print(f"❌ Erro ao migrar o documento {sigdoc}: {e}")

    cursor.close()
    conexao.close()
    print("\n🏁 MIGRACAO CONCLUÍDA!")
    print(f"✔️ Documentos salvos com sucesso: {sucessos}")
    print(f"⚠️ Linhas em branco ignoradas: {ignorados}")
    print(f"❌ Registros com falha operacional: {erros}")

if __name__ == "__main__":
    migrar_dados()