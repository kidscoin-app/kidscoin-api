# PARTE 2 - Progresso Atual

## ✅ CONCLUÍDO (8 commits)

1. ✅ Enums (8 arquivos)
2. ✅ Entidades Task e TaskAssignment
3. ✅ Entidades Wallet, Transaction, Savings
4. ✅ Entidades Reward, Redemption
5. ✅ Entidades UserXP, Badge, UserBadge
6. ✅ Entidade Notification
7. ✅ Repositories (11 arquivos)
8. ✅ DTO CreateTaskRequest

## 🔄 EM ANDAMENTO

### Próximos Passos (em ordem de prioridade):

#### 1. DTOs Restantes (Request)
Criar arquivos em `dto/request/`:
- ApproveTaskRequest.java
- RejectTaskRequest.java
- CreateRewardRequest.java
- CreateRedemptionRequest.java
- DepositSavingsRequest.java
- WithdrawSavingsRequest.java

#### 2. DTOs (Response)
Criar arquivos em `dto/response/`:
- TaskResponse.java
- TaskAssignmentResponse.java
- WalletResponse.java
- TransactionResponse.java
- SavingsResponse.java
- RewardResponse.java
- RedemptionResponse.java
- GamificationResponse.java
- BadgeResponse.java
- NotificationResponse.java

#### 3. NotificationService (PRIORIDADE ALTA)
```java
@Service
public class NotificationService {
    public void create(UUID userId, NotificationType type, String title,
                      String message, ReferenceType refType, UUID refId) {
        // Criar notification no banco
    }
}
```

#### 4. WalletService (CRÍTICO)
```java
@Service
@Transactional
public class WalletService {
    public Integer credit(UUID childId, Integer amount, String desc,
                         ReferenceType refType, UUID refId) {
        // Lock wallet
        // balance += amount
        // totalEarned += amount
        // Criar transaction CREDIT
    }

    public Integer debit(UUID childId, Integer amount, String desc,
                        ReferenceType refType, UUID refId) {
        // Lock wallet
        // Validar saldo
        // balance -= amount
        // totalSpent += amount
        // Criar transaction DEBIT
    }
}
```

#### 5. BadgeService
```java
@Service
public class BadgeService {
    public List<Badge> checkAndUnlock(UUID childId) {
        // Para cada badge não conquistada:
        //   - Verificar critério
        //   - Se satisfeito: criar UserBadge
        // Retornar badges desbloqueadas
    }
}
```

#### 6. GamificationService
```java
@Service
@Transactional
public class GamificationService {
    public GamificationResult addXP(UUID childId, Integer xp, String reason) {
        // 1. Buscar UserXP
        // 2. totalXp += xp, currentXp += xp
        // 3. Verificar subida de nível (loop calculando thresholds)
        // 4. Verificar badges
        // 5. Se badges: adicionar XP bônus e re-verificar nível
        // 6. Criar notifications
        // 7. Retornar resultado
    }

    private int calculateXPForLevel(int level) {
        return level * 100 + (level - 1) * 50;
    }
}
```

#### 7. TaskService + TaskController
**Service:**
```java
@Service
@Transactional
public class TaskService {
    public TaskResponse createTask(CreateTaskRequest request, User parent) {
        // Criar Task
        // Para cada childId: criar TaskAssignment
    }

    public TaskAssignmentResponse completeTask(UUID assignmentId, User child) {
        // Validar que é a criança atribuída
        // Status → COMPLETED
        // completedAt = now
        // Notificar pais
    }

    public TaskAssignmentResponse approveTask(UUID assignmentId, User parent) {
        // Validar que é pai da família
        // Status → APPROVED
        // approvedAt = now, approvedBy = parent
        // **SEQUÊNCIA CRÍTICA:**
        // 1. WalletService.credit()
        // 2. GamificationService.addXP()
        // 3. NotificationService.create()
    }

    public TaskAssignmentResponse rejectTask(UUID assignmentId,
                                             String reason, User parent) {
        // Status → REJECTED
        // rejectionReason = reason
        // Notificar criança
    }
}
```

**Controller:**
```java
@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    POST /api/tasks - createTask (PARENT)
    GET /api/tasks - getTasks (PARENT: todas, CHILD: suas)
    POST /api/tasks/{assignmentId}/complete - completeTask (CHILD)
    POST /api/tasks/{assignmentId}/approve - approveTask (PARENT)
    POST /api/tasks/{assignmentId}/reject - rejectTask (PARENT)
}
```

#### 8. RewardService + RedemptionService
**RewardService:**
- createReward (PARENT)
- getRewards (PARENT: todas, CHILD: ativas)

**RedemptionService:**
- requestRedemption (CHILD) - cria PENDING, NÃO debita moedas
- approveRedemption (PARENT) - AGORA debita moedas
- rejectRedemption (PARENT)

#### 9. SavingsService
- deposit() - debitar wallet, creditar savings
- withdraw() - calcular bônus por tempo, debitar savings, creditar wallet
- @Scheduled - job semanal de rendimento 2%

#### 10. Atualizar UserService.createChild()
```java
// Após criar User child:
Wallet wallet = new Wallet();
wallet.setChild(child);
wallet.setBalance(0);
walletRepository.save(wallet);

UserXP userXP = new UserXP();
userXP.setUser(child);
userXP.setCurrentLevel(1);
userXP.setCurrentXp(0);
userXPRepository.save(userXP);

Savings savings = new Savings();
savings.setChild(child);
savings.setBalance(0);
savingsRepository.save(savings);
```

#### 11. Seeds de Badges (data.sql)
```sql
INSERT INTO badges (id, name, description, icon_name, criteria_type, criteria_value, xp_bonus, created_at)
VALUES
  (gen_random_uuid(), 'Primeira Tarefa', 'Complete sua primeira tarefa', 'star', 'TASK_COUNT', 1, 25, NOW()),
  (gen_random_uuid(), 'Poupador Iniciante', 'Acumule 100 moedas', 'piggy-bank', 'CURRENT_BALANCE', 100, 50, NOW()),
  ...
```

#### 12. README Atualizado
- Adicionar documentação de todos novos endpoints
- Fluxo completo de uso
- Exemplos de requests

## 📊 Estatísticas

- **Commits Parte 2:** 8
- **Commits Total:** 19
- **Arquivos criados Parte 2:** ~30
- **Faltam:** ~40 arquivos (DTOs, Services, Controllers)

## 🎯 Meta Final

- **15-20 commits na Parte 2**
- Sistema completo e funcional
- Todos endpoints testáveis
- README atualizado

## 💡 Ordem Recomendada de Implementação

1. ✅ DTOs Request/Response restantes (2 commits)
2. ✅ NotificationService (1 commit)
3. ✅ WalletService (1 commit)
4. ✅ GamificationService + BadgeService (2 commits)
5. ✅ TaskService + TaskController (2 commits)
6. ✅ RewardService + RedemptionService (2 commits)
7. ✅ SavingsService (1 commit)
8. ✅ Atualizar UserService (1 commit)
9. ✅ Seeds de badges (1 commit)
10. ✅ README e documentação final (1 commit)

**Total estimado:** 14 commits adicionais = **22 commits Parte 2** ✓
