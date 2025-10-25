# 📱 Guia de Integração Mobile - KidsCoins API

## 🌐 Configuração

### Base URL
```
http://SEU_IP_LOCAL:8080/api
```

**Exemplo:** `http://192.168.1.100:8080/api`

### Headers Padrão
```
Content-Type: application/json
```

### Headers com Autenticação
```
Content-Type: application/json
Authorization: Bearer {access_token}
```

---

## 🔐 Autenticação

### 1. Registro de Pais (Novo Usuário)

**POST** `/auth/register`

```json
{
  "email": "pai@exemplo.com",
  "password": "senha123",
  "fullName": "João Silva",
  "familyName": "Família Silva"
}
```

**Response 200:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "expiresIn": 86400000,
  "user": {
    "id": "uuid",
    "email": "pai@exemplo.com",
    "fullName": "João Silva",
    "role": "PARENT",
    "familyId": "uuid",
    "familyName": "Família Silva"
  }
}
```

---

### 2. Login

**POST** `/auth/login`

```json
{
  "email": "pai@exemplo.com",
  "password": "senha123"
}
```

**Response 200:** Igual ao registro

---

### 3. Renovar Token

**POST** `/auth/refresh`

```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Response 200:**
```json
{
  "accessToken": "novo_access_token",
  "refreshToken": "novo_refresh_token",
  "tokenType": "Bearer",
  "expiresIn": 86400000
}
```

**IMPORTANTE:**
- Access Token expira em **24 horas**
- Refresh Token expira em **7 dias**
- Salvar ambos tokens no dispositivo (SecureStorage)

---

## 👨‍👩‍👧‍👦 Gestão de Usuários

### 4. Dados do Usuário Logado

**GET** `/users/me`

**Headers:** `Authorization: Bearer {token}`

**Response 200:**
```json
{
  "id": "uuid",
  "email": "pai@exemplo.com",
  "fullName": "João Silva",
  "role": "PARENT",
  "familyId": "uuid",
  "familyName": "Família Silva",
  "avatarUrl": null,
  "createdAt": "2025-10-24T12:00:00"
}
```

---

### 5. Criar Perfil de Criança

**POST** `/users/children`

**Headers:** `Authorization: Bearer {token}`

```json
{
  "fullName": "Maria Silva",
  "pin": "1234",
  "avatarUrl": "https://example.com/avatar.png"
}
```

**Response 200:**
```json
{
  "id": "uuid",
  "email": "maria.silva.abc123@child.local",
  "fullName": "Maria Silva",
  "role": "CHILD",
  "familyId": "uuid",
  "familyName": "Família Silva",
  "avatarUrl": "https://example.com/avatar.png",
  "createdAt": "2025-10-24T12:00:00"
}
```

**IMPORTANTE:**
- Email é gerado automaticamente
- PIN é usado para login da criança
- Ao criar criança, são criados automaticamente:
  - Carteira (balance: 0)
  - XP (level: 1, xp: 0)
  - Poupança (balance: 0)

---

### 6. Listar Crianças da Família

**GET** `/users/children`

**Headers:** `Authorization: Bearer {token}`

**Response 200:**
```json
[
  {
    "id": "uuid",
    "email": "maria.silva.abc123@child.local",
    "fullName": "Maria Silva",
    "role": "CHILD",
    "familyId": "uuid",
    "familyName": "Família Silva",
    "avatarUrl": "https://example.com/avatar.png",
    "createdAt": "2025-10-24T12:00:00"
  }
]
```

---

## ✅ Tarefas

### 7. Criar Tarefa (Pais)

**POST** `/tasks`

**Headers:** `Authorization: Bearer {token}`

```json
{
  "title": "Arrumar a cama",
  "description": "Arrumar a cama todos os dias pela manhã",
  "coinValue": 5,
  "xpValue": 10,
  "category": "LIMPEZA",
  "childrenIds": ["uuid-crianca-1", "uuid-crianca-2"]
}
```

**Categorias:** `LIMPEZA`, `ORGANIZACAO`, `ESTUDOS`, `CUIDADOS`, `OUTRAS`

**Response 200:**
```json
{
  "id": "uuid",
  "title": "Arrumar a cama",
  "description": "Arrumar a cama todos os dias pela manhã",
  "coinValue": 5,
  "xpValue": 10,
  "category": "LIMPEZA",
  "status": "ACTIVE",
  "familyId": "uuid",
  "createdBy": {
    "id": "uuid",
    "fullName": "João Silva"
  },
  "createdAt": "2025-10-24T12:00:00"
}
```

---

