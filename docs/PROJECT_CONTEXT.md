# Educação Financeira Infantil Gamificada (KidsCoins)

## 📋 Sobre o Projeto

Sistema mobile que ensina crianças de 6-14 anos sobre finanças através de tarefas domésticas gamificadas.

**Contexto:** TCC de Ciência da Computação - UNIP
**Equipe:** 5 estudantes (nível técnico básico-intermediário)
**Prazo:** 1 mês de desenvolvimento
**Objetivo:** Sistema funcional para apresentação

---

## ⚠️ RESTRIÇÕES IMPORTANTES

### Nível de Complexidade

- Código SIMPLES e COMPREENSÍVEL
- SEM Clean Architecture, DDD, CQRS
- SEM microsserviços (monolito simples)
- SEM abstrações desnecessárias
- Comentários em português quando necessário

### Mantra

> **"Simples, funcional e compreensível"**

A equipe precisa ENTENDER o código para defender na banca.

---

## 🎯 O Que o Sistema Faz

### Fluxo Principal

```
Pai cria tarefa → Criança completa → Pai aprova
→ Sistema credita moedas + XP → Criança resgata recompensas
```

### Conceito

- Crianças ganham **moedas virtuais** completando tarefas
- Usam moedas para resgatar **recompensas** (criadas pelos pais)
- Sistema de **gamificação**: níveis, XP e badges
- Ensina: ganhar, poupar, gastar conscientemente, planejar

---

## 👥 Perfis de Usuário

### PARENT (Pai/Responsável)

- Cria tarefas e recompensas
- Aprova/rejeita conclusões
- Visualiza progresso dos filhos
- Gerencia perfis das crianças

### CHILD (Criança)

- Visualiza tarefas disponíveis
- Marca como concluída
- Vê saldo de moedas
- Solicita resgates
- Acompanha nível, XP e badges

---

## ✅ Funcionalidades

### 1. Autenticação

- Cadastro de pais (email + senha)
- Login com JWT (24h)
- Criação de perfis de crianças (nome + PIN 4 dígitos)

### 2. Tarefas

- Pai cria: título, descrição, moedas, XP
- Atribuição a crianças
- Criança marca como concluída
- Pai aprova ou rejeita
- Status: PENDING → COMPLETED → APPROVED/REJECTED

### 3. Carteira Virtual

- Saldo de moedas por criança
- Histórico de transações (CREDIT/DEBIT)
- Estatísticas: total ganho, total gasto

### 4. Poupança (OPCIONAL)

- Criança guarda moedas separadamente
- Rendimento automático semanal (2%)
- Bônus por tempo guardado (7 dias: +2%, 30 dias: +10%)

### 5. Loja de Recompensas

- Pai cria catálogo (nome, descrição, custo)
- Criança solicita resgate
- Moedas NÃO debitadas até aprovação
- Pai aprova ou rejeita

### 6. Gamificação

**Níveis:** 10 níveis (Iniciante → Mestre)

- Fórmula XP: `nivel * 100 + (nivel-1) * 50`

**XP por Ações:**

- Tarefa simples: +50 XP
- Tarefa complexa: +100 XP
- Primeira do dia: +25 XP bônus

**Badges:** 6-8 conquistas

1. Primeira Tarefa (1 tarefa)
2. Poupador Iniciante (100 moedas)
3. Trabalhador Dedicado (10 tarefas)
4. Dia Produtivo (5 tarefas em 1 dia)
5. Consistente (7 dias seguidos)
6. Planejador (200 moedas guardadas por 7 dias)
7. Comprador Consciente (primeiro resgate)
8. Milionário (1000 moedas lifetime)

### 7. Notificações

- Push via Expo
- Eventos: tarefa aprovada, level up, badge desbloqueada, etc.

---

## 🏗️ Stack Tecnológica

### Backend

- Java 17 + Spring Boot 3.2+
- PostgreSQL 15
- JWT (Spring Security)
- Deploy: Railway.app

### Mobile

- React Native + Expo
- TypeScript
- React Native Paper (UI)
- React Navigation 6
- Axios

---

## 📦 Estrutura Backend (SIMPLES)

```
src/main/java/com/educacaofinanceira/
├── config/          (Security, JWT, CORS)
├── controller/      (Endpoints REST)
├── service/         (Lógica de negócio)
├── repository/      (JPA - acesso ao banco)
├── model/           (Entidades @Entity)
├── dto/             (Request/Response)
├── security/        (JWT Provider, Filters)
├── exception/       (Exceções customizadas)
└── util/            (Helpers, constantes)
```

