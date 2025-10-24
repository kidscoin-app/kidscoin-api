# 📱 Guia Completo da API Backend - KidsCoins

**Para integração com Mobile React Native**

Este documento contém todas as informações necessárias para integrar o mobile com a API backend do KidsCoins.

---

## 📋 Índice

1. [Visão Geral](#visão-geral)
2. [Autenticação](#autenticação)
3. [Endpoints Completos](#endpoints-completos)
4. [Modelos de Dados (DTOs)](#modelos-de-dados-dtos)
5. [Fluxos de Uso](#fluxos-de-uso)
6. [Regras de Negócio](#regras-de-negócio)
7. [Tratamento de Erros](#tratamento-de-erros)
8. [Exemplos de Código](#exemplos-de-código)

---

## 🎯 Visão Geral

### URL Base
```
Desenvolvimento: http://localhost:8080
Produção: https://seu-app.railway.app
```

### Tecnologias
- **Framework:** Spring Boot 3.2.5
- **Autenticação:** JWT (Bearer Token)
- **Banco de Dados:** PostgreSQL 15
- **Formato:** JSON

### Princípios
- RESTful API
- Autenticação stateless (JWT)
- Validações server-side
- Mensagens de erro em português
- Código simples e compreensível

---

## 🔐 Autenticação

### Sistema de Tokens

A API usa **JWT (JSON Web Tokens)** para autenticação.

#### Access Token
- **Duração:** 24 horas
- **Uso:** Incluir em todas as requisições autenticadas
- **Header:** `Authorization: Bearer {token}`

#### Refresh Token
- **Duração:** 7 dias
- **Uso:** Renovar access token expirado
- **Armazenamento:** Banco de dados (pode ser revogado)

### Fluxo de Autenticação

#### 1. Registro de Pais (PARENT)

```http
POST /api/auth/register
Content-Type: application/json

{
  "email": "pai@example.com",
  "password": "senha123",
  "fullName": "João Silva",
  "familyName": "Família Silva"
}
```

**Response 200:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIs...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIs...",
  "tokenType": "Bearer",
  "expiresIn": 86400,
  "user": {
    "id": "uuid",
    "email": "pai@example.com",
    "fullName": "João Silva",
    "role": "PARENT",
    "familyId": "uuid",
    "avatarUrl": null
  }
}
```

#### 2. Login

```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "pai@example.com",
  "password": "senha123"
}
```

**Response:** Igual ao registro

#### 3. Login de Criança (CHILD)

```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "joao.abc123@child.local",
  "password": "1234"
}
```

**Nota:** Email é gerado automaticamente, senha é o PIN de 4 dígitos.

#### 4. Renovar Token

```http
POST /api/auth/refresh
Content-Type: application/json

{
  "refreshToken": "eyJhbGciOiJIUzI1NiIs..."
}
```

**Response 200:**
```json
{
  "accessToken": "novo_token...",
  "refreshToken": "novo_refresh_token...",
  "tokenType": "Bearer",
  "expiresIn": 86400
}
```

### Como Usar Tokens no Mobile

```typescript
// Axios exemplo
import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080',
});

// Interceptor para adicionar token
api.interceptors.request.use((config) => {
  const token = await AsyncStorage.getItem('@token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Interceptor para refresh token
api.interceptors.response.use(
  (response) => response,
  async (error) => {
    if (error.response?.status === 401) {
      const refreshToken = await AsyncStorage.getItem('@refreshToken');

      try {
        const { data } = await axios.post('/api/auth/refresh', {
          refreshToken
        });

        await AsyncStorage.setItem('@token', data.accessToken);
        await AsyncStorage.setItem('@refreshToken', data.refreshToken);

        // Retry request original
        error.config.headers.Authorization = `Bearer ${data.accessToken}`;
        return axios(error.config);
      } catch (refreshError) {
        // Logout e redirecionar para login
        await AsyncStorage.clear();
        navigation.navigate('Login');
      }
    }
    return Promise.reject(error);
  }
);
```

---

## 📡 Endpoints Completos

### 👤 Usuários

#### Obter usuário logado
```http
GET /api/users/me
Authorization: Bearer {token}
```

**Response 200:**
```json
{
  "id": "uuid",
  "email": "pai@example.com",
  "fullName": "João Silva",
  "role": "PARENT",
  "familyId": "uuid",
  "avatarUrl": null
}
```

#### Criar perfil de criança
```http
POST /api/users/children
Authorization: Bearer {token}
Content-Type: application/json

{
  "fullName": "Maria Silva",
  "pin": "1234",
  "avatarUrl": "https://..."
}
```

**Response 200:**
```json
{
  "id": "uuid",
  "email": "maria.abc123@child.local",
  "fullName": "Maria Silva",
  "role": "CHILD",
  "familyId": "uuid",
  "avatarUrl": "https://..."
}
```

**Nota:** Email é gerado automaticamente.

#### Listar crianças da família
```http
GET /api/users/children
Authorization: Bearer {token}
```

**Response 200:**
```json
[
  {
    "id": "uuid",
    "email": "maria.abc123@child.local",
    "fullName": "Maria Silva",
    "role": "CHILD",
    "familyId": "uuid",
    "avatarUrl": "https://..."
  }
]
```

---

### 📋 Tarefas

#### Criar tarefa (PARENT)
```http
POST /api/tasks
Authorization: Bearer {token}
Content-Type: application/json

{
  "title": "Arrumar o quarto",
  "description": "Organizar brinquedos e fazer a cama",
  "coinValue": 10,
  "xpValue": 50,
  "category": "ORGANIZACAO",
  "childrenIds": ["uuid1", "uuid2"]
}
```

**Categorias disponíveis:**
- `LIMPEZA`
- `ORGANIZACAO`
- `ESTUDOS`
- `CUIDADOS`
- `OUTRAS`

**Response 200:**
```json
{
  "id": "uuid",
  "title": "Arrumar o quarto",
  "description": "Organizar brinquedos e fazer a cama",
  "coinValue": 10,
  "xpValue": 50,
  "category": "ORGANIZACAO",
  "status": "ACTIVE",
  "familyId": "uuid",
  "createdByName": "João Silva",
  "createdAt": "2025-01-24T10:00:00"
}
```

#### Listar tarefas
```http
GET /api/tasks
Authorization: Bearer {token}
```

**Response 200 (PARENT vê todas, CHILD vê apenas as suas):**
```json
[
  {
    "id": "uuid",
    "task": {
      "id": "uuid",
      "title": "Arrumar o quarto",
      "description": "Organizar brinquedos e fazer a cama",
      "coinValue": 10,
      "xpValue": 50,
      "category": "ORGANIZACAO",
      "status": "ACTIVE",
      "familyId": "uuid",
      "createdByName": "João Silva",
      "createdAt": "2025-01-24T10:00:00"
    },
    "childId": "uuid",
    "childName": "Maria Silva",
    "status": "PENDING",
    "completedAt": null,
    "approvedAt": null,
    "approvedByName": null,
    "rejectionReason": null,
    "createdAt": "2025-01-24T10:00:00"
  }
]
```

**Status possíveis:**
- `PENDING` - Aguardando criança completar
- `COMPLETED` - Criança marcou como concluída, aguardando aprovação
- `APPROVED` - Pai aprovou (moedas e XP creditados)
- `REJECTED` - Pai rejeitou

#### Marcar tarefa como concluída (CHILD)
```http
POST /api/tasks/{assignmentId}/complete
Authorization: Bearer {token}
```

**Response 200:** TaskAssignmentResponse com status `COMPLETED`

#### Aprovar tarefa (PARENT)
```http
POST /api/tasks/{assignmentId}/approve
Authorization: Bearer {token}
```

**Response 200:** TaskAssignmentResponse com status `APPROVED`

**Importante:** Esta operação:
1. Credita moedas na carteira
2. Adiciona XP
3. Verifica level up automático
4. Verifica badges desbloqueadas
5. Cria notificações

#### Rejeitar tarefa (PARENT)
```http
POST /api/tasks/{assignmentId}/reject
Authorization: Bearer {token}
Content-Type: application/json

{
  "rejectionReason": "Não foi feito corretamente"
}
```

**Response 200:** TaskAssignmentResponse com status `REJECTED`

---

### 💰 Carteira

#### Ver carteira
```http
GET /api/wallet?childId={uuid}
Authorization: Bearer {token}
```

**Query param `childId` é opcional:**
- PARENT: pode passar childId para ver carteira de qualquer filho
- CHILD: se não passar, vê a própria carteira

**Response 200:**
```json
{
  "id": "uuid",
  "childId": "uuid",
  "childName": "Maria Silva",
  "balance": 150,
  "totalEarned": 200,
  "totalSpent": 50
}
```

#### Ver histórico de transações
```http
GET /api/wallet/transactions?childId={uuid}&limit=20&offset=0
Authorization: Bearer {token}
```

**Response 200:**
```json
[
  {
    "id": "uuid",
    "type": "CREDIT",
    "amount": 10,
    "balanceBefore": 140,
    "balanceAfter": 150,
    "description": "Tarefa aprovada: Arrumar o quarto",
    "referenceType": "TASK",
    "referenceId": "uuid",
    "createdAt": "2025-01-24T14:00:00"
  },
  {
    "id": "uuid",
    "type": "DEBIT",
    "amount": 20,
    "balanceBefore": 160,
    "balanceAfter": 140,
    "description": "Resgate aprovado: Pizza no fim de semana",
    "referenceType": "REWARD",
    "referenceId": "uuid",
    "createdAt": "2025-01-24T13:00:00"
  }
]
```

**Tipos de transação:**
- `CREDIT` - Entrada de moedas
- `DEBIT` - Saída de moedas

**Tipos de referência:**
- `TASK` - Tarefa aprovada
- `REWARD` - Resgate de recompensa
- `SAVINGS` - Movimentação com poupança
- `ADJUSTMENT` - Ajuste manual

---

### 🎁 Recompensas

#### Criar recompensa (PARENT)
```http
POST /api/rewards
Authorization: Bearer {token}
Content-Type: application/json

{
  "name": "Pizza no fim de semana",
  "description": "Pizza da sua escolha no sábado",
  "coinCost": 50,
  "category": "Comida",
  "imageUrl": "https://..."
}
```

**Response 200:**
```json
{
  "id": "uuid",
  "name": "Pizza no fim de semana",
  "description": "Pizza da sua escolha no sábado",
  "coinCost": 50,
  "category": "Comida",
  "imageUrl": "https://...",
  "isActive": true,
  "familyId": "uuid",
  "createdByName": "João Silva",
  "createdAt": "2025-01-24T10:00:00"
}
```

#### Listar recompensas
```http
GET /api/rewards?activeOnly=false
Authorization: Bearer {token}
```

**Query param `activeOnly`:**
- PARENT: default `false` (vê todas)
- CHILD: sempre `true` (vê apenas ativas)

**Response 200:** Array de RewardResponse

#### Ativar/Desativar recompensa (PARENT)
```http
PATCH /api/rewards/{rewardId}/toggle
Authorization: Bearer {token}
```

**Response 200:** RewardResponse com `isActive` alterado

---

### 🛒 Resgates

#### Solicitar resgate (CHILD)
```http
POST /api/redemptions
Authorization: Bearer {token}
Content-Type: application/json

{
  "rewardId": "uuid"
}
```

**Nota:** Moedas NÃO são debitadas neste momento. Sistema apenas valida se tem saldo.

**Response 200:**
```json
{
  "id": "uuid",
  "reward": {
    "id": "uuid",
    "name": "Pizza no fim de semana",
    "coinCost": 50,
    ...
  },
  "childId": "uuid",
  "childName": "Maria Silva",
  "status": "PENDING",
  "requestedAt": "2025-01-24T14:00:00",
  "reviewedAt": null,
  "reviewedByName": null,
  "rejectionReason": null
}
```

**Status possíveis:**
- `PENDING` - Aguardando aprovação do pai
- `APPROVED` - Aprovado (moedas debitadas)
- `REJECTED` - Rejeitado

#### Listar resgates
```http
GET /api/redemptions?status=PENDING
Authorization: Bearer {token}
```

**Query param `status` (opcional):** PENDING, APPROVED, REJECTED

**Response 200:** Array de RedemptionResponse

#### Aprovar resgate (PARENT)
```http
POST /api/redemptions/{redemptionId}/approve
Authorization: Bearer {token}
```

**Response 200:** RedemptionResponse com status `APPROVED`

**Importante:** AGORA as moedas são debitadas da carteira.

#### Rejeitar resgate (PARENT)
```http
POST /api/redemptions/{redemptionId}/reject
Authorization: Bearer {token}
Content-Type: application/json

{
  "rejectionReason": "Você precisa terminar as tarefas primeiro"
}
```

**Response 200:** RedemptionResponse com status `REJECTED`

---

### 🏦 Poupança

#### Ver poupança
```http
GET /api/savings?childId={uuid}
Authorization: Bearer {token}
```

**Response 200:**
```json
{
  "id": "uuid",
  "childId": "uuid",
  "childName": "Maria Silva",
  "balance": 100,
  "totalDeposited": 80,
  "totalEarned": 20,
  "lastDepositAt": "2025-01-17T10:00:00"
}
```

#### Depositar na poupança
```http
POST /api/savings/deposit?childId={uuid}
Authorization: Bearer {token}
Content-Type: application/json

{
  "amount": 50
}
```

**Importante:**
- Debita da carteira
- Credita na poupança
- Atualiza `lastDepositAt`

**Response 200:** SavingsResponse atualizado

#### Sacar da poupança
```http
POST /api/savings/withdraw?childId={uuid}
Authorization: Bearer {token}
Content-Type: application/json

{
  "amount": 30
}
```

**Importante - Bônus por tempo guardado:**
- < 7 dias: 0% de bônus
- 7-29 dias: +2% de bônus
- 30+ dias: +10% de bônus

**Exemplo:** Saque de 100 moedas após 30 dias = 110 moedas na carteira!

**Response 200:** SavingsResponse atualizado

#### Rendimento Automático

O sistema aplica **2% de rendimento toda semana** (domingo à meia-noite) automaticamente.
Criança recebe notificação quando isso acontece.

---

### 🎮 Gamificação

#### Ver dados de gamificação
```http
GET /api/gamification?childId={uuid}
Authorization: Bearer {token}
```

**Response 200:**
```json
{
  "currentLevel": 3,
  "currentXp": 75,
  "totalXp": 450,
  "xpForNextLevel": 550,
  "xpNeededForNextLevel": 100,
  "badges": [
    {
      "id": "uuid",
      "name": "Primeira Tarefa",
      "description": "Complete sua primeira tarefa",
      "iconName": "star",
      "criteriaType": "TASK_COUNT",
      "criteriaValue": 1,
      "xpBonus": 25,
      "unlocked": true,
      "unlockedAt": "2025-01-20T10:00:00"
    },
    {
      "id": "uuid",
      "name": "Poupador Iniciante",
      "description": "Acumule 100 moedas na carteira",
      "iconName": "piggy-bank",
      "criteriaType": "CURRENT_BALANCE",
      "criteriaValue": 100,
      "xpBonus": 50,
      "unlocked": false,
      "unlockedAt": null
    }
  ]
}
```

#### Níveis

Total: **10 níveis** (1 a 10)

**Fórmula XP por nível:**
```
Para alcançar o nível N, precisa de XP total =
soma de (i * 100 + (i-1) * 50) para i de 1 até N

Exemplos:
- Nível 1: 0 XP
- Nível 2: 100 XP
- Nível 3: 250 XP
- Nível 4: 450 XP
- Nível 5: 700 XP
- Nível 10: 4500 XP
```

#### Badges Disponíveis

1. **Primeira Tarefa** - 1 tarefa completa (+25 XP)
2. **Poupador Iniciante** - 100 moedas na carteira (+50 XP)
3. **Trabalhador Dedicado** - 10 tarefas completas (+75 XP)
4. **Dia Produtivo** - 5 tarefas em 1 dia (+100 XP)
5. **Consistente** - 7 dias seguidos com tarefas (+150 XP)
6. **Planejador** - 200 moedas na poupança (+100 XP)
7. **Comprador Consciente** - Primeiro resgate (+50 XP)
8. **Milionário** - 1000 moedas ganhas no total (+200 XP)

**Importante:** Quando uma badge é desbloqueada, o XP bônus é adicionado automaticamente e pode causar level up!

---

### 🔔 Notificações

#### Listar notificações
```http
GET /api/notifications
Authorization: Bearer {token}
```

**Response 200:**
```json
[
  {
    "id": "uuid",
    "type": "TASK_APPROVED",
    "title": "Tarefa aprovada!",
    "message": "Você ganhou 10 moedas e 50 XP por completar: Arrumar o quarto",
    "referenceType": "TASK",
    "referenceId": "uuid",
    "isRead": false,
    "readAt": null,
    "createdAt": "2025-01-24T14:00:00"
  }
]
```

**Tipos de notificação:**
- `TASK_ASSIGNED` - Nova tarefa atribuída
- `TASK_COMPLETED` - Criança completou tarefa (para pai)
- `TASK_APPROVED` - Tarefa aprovada (para criança)
- `TASK_REJECTED` - Tarefa rejeitada (para criança)
- `LEVEL_UP` - Subiu de nível
- `BADGE_UNLOCKED` - Badge desbloqueada
- `REDEMPTION_REQUESTED` - Resgate solicitado (para pai)
- `REDEMPTION_APPROVED` - Resgate aprovado (para criança)
- `REDEMPTION_REJECTED` - Resgate rejeitado (para criança)
- `SAVINGS_DEPOSIT` - Depósito na poupança
- `SAVINGS_WITHDRAWAL` - Saque da poupança
- `SAVINGS_INTEREST` - Rendimento semanal

#### Marcar notificação como lida
```http
PATCH /api/notifications/{notificationId}/read
Authorization: Bearer {token}
```

**Response 200:** Vazio (204 No Content)

#### Marcar todas como lidas
```http
PATCH /api/notifications/read-all
Authorization: Bearer {token}
```

**Response 200:** Vazio (204 No Content)

#### Contar não lidas
```http
GET /api/notifications/unread-count
Authorization: Bearer {token}
```

**Response 200:**
```json
5
```

---

## 📦 Modelos de Dados (DTOs)

### Request DTOs

#### RegisterRequest
```typescript
{
  email: string;          // Obrigatório, formato email
  password: string;       // Obrigatório, mínimo 8 caracteres
  fullName: string;       // Obrigatório
  familyName: string;     // Obrigatório
}
```

#### LoginRequest
```typescript
{
  email: string;          // Obrigatório
  password: string;       // Obrigatório
}
```

#### CreateChildRequest
```typescript
{
  fullName: string;       // Obrigatório
  pin: string;            // Obrigatório, 4 dígitos
  avatarUrl?: string;     // Opcional
}
```

#### CreateTaskRequest
```typescript
{
  title: string;          // Obrigatório
  description?: string;   // Opcional
  coinValue: number;      // Obrigatório, positivo
  xpValue: number;        // Obrigatório, positivo
  category: TaskCategory; // Obrigatório
  childrenIds: string[];  // Obrigatório, mínimo 1
}

enum TaskCategory {
  LIMPEZA = "LIMPEZA",
  ORGANIZACAO = "ORGANIZACAO",
  ESTUDOS = "ESTUDOS",
  CUIDADOS = "CUIDADOS",
  OUTRAS = "OUTRAS"
}
```

#### RejectTaskRequest
```typescript
{
  rejectionReason: string; // Obrigatório
}
```

#### CreateRewardRequest
```typescript
{
  name: string;           // Obrigatório
  description?: string;   // Opcional
  coinCost: number;       // Obrigatório, positivo
  category?: string;      // Opcional
  imageUrl?: string;      // Opcional
}
```

#### CreateRedemptionRequest
```typescript
{
  rewardId: string;       // Obrigatório, UUID
}
```

#### RejectRedemptionRequest
```typescript
{
  rejectionReason: string; // Obrigatório
}
```

#### DepositSavingsRequest / WithdrawSavingsRequest
```typescript
{
  amount: number;         // Obrigatório, positivo
}
```

### Response DTOs

#### AuthResponse
```typescript
{
  accessToken: string;
  refreshToken: string;
  tokenType: "Bearer";
  expiresIn: number;      // segundos (86400 = 24h)
  user: UserResponse;
}
```

#### UserResponse
```typescript
{
  id: string;             // UUID
  email: string;
  fullName: string;
  role: "PARENT" | "CHILD";
  familyId: string;       // UUID
  avatarUrl: string | null;
}
```

#### TaskResponse
```typescript
{
  id: string;
  title: string;
  description: string | null;
  coinValue: number;
  xpValue: number;
  category: TaskCategory;
  status: "ACTIVE" | "INACTIVE";
  familyId: string;
  createdByName: string;
  createdAt: string;      // ISO 8601
}
```

#### TaskAssignmentResponse
```typescript
{
  id: string;
  task: TaskResponse;
  childId: string;
  childName: string;
  status: "PENDING" | "COMPLETED" | "APPROVED" | "REJECTED";
  completedAt: string | null;
  approvedAt: string | null;
  approvedByName: string | null;
  rejectionReason: string | null;
  createdAt: string;
}
```

#### WalletResponse
```typescript
{
  id: string;
  childId: string;
  childName: string;
  balance: number;
  totalEarned: number;
  totalSpent: number;
}
```

#### TransactionResponse
```typescript
{
  id: string;
  type: "CREDIT" | "DEBIT";
  amount: number;
  balanceBefore: number;
  balanceAfter: number;
  description: string;
  referenceType: "TASK" | "REWARD" | "SAVINGS" | "ADJUSTMENT" | null;
  referenceId: string | null;
  createdAt: string;
}
```

#### SavingsResponse
```typescript
{
  id: string;
  childId: string;
  childName: string;
  balance: number;
  totalDeposited: number;
  totalEarned: number;
  lastDepositAt: string | null;
}
```

#### RewardResponse
```typescript
{
  id: string;
  name: string;
  description: string | null;
  coinCost: number;
  category: string | null;
  imageUrl: string | null;
  isActive: boolean;
  familyId: string;
  createdByName: string;
  createdAt: string;
}
```

#### RedemptionResponse
```typescript
{
  id: string;
  reward: RewardResponse;
  childId: string;
  childName: string;
  status: "PENDING" | "APPROVED" | "REJECTED";
  requestedAt: string;
  reviewedAt: string | null;
  reviewedByName: string | null;
  rejectionReason: string | null;
}
```

#### GamificationResponse
```typescript
{
  currentLevel: number;         // 1-10
  currentXp: number;            // XP no nível atual
  totalXp: number;              // XP total acumulado
  xpForNextLevel: number;       // XP necessário para próximo nível
  xpNeededForNextLevel: number; // Quanto falta
  badges: BadgeResponse[];
}
```

#### BadgeResponse
```typescript
{
  id: string;
  name: string;
  description: string;
  iconName: string;
  criteriaType: BadgeCriteriaType;
  criteriaValue: number;
  xpBonus: number;
  unlocked: boolean;
  unlockedAt: string | null;
}

enum BadgeCriteriaType {
  TASK_COUNT = "TASK_COUNT",
  CURRENT_BALANCE = "CURRENT_BALANCE",
  TOTAL_COINS_EARNED = "TOTAL_COINS_EARNED",
  REDEMPTION_COUNT = "REDEMPTION_COUNT",
  SAVINGS_AMOUNT = "SAVINGS_AMOUNT",
  TASKS_IN_ONE_DAY = "TASKS_IN_ONE_DAY",
  STREAK_DAYS = "STREAK_DAYS",
  DAYS_SAVED = "DAYS_SAVED"
}
```

#### NotificationResponse
```typescript
{
  id: string;
  type: NotificationType;
  title: string;
  message: string;
  referenceType: ReferenceType | null;
  referenceId: string | null;
  isRead: boolean;
  readAt: string | null;
  createdAt: string;
}
```

---

## 🔄 Fluxos de Uso

### Fluxo 1: Registro e Login

```
1. Pai se registra (POST /api/auth/register)
2. Sistema cria família automaticamente
3. Pai faz login (POST /api/auth/login)
4. Pai cria perfil de criança (POST /api/users/children)
   - Sistema cria Wallet, UserXP e Savings automaticamente
5. Criança faz login com email gerado e PIN
```

### Fluxo 2: Ciclo Completo de Tarefa

```
1. Pai cria tarefa (POST /api/tasks)
2. Sistema atribui para crianças selecionadas
3. Criança recebe notificação (TASK_ASSIGNED)
4. Criança vê tarefa na lista (GET /api/tasks)
5. Criança completa tarefa (POST /api/tasks/{id}/complete)
6. Pai recebe notificação (TASK_COMPLETED)
7. Pai aprova tarefa (POST /api/tasks/{id}/approve)
   - Sistema credita moedas
   - Sistema adiciona XP
   - Sistema verifica level up
   - Sistema verifica badges
   - Criança recebe notificações (TASK_APPROVED, LEVEL_UP?, BADGE_UNLOCKED?)
8. Criança vê saldo atualizado (GET /api/wallet)
9. Criança vê nível/XP atualizado (GET /api/gamification)
```

### Fluxo 3: Resgate de Recompensa

```
1. Pai cria recompensa (POST /api/rewards)
2. Criança vê loja (GET /api/rewards?activeOnly=true)
3. Criança solicita resgate (POST /api/redemptions)
   - Sistema valida saldo (mas não debita)
4. Pai recebe notificação (REDEMPTION_REQUESTED)
5. Pai aprova resgate (POST /api/redemptions/{id}/approve)
   - Sistema debita moedas
   - Criança recebe notificação (REDEMPTION_APPROVED)
6. Criança vê saldo atualizado (GET /api/wallet)
```

### Fluxo 4: Uso da Poupança

```
1. Criança tem moedas na carteira
2. Criança deposita (POST /api/savings/deposit)
   - Débito da carteira
   - Crédito na poupança
3. A cada domingo, sistema aplica 2% de rendimento automaticamente
4. Criança recebe notificação (SAVINGS_INTEREST)
5. Após 7+ dias, criança pode sacar com bônus
6. Criança saca (POST /api/savings/withdraw)
   - Débito da poupança
   - Crédito na carteira (valor + bônus)
```

---

## ⚠️ Regras de Negócio Importantes

### Autenticação e Autorização

1. **PARENT pode:**
   - Criar tarefas e recompensas
   - Aprovar/rejeitar tarefas e resgates
   - Ver dados de todas as crianças da família
   - Criar perfis de crianças

2. **CHILD pode:**
   - Ver apenas suas próprias tarefas
   - Completar tarefas atribuídas a si
   - Ver recompensas ativas
   - Solicitar resgates
   - Ver/gerenciar própria carteira e poupança
   - Ver próprios dados de gamificação

### Tarefas

1. Status deve seguir ordem: `PENDING → COMPLETED → APPROVED/REJECTED`
2. Apenas criança atribuída pode marcar como concluída
3. Apenas pai da família pode aprovar/rejeitar
4. Ao aprovar: credita moedas + XP + verifica badges + notifica

### Carteira

1. **Lock pessimista:** Evita race conditions em operações simultâneas
2. Saldo nunca pode ser negativo
3. Transações são imutáveis (não podem ser editadas/deletadas)
4. Toda transação tem referência ao que causou (tarefa, resgate, etc.)

### Resgates

1. **Moedas NÃO debitadas na solicitação** (apenas valida)
2. **Moedas debitadas apenas na aprovação**
3. Se rejeitado, moedas permanecem na carteira

### Gamificação

1. XP é adicionado apenas quando tarefa é aprovada
2. Level up é automático quando XP total atinge threshold
3. Badges são verificadas automaticamente após ganhar XP
4. XP bônus de badges pode causar level up em cascata
5. Badges são únicas (não pode desbloquear duas vezes)

### Poupança

1. Rendimento de 2% aplicado automaticamente toda semana
2. Bônus de saque baseado em `lastDepositAt`
3. Criança pode depositar/sacar a qualquer momento
4. PARENT pode fazer operações em nome da criança

---

## ❌ Tratamento de Erros

### Códigos HTTP

- `200 OK` - Sucesso
- `201 Created` - Recurso criado
- `204 No Content` - Sucesso sem retorno
- `400 Bad Request` - Validação falhou
- `401 Unauthorized` - Token inválido/expirado
- `403 Forbidden` - Sem permissão
- `404 Not Found` - Recurso não encontrado
- `500 Internal Server Error` - Erro do servidor

### Formato de Erro

```json
{
  "timestamp": "2025-01-24T14:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Saldo insuficiente. Saldo atual: 10 moedas",
  "path": "/api/redemptions/approve"
}
```

### Mensagens de Erro Comuns

#### Validação (400)
```json
{
  "message": "Título é obrigatório"
}
```

#### Autenticação (401)
```json
{
  "message": "Token inválido ou expirado"
}
```

#### Autorização (403)
```json
{
  "message": "Você não tem permissão para aprovar esta tarefa"
}
```

#### Não Encontrado (404)
```json
{
  "message": "Tarefa não encontrada"
}
```

#### Regra de Negócio (400)
```json
{
  "message": "Saldo insuficiente. Saldo atual: 10 moedas"
}
```

---

## 💻 Exemplos de Código Mobile

### Setup do Axios

```typescript
// src/services/api.ts
import axios from 'axios';
import AsyncStorage from '@react-native-async-storage/async-storage';

const api = axios.create({
  baseURL: __DEV__
    ? 'http://localhost:8080'
    : 'https://seu-app.railway.app',
  timeout: 10000,
});

// Request interceptor
api.interceptors.request.use(
  async (config) => {
    const token = await AsyncStorage.getItem('@kidscoin:token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response interceptor
api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    // Se 401 e não é retry, tentar refresh token
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;

      try {
        const refreshToken = await AsyncStorage.getItem('@kidscoin:refreshToken');

        const { data } = await axios.post(
          `${api.defaults.baseURL}/api/auth/refresh`,
          { refreshToken }
        );

        await AsyncStorage.setItem('@kidscoin:token', data.accessToken);
        await AsyncStorage.setItem('@kidscoin:refreshToken', data.refreshToken);

        originalRequest.headers.Authorization = `Bearer ${data.accessToken}`;
        return api(originalRequest);
      } catch (refreshError) {
        // Logout
        await AsyncStorage.clear();
        // Navigate to login
        return Promise.reject(refreshError);
      }
    }

    return Promise.reject(error);
  }
);

export default api;
```

### Serviço de Autenticação

```typescript
// src/services/auth.service.ts
import api from './api';
import AsyncStorage from '@react-native-async-storage/async-storage';

export interface RegisterData {
  email: string;
  password: string;
  fullName: string;
  familyName: string;
}

export interface LoginData {
  email: string;
  password: string;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
  user: {
    id: string;
    email: string;
    fullName: string;
    role: 'PARENT' | 'CHILD';
    familyId: string;
    avatarUrl: string | null;
  };
}

class AuthService {
  async register(data: RegisterData): Promise<AuthResponse> {
    const response = await api.post<AuthResponse>('/api/auth/register', data);
    await this.saveTokens(response.data);
    return response.data;
  }

  async login(data: LoginData): Promise<AuthResponse> {
    const response = await api.post<AuthResponse>('/api/auth/login', data);
    await this.saveTokens(response.data);
    return response.data;
  }

  async logout(): Promise<void> {
    await AsyncStorage.multiRemove([
      '@kidscoin:token',
      '@kidscoin:refreshToken',
      '@kidscoin:user',
    ]);
  }

  async getCurrentUser() {
    const response = await api.get('/api/users/me');
    return response.data;
  }

  private async saveTokens(data: AuthResponse): Promise<void> {
    await AsyncStorage.multiSet([
      ['@kidscoin:token', data.accessToken],
      ['@kidscoin:refreshToken', data.refreshToken],
      ['@kidscoin:user', JSON.stringify(data.user)],
    ]);
  }

  async getStoredUser() {
    const user = await AsyncStorage.getItem('@kidscoin:user');
    return user ? JSON.parse(user) : null;
  }
}

export default new AuthService();
```

### Serviço de Tarefas

```typescript
// src/services/task.service.ts
import api from './api';

export interface CreateTaskData {
  title: string;
  description?: string;
  coinValue: number;
  xpValue: number;
  category: 'LIMPEZA' | 'ORGANIZACAO' | 'ESTUDOS' | 'CUIDADOS' | 'OUTRAS';
  childrenIds: string[];
}

class TaskService {
  async createTask(data: CreateTaskData) {
    const response = await api.post('/api/tasks', data);
    return response.data;
  }

  async getTasks() {
    const response = await api.get('/api/tasks');
    return response.data;
  }

  async completeTask(assignmentId: string) {
    const response = await api.post(`/api/tasks/${assignmentId}/complete`);
    return response.data;
  }

  async approveTask(assignmentId: string) {
    const response = await api.post(`/api/tasks/${assignmentId}/approve`);
    return response.data;
  }

  async rejectTask(assignmentId: string, reason: string) {
    const response = await api.post(`/api/tasks/${assignmentId}/reject`, {
      rejectionReason: reason,
    });
    return response.data;
  }
}

export default new TaskService();
```

### Hook de Autenticação

```typescript
// src/hooks/useAuth.ts
import { createContext, useContext, useState, useEffect } from 'react';
import authService, { AuthResponse } from '../services/auth.service';

interface AuthContextData {
  user: AuthResponse['user'] | null;
  loading: boolean;
  signIn: (email: string, password: string) => Promise<void>;
  signOut: () => Promise<void>;
}

const AuthContext = createContext<AuthContextData>({} as AuthContextData);

export function AuthProvider({ children }) {
  const [user, setUser] = useState<AuthResponse['user'] | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadStoredData();
  }, []);

  async function loadStoredData() {
    try {
      const storedUser = await authService.getStoredUser();
      if (storedUser) {
        // Validar token
        const currentUser = await authService.getCurrentUser();
        setUser(currentUser);
      }
    } catch (error) {
      console.log('Token inválido:', error);
      await authService.logout();
    } finally {
      setLoading(false);
    }
  }

  async function signIn(email: string, password: string) {
    const data = await authService.login({ email, password });
    setUser(data.user);
  }

  async function signOut() {
    await authService.logout();
    setUser(null);
  }

  return (
    <AuthContext.Provider value={{ user, loading, signIn, signOut }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  return useContext(AuthContext);
}
```

### Exemplo de Tela

```typescript
// src/screens/TaskListScreen.tsx
import React, { useEffect, useState } from 'react';
import { View, FlatList, Text } from 'react-native';
import taskService from '../services/task.service';
import { useAuth } from '../hooks/useAuth';

export function TaskListScreen() {
  const { user } = useAuth();
  const [tasks, setTasks] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadTasks();
  }, []);

  async function loadTasks() {
    try {
      const data = await taskService.getTasks();
      setTasks(data);
    } catch (error) {
      console.error('Erro ao carregar tarefas:', error);
    } finally {
      setLoading(false);
    }
  }

  async function handleCompleteTask(assignmentId: string) {
    try {
      await taskService.completeTask(assignmentId);
      await loadTasks(); // Recarregar lista
    } catch (error) {
      console.error('Erro ao completar tarefa:', error);
    }
  }

  return (
    <View>
      <FlatList
        data={tasks}
        keyExtractor={(item) => item.id}
        renderItem={({ item }) => (
          <TaskItem
            task={item}
            onComplete={() => handleCompleteTask(item.id)}
            isChild={user?.role === 'CHILD'}
          />
        )}
      />
    </View>
  );
}
```

---

## 🚀 Checklist de Integração

### Setup Inicial
- [ ] Configurar baseURL do Axios
- [ ] Implementar interceptors (token, refresh)
- [ ] Configurar AsyncStorage para tokens
- [ ] Criar context de autenticação

### Autenticação
- [ ] Tela de registro (PARENT)
- [ ] Tela de login (PARENT e CHILD)
- [ ] Logout
- [ ] Persistência de sessão
- [ ] Refresh token automático

### Funcionalidades PARENT
- [ ] Dashboard com visão geral
- [ ] Criar perfil de criança
- [ ] Criar tarefas
- [ ] Aprovar/rejeitar tarefas
- [ ] Criar recompensas
- [ ] Aprovar/rejeitar resgates
- [ ] Ver progresso das crianças

### Funcionalidades CHILD
- [ ] Dashboard infantil
- [ ] Ver tarefas disponíveis
- [ ] Marcar tarefas como concluídas
- [ ] Ver carteira (saldo, transações)
- [ ] Loja de recompensas
- [ ] Solicitar resgates
- [ ] Ver gamificação (nível, XP, badges)
- [ ] Poupança (depositar, sacar)
- [ ] Ver notificações

### Extras
- [ ] Push notifications (Expo)
- [ ] Loading states
- [ ] Error handling
- [ ] Offline mode (opcional)
- [ ] Animações
- [ ] Dark mode

---

## 📞 Suporte

Para dúvidas sobre a API:
1. Consulte este documento
2. Verifique o arquivo `PROGRESS.md` para contexto
3. Verifique o arquivo `PROJECT_CONTEXT.md` para regras de negócio

---

**Versão:** 1.0.0
**Última atualização:** 2025-01-24
**Status:** Produção Ready ✅