### 8. Listar Tarefas

**GET** `/tasks`

**Headers:** `Authorization: Bearer {token}`

**Para Pais:** Retorna todas tarefas da família
**Para Crianças:** Retorna apenas tarefas atribuídas à criança

**Response 200:**
```json
[
  {
    "id": "uuid",
    "task": {
      "id": "uuid",
      "title": "Arrumar a cama",
      "description": "Arrumar a cama todos os dias pela manhã",
      "coinValue": 5,
      "xpValue": 10,
      "category": "LIMPEZA"
    },
    "assignedToChild": {
      "id": "uuid",
      "fullName": "Maria Silva"
    },
    "status": "PENDING",
    "completedAt": null,
    "approvedAt": null
  }
]
```

**Status:** `PENDING`, `COMPLETED`, `APPROVED`, `REJECTED`

---

### 9. Marcar Tarefa como Completa (Criança)

**POST** `/tasks/{assignmentId}/complete`

**Headers:** `Authorization: Bearer {token_crianca}`

**Response 200:**
```json
{
  "id": "uuid",
  "task": {...},
  "assignedToChild": {...},
  "status": "COMPLETED",
  "completedAt": "2025-10-24T14:30:00",
  "approvedAt": null
}
```

---

### 10. Aprovar Tarefa (Pais)

**POST** `/tasks/{assignmentId}/approve`

**Headers:** `Authorization: Bearer {token_pai}`

**Response 200:**
```json
{
  "id": "uuid",
  "task": {...},
  "assignedToChild": {...},
  "status": "APPROVED",
  "completedAt": "2025-10-24T14:30:00",
  "approvedAt": "2025-10-24T15:00:00",
  "approvedBy": {
    "id": "uuid",
    "fullName": "João Silva"
  }
}
```

**O que acontece ao aprovar:**
1. ✅ Crédito de moedas na carteira
2. ✅ Adição de XP (pode subir de nível!)
3. ✅ Verificação de badges (pode desbloquear!)
4. ✅ Notificação para a criança

---

### 11. Rejeitar Tarefa (Pais)

**POST** `/tasks/{assignmentId}/reject`

**Headers:** `Authorization: Bearer {token_pai}`

```json
{
  "rejectionReason": "A cama não foi bem arrumada"
}
```

**Response 200:**
```json
{
  "id": "uuid",
  "task": {...},
  "status": "REJECTED",
  "rejectionReason": "A cama não foi bem arrumada"
}
```

---

## 💰 Carteira

### 12. Ver Carteira

**GET** `/wallet/{childId}`

**Headers:** `Authorization: Bearer {token}`

**Response 200:**
```json
{
  "childId": "uuid",
  "childName": "Maria Silva",
  "balance": 150,
  "totalEarned": 200,
  "totalSpent": 50
}
```

---

### 13. Ver Transações

**GET** `/wallet/{childId}/transactions?limit=20&offset=0`

**Headers:** `Authorization: Bearer {token}`

**Response 200:**
```json
[
  {
    "id": "uuid",
    "type": "CREDIT",
    "amount": 5,
    "balanceBefore": 145,
    "balanceAfter": 150,
    "description": "Tarefa aprovada: Arrumar a cama",
    "referenceType": "TASK",
    "referenceId": "uuid-task",
    "createdAt": "2025-10-24T15:00:00"
  }
]
```

**Tipos:** `CREDIT`, `DEBIT`
**Referências:** `TASK`, `REWARD`, `SAVINGS`, `ADJUSTMENT`

---

## 🎁 Recompensas

### 14. Criar Recompensa (Pais)

**POST** `/rewards`

**Headers:** `Authorization: Bearer {token}`

```json
{
  "name": "Sorvete",
  "description": "Um sorvete na sorveteria",
  "coinCost": 50,
  "category": "COMIDA",
  "imageUrl": "https://example.com/sorvete.png"
}
```

**Response 200:**
```json
{
  "id": "uuid",
  "name": "Sorvete",
  "description": "Um sorvete na sorveteria",
  "coinCost": 50,
  "category": "COMIDA",
  "imageUrl": "https://example.com/sorvete.png",
  "isActive": true
}
```

---

### 15. Listar Recompensas

**GET** `/rewards`

**Headers:** `Authorization: Bearer {token}`

**Response 200:**
```json
[
  {
    "id": "uuid",
    "name": "Sorvete",
    "description": "Um sorvete na sorveteria",
    "coinCost": 50,
    "category": "COMIDA",
    "imageUrl": "https://example.com/sorvete.png",
    "isActive": true
  }
]
```

