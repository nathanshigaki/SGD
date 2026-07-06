# Notas Técnicas — SGD (Revisão Arquitetural e Requisitos)

Registro da análise arquitetural cruzando a implementação atual com as diretrizes do Documento de Arquitetura de Software (DAS).

## 1. Segurança e Gerenciamento de Credenciais

**Status atual das chaves JWT:** A segurança do ambiente local está correta. O arquivo `.gitignore` previne com sucesso que os pares de chaves criptográficas (`app.key` e `app.pub`) subam para o repositório.

**Ponto de atenção para Deploy:** No `application.properties`, as chaves estão configuradas para leitura diretamente do classpath (`classpath:app.pub` e `classpath:app.key`). Para um ambiente de produção em nuvem corporativa, é fortemente recomendado desacoplar esses arquivos do build. Ferramentas nativas de nuvem, como o **Azure Key Vault**, podem armazenar esses segredos de forma centralizada e injetá-los no contêiner de API Management ou na configuração do Spring em tempo de execução, garantindo que as chaves rotacionem sem necessidade de novos deploys.

## 2. Aderência ao DAS (Escopo Principal)

### 2.1. Controle de Acesso Baseado em Funções (RBAC)
- **Implementado:** O `SecurityFilterChain` exige que qualquer requisição esteja autenticada (`.anyRequest().authenticated()`). A geração do token também já inclui as `roles` corretamente (`scope`).
- **Gap Arquitetural:** O controle granular entre administradores e usuários comuns, exigido pelo DAS, ainda não ocorre nas rotas. Um usuário comum autenticado consegue chamar `DELETE /usuarios/{id}`.
- **Ação:** Adicionar `@EnableMethodSecurity` na classe de configuração de segurança e proteger os endpoints sensíveis nos *Controllers* com anotações como `@PreAuthorize("hasRole('ADMIN')")`.

### 2.2. Performance e Mecanismo de Filtros
- **Implementado:** O repositório customizado `findAllWithResponsaveis()` evita o problema de N+1 queries de forma muito competente ao usar `LEFT JOIN FETCH`.
- **Gap Arquitetural:** Os métodos de listagem (`DocumentoController.getAll()`) retornam listas completas em memória (`List<DocumentoResponse>`). O volume de documentos governamentais inviabilizará o tráfego de dados e causará lentidão na interface em React.
- **Ação:** Refatorar a camada Controller, Service e Repository para utilizar a interface `Pageable` do Spring Data. Para atender ao requisito de "filtros avançados" do DAS, deve-se aplicar o padrão `JpaSpecificationExecutor`, que possibilita buscas dinâmicas (ex: filtro combinado por data e sigdoc) de maneira escalável.

### 2.3. Log de Auditoria (Rastreabilidade)
- **Implementado:** A infraestrutura de banco de dados preparou a tabela correta na migration Flyway `V6__create-table-historico.sql`.
- **Gap Arquitetural:** Não há entidades JPA, repositórios ou chamadas nos serviços que gravem logs nesta tabela. O escopo principal do sistema exige "registro rigoroso de log de auditoria".
- **Ação:** Adicionar as classes de domínio referentes ao Histórico e utilizar um padrão de interceptação (como *Hibernate Envers* ou eventos do Spring, `ApplicationEventPublisher`) para registrar transparentemente o antes e o depois (`valores JSONB`) sempre que um documento for alterado.

## 3. Inconsistências de Engenharia e Regras de Negócio

### 3.1. Conflito no Padrão de Deleção (Hard vs Soft Delete)
O sistema aplica eficientemente o modelo de *Soft Delete* para documentos (`@SQLRestriction("deletado_em IS NULL")`) e deleção via set do atributo em `DocumentoService`. No entanto, em `UsuarioService` e `OrgaoService`, ocorre deleção física direta no banco (`repository.delete(entidade)`). Se houver exclusão de um Órgão com documentos atrelados, a aplicação retornará erro interno (500) por violação de Foreign Key no Postgres. Recomenda-se aplicar validação prévia na exclusão ou estender o *Soft Delete* a todos os cadastros.

### 3.2. Validação Faltante em Fluxos de Atualização
No método `UsuarioService.createUsuario`, a verificação de unicidade de e-mail impede duplicações (`findByEmail`). Porém, em `UsuarioService.updateUsuario`, essa camada de segurança não existe. A edição do próprio perfil com um e-mail já existente estourará a constraint relacional diretamente no log da aplicação, sem feedback polido ao cliente.

### 3.3. Expansão do GlobalExceptionHandler
O manipulador de exceções lidou perfeitamente com quebras de validação de DTOs e erros de negócio mapeados (`NotFoundException`, `InvalidArgumentException`). Como evolução iminente, é necessário englobar o tratamento da `DataIntegrityViolationException` lançada pelo Hibernate, assegurando que o contrato da API retorne mensagens JSON amigáveis em vez do *stack trace* nativo em casos de infração de constrições do BD.
