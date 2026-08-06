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

# =====================================================================
# MAPAS DE PADRONIZAÇÃO (Baseados no constants.ts do Frontend)
# =====================================================================

MAPA_SITUACAO = {
    'em analise': 'EM_ANALISE',
    'pendente': 'PENDENTE',
    'aprovado': 'APROVADO',
    'concluido': 'CONCLUIDO',
    'aguardando validacao': 'AGUARDANDO_VALIDACAO',
    'tramitado': 'TRAMITADO',
    'devolvido': 'DEVOLVIDO',
    'aguardando retorno': 'AGUARDANDO_RETORNO'
}

MAPA_CARACTERIZACAO = {
    'analise de dados': 'ANALISE_DADOS_APRENDIZADO_MAQUINA_INTELIGENCIA_ARTIFICIAL',
    'computacao em ti': 'COMPUTACAO_TI',
    'computacao em nuvem': 'COMPUTACAO_NUVEM',
    'comunicacao de dados': 'COMUNICACAO_DADOS',
    'consultoria': 'CONSULTORIA_TI',
    'desenvolvimento e susten': 'DESENVOLVIMENTO_SUSTENTACAO_SISTEMAS', # Pega o erro de digitação da planilha (Sustenção)
    'hospedagem de sistemas': 'HOSPEDAGEM_SISTEMAS',
    'impressao': 'IMPRESSAO_DIGITALIZACAO',
    'infraestrutura de ti': 'INFRAESTRUTURA_TI',
    'internet das coisas': 'IOT',
    'materiais e equipamentos': 'MATERIAIS_EQUIPAMENTOS_TI',
    'processo seletivo': 'PROCESSO_SELETIVO',
    'seguranca da informatica': 'SEGURANCA_INFORMATICA_PRIVACIDADE',
    'software de prateleira': 'SOFTWARE_PRATELEIRA',
    'software e aplicativos': 'SOFTWARE_APLICATIVOS',
    'suporte e atendimento': 'SUPORTE_ATENDIMENTO_USUARIO_TI'
}

MAPA_TIPO_CONTRATACAO = {
    'pregao eletronico': 'PREGAO_ELETRONICO',
    'adesao a ata': 'ADESAO_ATA',
    'inexigib': 'INEXIGIBILIDADE_LICITACAO', # Pega inexigibilidade e suas variações com erro de digitação
    'compra direta': 'COMPRA_DIRETA',
    'dispensa': 'DISPENSA_LICITACAO',
    'sbqc': 'SBQC',
    'gn2350': 'GN2350-15',
    'bid': 'GN2350-15'
}

MAPA_PARECER = {
    'favoravel com ressalva': 'FAVORAVEL_COM_RESSALVA', # Pega as variações de plural (ressalvas) da planilha
    'nao favoravel': 'NAO_FAVORAVEL',
    'desfavoravel': 'NAO_FAVORAVEL',
    'favoravel': 'FAVORAVEL',
    'devolvido': 'DEVOLVIDO',
    'tramitado': 'TRAMITADO'
}

def padronizar_enum(valor_bruto, mapa):
    """Varre o mapa e tenta encontrar correspondência parcial numa string já limpa de acentos e maiúsculas."""
    if pd.isna(valor_bruto) or str(valor_bruto).strip() == '':
        return None
        
    texto_limpo = normalizar_cabecalho(str(valor_bruto))
    
    for chave_parcial, valor_constante in mapa.items():
        if chave_parcial in texto_limpo:
            return valor_constante
            
    return None # Se for "Não se aplica" ou outra coisa não prevista, retorna Nulo pro banco.

# =====================================================================


def migrar_dados():
    if not os.path.exists(EXCEL_PATH):
        print(f"❌ Erro: Arquivo {EXCEL_PATH} não encontrado.")
        return

    print("📊 Lendo arquivo Excel...")
    df = pd.read_excel(EXCEL_PATH, sheet_name="FILA AQ-TI", header=4)
    
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
        
    except Exception as e:
        print(f"❌ Erro ao conectar no Postgres: {e}")
        return

    sucessos = 0
    erros = 0
    ignorados = 0

    print("🚀 Iniciando migração de registros...")
    
    for index, row in df.iterrows():
        sigdoc = limpar_texto(row.get('codigo sigadoc'))
        
        if not sigdoc:
            ignorados += 1
            continue
            
        sigdoc_upper = sigdoc.upper()
        prefixo_raw = sigdoc.split('-')[0].upper().strip().replace(" ", "")
        
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

        nome_excel = limpar_texto(row.get('analista')) 
        usuarios_ids_encontrados = []
        if nome_excel:
            nome_excel_limpo = nome_excel.lower()
            for nome_db, u_id in mapa_usuarios.items():
                if nome_db in nome_excel_limpo:
                    usuarios_ids_encontrados.append(u_id)

        valor_bruto = row.get('valor') if row.get('valor') is not None else row.get('valor da proposta')
        try:
            valor = float(valor_bruto)
            if math.isnan(valor): valor = 0.0
        except (ValueError, TypeError):
            valor = 0.0

        chegou_em = limpar_data(row.get('data entrada sigadoc'))
        concluiu_em = limpar_data(row.get('data de conclusao'))

        em_espera = 0
        if chegou_em:
            data_referencia = concluiu_em if concluiu_em else datetime.now()
            diferenca = data_referencia.date() - chegou_em.date()
            em_espera = max(0, diferenca.days)

        iniciado_bruto = limpar_texto(row.get('prioridade'))
        iniciado = True if iniciado_bruto in ['1', '1.0', 'SIM', 'sim'] else False

        condes_bruto = limpar_texto(row.get('autorizacao condes'))
        condes = True if (condes_bruto and condes_bruto.lower() == 'sim') or (valor >= 400000) else False

        resumo = limpar_texto(row.get('apelido'))
        objeto = limpar_texto(row.get('objeto'))
        recomendacao = limpar_texto(row.get('observacao'))
        
        # ----------------------------------------------------------------------
        # APLICAÇÃO DOS NOVOS MAPEAMENTOS (Baseados no Constants)
        # ----------------------------------------------------------------------
        caracterizacao_ti = padronizar_enum(row.get('caracterizacao de ti'), MAPA_CARACTERIZACAO)
        tipo_contratacao = padronizar_enum(row.get('tipo contratacao'), MAPA_TIPO_CONTRATACAO)
        parecer_final = padronizar_enum(row.get('parecer final'), MAPA_PARECER)
        
        situacao = padronizar_enum(row.get('situacao'), MAPA_SITUACAO)
        # Regra de negócio de fallback caso a situação venha vazia
        if not situacao: 
            situacao = "CONCLUIDO" if concluiu_em else "EM_ANALISE"

        try:
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

            if usuarios_ids_encontrados:
                for u_id in usuarios_ids_encontrados:
                    query_relacao = """
                        INSERT INTO documento_usuarios (documento_id, usuario_id, cargo)
                        VALUES (%s, %s, %s)
                    """
                    cursor.execute(query_relacao, (doc_id, u_id, "ANALISTA"))
            
            conexao.commit()
            sucessos += 1
            print(f"✅ {sigdoc} inserido. Situação: {situacao} | Tipo: {tipo_contratacao}")

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