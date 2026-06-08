# Projeto-A3

# 💰 Controle Financeiro

> Sistema desktop de controle financeiro pessoal com foco em educação financeira, alinhado ao **ODS 4 — Educação de Qualidade** da ONU.

Desenvolvido como projeto avaliativo da disciplina **Programação de Soluções Computacionais**, o sistema ensina o usuário a organizar receitas, despesas, metas de poupança e limites mensais de consumo por meio de uma interface gráfica intuitiva com análise de dados em tempo real.

---

## 📋 Índice

- [Sobre o Projeto](#sobre-o-projeto)
- [Tecnologias](#tecnologias)
- [Requisitos do Professor](#requisitos-do-professor)
- [Funcionalidades](#funcionalidades)
- [Estrutura do Projeto](#estrutura-do-projeto)
- [Banco de Dados](#banco-de-dados)
- [Como Rodar o Projeto](#como-rodar-o-projeto)
- [Logins de Teste](#logins-de-teste)

---

## Sobre o Projeto

O **Controle Financeiro** é um aplicativo desktop desenvolvido em **Java Swing** que permite ao usuário registrar e acompanhar sua vida financeira de forma educativa. O sistema exibe alertas inteligentes, dicas financeiras contextuais e gráficos de análise baseados no comportamento real de consumo do usuário.

### Alinhamento ODS 4 — Educação de Qualidade

| Recurso do sistema | Como contribui com o ODS 4 |
|---|---|
| Dicas financeiras contextuais | Educa o usuário com base no seu comportamento real |
| Indicador de saúde financeira | Ensina a identificar situações de risco financeiro |
| Alertas de orçamento | Estimula hábitos conscientes de consumo |
| Metas de poupança com progresso | Incentiva planejamento e disciplina financeira |
| Dashboard com gráficos interativos | Torna dados financeiros compreensíveis para qualquer perfil |
| Filtro de período no dashboard | Permite análise histórica e aprendizado a longo prazo |
| Cadastro de novos usuários | Acesso democrático à educação financeira |

---

## Tecnologias

| Tecnologia | Versão | Uso |
|---|---|---|
| Java (JDK) | 17+ | Linguagem principal |
| Java Swing | Nativa JDK | Interface gráfica desktop |
| JDBC | Nativa JDK | Comunicação com o banco de dados |
| MySQL | 8.0+ | Banco de dados relacional |
| JFreeChart | 1.5.4 | Gráficos no dashboard (pizza, barras, linha) |
| MySQL Connector/J | 8.0.33 | Driver JDBC |

---

## Requisitos do Professor

| Requisito | Status | Como foi implementado |
|---|---|---|
| ✅ Interface gráfica desktop | Atendido | Java Swing — JFrame, JPanel, JTable, JDialog, JSplitPane |
| ✅ Banco de dados MySQL | Atendido | MySQL 8.0 com 8 tabelas, índices e view |
| ✅ Autenticação de usuários | Atendido | Login por e-mail + senha via tabela `usuarios` |
| ✅ Dois tipos de usuário | Atendido | `ADMIN` (acesso total) e `USER` (acesso restrito) |
| ✅ Mínimo 3 CRUDs completos | Atendido | **6 CRUDs**: Transações, Metas, Orçamentos, Usuários, Categorias, Dicas |
| ✅ Dashboard com análise | Atendido | Cards KPI + gráficos JFreeChart + saúde financeira + filtro de período |
| ✅ Alinhamento ODS | Atendido | ODS 4 — Educação de Qualidade |

---

## Funcionalidades

### 👤 Usuário comum (USER)

- **Cadastro** — criação de conta diretamente na tela de login
- **Login e autenticação** — acesso por e-mail e senha
- **Transações** — registrar receitas e despesas com suporte a parcelamento automático; filtro por mês, ano e tipo (Receita/Despesa)
- **Metas financeiras** — criar objetivos de poupança com prazo, acompanhar progresso via barra de progresso e realizar aportes
- **Orçamentos mensais** — definir teto de gastos por categoria com alertas visuais (OK / ATENÇÃO / ESTOURADO) e barra de progresso colorida
- **Dashboard pessoal** — cards KPI (receitas, despesas, saldo, metas ativas), gráficos JFreeChart, indicador de saúde financeira com dica contextual, seção de metas e orçamentos do mês; **filtro de mês e ano** para consultar qualquer período

### 🔑 Administrador (ADMIN)

- **Visão geral do sistema** — dashboard com dados consolidados de todos os usuários: KPIs, distribuição de saúde financeira e top categorias
- **Gerenciar usuários** — painel analítico com condição financeira de cada usuário (receitas, despesas, saldo, % gasto, saúde, metas, transações); busca por nome/e-mail; filtro por status, função e saúde financeira
- **Categorias globais** — análise de gastos por categoria com gráficos de distribuição (pizza/barras), filtro por tipo (Despesas/Receitas), barra de participação percentual e detalhe ao clicar (evolução 6 meses + top usuários da categoria)

### 📊 Dashboard do Usuário (USER)

| Card / Gráfico | Dado exibido |
|---|---|
| Filtro de período | Combo de mês e ano — consulta qualquer período histórico |
| Total Receitas do Mês | Soma das entradas do período selecionado |
| Total Despesas do Mês | Soma das saídas do período selecionado |
| Saldo do Mês | Receitas − Despesas |
| Metas Ativas | Quantidade de metas em andamento |
| Gráfico de pizza | Distribuição de gastos por categoria |
| Gráfico de barras | Receitas vs Despesas nos últimos 6 meses |
| Saúde financeira | 🟢 Saudável / 🟡 Atenção / 🔴 Risco |
| Dica contextual | Dica financeira baseada na saúde calculada |
| Seção Metas Ativas | Cards com barra de progresso, valor atual/alvo e prazo |
| Seção Orçamentos | Cards com status (OK/ATENÇÃO/ESTOURADO), barra de progresso e valores |

### 📊 Dashboard do Administrador (ADMIN)

| Card / Gráfico | Dado exibido |
|---|---|
| Usuários Ativos | Total de usuários com funcao USER e ativo = true |
| Categorias Globais | Total de categorias sem usuario_id |
| Dicas Ativas | Total de dicas com ativo = true |
| Transações no Mês | Total de movimentações do mês |
| Em Risco / Atenção / Saudáveis | Distribuição de saúde com base em receitas reais |
| Gráfico de pizza | Distribuição de saúde de todos os usuários |
| Gráfico de barras | Top 5 categorias com mais gastos no sistema |
| Tabela de condição financeira | Todos os usuários com receitas, despesas, saldo, % gasto, saúde, metas e transações — filtrável |
| Tabela de alertas | Apenas usuários com saúde RISCO ou ATENÇÃO |

---

## Lógica de Saúde Financeira

A saúde é calculada com base nas **receitas reais do mês** (não na renda cadastrada):

| Condição | Status |
|---|---|
| Despesas ≥ Receitas ou saldo negativo | 🔴 RISCO |
| % gasto ≥ 90% das receitas | 🔴 RISCO |
| % gasto ≥ 70% das receitas | 🟡 ATENÇÃO |
| % gasto < 70% das receitas | 🟢 SAUDÁVEL |

> Se o usuário não tiver receitas no mês, usa a renda mensal cadastrada como base. Se ambas forem zero, exibe SAUDÁVEL por falta de dados.

---

## Estrutura do Projeto

```
ControleFinanceiro/
│
├── lib/                                         # Dependências externas (.jar)
│   ├── mysql-connector-j-8.0.33.jar
│   └── jfreechart-1.5.4.jar
│
├── src/
│   └── com/controleFinanceiro/                  # Código-fonte Java
│       │
│       ├── Main.java                            # Ponto de entrada da aplicação
│       │
│       ├── db/
│       │   └── ConnectionFactory.java           # Conexão JDBC com o MySQL
│       │
│       ├── model/                               # POJOs — espelham as tabelas do banco
│       │   ├── Usuario.java
│       │   ├── Categoria.java
│       │   ├── Transacao.java
│       │   ├── Meta.java
│       │   ├── Orcamento.java
│       │   ├── DicaFinanceira.java
│       │   ├── ResumoMensal.java
│       │   └── DadosDashboard.java
│       │
│       ├── DAO/                                 # Acesso ao banco via PreparedStatement
│       │   ├── UsuarioDAO.java
│       │   ├── CategoriaDAO.java
│       │   ├── TransacaoDAO.java
│       │   ├── MetaDAO.java
│       │   ├── OrcamentoDAO.java
│       │   ├── DicaFinanceiraDAO.java
│       │   ├── dashboardDAO.java
│       │   └── AdminDashboardDAO.java
│       │
│       ├── service/                             # Regras de negócio e cálculos
│       │   ├── TransacaoService.java
│       │   ├── MetaService.java
│       │   ├── OrcamentoService.java
│       │   └── DashboardService.java
│       │
│       ├── controller/                          # Intermediários entre View e Service
│       │   ├── SessaoAtual.java
│       │   ├── DashboardController.java
│       │   ├── MetaController.java
│       │   └── OrcamentoController.java
│       │
│       ├── util/                                # Utilitários reutilizáveis
│       │   └── Formatador.java
│       │
│       └── view/                                # Telas Java Swing
│           ├── TelaLogin.java                   # Login + cadastro
│           ├── CadastroDialog.java              # Dialog de criação de conta
│           ├── CardKPI.java                     # Componente visual de card KPI
│           ├── dashBoardPrincipal.java          # Janela principal do USER
│           ├── dashBoardAdmin.java              # Janela principal do ADMIN
│           ├── DashboardPanel.java              # Dashboard do USER com filtro de período
│           ├── AdminDashboardPanel.java         # Dashboard do ADMIN
│           ├── TransacoesPanel.java             # CRUD de transações
│           ├── TransacaoFormDialog.java         # Formulário de transação
│           ├── MetasPanel.java                  # CRUD de metas com cards clicáveis
│           ├── MetaFormDialog.java              # Formulário de meta
│           ├── OrcamentosPanel.java             # CRUD de orçamentos com cards clicáveis
│           ├── OrcamentoFormDialog.java         # Formulário de orçamento
│           ├── UsuariosPanel.java               # Painel analítico de usuários (ADMIN)
│           └── CategoriasGlobaisPanel.java      # Análise de gastos por categoria (ADMIN)
│
└── sql/
    ├── controle_financeiro_schema.sql           # Criação do banco e tabelas
    └── controle_financeiro_seed.sql             # Dados iniciais de teste
```

---

## Banco de Dados

O banco `controle_financeiro` é composto por **8 tabelas** com relacionamentos via chaves estrangeiras, índices de performance e uma view para o dashboard.

```
usuarios (1) ──── (N) transacoes
usuarios (1) ──── (N) metas
usuarios (1) ──── (N) orcamentos
categorias (1) ── (N) transacoes
categorias (1) ── (N) orcamentos
dicas_financeiras — tabela independente (ADMIN)
```

### Tabelas

| Tabela | Campos principais | Finalidade |
|---|---|---|
| `usuarios` | id, nome, email, senha, funcao, renda_mensal, perfil_investidor, ativo | Autenticação e controle de acesso |
| `categorias` | id, usuario_id, nome, tipo, cor, icone | Categorias globais (usuario_id NULL) e personalizadas |
| `transacoes` | id, usuario_id, categoria_id, valor, tipo, descricao, data_transacao, parcelado, numero_parcela, total_parcela, grupo_parcela | Receitas e despesas com parcelamento |
| `metas` | id, usuario_id, nome, descricao, valor_alvo, valor_atual, prazo, status | Objetivos de poupança com prazo |
| `orcamentos` | id, usuario_id, categoria_id, valor_limite, mes, ano | Limites mensais por categoria |
| `dicas_financeiras` | id, condicao, conteudo, ativo | Dicas educativas por status de saúde |

### View

| View | Finalidade |
|---|---|
| `vw_resumo_mensal` | Agrega receitas, despesas e saldo por usuário/mês — usada no dashboard e no cálculo de saúde |

---

## Como Rodar o Projeto

### Pré-requisitos

- [Java JDK 17+](https://www.oracle.com/java/technologies/downloads/)
- [MySQL 8.0+](https://dev.mysql.com/downloads/)
- [IntelliJ IDEA](https://www.jetbrains.com/idea/) ou [NetBeans](https://netbeans.apache.org/)

---

### 1. Clone o repositório

```bash
git clone https://github.com/Nativoh/Projeto-A3.git
cd Projeto-A3
```

---

### 2. Adicione as dependências

No IntelliJ:
```
File → Project Structure → Libraries → + → Java
Selecione os dois arquivos da pasta lib/:
  - mysql-connector-j-8.0.33.jar
  - jfreechart-1.5.4.jar
```

---

### 3. Importe o banco de dados

**Opção A — MySQL Workbench:**
1. Abra o Workbench e conecte no `localhost`
2. **Server → Data Import**
3. Selecione **Import from Self-Contained File**
4. Navegue até `sql/controle_financeiro_schema.sql`
5. Em **Default Schema** selecione ou crie o banco `controle_financeiro`
6. Clique em **Start Import**
7. Repita o processo para `sql/controle_financeiro_seed.sql`

**Opção B — Terminal MySQL:**
```bash
mysql -u root -p < sql/controle_financeiro_schema.sql
mysql -u root -p controle_financeiro < sql/controle_financeiro_seed.sql
```

---

### 4. Configure a conexão com o banco

Abra `src/com/controleFinanceiro/db/ConnectionFactory.java` e ajuste com suas credenciais:

```java
private final String usuario = "root";          // seu usuário MySQL
private final String senha   = "sua_senha";     // sua senha MySQL
private final String host    = "localhost";
private final int    port    = 3306;
private final String cb      = "controle_financeiro";
```

---

### 5. Execute o projeto

Clique com botão direito em `Main.java` → **Run 'Main'** ▶️

A tela de login será exibida e o sistema estará pronto para uso.

---

## Logins de Teste

| Perfil | E-mail | Senha | Acesso |
|---|---|---|---|
| 🔑 Administrador | admin@teste.com | 123 | Total — dashboard, usuários, categorias e dicas |
| 👤 Usuário Comum | usuario@teste.com | 123 | Restrito — transações, metas, orçamentos e dashboard pessoal |


---

<div align="center">

**Programação de Soluções Computacionais**
Java Swing · MySQL · JDBC · JFreeChart · ODS 4 — Educação de Qualidade

</div>