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

---

## Sobre o Projeto

O **Controle Financeiro** é um aplicativo desktop desenvolvido em **Java Swing** que permite ao usuário registrar e acompanhar sua vida financeira de forma educativa. O sistema exibe alertas inteligentes, dicas financeiras contextuais e gráficos de análise baseados no comportamento real de consumo do usuário.

### Alinhamento ODS 4 — Educação de Qualidade

| Recurso do sistema | Como contribui com o ODS 4 |
|---|---|
| Dicas financeiras contextuais | Educa o usuário com base no seu comportamento real |
| Indicador de saúde financeira | Ensina a identificar situações de risco financeiro |
| Alertas de orçamento | Estimula hábitos conscientes de consumo |
| Metas de poupança | Incentiva planejamento e disciplina financeira |
| Dashboard com gráficos | Torna dados financeiros compreensíveis para qualquer perfil |

---

## Tecnologias

| Tecnologia | Versão | Uso |
|---|---|---|
| Java (JDK) | 17+ | Linguagem principal |
| Java Swing | Nativa JDK | Interface gráfica desktop |
| JDBC | Nativa JDK | Comunicação com o banco de dados |
| MySQL | 8.0+ | Banco de dados relacional |
| JFreeChart | 1.5.4 | Gráficos no dashboard |
| MySQL Connector/J | 8.0.33 | Driver JDBC |

---

## Requisitos do Professor

| Requisito | Status | Como foi implementado |
|---|---|---|
| ✅ Interface gráfica desktop | Atendido | Java Swing — JFrame, JPanel, JTable, JDialog |
| ✅ Banco de dados MySQL | Atendido | MySQL 8.0 com 7 tabelas, índices e view |
| ✅ Autenticação de usuários | Atendido | Login por e-mail + senha via tabela `usuarios` |
| ✅ Dois tipos de usuário | Atendido | `ADMIN` (acesso total) e `USER` (acesso restrito) |
| ✅ Mínimo 3 CRUDs completos | Atendido | **6 CRUDs**: Transações, Metas, Orçamentos, Usuários, Categorias, Dicas |
| ✅ Dashboard com análise | Atendido | Cards KPI + gráficos JFreeChart + saúde financeira |
| ✅ Alinhamento ODS | Atendido | ODS 4 — Educação de Qualidade |

---

## Funcionalidades

### 👤 Usuário comum (USER)

- **Login e autenticação** — acesso por e-mail e senha
- **Transações** — registrar receitas e despesas, com suporte a parcelamento automático
- **Metas financeiras** — criar objetivos de poupança com prazo e acompanhar o progresso via barra de progresso
- **Orçamentos mensais** — definir teto de gastos por categoria e receber alertas visuais
- **Dashboard pessoal** — cards de KPI (receitas, despesas, saldo), gráficos e indicador de saúde financeira

### 🔑 Administrador (ADMIN)

Todas as funcionalidades do USER, mais:

- **Gerenciar usuários** — visualizar, ativar e desativar contas
- **Gerenciar categorias globais** — criar categorias visíveis a todos os usuários
- **Gerenciar dicas financeiras** — criar, editar e ativar/desativar dicas educativas

### 📊 Dashboard

| Card / Gráfico | Dado exibido |
|---|---|
| Total Receitas do Mês | Soma das entradas do mês atual |
| Total Despesas do Mês | Soma das saídas do mês atual |
| Saldo do Mês | Receitas − Despesas |
| Metas Ativas | Quantidade de metas em andamento |
| Gráfico de pizza | Distribuição de gastos por categoria |
| Gráfico de barras | Receitas vs Despesas nos últimos 6 meses |
| Saúde financeira | 🟢 Saudável / 🟡 Atenção / 🔴 Risco |

---

## Estrutura do Projeto

```
ControleFinanceiro/
│
├── lib/                                        # Dependências externas (.jar)
│   ├── mysql-connector-j-8.0.33.jar
│   └── jfreechart-1.5.4.jar
│
├── src/br/edu/finwise/controlefinanceiro/      # Código-fonte Java
│   │
│   ├── Main.java                               # Ponto de entrada da aplicação
│   │
│   ├── config/
│   │   ├── ConexaoBD.java                      # Singleton de conexão JDBC
│   │   └── Constantes.java                     # Constantes globais do sistema
│   │
│   ├── model/                                  # POJOs — espelham as tabelas do banco
│   │   ├── Usuario.java
│   │   ├── Categoria.java
│   │   ├── Transacao.java
│   │   ├── Meta.java
│   │   ├── ContribuicaoMeta.java
│   │   ├── Orcamento.java
│   │   ├── DicaFinanceira.java
│   │   └── ResumoMensal.java
│   │
│   ├── dao/                                    # Acesso ao banco via PreparedStatement
│   │   ├── UsuarioDAO.java
│   │   ├── CategoriaDAO.java
│   │   ├── TransacaoDAO.java
│   │   ├── MetaDAO.java
│   │   ├── ContribuicaoMetaDAO.java
│   │   ├── OrcamentoDAO.java
│   │   ├── DicaFinanceiraDAO.java
│   │   └── DashboardDAO.java
│   │
│   ├── service/                                # Regras de negócio e cálculos
│   │   ├── AutenticacaoService.java
│   │   ├── TransacaoService.java
│   │   ├── MetaService.java
│   │   ├── OrcamentoService.java
│   │   ├── DashboardService.java
│   │   └── SaudeFinanceira.java
│   │
│   ├── controller/                             # Intermediários entre View e Service
│   │   ├── SessaoAtual.java
│   │   ├── AutenticacaoController.java
│   │   ├── TransacaoController.java
│   │   ├── MetaController.java
│   │   ├── OrcamentoController.java
│   │   ├── AdminController.java
│   │   └── DashboardController.java
│   │
│   ├── view/                                   # Telas Java Swing
│   │   ├── LoginFrame.java
│   │   ├── MainFrame.java
│   │   ├── DashboardPanel.java
│   │   ├── TransacoesPanel.java
│   │   ├── TransacaoFormDialog.java
│   │   ├── MetasPanel.java
│   │   ├── MetaFormDialog.java
│   │   ├── OrcamentosPanel.java
│   │   └── AdminPanel.java
│   │
│   └── util/                                   # Utilitários reutilizáveis
│       ├── ValidadorCampos.java
│       └── Formatador.java
│
└── sql/
    ├── controle_financeiro_schema.sql          # Criação do banco e tabelas
    └── controle_financeiro_seed.sql            # Dados iniciais (admin + categorias)
```

---

## Banco de Dados

O banco `controle_financeiro` é composto por **7 tabelas** com relacionamentos via chaves estrangeiras, índices de performance e uma view para o dashboard.

```
usuarios (1) ──── (N) transacoes
usuarios (1) ──── (N) metas ──── (N) contribuicoes_meta
usuarios (1) ──── (N) orcamentos
categorias (1) ── (N) transacoes
categorias (1) ── (N) orcamentos
dicas_financeiras — tabela independente (ADMIN)
```

### Tabelas

| Tabela | Campos | Finalidade |
|---|---|---|
| `usuarios` | 10 | Autenticação e controle de acesso |
| `categorias` | 7 | Categorias globais e personalizadas |
| `transacoes` | 13 | Receitas e despesas com parcelamento |
| `metas` | 10 | Objetivos de poupança com prazo |
| `contribuicoes_meta` | 6 | Histórico de aportes em metas |
| `orcamentos` | 7 | Limites mensais por categoria |
| `dicas_financeiras` | 7 | Dicas educativas contextuais |

---
