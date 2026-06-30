# **Documento de Arquitetura de Software (DAS)**

---

## 1. Objetivos e Contexto (O "Porquê")

Antes de detalhar a implementação técnica, estabelecemos os direcionamentos de negócio do projeto.

* **Problema:** A organização enfrenta dificuldades no controle, centralização e rastreabilidade de arquivos, prejudicando a governança e a preparação estratégica para reuniões corporativas.
* **Objetivo:** Desenvolver uma plataforma centralizada para proporcionar um melhor gerenciamento dos documentos e suporte organizacional analítico para futuras reuniões.
* **Escopo (Dentro):** * Sistema de autenticação seguro via login.
    * CRUD completo de documentos visualizados em formato de tabela interativa.
    * Mecanismos de filtros avançados para pesquisa rápida de arquivos.
    * Registro rigoroso de log de auditoria para monitorar o que os usuários fizeram.
    * Controle de acesso baseado em funções, dividindo os perfis em usuários normais e administradores (admins).
* **Escopo (Fora):** Integrações automáticas com ferramentas externas de videoconferência ou fluxos complexos de assinatura digital externa nesta primeira versão.

---

## 2. Visão Geral do Sistema (O "O quê")

A arquitetura do sistema é composta por uma aplicação web moderna que consome serviços de uma API de backend centralizada.

* **Atores do Sistema:** Usuários normais (realizam consultas e operações básicas) e Administradores (gerenciam o acervo de documentos, logs e permissões avançadas).
* **Fluxo de Integração:** O ecossistema isola a camada cliente (Frontend em React) da camada de persistência e lógica de negócios (Backend em Java), trafegando dados de forma segura dentro de um ambiente isolado por containers.

---

## 3. Decisões Tecnológicas (A Stack)

As escolhas tecnológicas foram planejadas para garantir escalabilidade, segurança e manutenibilidade do software.

| Componente | Tecnologia | Justificativa Técnica |
| :--- | :--- | :--- |
| **Frontend** | React | Componentização eficiente da interface, proporcionando alto desempenho na renderização de tabelas de dados e reatividade na aplicação de filtros de pesquisa. |
| **Backend** | Java / Spring Boot | Framework robusto que adota padrões de mercado consolidados, garantindo segurança nativa, alta performance e facilidade na construção de APIs RESTful. |
| **Banco de Dados** | PostgreSQL | Banco de dados relacional ideal para manter a integridade, consistência e o mapeamento complexo de chaves estrangeiras necessárias para o controle de documentos. |
| **Infraestrutura** | Docker | Uso de containers para isolar a aplicação e o banco de dados, simplificando os processos de implantação, replicação e execução do ambiente de desenvolvimento à produção. |
| **Autenticação** | JWT (JSON Web Token) | Estrutura *stateless* para o gerenciamento de senhas e sessões de forma segura, permitindo o controle preciso de permissões entre usuários comuns e administradores. |
| **Persistência / ORM** | JPA / Hibernate | Abstração da camada de dados para otimizar as consultas e acelerar o desenvolvimento das operações de CRUD. |
| **Migrações de BD** | Flyway | Ferramenta de versionamento do esquema do banco de dados, assegurando que todas as alterações estruturais sejam aplicadas de forma automatizada e segura. |

---

## 4. Arquitetura de Software (O "Como")

O projeto será estruturado de forma desacoplada seguindo boas práticas de desenvolvimento de software.

* **Padrão Arquitetural:** O backend utilizará uma **Arquitetura em Camadas** tradicional:
    * **Camada Controller:** Responsável por expor os endpoints da API REST, receber as requisições HTTP e validar os dados de entrada.
    * **Camada Service:** Concentra todas as regras de negócio do sistema, validações de perfil (normal vs admin) e tratamento das lógicas de log.
    * **Camada Repository:** Interface de abstração de dados (via JPA) responsável por executar as operações de leitura e escrita diretamente no PostgreSQL.
* **Comunicação:** O frontend em React fará a comunicação com o Spring Boot por meio de chamadas HTTP síncronas utilizando o padrão RESTful. As payloads de dados trafegarão exclusivamente no formato JSON. Toda rota protegida exigirá a validação do token JWT enviado no cabeçalho (*Authorization Header*) da requisição.

---

## 5. Modelo de Dados Principal

Para suportar as regras de negócio descritas, o modelo de banco de dados conterá as seguintes entidades fundamentais:

### Tabela: `usuarios`
Armazena os dados cadastrais e as credenciais de acesso de cada colaborador.
* `id`: `uuid` (Chave Primária)
* `nome`: `string`
* `email`: `string`
* `senha`: `string` (Armazenada de forma criptografada)
* `permissoes`: `string[]` (Define os papéis no sistema, diferenciando usuários comuns de admins)
* `criado_em`: `timestamp`
* `atualizado_em`: `timestamp`

### Tabela: `documentos`
Entidade central do negócio que gerencia as informações dos arquivos e pautas corporativas.
* `id`: `uuid` (Chave Primária)
* `orgao_id` : `uuid`
* `sigdoc`: `string`
* `chegou_em` : `date`
* `concluiu_em` : `date`
* `em_espera` : `int`
* `valor` : `numeric`
* `situacao`: `string`
* `caracterizacao_ti` : `string`
* `iniciado` : `boolean`
* `condes` : `boolean`
* `resumo`: `string` (Utilizado nos filtros de pesquisa da tabela)
* `tipo_contratacao` : `string`
* `objeto`: `string`
* `recomendacao` : `string`
* `parecer_final` : `string`
* `deletado_em` : `timestamp`
* `criado_em`: `timestamp`
* `atualizado_em`: `timestamp`

### Tabela: `documento_usuarios`
Tabela associativa que vincula os usuários aos respectivos documentos, definindo responsabilidades.
* `id`: `uuid` (Chave Primária)
* `documento_id`: `uuid` (Chave Estrangeira ligada à tabela `documentos`)
* `usuario_id`: `uuid` (Chave Estrangeira ligada à tabela `usuarios`)
* `cargo`: `string`
* `criado_em`: `timestamp`
* `atualizado_em`: `timestamp`

### Tabela: `historico` (Log de Auditoria)
Estrutura encarregada de registrar minuciosamente as ações efetuadas na plataforma para fins de controle dos administradores.
* `id`: `uuid` (Chave Primária)
* `documento_id`: `uuid` (Chave Estrangeira)
* `usuario_id`: `uuid` (Chave Estrangeira identificando o autor da modificação)
* `aprovador_id` : `uuid` (Chave Estrangeira)
* `acao`: `string` (Ex: "Criação", "Alteração de Status", "Exclusão")
* `valores`: `jsonb` (Estrutura flexível contendo o estado anterior e posterior dos dados modificados)
* `criado_em`: `timestamp` (Data e hora exata do log)