**Padrão:** Controller → Service → Repository

---

## 📦 Estrutura Mobile (SIMPLES)

```
src/
├── screens/        (Telas por perfil)
│   ├── auth/      (Login, Register)
│   ├── parent/    (Dashboard, criar tarefa, aprovar)
│   └── child/     (Dashboard, tarefas, loja, badges)
├── components/    (Componentes reutilizáveis)
├── navigation/    (React Navigation)
├── services/      (API calls - Axios)
├── contexts/      (Estado global - Context API)
├── utils/         (Helpers)
└── types/         (TypeScript types)
```

---

## 💾 Entidades Principais

### User

- id, email, password (BCrypt), full_name
- role (PARENT/CHILD), family_id
- pin (para CHILD), avatar_url

### Family

- id, name

### Task

- id, family_id, created_by_user_id
- title, description, coin_value, xp_value
- category, status (ACTIVE/INACTIVE)

### TaskAssignment

- id, task_id, assigned_to_child_id
- status (PENDING/COMPLETED/APPROVED/REJECTED)
- completed_at, approved_at, rejection_reason

### Wallet

- id, child_id (unique)
- balance, total_earned, total_spent

### Transaction

- id, wallet_id, type (CREDIT/DEBIT)
- amount, balance_before, balance_after
- description, reference_type, reference_id

### Savings (OPCIONAL)

- id, child_id (unique)
- balance, total_deposited, total_earned
- last_deposit_at

### Reward

- id, family_id, created_by_parent_id
- name, description, coin_cost
- category, image_url, is_active

### Redemption

- id, reward_id, child_id
- status (PENDING/APPROVED/REJECTED)
- requested_at, reviewed_at, rejection_reason

### UserXP

- id, user_id (unique)
- current_level (1-10), current_xp, total_xp

### Badge

- id, name, description, icon_name
- criteria_type, criteria_value, xp_bonus

### UserBadge

- id, user_id, badge_id
- unlocked_at

### Notification

- id, user_id, type, title, message
- is_read, created_at

---

## 🔐 Segurança

### JWT

- Access Token: 24h
- Refresh Token: 7 dias (salvo no banco)
- Secret em variável de ambiente
- Claims: userId, role, familyId

### Senhas

- BCrypt strength 12
- Mínimo 8 caracteres

### Autorização

- PARENT: acesso total à família
- CHILD: acesso apenas aos próprios dados

---

## 📋 Regras de Negócio Importantes

### Aprovar Tarefa

1. TaskAssignment → APPROVED
2. Creditar moedas na Wallet
3. Adicionar XP ao UserXP
4. Verificar subida de nível
5. Verificar e desbloquear badges
6. Registrar Transaction
7. Notificar criança

### Resgate de Recompensa

1. Criança solicita → Redemption PENDING
2. Moedas NÃO debitadas ainda
3. Pai aprova → Debita moedas
4. Registrar Transaction
5. Notificar criança

### Cálculo de Nível

- Loop somando XP necessário por nível
- Se total_xp >= threshold, incrementar level

### Verificação de Badges

- Checada após cada ação relevante
- Se critério atingido: criar UserBadge + dar XP bônus

---

## 🎨 Princípios de Código

### Estilo

- Código limpo e legível
- Nomes descritivos
- Comentários em português quando não-óbvio
- Evitar abstrações desnecessárias
- Clareza > "elegância"

### Commits

- Mensagens claras em português
- Padrão: `tipo: descrição`
- Tipos: feat, fix, refactor, docs, style
- Commits frequentes por funcionalidade
- Exemplo: `feat: adiciona endpoint de aprovar tarefa`

---

## 🚀 Deploy

### Backend - Railway.app

- Conectar repo GitHub
- Adicionar PostgreSQL
- Variáveis: DATABASE_URL, JWT_SECRET

### Mobile - Expo

- `expo build:android` para APK
- Testar com Expo Go em desenvolvimento

---

## 🎯 Lembre-se

Este é um projeto ACADÊMICO com:

- ✅ Prazo APERTADO (1 mês)
- ✅ Equipe JÚNIOR
- ✅ Foco em FUNCIONALIDADE
- ❌ NÃO over-engineering
- ❌ NÃO padrões complexos desnecessários

**Objetivo:** Sistema que funciona e que a equipe compreende para defender.