---

### 16. Solicitar Resgate (Criança)

**POST** `/redemptions`

**Headers:** `Authorization: Bearer {token_crianca}`

```json
{
  "rewardId": "uuid-recompensa"
}
```

**Response 200:**
```json
{
  "id": "uuid",
  "reward": {...},
  "child": {
    "id": "uuid",
    "fullName": "Maria Silva"
  },
  "status": "PENDING",
  "requestedAt": "2025-10-24T16:00:00"
}
```

**IMPORTANTE:** Moedas **NÃO são debitadas** na solicitação!

---

### 17. Aprovar Resgate (Pais)

**POST** `/redemptions/{redemptionId}/approve`

**Headers:** `Authorization: Bearer {token_pai}`

**Response 200:**
```json
{
  "id": "uuid",
  "reward": {...},
  "child": {...},
  "status": "APPROVED",
  "requestedAt": "2025-10-24T16:00:00",
  "reviewedAt": "2025-10-24T16:30:00",
  "reviewedBy": {
    "id": "uuid",
    "fullName": "João Silva"
  }
}
```

**O que acontece:**
1. ✅ Moedas são debitadas da carteira
2. ✅ Criança recebe notificação

---

### 18. Rejeitar Resgate (Pais)

**POST** `/redemptions/{redemptionId}/reject`

**Headers:** `Authorization: Bearer {token_pai}`

```json
{
  "rejectionReason": "Você tem uma prova amanhã"
}
```

---

## 🏦 Poupança

### 19. Depositar na Poupança

**POST** `/savings/{childId}/deposit`

**Headers:** `Authorization: Bearer {token}`

```json
{
  "amount": 50
}
```

**Response 200:**
```json
{
  "childId": "uuid",
  "childName": "Maria Silva",
  "balance": 100,
  "totalDeposited": 100,
  "totalEarned": 0,
  "lastDepositAt": "2025-10-24T17:00:00"
}
```

---

### 20. Sacar da Poupança

**POST** `/savings/{childId}/withdraw`

**Headers:** `Authorization: Bearer {token}`

```json
{
  "amount": 50
}
```

**Response 200:**
```json
{
  "childId": "uuid",
  "childName": "Maria Silva",
  "balance": 50,
  "totalDeposited": 100,
  "totalEarned": 2,
  "lastDepositAt": "2025-10-24T17:00:00"
}
```

**Bônus por Tempo:**
- Menos de 7 dias: 0%
- 7-29 dias: +2%
- 30+ dias: +10%

**Rendimento Semanal:**
- Todo domingo à meia-noite: +2% sobre o saldo

---

## 🎮 Gamificação

### 21. Ver Gamificação

**GET** `/gamification/{childId}`

**Headers:** `Authorization: Bearer {token}`

**Response 200:**
```json
{
  "childId": "uuid",
  "childName": "Maria Silva",
  "currentLevel": 3,
  "currentXp": 50,
  "totalXp": 550,
  "xpForNextLevel": 100,
  "badges": [
    {
      "id": "uuid",
      "name": "Primeira Tarefa",
      "description": "Complete sua primeira tarefa",
      "iconName": "star",
      "unlocked": true,
      "unlockedAt": "2025-10-24T15:00:00",
      "xpBonus": 25
    },
    {
      "id": "uuid",
      "name": "Poupador Iniciante",
      "description": "Acumule 100 moedas na carteira",
      "iconName": "piggy-bank",
      "unlocked": false,
      "unlockedAt": null,
      "xpBonus": 50
    }
  ]
}
```

**Cálculo de XP por Nível:**
- Nível 1→2: 100 XP
- Nível 2→3: 250 XP
- Nível 3→4: 450 XP
- Fórmula: `Σ(i * 100 + (i-1) * 50)` para i de 1 até N

---

## 🔔 Notificações

### 22. Listar Notificações

**GET** `/notifications`

**Headers:** `Authorization: Bearer {token}`

**Response 200:**
```json
[
  {
    "id": "uuid",
    "type": "TASK_APPROVED",
    "title": "Tarefa aprovada!",
    "message": "Você ganhou 5 moedas e 10 XP!",
    "referenceType": "TASK",
    "referenceId": "uuid-task",
    "isRead": false,
    "readAt": null,
    "createdAt": "2025-10-24T15:00:00"
  }
]
```

**Tipos de Notificação:**
- `TASK_ASSIGNED` - Tarefa atribuída
- `TASK_COMPLETED` - Criança completou
- `TASK_APPROVED` - Tarefa aprovada
- `TASK_REJECTED` - Tarefa rejeitada
- `LEVEL_UP` - Subiu de nível
- `BADGE_UNLOCKED` - Badge desbloqueada
- `REDEMPTION_REQUESTED` - Resgate solicitado
- `REDEMPTION_APPROVED` - Resgate aprovado
- `REDEMPTION_REJECTED` - Resgate rejeitado
- `SAVINGS_DEPOSIT` - Depósito na poupança
- `SAVINGS_WITHDRAWAL` - Saque da poupança
- `SAVINGS_INTEREST` - Rendimento semanal

---

### 23. Marcar Notificação como Lida

**PUT** `/notifications/{notificationId}/read`

**Headers:** `Authorization: Bearer {token}`

**Response 200:**
```json
{
  "id": "uuid",
  "isRead": true,
  "readAt": "2025-10-24T18:00:00"
}
```

---

### 24. Marcar Todas como Lidas

**PUT** `/notifications/read-all`

**Headers:** `Authorization: Bearer {token}`

**Response 200:** `200 OK`

---

## 🎯 Badges Disponíveis (Seeds)

1. **Primeira Tarefa** - Complete sua primeira tarefa (1 tarefa) - 25 XP
2. **Poupador Iniciante** - Acumule 100 moedas na carteira (100 moedas) - 50 XP
3. **Trabalhador Dedicado** - Complete 10 tarefas (10 tarefas) - 75 XP
4. **Dia Produtivo** - Complete 5 tarefas em um dia (5 em 1 dia) - 100 XP
5. **Consistente** - Complete tarefas por 7 dias seguidos (7 dias streak) - 150 XP
6. **Planejador** - Guarde 200 moedas na poupança (200 na poupança) - 100 XP
7. **Comprador Consciente** - Resgate sua primeira recompensa (1 resgate) - 50 XP
8. **Milionário** - Ganhe 1000 moedas no total (1000 total) - 200 XP

---

## 🔒 Códigos de Erro

| Código | Descrição |
|--------|-----------|
| 400 | Bad Request - Dados inválidos |
| 401 | Unauthorized - Token inválido ou expirado |
| 403 | Forbidden - Sem permissão |
| 404 | Not Found - Recurso não encontrado |
| 409 | Conflict - Email já existe |
| 500 | Internal Server Error |

**Formato de Erro:**
```json
{
  "timestamp": "2025-10-24T12:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Email já cadastrado",
  "path": "/api/auth/register"
}
```

---

## ✅ Checklist de Implementação Mobile

### Autenticação
- [ ] Registro de pais
- [ ] Login (pais e crianças)
- [ ] Renovação automática de token
- [ ] Logout
- [ ] Armazenamento seguro de tokens

### Perfis
- [ ] Criar perfil de criança
- [ ] Listar crianças
- [ ] Seletor de criança ativa

### Tarefas
- [ ] Criar tarefa (pais)
- [ ] Listar tarefas
- [ ] Completar tarefa (criança)
- [ ] Aprovar/Rejeitar (pais)
- [ ] Filtros por status

### Carteira
- [ ] Visualizar saldo
- [ ] Histórico de transações
- [ ] Paginação de transações

### Recompensas
- [ ] Criar recompensa (pais)
- [ ] Listar recompensas
- [ ] Solicitar resgate (criança)
- [ ] Aprovar/Rejeitar (pais)

### Poupança
- [ ] Depositar
- [ ] Sacar (com cálculo de bônus)
- [ ] Visualizar rendimentos

### Gamificação
- [ ] Visualizar nível e XP
- [ ] Progress bar para próximo nível
- [ ] Lista de badges
- [ ] Badges desbloqueadas vs bloqueadas
- [ ] Animação ao desbloquear badge
- [ ] Animação ao subir de nível

### Notificações
- [ ] Listar notificações
- [ ] Badge de não lidas
- [ ] Marcar como lida
- [ ] Navegação ao clicar (referenceType/referenceId)
- [ ] Push notifications (futuro)

---

## 📝 Notas Importantes

1. **Mesmo WiFi:** Mobile e PC devem estar na mesma rede
2. **CORS:** Já está habilitado na API
3. **Tokens:** Salvar em SecureStorage/Keychain
4. **Renovação:** Implementar renovação automática antes do token expirar
5. **Offline:** Considerar cache local para melhor UX
6. **Validações:** Sempre validar dados antes de enviar
7. **Loading:** Mostrar indicadores durante requisições
8. **Erros:** Tratar todos códigos de erro adequadamente

---

**Versão da API:** 1.0.0
**Última atualização:** 24/10/2025
