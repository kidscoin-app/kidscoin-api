# 📊 PROGRESS - KidsCoins API

## 📌 Contexto do Projeto

Sistema de educação financeira infantil gamificada (TCC - UNIP)

**Stack:** Java 17, Spring Boot 3.2.5, PostgreSQL 15, JWT
**Objetivo:** API REST funcional dividida em 2 partes

---

## ✅ PARTE 1 - COMPLETA (12 commits)

### Funcionalidades Implementadas

#### 1. Estrutura Base
- ✅ Projeto Spring Boot configurado
- ✅ Maven com todas dependências (web, jpa, security, jwt, validation, lombok)
- ✅ application.yml configurado (PostgreSQL, JWT, logging)
- ✅ .gitignore criado

#### 2. Autenticação JWT
- ✅ JwtTokenProvider (geração e validação de tokens)
- ✅ JwtAuthenticationFilter (filtro de autenticação)
- ✅ CustomUserDetailsService
- ✅ SecurityConfig (BCrypt strength 12, CORS habilitado)
- ✅ Access Token (24h) + Refresh Token (7 dias)

#### 3. Entidades Base
- ✅ User (id, email, password, fullName, role, family, pin, avatarUrl)
- ✅ Family (id, name)
- ✅ RefreshToken (id, token, user, expiresAt, revoked)
- ✅ UserRole enum (PARENT, CHILD)

#### 4. Repositories Base
- ✅ UserRepository (findByEmail, existsByEmail, findByFamilyIdAndRole)
- ✅ FamilyRepository
- ✅ RefreshTokenRepository (findByToken, deleteByUserId)

#### 5. Autenticação - Endpoints
- ✅ POST /api/auth/register - Registro de pais
- ✅ POST /api/auth/login - Login
- ✅ POST /api/auth/refresh - Renovação de token

#### 6. Gestão de Usuários - Endpoints
- ✅ GET /api/users/me - Dados do usuário logado
- ✅ POST /api/users/children - Criar perfil de criança
- ✅ GET /api/users/children - Listar crianças da família

#### 7. Exception Handling
- ✅ GlobalExceptionHandler
- ✅ ResourceNotFoundException (404)
- ✅ UnauthorizedException (403)
- ✅ ErrorResponse DTO
- ✅ Validações de DTOs (@Valid)

#### 8. DTOs Parte 1
- ✅ LoginRequest, RegisterRequest, RefreshTokenRequest, CreateChildRequest
- ✅ AuthResponse, UserResponse

#### 9. Documentação
- ✅ README.md completo com instruções
- ✅ PARTE1_COMPLETA.md (resumo)
- ✅ TESTE_RAPIDO.md (guia de testes)
- ✅ endpoints.http (exemplos REST Client)

### Commits Parte 1
```
9d5e0dc docs: adiciona arquivo com exemplos de endpoints HTTP
86e0e9f config: habilita CORS no Spring Security
42fd933 docs: adiciona guia de testes rápidos
b9fa561 docs: adiciona resumo da Parte 1 completa
4bb9f53 docs: adiciona README com instruções
8d7acd2 feat: adiciona exception handling global
1e6f2b0 feat: adiciona gestão de perfis e crianças
dd25643 feat: implementa autenticação completa
fc62ab0 feat: implementa JWT e configuração de segurança
bf5f128 feat: cria repositories base
5c0a7b6 feat: adiciona entidades User, Family e RefreshToken
80e0d6c config: inicializa projeto Spring Boot
```

---

## 🔄 PARTE 2 - EM PROGRESSO (9 commits até agora)

### ✅ Concluído

#### 1. Enums (8 arquivos)
- ✅ TaskStatus (ACTIVE, INACTIVE)
- ✅ TaskCategory (LIMPEZA, ORGANIZACAO, ESTUDOS, CUIDADOS, OUTRAS)
- ✅ AssignmentStatus (PENDING, COMPLETED, APPROVED, REJECTED)
- ✅ TransactionType (CREDIT, DEBIT)
- ✅ ReferenceType (TASK, REWARD, SAVINGS, ADJUSTMENT)
- ✅ RedemptionStatus (PENDING, APPROVED, REJECTED)
- ✅ NotificationType (TASK_COMPLETED, TASK_APPROVED, TASK_REJECTED, LEVEL_UP, BADGE_UNLOCKED, etc)
- ✅ BadgeCriteriaType (TASK_COUNT, TOTAL_COINS_EARNED, CURRENT_BALANCE, STREAK_DAYS, etc)

#### 2. Entidades de Tarefas (2 arquivos)
- ✅ Task (id, family, createdBy, title, description, coinValue, xpValue, category, status)
- ✅ TaskAssignment (id, task, assignedToChild, status, completedAt, approvedAt, approvedBy, rejectionReason)

#### 3. Entidades de Carteira (3 arquivos)
- ✅ Wallet (id, child, balance, totalEarned, totalSpent)
- ✅ Transaction (id, wallet, type, amount, balanceBefore, balanceAfter, description, referenceType, referenceId)
- ✅ Savings (id, child, balance, totalDeposited, totalEarned, lastDepositAt)

#### 4. Entidades de Recompensas (2 arquivos)
- ✅ Reward (id, family, createdBy, name, description, coinCost, category, imageUrl, isActive)
- ✅ Redemption (id, reward, child, status, requestedAt, reviewedAt, reviewedBy, rejectionReason)

#### 5. Entidades de Gamificação (3 arquivos)
- ✅ UserXP (id, user, currentLevel, currentXp, totalXp, lastLevelUpAt)
- ✅ Badge (id, name, description, iconName, criteriaType, criteriaValue, xpBonus)
- ✅ UserBadge (id, user, badge, unlockedAt) - unique constraint (user_id, badge_id)

#### 6. Entidade de Notificação (1 arquivo)
- ✅ Notification (id, user, type, title, message, referenceType, referenceId, isRead, readAt)

#### 7. Repositories (11 arquivos)
- ✅ TaskRepository (findByFamilyId)
- ✅ TaskAssignmentRepository (findByAssignedToChildId, findByStatus, countByAssignedToChildIdAndStatus, countByAssignedToChildIdAndStatusAndApprovedAtBetween, findByAssignedToChildIdAndStatusOrderByApprovedAtDesc)
- ✅ WalletRepository (findByChildId, findByChildIdWithLock com @Lock PESSIMISTIC_WRITE)
- ✅ TransactionRepository (findByWalletIdOrderByCreatedAtDesc com Pageable)
- ✅ SavingsRepository (findByChildId, findAllByBalanceGreaterThan)
- ✅ RewardRepository (findByFamilyId, findByFamilyIdAndIsActive)
- ✅ RedemptionRepository (findByStatus, findByChildId, countByChildIdAndStatus)
- ✅ UserXPRepository (findByUserId)
- ✅ BadgeRepository (padrão JPA)
- ✅ UserBadgeRepository (existsByUserIdAndBadgeId, findByUserId)
- ✅ NotificationRepository (findByUserIdOrderByCreatedAtDesc, countByUserIdAndIsRead)

#### 8. DTOs Parciais (1 arquivo)
- ✅ CreateTaskRequest

### Commits Parte 2
```
c9f6954 docs: adiciona guia de progresso da Parte 2
9eab1de feat: adiciona DTO de criação de tarefa
d02f765 feat: cria repositories para todas entidades
3d9664d feat: adiciona entidades de gamificação
be82561 feat: adiciona entidades de recompensas
d8be818 feat: adiciona entidades de carteira e transações
4c3fd75 feat: adiciona entidades Task e TaskAssignment
be3e1bf feat: adiciona entidade de notificação
2497bb7 feat: adiciona enums para tarefas, transações e gamificação
```

---

## ❌ FALTA IMPLEMENTAR

### 1. DTOs Request (6 arquivos)
- [ ] ApproveTaskRequest (rejectionReason opcional)
- [ ] RejectTaskRequest (rejectionReason obrigatório)
- [ ] CreateRewardRequest (name, description, coinCost, category, imageUrl)
- [ ] CreateRedemptionRequest (rewardId)
- [ ] DepositSavingsRequest (amount)
- [ ] WithdrawSavingsRequest (amount)

### 2. DTOs Response (10 arquivos)
- [ ] TaskResponse (dados da Task)
- [ ] TaskAssignmentResponse (dados do TaskAssignment com task aninhada)
- [ ] WalletResponse (balance, totalEarned, totalSpent)
- [ ] TransactionResponse (dados da Transaction)
- [ ] SavingsResponse (balance, totalDeposited, totalEarned, lastDepositAt)
- [ ] RewardResponse (dados da Reward)
- [ ] RedemptionResponse (dados da Redemption com reward aninhada)
- [ ] GamificationResponse (currentLevel, currentXp, totalXp, xpForNextLevel, badges[])
- [ ] BadgeResponse (dados da Badge + unlocked boolean)
- [ ] NotificationResponse (dados da Notification)

### 3. NotificationService (ALTA PRIORIDADE)
```java
@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public void create(UUID userId, NotificationType type, String title,
                      String message, ReferenceType refType, UUID refId) {
        // Criar notification no banco
        // Futuramente: integrar push notifications
    }

    public List<NotificationResponse> getUserNotifications(UUID userId) {
        // Buscar notificações ordenadas por data
    }

    public void markAsRead(UUID notificationId) {
        // Marcar isRead = true, readAt = now
    }

    public void markAllAsRead(UUID userId) {
        // Marcar todas do usuário como lidas
    }
}
```

### 4. WalletService (CRÍTICO - CORE)
```java
@Service
@Transactional
public class WalletService {
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    public Integer credit(UUID childId, Integer amount, String description,
                         ReferenceType referenceType, UUID referenceId) {
        // 1. Buscar Wallet com lock (findByChildIdWithLock)
        // 2. Calcular balanceBefore = wallet.balance
        // 3. wallet.balance += amount
        // 4. wallet.totalEarned += amount
        // 5. Criar Transaction CREDIT
        // 6. Salvar wallet
        // 7. Retornar novo balance
    }

    public Integer debit(UUID childId, Integer amount, String description,
                        ReferenceType referenceType, UUID referenceId) {
        // 1. Buscar Wallet com lock
        // 2. Validar: if (wallet.balance < amount) throw exception
        // 3. Calcular balanceBefore
        // 4. wallet.balance -= amount
        // 5. wallet.totalSpent += amount
        // 6. Criar Transaction DEBIT
        // 7. Salvar wallet
        // 8. Retornar novo balance
    }

    public WalletResponse getWallet(UUID childId, User requestingUser) {
        // Validar acesso (pai da família ou própria criança)
        // Buscar e retornar wallet
    }

    public List<TransactionResponse> getTransactions(UUID childId, User user,
                                                     Integer limit, Integer offset) {
        // Validar acesso
        // Buscar transações com paginação
    }
}
```

### 5. BadgeService
```java
@Service
public class BadgeService {
    // Repositories: badge, userBadge, taskAssignment, wallet, savings, redemption

    public List<Badge> checkAndUnlock(UUID childId) {
        List<Badge> unlockedBadges = new ArrayList<>();
        List<Badge> allBadges = badgeRepository.findAll();

        for (Badge badge : allBadges) {
            // Se já possui, pular
            if (userBadgeRepository.existsByUserIdAndBadgeId(childId, badge.getId())) {
                continue;
            }

            // Verificar critério baseado no tipo
            boolean criteriamet = false;
            switch (badge.getCriteriaType()) {
                case TASK_COUNT:
                    long taskCount = taskAssignmentRepository
                        .countByAssignedToChildIdAndStatus(childId, APPROVED);
                    criteriaMet = taskCount >= badge.getCriteriaValue();
                    break;

                case CURRENT_BALANCE:
                    Wallet wallet = walletRepository.findByChildId(childId).orElse(null);
                    criteriaMet = wallet != null &&
                                 wallet.getBalance() >= badge.getCriteriaValue();
                    break;

                case TOTAL_COINS_EARNED:
                    Wallet w = walletRepository.findByChildId(childId).orElse(null);
                    criteriaMet = w != null &&
                                 w.getTotalEarned() >= badge.getCriteriaValue();
                    break;

                case REDEMPTION_COUNT:
                    long redemptionCount = redemptionRepository
                        .countByChildIdAndStatus(childId, APPROVED);
                    criteriaMet = redemptionCount >= badge.getCriteriaValue();
                    break;

                case SAVINGS_AMOUNT:
                    Savings savings = savingsRepository.findByChildId(childId).orElse(null);
                    criteriaMet = savings != null &&
                                 savings.getBalance() >= badge.getCriteriaValue();
                    break;

                case TASKS_IN_ONE_DAY:
                    // Buscar tarefas aprovadas por dia e verificar max
                    // Complexo: agrupar por data e contar
                    break;

                case STREAK_DAYS:
                    // Calcular dias consecutivos com tarefas aprovadas
                    // Complexo: analisar sequência de datas
                    break;

                case DAYS_SAVED:
                    Savings s = savingsRepository.findByChildId(childId).orElse(null);
                    if (s != null && s.getLastDepositAt() != null) {
                        long days = ChronoUnit.DAYS.between(
                            s.getLastDepositAt(), LocalDateTime.now());
                        criteriaMet = days >= badge.getCriteriaValue();
                    }
                    break;
            }

            if (criteriaMet) {
                UserBadge userBadge = new UserBadge();
                userBadge.setUser(userRepository.findById(childId).get());
                userBadge.setBadge(badge);
                userBadgeRepository.save(userBadge);
                unlockedBadges.add(badge);
            }
        }

        return unlockedBadges;
    }
}
```

### 6. GamificationService
```java
@Service
@Transactional
public class GamificationService {
    private final UserXPRepository userXPRepository;
    private final BadgeService badgeService;
    private final NotificationService notificationService;

    public GamificationResult addXP(UUID childId, Integer xpAmount, String reason) {
        // 1. Buscar UserXP
        UserXP userXP = userXPRepository.findByUserId(childId)
            .orElseThrow(() -> new ResourceNotFoundException("UserXP não encontrado"));

        // 2. Adicionar XP
        userXP.setTotalXp(userXP.getTotalXp() + xpAmount);
        userXP.setCurrentXp(userXP.getCurrentXp() + xpAmount);

        // 3. Verificar subida de nível
        boolean leveledUp = false;
        int newLevel = userXP.getCurrentLevel();

        while (newLevel < 10) {
            int xpNeeded = calculateXPForLevel(newLevel + 1);
            if (userXP.getTotalXp() >= xpNeeded) {
                newLevel++;
                userXP.setCurrentLevel(newLevel);
                userXP.setLastLevelUpAt(LocalDateTime.now());
                leveledUp = true;
            } else {
                break;
            }
        }

        // Ajustar currentXp (xp no nível atual)
        if (leveledUp) {
            int xpForCurrentLevel = calculateXPForLevel(newLevel);
            userXP.setCurrentXp(userXP.getTotalXp() - xpForCurrentLevel);
        }

        userXPRepository.save(userXP);

        // 4. Verificar badges
        List<Badge> unlockedBadges = badgeService.checkAndUnlock(childId);

        // 5. Se desbloqueou badges, adicionar XP bônus e re-verificar nível
        if (!unlockedBadges.isEmpty()) {
            int bonusXP = unlockedBadges.stream()
                .mapToInt(Badge::getXpBonus)
                .sum();
            if (bonusXP > 0) {
                // Chamada recursiva com XP bônus
                return addXP(childId, bonusXP, "Bônus de badges");
            }
        }

        // 6. Criar notificações
        if (leveledUp) {
            notificationService.create(childId, NotificationType.LEVEL_UP,
                "Subiu de nível!",
                "Parabéns! Você chegou ao nível " + newLevel,
                null, null);
        }

        for (Badge badge : unlockedBadges) {
            notificationService.create(childId, NotificationType.BADGE_UNLOCKED,
                "Nova conquista!",
                "Você desbloqueou: " + badge.getName(),
                null, badge.getId());
        }

        // 7. Retornar resultado
        return new GamificationResult(leveledUp, newLevel, unlockedBadges);
    }

    private int calculateXPForLevel(int level) {
        int totalXP = 0;
        for (int i = 1; i <= level; i++) {
            totalXP += i * 100 + (i - 1) * 50;
        }
        return totalXP;
    }

    public GamificationResponse getGamification(UUID childId) {
        // Buscar UserXP
        // Buscar badges desbloqueadas
        // Calcular XP para próximo nível
        // Retornar GamificationResponse
    }
}
```

### 7. TaskService + TaskController
**Service:**
```java
@Service
@Transactional
public class TaskService {
    // Todos os repositories necessários + WalletService, GamificationService, NotificationService

    public TaskResponse createTask(CreateTaskRequest request, User parent) {
        // 1. Criar Task
        Task task = new Task();
        task.setFamily(parent.getFamily());
        task.setCreatedBy(parent);
        task.setTitle(request.getTitle());
        // ... outros campos
        task = taskRepository.save(task);

        // 2. Para cada childId em request.getChildrenIds():
        for (UUID childId : request.getChildrenIds()) {
            TaskAssignment assignment = new TaskAssignment();
            assignment.setTask(task);
            assignment.setAssignedToChild(userRepository.findById(childId).get());
            taskAssignmentRepository.save(assignment);
        }

        return TaskResponse.fromTask(task);
    }

    public List<TaskAssignmentResponse> getTasks(User user) {
        if (user.getRole() == UserRole.PARENT) {
            // Buscar todas da família
            List<Task> tasks = taskRepository.findByFamilyId(user.getFamily().getId());
            // Buscar assignments dessas tasks
        } else {
            // Buscar apenas assignments da criança
            return taskAssignmentRepository.findByAssignedToChildId(user.getId());
        }
    }

    public TaskAssignmentResponse completeTask(UUID assignmentId, User child) {
        TaskAssignment assignment = taskAssignmentRepository.findById(assignmentId)
            .orElseThrow();

        // Validar que é a criança atribuída
        if (!assignment.getAssignedToChild().getId().equals(child.getId())) {
            throw new UnauthorizedException("Não autorizado");
        }

        // Marcar como completada
        assignment.setStatus(AssignmentStatus.COMPLETED);
        assignment.setCompletedAt(LocalDateTime.now());
        assignment = taskAssignmentRepository.save(assignment);

        // Notificar pais da família
        List<User> parents = userRepository.findByFamilyIdAndRole(
            child.getFamily().getId(), UserRole.PARENT);
        for (User parent : parents) {
            notificationService.create(parent.getId(),
                NotificationType.TASK_COMPLETED,
                "Tarefa completada",
                child.getFullName() + " completou: " + assignment.getTask().getTitle(),
                ReferenceType.TASK, assignmentId);
        }

        return TaskAssignmentResponse.fromAssignment(assignment);
    }

    public TaskAssignmentResponse approveTask(UUID assignmentId, User parent) {
        TaskAssignment assignment = taskAssignmentRepository.findById(assignmentId)
            .orElseThrow();

        // Validar que é pai da família
        // Validar que status = COMPLETED

        // Marcar como aprovada
        assignment.setStatus(AssignmentStatus.APPROVED);
        assignment.setApprovedAt(LocalDateTime.now());
        assignment.setApprovedBy(parent);
        assignment = taskAssignmentRepository.save(assignment);

        UUID childId = assignment.getAssignedToChild().getId();
        Task task = assignment.getTask();

        // **SEQUÊNCIA CRÍTICA:**
        // 1. Creditar moedas
        walletService.credit(childId, task.getCoinValue(),
            "Tarefa aprovada: " + task.getTitle(),
            ReferenceType.TASK, assignmentId);

        // 2. Adicionar XP (que verificará badges e níveis)
        GamificationResult result = gamificationService.addXP(childId,
            task.getXpValue(),
            "Tarefa aprovada: " + task.getTitle());

        // 3. Notificar criança
        notificationService.create(childId, NotificationType.TASK_APPROVED,
            "Tarefa aprovada!",
            "Você ganhou " + task.getCoinValue() + " moedas e " +
            task.getXpValue() + " XP!",
            ReferenceType.TASK, assignmentId);

        return TaskAssignmentResponse.fromAssignment(assignment);
    }

    public TaskAssignmentResponse rejectTask(UUID assignmentId,
                                            String rejectionReason, User parent) {
        // Similar ao approve, mas status = REJECTED
        // Não credita moedas/XP
        // Notificar criança com motivo
    }
}
```

**Controller:**
```java
@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(
            @Valid @RequestBody CreateTaskRequest request) {
        User parent = getCurrentUser(); // helper
        return ResponseEntity.ok(taskService.createTask(request, parent));
    }

    @GetMapping
    public ResponseEntity<List<TaskAssignmentResponse>> getTasks() {
        User user = getCurrentUser();
        return ResponseEntity.ok(taskService.getTasks(user));
    }

    @PostMapping("/{assignmentId}/complete")
    public ResponseEntity<TaskAssignmentResponse> completeTask(
            @PathVariable UUID assignmentId) {
        User child = getCurrentUser();
        return ResponseEntity.ok(taskService.completeTask(assignmentId, child));
    }

    @PostMapping("/{assignmentId}/approve")
    public ResponseEntity<TaskAssignmentResponse> approveTask(
            @PathVariable UUID assignmentId) {
        User parent = getCurrentUser();
        return ResponseEntity.ok(taskService.approveTask(assignmentId, parent));
    }

    @PostMapping("/{assignmentId}/reject")
    public ResponseEntity<TaskAssignmentResponse> rejectTask(
            @PathVariable UUID assignmentId,
            @RequestBody RejectTaskRequest request) {
        User parent = getCurrentUser();
        return ResponseEntity.ok(taskService.rejectTask(
            assignmentId, request.getRejectionReason(), parent));
    }
}
```

### 8. RewardService + RedemptionService + Controllers
Similar à estrutura acima, implementar:
- RewardService: createReward, getRewards
- RedemptionService: requestRedemption (cria PENDING, NÃO debita), approveRedemption (AGORA debita via walletService.debit), rejectRedemption
- RewardController e RedemptionController

### 9. SavingsService + Controller
```java
@Service
@Transactional
public class SavingsService {
    public SavingsResponse deposit(UUID childId, Integer amount, User user) {
        // 1. Validar acesso
        // 2. Debitar de Wallet
        // 3. Creditar em Savings
        // 4. Atualizar lastDepositAt
    }

    public SavingsResponse withdraw(UUID childId, Integer amount, User user) {
        // 1. Validar acesso
        // 2. Calcular bônus por tempo guardado:
        //    - < 7 dias: 0%
        //    - 7-29 dias: +2%
        //    - 30+ dias: +10%
        // 3. Debitar de Savings
        // 4. Creditar em Wallet (valor + bônus)
    }

    @Scheduled(cron = "0 0 0 * * SUN") // Todo domingo meia-noite
    public void applyWeeklyInterest() {
        // Para cada Savings com balance > 0:
        // 1. Calcular 2% de rendimento
        // 2. Adicionar ao balance e totalEarned
        // 3. Criar notificação para a criança
    }
}
```

### 10. Atualizar UserService.createChild()
Adicionar no final do método createChild(), após salvar User:

```java
// Criar Wallet
Wallet wallet = new Wallet();
wallet.setChild(child);
wallet.setBalance(0);
wallet.setTotalEarned(0);
wallet.setTotalSpent(0);
walletRepository.save(wallet);

// Criar UserXP
UserXP userXP = new UserXP();
userXP.setUser(child);
userXP.setCurrentLevel(1);
userXP.setCurrentXp(0);
userXP.setTotalXp(0);
userXPRepository.save(userXP);

// Criar Savings
Savings savings = new Savings();
savings.setChild(child);
savings.setBalance(0);
savings.setTotalDeposited(0);
savings.setTotalEarned(0);
savingsRepository.save(savings);
```

### 11. Seeds de Badges (src/main/resources/data.sql)
```sql
-- Limpar badges existentes (cuidado em produção!)
TRUNCATE TABLE user_badges, badges RESTART IDENTITY CASCADE;

-- Inserir 8 badges
INSERT INTO badges (id, name, description, icon_name, criteria_type, criteria_value, xp_bonus, created_at)
VALUES
  (gen_random_uuid(), 'Primeira Tarefa', 'Complete sua primeira tarefa', 'star', 'TASK_COUNT', 1, 25, NOW()),
  (gen_random_uuid(), 'Poupador Iniciante', 'Acumule 100 moedas na carteira', 'piggy-bank', 'CURRENT_BALANCE', 100, 50, NOW()),
  (gen_random_uuid(), 'Trabalhador Dedicado', 'Complete 10 tarefas', 'trophy', 'TASK_COUNT', 10, 75, NOW()),
  (gen_random_uuid(), 'Dia Produtivo', 'Complete 5 tarefas em um dia', 'fire', 'TASKS_IN_ONE_DAY', 5, 100, NOW()),
  (gen_random_uuid(), 'Consistente', 'Complete tarefas por 7 dias seguidos', 'calendar', 'STREAK_DAYS', 7, 150, NOW()),
  (gen_random_uuid(), 'Planejador', 'Guarde 200 moedas na poupança', 'vault', 'SAVINGS_AMOUNT', 200, 100, NOW()),
  (gen_random_uuid(), 'Comprador Consciente', 'Resgate sua primeira recompensa', 'gift', 'REDEMPTION_COUNT', 1, 50, NOW()),
  (gen_random_uuid(), 'Milionário', 'Ganhe 1000 moedas no total', 'crown', 'TOTAL_COINS_EARNED', 1000, 200, NOW());
```

### 12. Atualizar README.md
Adicionar documentação completa de:
- Todos endpoints da Parte 2
- Exemplos de requests/responses
- Fluxo completo: criar tarefa → aprovar → XP → badge
- Endpoints de carteira, recompensas, poupança, gamificação, notificações

### 13. Criar PARTE2_COMPLETA.md
Resumo similar ao PARTE1_COMPLETA.md

---

## 📈 Estatísticas

### Commits
- **Parte 1:** 12 commits
- **Parte 2:** 9 commits (em progresso)
- **Total:** 21 commits
- **Meta Final:** 30-35 commits

### Arquivos Criados
- **Parte 1:** ~30 arquivos Java
- **Parte 2 (até agora):** ~35 arquivos Java
- **Parte 2 (faltam):** ~40 arquivos (DTOs, Services, Controllers)

### Estrutura Atual
```
src/main/java/com/educacaofinanceira/
├── config/ (2)
├── controller/ (2) - FALTAM 5
├── service/ (3) - FALTAM 7
├── repository/ (14)
├── model/ (14)
│   └── enums/ (9)
├── dto/
│   ├── request/ (5) - FALTAM 6
│   └── response/ (2) - FALTAM 10
├── security/ (3)
└── exception/ (4)
```

---

## 🎯 Próximos Passos (Em Ordem)

1. **DTOs Request/Response** (16 arquivos) - 2 commits
2. **NotificationService** - 1 commit
3. **WalletService** - 1 commit
4. **BadgeService** - 1 commit
5. **GamificationService** - 1 commit
6. **TaskService + TaskController** - 2 commits (service + controller)
7. **RewardService + RedemptionService + Controllers** - 2 commits
8. **SavingsService + Controller** - 1 commit
9. **Atualizar UserService** - 1 commit
10. **Seeds (data.sql)** - 1 commit
11. **README + PARTE2_COMPLETA.md** - 1 commit

**Total:** ~14 commits adicionais → **23 commits Parte 2** → **35 commits total** ✓

---

## 🔑 Pontos Críticos

### 1. Fluxo de Aprovação de Tarefa
```
TaskService.approveTask():
  1. Atualizar status → APPROVED
  2. WalletService.credit() - creditar moedas
  3. GamificationService.addXP() - adicionar XP
     ├─ Verificar subida de nível
     ├─ BadgeService.checkAndUnlock()
     ├─ Se badges: adicionar XP bônus recursivamente
     └─ Criar notificações (level up, badges)
  4. NotificationService.create() - tarefa aprovada
```

### 2. Lock em Wallet
Usar `@Lock(LockModeType.PESSIMISTIC_WRITE)` no WalletRepository para evitar race conditions em créditos/débitos concorrentes.

### 3. Cálculo de XP para Níveis
Fórmula: Para chegar ao nível N, precisa de XP total = soma de (i * 100 + (i-1) * 50) para i de 1 até N.

Exemplo:
- Nível 1: 0 XP
- Nível 2: 100 XP (1*100 + 0*50)
- Nível 3: 250 XP (100 + 150)
- Nível 4: 450 XP (250 + 200)

### 4. Resgate de Recompensas
**IMPORTANTE:** Moedas só são debitadas quando o pai APROVA o resgate, não na solicitação.

### 5. Validações de Acesso
- PARENT só acessa dados da própria família (verificar family_id)
- CHILD só acessa próprios dados (verificar user_id)

---

## 📝 Notas Importantes

- Código SIMPLES e COMPREENSÍVEL (sem over-engineering)
- Commits FREQUENTES com mensagens CLARAS em português
- Padrão de commit: `tipo: descrição` (feat, fix, docs, config, refactor)
- @Transactional em operações que modificam múltiplas entidades
- Validações com @Valid nos DTOs
- Exception handling já configurado na Parte 1
- CORS já habilitado
- JWT já funcionando

---

## 🚀 Como Continuar

1. Ler este arquivo (PROGRESS.md)
2. Verificar último commit: `git log --oneline -1`
3. Seguir seção "Próximos Passos" acima
4. Fazer commits frequentes (a cada funcionalidade implementada)
5. Atualizar este arquivo ao concluir grandes etapas
6. Manter código simples e comentado em português quando necessário

---

## 🔧 SESSÃO DE CORREÇÃO - 24/10/2025

### ✅ PARTE 2 - 100% COMPLETA!

Após análise detalhada do código, descobrimos que **TODA a Parte 2 já foi implementada!**

#### Status de Implementação

**Services (10 arquivos):** ✅ COMPLETO
- AuthService, UserService
- NotificationService
- WalletService
- BadgeService
- GamificationService
- TaskService
- RewardService, RedemptionService
- SavingsService

**Controllers (9 arquivos):** ✅ COMPLETO
- AuthController, UserController
- TaskController
- RewardController, RedemptionController
- SavingsController
- WalletController
- GamificationController
- NotificationController

**DTOs Request (12 arquivos):** ✅ COMPLETO
- RegisterRequest, LoginRequest, RefreshTokenRequest
- CreateChildRequest
- CreateTaskRequest, RejectTaskRequest
- CreateRewardRequest
- CreateRedemptionRequest, ApproveRedemptionRequest, RejectRedemptionRequest
- DepositSavingsRequest, WithdrawSavingsRequest

**DTOs Response (12 arquivos):** ✅ COMPLETO
- UserResponse, AuthResponse
- TaskResponse, TaskAssignmentResponse
- WalletResponse, TransactionResponse
- SavingsResponse
- RewardResponse, RedemptionResponse
- BadgeResponse, GamificationResponse
- NotificationResponse

**Seeds:** ✅ COMPLETO
- data.sql com 8 badges implementados

**Inicialização Automática:** ✅ COMPLETO
- UserService.createChild() cria automaticamente Wallet, UserXP e Savings

### Correções Aplicadas Nesta Sessão

#### 1. Configuração Java 17
- ✅ Verificado que Java 17.0.12 LTS está instalado
- ✅ Configurado JAVA_HOME para C:\Program Files\Java\jdk-17

#### 2. Correção do NotificationType
Adicionado 3 valores faltantes ao enum:
```java
TASK_ASSIGNED,         // Tarefa atribuída à criança
SAVINGS_DEPOSIT,       // Depósito na poupança
SAVINGS_WITHDRAWAL,    // Saque da poupança
```

#### 3. Teste de Compilação
- ✅ Projeto compila com sucesso: `mvn clean compile`
- ✅ 91 arquivos Java compilados sem erros
- ✅ Lombok funcionando perfeitamente

### Estatísticas Finais

**Total de Arquivos Java:** ~110 arquivos
- Entidades: 14 (+ 9 enums)
- Repositories: 14
- Services: 10
- Controllers: 9
- DTOs Request: 12
- DTOs Response: 12
- Security/Config: 5
- Exceptions: 4

**Commits Estimados:** 23 commits (Parte 2)
**Commits Totais:** 35 commits (Parte 1 + Parte 2)

### Próximos Passos

#### 1. Preparar Ambiente de Testes ⏳
```bash
# Iniciar PostgreSQL
# Criar banco de dados
psql -U postgres -c "CREATE DATABASE educacao_financeira;"

# Executar aplicação
mvn spring-boot:run
```

#### 2. Testar Endpoints ⏳
- Testar autenticação (registro, login, refresh token)
- Testar criação de crianças
- Testar fluxo completo de tarefas
- Testar sistema de gamificação
- Testar poupança e recompensas

#### 3. Documentação Final ⏳
- Atualizar README.md com guia completo de endpoints
- Criar PARTE2_COMPLETA.md
- Criar exemplos de requests em endpoints.http

---

## 🔧 SESSÃO DE CORREÇÃO - 25/10/2025

### ✅ Correção do Sistema de Username para Crianças

#### Problema Identificado
Após implementar o sistema de username para crianças (removendo a necessidade de email com sufixo @child.local), a aplicação apresentava erro ao criar criança:

```
ERROR: null value in column 'email' of relation 'users' violates not-null constraint
```

#### Causa Raiz
1. **Código Java**: Já estava correto - `CreateChildRequest` usava `username`, `UserService.createChild()` não setava email
2. **Banco de Dados**: Tabela `users` tinha constraint `NOT NULL` na coluna `email` de quando foi criada inicialmente
3. **Hibernate ddl-auto: update**: Não remove constraints existentes, apenas adiciona novas colunas

#### Solução Aplicada

**1. Atualização da Entidade User** (`src/main/java/com/educacaofinanceira/model/User.java`)
```java
@Column(unique = true, length = 100, nullable = true)
private String email; // Apenas para PARENT (nullable pois CHILD não usa email)

@Column(unique = true, length = 50, nullable = true)
private String username; // Apenas para CHILD (nullable pois PARENT não usa username)
```

**2. Script SQL de Migração** (`src/main/resources/fix_email_nullable.sql`)
```sql
-- Alterar coluna email para aceitar NULL
ALTER TABLE users ALTER COLUMN email DROP NOT NULL;

-- Alterar coluna username para aceitar NULL
ALTER TABLE users ALTER COLUMN username DROP NOT NULL;
```

**3. Execução da Migração**
```bash
psql -U postgres -d educacao_financeira -c "ALTER TABLE users ALTER COLUMN email DROP NOT NULL; ALTER TABLE users ALTER COLUMN username DROP NOT NULL;"
```

#### Resultado
✅ Crianças podem ser criadas com apenas `username` (sem email)
✅ Login de PARENT funciona com `emailOrUsername` = email
✅ Login de CHILD funciona com `emailOrUsername` = username
✅ Sistema totalmente funcional

#### Arquivos Modificados
- `src/main/java/com/educacaofinanceira/model/User.java` - Adicionado `nullable = true` para email e username
- `src/main/resources/fix_email_nullable.sql` - Criado script de migração
- `docs/PROGRESS.md` - Documentação atualizada

#### Exemplo de Uso

**Criar criança:**
```json
POST /api/users/children
{
  "fullName": "João Silva",
  "username": "joaozinho",
  "age": 8,
  "pin": "1234",
  "avatarUrl": "https://example.com/avatar.png"
}
```

**Login criança:**
```json
POST /api/auth/login
{
  "emailOrUsername": "joaozinho",
  "password": "1234"
}
```

**Login pai:**
```json
POST /api/auth/login
{
  "emailOrUsername": "pai@example.com",
  "password": "senha123"
}
```

---

## 🔧 SESSÃO DE CORREÇÃO - 25/10/2025 (Parte 2)

### ✅ Correção de Bugs Críticos no Sistema de Tarefas

Após integração com o frontend, foram identificados e corrigidos 4 bugs críticos que impediam o funcionamento completo do sistema de tarefas.

---

#### Bug #1: Tarefas Não Apareciam na Lista

**Problema Identificado:**
Ao criar uma tarefa no painel do pai, ela não aparecia na lista de tarefas (nem para o pai, nem para a criança).

**Causa Raiz:**
No método `TaskService.getTasks()` (linha 100), o código estava buscando assignments com status null:
```java
List<TaskAssignment> taskAssignments = taskAssignmentRepository.findByStatus(null);
```
Isso retornava lista vazia, pois nenhum assignment tem status null (o padrão é `PENDING`).

**Solução Aplicada:**

1. **Criado novo método no `TaskAssignmentRepository`:**
```java
List<TaskAssignment> findByTaskId(UUID taskId);
```

2. **Corrigido a lógica no `TaskService.getTasks()`:**
```java
for (Task task : familyTasks) {
    List<TaskAssignment> taskAssignments = taskAssignmentRepository.findByTaskId(task.getId());
    assignments.addAll(taskAssignments);
}
```

**Arquivos Modificados:**
- `src/main/java/com/educacaofinanceira/repository/TaskAssignmentRepository.java` - Adicionado método `findByTaskId()`
- `src/main/java/com/educacaofinanceira/service/TaskService.java` - Corrigido loop de busca de assignments

---

#### Bug #2: Erro 500 ao Listar Tarefas (Lazy Loading)

**Problema Identificado:**
Erro HTTP 500 ao carregar tarefas:
```
could not initialize proxy [com.educacaofinanceira.model.Task#...] - no Session
```

**Causa Raiz:**
O método `TaskService.getTasks()` não tinha `@Transactional`, causando erro de lazy loading ao converter entidades para DTOs. As entidades `Task`, `TaskAssignment` e `User` têm relacionamentos LAZY (`Family`, `User`, etc.) que eram acessados fora da transação.

**Relacionamentos Lazy Identificados:**
- `Task.family` (FetchType.LAZY)
- `Task.createdBy` (FetchType.LAZY)
- `TaskAssignment.task` (FetchType.LAZY)
- `TaskAssignment.assignedToChild` (FetchType.LAZY)
- `TaskAssignment.approvedBy` (FetchType.LAZY)

**Solução Aplicada:**

Adicionado `@Transactional(readOnly = true)` no método `TaskService.getTasks()`:
```java
@Transactional(readOnly = true)
public List<TaskAssignmentResponse> getTasks(User user) {
    // ... código de busca e conversão para DTO
}
```

Com a transação ativa, o Hibernate pode carregar relacionamentos lazy durante a conversão para DTO.

**Arquivos Modificados:**
- `src/main/java/com/educacaofinanceira/service/TaskService.java` - Adicionado `@Transactional(readOnly = true)`

---

#### Bug #3: Username Não Aparecia no Frontend

**Problema Identificado:**
Na aba de crianças cadastradas do painel do pai, o username vinha como `undefined`, impossibilitando a exibição abaixo do nome.

**Causa Raiz:**
O DTO `UserResponse` não incluía o campo `username`, enviando apenas `email`, `fullName`, `role`, etc.

**Solução Aplicada:**

1. **Adicionado campo `username` no `UserResponse`:**
```java
private String username;
```

2. **Atualizado método `fromUser()` para incluir username:**
```java
response.setUsername(user.getUsername());
```

**Estrutura do UserResponse Atualizada:**
```json
{
  "id": "uuid",
  "email": "pai@example.com",        // null para CHILD
  "username": "joaozinho",            // null para PARENT
  "fullName": "João Silva",
  "role": "CHILD",
  "familyId": "uuid",
  "avatarUrl": "https://..."
}
```

**Arquivos Modificados:**
- `src/main/java/com/educacaofinanceira/dto/response/UserResponse.java` - Adicionado campo `username` e mapeamento

---

#### Bug #4: Criança Não Conseguia Acessar Suas Tarefas

**Problema Identificado:**
Ao abrir a tela de tarefas no perfil da criança, não aparecia nenhuma tarefa e o console mostrava:
```
ResourceNotFoundException: Usuário não encontrado
```

**Causa Raiz:**
O `SecurityHelper.getAuthenticatedUser()` buscava usuário **APENAS por email**:
```java
String email = SecurityContextHolder.getContext().getAuthentication().getName();
return userRepository.findByEmail(email)
    .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
```

Quando a criança fazia login com username, o JWT continha o username, mas o SecurityHelper tentava buscar por email e falhava.

**Problema Adicional:**
O `User` retornado não estava em uma transação, causando lazy loading error ao acessar `user.getFamily()` no `TaskService.getTasks()`.

**Solução Aplicada:**

1. **Corrigido busca para email OU username:**
```java
String emailOrUsername = SecurityContextHolder.getContext().getAuthentication().getName();

return userRepository.findByEmail(emailOrUsername)
        .orElseGet(() -> userRepository.findByUsername(emailOrUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado")));
```

2. **Adicionado `@Transactional(readOnly = true)`:**
```java
@Transactional(readOnly = true)
public User getAuthenticatedUser() {
    // ... código de busca
}
```

Isso garante que o relacionamento `Family` (lazy) pode ser carregado quando necessário.

**Arquivos Modificados:**
- `src/main/java/com/educacaofinanceira/util/SecurityHelper.java` - Corrigido busca e adicionado transação

---

### 📊 Resumo das Correções

| Bug | Componente | Tipo | Severidade |
|-----|-----------|------|------------|
| #1 | TaskService.getTasks() | Lógica incorreta | 🔴 Crítico |
| #2 | TaskService.getTasks() | Lazy Loading | 🔴 Crítico |
| #3 | UserResponse | DTO incompleto | 🟡 Médio |
| #4 | SecurityHelper | Autenticação | 🔴 Crítico |

### 📁 Arquivos Modificados (4 arquivos)

1. `TaskAssignmentRepository.java` - Adicionado método `findByTaskId(UUID taskId)`
2. `TaskService.java` - Corrigido lógica de busca + adicionado `@Transactional(readOnly = true)`
3. `UserResponse.java` - Adicionado campo `username`
4. `SecurityHelper.java` - Corrigido busca por email/username + adicionado `@Transactional(readOnly = true)`

### ✅ Resultado Final

Após as correções:
- ✅ Tarefas criadas pelo pai aparecem na lista
- ✅ Tarefas aparecem no painel da criança
- ✅ Sem mais erros 500 de lazy loading
- ✅ Username da criança exibido corretamente no frontend
- ✅ Autenticação funcionando para PARENT (email) e CHILD (username)

### 🧪 Fluxo de Teste Completo

**1. Login como PAI:**
```json
POST /api/auth/login
{"emailOrUsername": "pai@example.com", "password": "senha123"}
```

**2. Criar criança:**
```json
POST /api/users/children
{"fullName": "João", "username": "joaozinho", "age": 8, "pin": "1234"}
```

**3. Criar tarefa:**
```json
POST /api/tasks
{"title": "Arrumar quarto", "coinValue": 50, "xpValue": 100, "category": "LIMPEZA", "childrenIds": ["uuid"]}
```

**4. Listar tarefas (como PAI):**
```
GET /api/tasks → Retorna todas assignments da família
```

**5. Login como CRIANÇA:**
```json
POST /api/auth/login
{"emailOrUsername": "joaozinho", "password": "1234"}
```

**6. Listar tarefas (como CRIANÇA):**
```
GET /api/tasks → Retorna apenas assignments da criança
```

**7. Completar tarefa:**
```
POST /api/tasks/{assignmentId}/complete
```

**8. Aprovar tarefa (como PAI):**
```
POST /api/tasks/{assignmentId}/approve
→ Credita moedas, XP, verifica badges, envia notificações
```

---

## 🔧 SESSÃO DE MELHORIAS - 26/10/2025

### ✅ Implementação de Endpoints de Exclusão

Após integração com o mobile, foram implementados 2 novos endpoints de exclusão para melhorar a gestão de dados.

---

#### Feature #1: Exclusão de Tarefa Atribuída

**Problema Identificado:**
O mobile tinha um botão para excluir tarefas, mas ao tentar excluir, recebia erro 404:
```
No static resource api/tasks/{id}
```

**Causa Raiz:**
Não existia endpoint `DELETE` no `TaskController`.

**Solução Implementada:**

1. **Criado método `TaskService.deleteTaskAssignment()`** (linha 244-275)
   ```java
   @Transactional
   public void deleteTaskAssignment(UUID assignmentId, User parent)
   ```

   **Validações:**
   - ✅ Apenas PARENT da mesma família pode excluir
   - ✅ Só permite excluir tarefas com status `PENDING` ou `REJECTED`
   - ❌ Bloqueia exclusão de tarefas `APPROVED` (já creditaram moedas/XP)
   - ❌ Bloqueia exclusão de tarefas `COMPLETED` (aguardando aprovação)
   - 🔔 Notifica criança sobre a remoção da tarefa

2. **Adicionado endpoint no `TaskController`** (linha 85-90)
   ```
   DELETE /api/tasks/{assignmentId}
   ```

   **Respostas:**
   - `204 No Content` - Exclusão bem-sucedida
   - `404 Not Found` - Tarefa não encontrada
   - `403 Forbidden` - Sem permissão
   - `400 Bad Request` - Status não permite exclusão

**Arquivos Modificados:**
- `src/main/java/com/educacaofinanceira/service/TaskService.java` - Adicionado método `deleteTaskAssignment()`
- `src/main/java/com/educacaofinanceira/controller/TaskController.java` - Adicionado endpoint `@DeleteMapping`

---

#### Feature #2: Exclusão de Criança Cadastrada

**Necessidade:**
Permitir que pais removam perfis de crianças cadastradas, incluindo todos os dados relacionados.

**Solução Implementada:**

1. **Criado método `UserService.deleteChild()`** (linha 113-179)
   ```java
   @Transactional
   public void deleteChild(UUID childId)
   ```

   **Exclusão em cascata (ordem reversa de dependências):**
   1. ✅ **RefreshTokens** - Tokens JWT da criança
   2. ✅ **Notifications** - Todas as notificações
   3. ✅ **UserBadges** - Conquistas desbloqueadas
   4. ✅ **Redemptions** - Resgates de recompensas
   5. ✅ **TaskAssignments** - Tarefas atribuídas (todos os status)
   6. ✅ **Transactions** - Histórico financeiro
   7. ✅ **Wallet** - Carteira digital
   8. ✅ **Savings** - Poupança
   9. ✅ **UserXP** - Gamificação (nível e XP)
   10. ✅ **User** - Perfil da criança

   **Validações de segurança:**
   - ❌ Apenas PARENT pode deletar crianças
   - ❌ Só pode deletar crianças da própria família
   - ❌ Não pode deletar usuários do tipo PARENT

2. **Adicionado endpoint no `UserController`** (linha 44-48)
   ```
   DELETE /api/users/children/{childId}
   ```

   **Respostas:**
   - `204 No Content` - Exclusão bem-sucedida
   - `404 Not Found` - Criança não encontrada
   - `403 Forbidden` - Sem permissão
   - `400 Bad Request` - Tentativa de deletar PARENT

**Arquivos Modificados:**
- `src/main/java/com/educacaofinanceira/service/UserService.java` - Adicionado método `deleteChild()` + 6 repositories
- `src/main/java/com/educacaofinanceira/controller/UserController.java` - Adicionado endpoint `@DeleteMapping`

---

### 📊 Resumo das Melhorias

| Feature | Endpoint | Método | Status |
|---------|----------|--------|--------|
| Excluir tarefa atribuída | `/api/tasks/{assignmentId}` | DELETE | ✅ Implementado |
| Excluir criança | `/api/users/children/{childId}` | DELETE | ✅ Implementado |

### 📁 Arquivos Modificados (4 arquivos)

1. `TaskService.java` - Adicionado método `deleteTaskAssignment()`
2. `TaskController.java` - Adicionado endpoint `DELETE /api/tasks/{id}`
3. `UserService.java` - Adicionado método `deleteChild()` + repositories
4. `UserController.java` - Adicionado endpoint `DELETE /api/users/children/{id}`

### ⚠️ Notas Importantes

**Exclusão de Tarefas:**
- Tarefas aprovadas **NÃO** podem ser deletadas (histórico protegido)
- Tarefas em aprovação **NÃO** podem ser deletadas (aguardando revisão do pai)
- Apenas tarefas pendentes ou rejeitadas podem ser removidas

**Exclusão de Crianças:**
- ⚠️ **ATENÇÃO:** Exclusão permanente - TODOS os dados são removidos
- ⚠️ Histórico de tarefas, saldo, conquistas, progresso - TUDO será perdido
- ⚠️ Operação irreversível - não há backup automático

### ✅ Resultado Final

- ✅ Endpoint de exclusão de tarefas funcionando
- ✅ Endpoint de exclusão de crianças funcionando
- ✅ Validações de segurança implementadas
- ✅ Exclusão em cascata de todas dependências
- ✅ Notificações enviadas quando apropriado
- ✅ Compilação sem erros

---

## 🔧 CORREÇÃO DE BUGS - 26/10/2025 (Parte 2)

### ✅ Correção Completa de LazyInitializationException

**Problema Identificado:**
Erro recorrente ao acessar endpoints do mobile (loja de recompensas, carteira):
```
LazyInitializationException: could not initialize proxy [User#...] - no Session
```

**Causa Raiz:**
Múltiplos métodos nos repositories buscavam entidades **sem JOIN FETCH**, deixando relacionamentos como **proxies lazy**. Quando o Jackson tentava serializar para JSON **fora da transação**, ocorria o erro.

**Locais onde ocorria o erro:**
1. `UserService.getCurrentUser()` - usado por `/api/users/me`
2. `AuthService.login()` - usado por `/api/auth/login`
3. `RewardService.getRewards()` - usado por `/api/rewards`
4. `WalletService.getWallet()` - usado por `/api/wallet` ← **Principal culpado**
5. `WalletService.getTransactions()` - usado por `/api/wallet/transactions`

---

### 🔧 Solução Implementada

**Estratégia:** Implementar **JOIN FETCH** em todas as queries que precisam acessar relacionamentos lazy.

#### 1. **UserRepository** - JOIN FETCH do Family
```java
@Query("SELECT u FROM User u JOIN FETCH u.family WHERE u.email = :email")
Optional<User> findByEmailWithFamily(@Param("email") String email);

@Query("SELECT u FROM User u JOIN FETCH u.family WHERE u.username = :username")
Optional<User> findByUsernameWithFamily(@Param("username") String username);
```

#### 2. **RewardRepository** - JOIN FETCH em cascata
```java
@Query("SELECT r FROM Reward r " +
       "JOIN FETCH r.family " +
       "JOIN FETCH r.createdBy cb " +
       "JOIN FETCH cb.family " +  // ← Cascata para carregar family do createdBy
       "WHERE r.family.id = :familyId AND r.isActive = :isActive")
List<Reward> findByFamilyIdAndIsActiveWithRelations(...);
```

#### 3. **WalletRepository** - JOIN FETCH do Child
```java
@Query("SELECT w FROM Wallet w JOIN FETCH w.child WHERE w.child.id = :childId")
Optional<Wallet> findByChildIdWithChild(@Param("childId") UUID childId);
```

#### 4. **JacksonConfig** - Proteção adicional
Configuração global do Jackson para **não** tentar carregar proxies lazy durante serialização:
```java
Hibernate5JakartaModule hibernateModule = new Hibernate5JakartaModule();
hibernateModule.configure(Feature.FORCE_LAZY_LOADING, false);
```

---

### 📁 Arquivos Modificados (10 arquivos)

| Arquivo | Mudança |
|---------|---------|
| `UserRepository.java` | +2 métodos com JOIN FETCH (email/username + family) |
| `RewardRepository.java` | +2 métodos com JOIN FETCH em cascata |
| `WalletRepository.java` | +1 método com JOIN FETCH (child) |
| `SecurityHelper.java` | Usa findBy...WithFamily() |
| `UserService.java` | Usa findBy...WithFamily() em getAuthenticatedUser() |
| `AuthService.java` | Usa findBy...WithFamily() em login() |
| `RewardService.java` | Usa find...WithRelations() |
| `WalletService.java` | Usa findByChildIdWithChild() em 2 métodos |
| `JacksonConfig.java` | **CRIADO** - Configuração Jackson Hibernate |
| `pom.xml` | +dependência jackson-datatype-hibernate5-jakarta |

---

### ✅ Resultado Final

- ✅ `/api/users/me` - Funciona sem erro
- ✅ `/api/auth/login` - Funciona sem erro
- ✅ `/api/rewards` - Lista recompensas sem erro
- ✅ `/api/wallet` - Retorna carteira sem erro ← **Problema principal resolvido**
- ✅ `/api/wallet/transactions` - Lista transações sem erro
- ✅ Loja de recompensas no mobile - **100% funcional**

**Impacto:** Todos os endpoints que retornam DTOs com relacionamentos lazy agora funcionam corretamente.

---

## 🔧 NOVAS FEATURES - 27/10/2025

### ✅ Feature #1: Endpoint de Retry para Tarefas Rejeitadas

**Objetivo:** Permitir que crianças tentem novamente completar tarefas que foram rejeitadas pelo pai.

**Implementação:**

**1. TaskService.retryTask()** (TaskService.java:244-267)
```java
@Transactional
public TaskAssignmentResponse retryTask(UUID assignmentId, User child)
```

**Lógica:**
- ✅ Valida que é a criança dona da tarefa
- ✅ Valida que status atual é `REJECTED`
- ✅ Reseta status para `PENDING`
- ✅ Limpa campos: `completedAt`, `approvedAt`, `approvedBy`, `rejectionReason`

**2. Endpoint criado:**
```
PUT /api/tasks/assignments/{assignmentId}/retry
```

**Arquivos modificados:**
- `TaskService.java` - Método `retryTask()`
- `TaskController.java` - Endpoint `@PutMapping`

---

### ✅ Feature #2: Sistema de Tarefas Recorrentes

**Objetivo:** Pais podem criar tarefas que se repetem automaticamente (diárias ou em dias específicos da semana).

#### Novos Componentes

**1. RecurrenceType.java** - Enum
```java
DAILY,   // Todos os dias
WEEKLY   // Dias específicos (MON, TUE, WED, etc)
```

**2. Task.java** - Novos campos
```java
private Boolean isRecurring;
private RecurrenceType recurrenceType;
private String recurrenceDays;  // "MON,WED,FRI"
private LocalDate recurrenceEndDate;  // Opcional
```

**3. RecurringTaskScheduler.java** - Job agendado
- Roda diariamente à meia-noite (`@Scheduled(cron = "0 0 0 * * *")`)
- Busca tarefas recorrentes ativas
- Verifica se hoje é dia configurado (DAILY sempre sim, WEEKLY verifica dias)
- Para cada criança, cria `TaskAssignment` se não existir PENDING/COMPLETED do dia
- Notifica criança automaticamente

**4. Queries customizadas:**
- `TaskRepository.findActiveRecurringTasks()` - Busca tarefas recorrentes válidas
- `TaskAssignmentRepository.existsActiveAssignmentForTaskAndChildToday()` - Previne duplicatas

#### Exemplo de Uso

**Criar tarefa recorrente (segunda a sexta):**
```json
POST /api/tasks
{
  "title": "Arrumar a cama",
  "coinValue": 10,
  "xpValue": 20,
  "category": "LIMPEZA",
  "childrenIds": ["uuid"],
  "isRecurring": true,
  "recurrenceType": "WEEKLY",
  "recurrenceDays": "MON,TUE,WED,THU,FRI",
  "recurrenceEndDate": null  // Sempre ativa
}
```

**Comportamento:**
- Backend cria tarefa "template"
- Todo dia às 00:00, job verifica e cria novos assignments automaticamente
- Crianças recebem notificação da nova tarefa
- Não cria duplicatas (valida antes de inserir)

#### Arquivos Criados/Modificados

**Novos (4):**
- `RecurrenceType.java` - Enum
- `RecurringTaskScheduler.java` - Job agendado
- `MIGRATE_RECURRING_TASKS.sql` - Script de migração
- `TAREFAS_RECORRENTES.md` - Documentação completa

**Modificados (6):**
- `Task.java` - 4 campos de recorrência
- `CreateTaskRequest.java` - DTOs de entrada
- `TaskResponse.java` - DTOs de saída
- `TaskService.java` - Salva configuração ao criar tarefa
- `TaskRepository.java` - Query `findActiveRecurringTasks()`
- `TaskAssignmentRepository.java` - Query anti-duplicata

---

### ⚠️ Erro Encontrado e Resolvido

**Problema:** Após implementar, API retornava erro 500 ao acessar qualquer endpoint:
```
ERRO: column t1_0.is_recurring does not exist
```

**Causa:** Novas colunas foram adicionadas ao modelo Java, mas não existiam no banco de dados PostgreSQL.

**Solução:**
1. Criado script `MIGRATE_RECURRING_TASKS.sql` com ALTER TABLE
2. Executado manualmente no PostgreSQL via cliente SQL
3. Aplicação reiniciada

**Colunas adicionadas:**
```sql
ALTER TABLE tasks ADD COLUMN is_recurring BOOLEAN NOT NULL DEFAULT false;
ALTER TABLE tasks ADD COLUMN recurrence_type VARCHAR(50);
ALTER TABLE tasks ADD COLUMN recurrence_days VARCHAR(100);
ALTER TABLE tasks ADD COLUMN recurrence_end_date DATE;
```

**Nota:** O Hibernate com `ddl-auto: update` não adiciona colunas automaticamente em tabelas existentes quando o servidor já está rodando. É necessário aplicar migrations manualmente ou reiniciar com banco vazio.

---

### 📊 Estatísticas da Sessão

**Arquivos novos:** 4
**Arquivos modificados:** 8
**Total compilado:** 94 arquivos Java
**Build status:** ✅ SUCCESS
**Features funcionais:** 2 (retry + recorrência)

---

### ✅ Resultado Final

**Endpoint de Retry:**
- ✅ `PUT /api/tasks/assignments/{id}/retry` funcionando
- ✅ Crianças podem tentar novamente tarefas rejeitadas
- ✅ Campos resetados corretamente

**Tarefas Recorrentes:**
- ✅ Job agendado configurado (meia-noite diária)
- ✅ Criação automática de assignments
- ✅ Suporte a DAILY e WEEKLY (dias específicos)
- ✅ Data de término opcional
- ✅ Anti-duplicatas funcionando
- ✅ Notificações automáticas

**Sistema completo:** Mobile pode criar tarefas recorrentes e crianças podem dar retry em tarefas rejeitadas.

---

## 🔧 CORREÇÃO DE BUG - 02/11/2025

### ✅ Correção: LazyInitializationException no SavingsService

**Problema Identificado:**
Ao acessar a tela de poupança no mobile, erro 500:
```
LazyInitializationException: could not initialize proxy [User#...] - no Session
```

**Causa Raiz:**
O método `SavingsService.getSavings()` estava **sem @Transactional**, causando erro ao acessar relacionamentos lazy (como `user.getFamily()`) no método `validateAccess()`.

**Solução Aplicada:**
```java
@Transactional(readOnly = true)  // ← ADICIONADO
public SavingsResponse getSavings(UUID childId, User requestingUser) {
    validateAccess(childId, requestingUser);
    // ...
}
```

**Arquivo modificado:**
- `SavingsService.java:115` - Adicionado `@Transactional(readOnly = true)`

---

### 📚 Sobre LazyInitializationException

**O que é:**
Erro que ocorre quando o Hibernate tenta acessar um relacionamento lazy (`@ManyToOne`, `@OneToOne` com `FetchType.LAZY`) **fora de uma transação ativa**.

**Por que acontece:**
- Relacionamentos lazy são proxies que só carregam dados quando acessados
- Sem transação ativa, não há sessão do Hibernate para buscar os dados
- Erro comum em métodos que retornam DTOs sem `@Transactional`

**Solução:**
Sempre adicionar `@Transactional(readOnly = true)` em métodos de leitura que:
- Retornam DTOs
- Acessam relacionamentos lazy direta ou indiretamente
- São chamados por controllers REST

**Ocorrências anteriores corrigidas:**
1. `TaskService.getTasks()` - Linha 1042 (PROGRESS.md)
2. `SecurityHelper.getAuthenticatedUser()` - Linha 1127
3. `UserRepository`, `RewardRepository`, `WalletRepository` - Adicionado JOIN FETCH (Linha 1373-1422)
4. `SavingsService.getSavings()` - **AGORA** (02/11/2025)

---

## 🔧 NOVA FEATURE - 03/11/2025

### ✅ Feature: Endpoint para Atualizar Avatar do Usuário

**Objetivo:** Permitir que usuários autenticados (PARENT ou CHILD) atualizem sua foto de perfil após o cadastro.

#### Problema Identificado

O sistema permitia definir `avatarUrl` apenas na **criação** do perfil:
- ✅ PARENT podia definir avatar ao criar criança via `CreateChildRequest`
- ❌ Não havia forma de **atualizar** o avatar depois
- ❌ Mobile precisava dessa funcionalidade para edição de perfil

#### Solução Implementada

**1. Criado DTO de Request:**
```java
// UpdateAvatarRequest.java
@Data
public class UpdateAvatarRequest {
    @NotBlank(message = "URL do avatar é obrigatória")
    @Size(max = 255, message = "URL do avatar deve ter no máximo 255 caracteres")
    private String avatarUrl;
}
```

**2. Implementado método no UserService:**
```java
// UserService.java
@Transactional
public UserResponse updateAvatar(String avatarUrl) {
    User user = getAuthenticatedUser();
    user.setAvatarUrl(avatarUrl);
    user = userRepository.save(user);
    return UserResponse.fromUser(user);
}
```

**3. Criado endpoint no UserController:**
```java
// UserController.java
@PatchMapping("/avatar")
public ResponseEntity<UserResponse> updateAvatar(@Valid @RequestBody UpdateAvatarRequest request) {
    UserResponse user = userService.updateAvatar(request.getAvatarUrl());
    return ResponseEntity.ok(user);
}
```

#### Características

**Segurança:**
- ✅ Requer autenticação JWT
- ✅ Usuário atualiza apenas **próprio** avatar
- ✅ Funciona para PARENT e CHILD
- ✅ Validação: URL obrigatória, máximo 255 caracteres

**Endpoint:**
```
PATCH /api/users/avatar
```

**Request Body:**
```json
{
  "avatarUrl": "https://example.com/avatar.png"
}
```

**Response (200 OK):**
```json
{
  "id": "uuid",
  "email": "pai@example.com",
  "username": "joaozinho",
  "fullName": "João Silva",
  "role": "CHILD",
  "familyId": "uuid",
  "avatarUrl": "https://example.com/avatar.png"
}
```

#### Arquivos Criados/Modificados

**Novos (1):**
- `UpdateAvatarRequest.java` - DTO com validação (13 linhas)

**Modificados (2):**
- `UserService.java` - Método `updateAvatar()` (+9 linhas)
- `UserController.java` - Endpoint `@PatchMapping("/avatar")` (+10 linhas)

**Total:** 3 arquivos, 32 linhas adicionadas

#### Resultado

✅ PARENT pode atualizar próprio avatar
✅ CHILD pode atualizar próprio avatar
✅ Validação funcionando corretamente
✅ Compilação sem erros
✅ Pronto para integração com mobile

---

## 🚀 PREPARAÇÃO PARA DEPLOY - 04/11/2025

### ✅ Configuração Completa para Railway

**Objetivo:** Preparar backend para hospedagem no Railway.app com deploy automatizado.

#### Arquivos Criados/Modificados

**1. application.yml - Variáveis de Ambiente**
```yaml
# Agora suporta variáveis de ambiente do Railway
DATABASE_URL=${DATABASE_URL:jdbc:postgresql://localhost:5432/educacao_financeira}
DATABASE_USERNAME=${DATABASE_USERNAME:postgres}
DATABASE_PASSWORD=${DATABASE_PASSWORD:postgres}
JWT_SECRET=${JWT_SECRET:chave-dev}
PORT=${PORT:8080}
LOG_LEVEL=${LOG_LEVEL:DEBUG}
```

**2. application-prod.yml - Profile de Produção**
- Show SQL: desabilitado
- Log Level: INFO/WARN
- Stacktrace: nunca exposto
- Format SQL: desabilitado

**3. railway.json - Configuração Railway**
```json
{
  "build": {
    "buildCommand": "mvn clean package -DskipTests"
  },
  "deploy": {
    "startCommand": "java -Dspring.profiles.active=prod -jar target/kidscoin-api-1.0.0.jar"
  }
}
```

**4. .env.example - Template de Variáveis**
- Exemplos para desenvolvimento local
- Referência para variáveis de produção
- Instruções de como gerar JWT Secret

**5. DEPLOY_RAILWAY.md - Guia Completo (300+ linhas)**
- Passo a passo detalhado
- Configuração de variáveis de ambiente
- Como gerar JWT Secret seguro
- Testes de endpoints
- Troubleshooting completo
- Monitoramento e logs
- Deploy contínuo (CD)
- Rollback de versões
- Custos e planos
- Conexão com mobile
- Checklist de apresentação TCC

**6. RAILWAY_QUICKSTART.md - Guia Rápido (5 minutos)**
- Deploy em 5 passos
- Comandos para gerar JWT Secret
- Teste rápido da API
- Solução de problemas comuns

#### Configurações Implementadas

**Variáveis de Ambiente Railway:**
- ✅ `DATABASE_URL` - URL completa do PostgreSQL (auto-gerada)
- ✅ `DATABASE_USERNAME` - Usuário do banco
- ✅ `DATABASE_PASSWORD` - Senha do banco
- ✅ `JWT_SECRET` - Chave de assinatura JWT (mínimo 256 bits)
- ✅ `SPRING_PROFILES_ACTIVE` - Profile Spring (prod)
- ✅ `PORT` - Porta dinâmica (Railway injeta automaticamente)
- ✅ `LOG_LEVEL` - Nível de log (INFO em prod)
- ✅ `HIBERNATE_DDL_AUTO` - Estratégia de DDL (update)

**Segurança em Produção:**
- ⚠️ JWT Secret precisa ser trocado (64+ caracteres)
- ✅ Logs reduzidos (INFO/WARN apenas)
- ✅ Stacktrace desabilitado em erros
- ✅ SQL queries não aparecem em logs
- ✅ CORS configurado (aceita todas origens para desenvolvimento)

**Deploy Automático:**
1. Push para GitHub (branch master)
2. Railway detecta mudança
3. Executa `mvn clean package -DskipTests`
4. Inicia aplicação com profile prod
5. Conecta ao PostgreSQL automaticamente

#### Checklist de Deploy

- [x] Variáveis de ambiente configuradas
- [x] Profile de produção criado
- [x] railway.json configurado
- [x] Documentação completa
- [x] Guia de troubleshooting
- [ ] **Próximo passo:** Criar projeto no Railway
- [ ] **Próximo passo:** Adicionar PostgreSQL
- [ ] **Próximo passo:** Configurar variáveis
- [ ] **Próximo passo:** Gerar domínio público
- [ ] **Próximo passo:** Testar endpoints

#### Arquivos Criados (6 arquivos)

1. `src/main/resources/application-prod.yml` - Profile de produção
2. `railway.json` - Config Railway
3. `.env.example` - Template de variáveis
4. `docs/DEPLOY_RAILWAY.md` - Guia completo
5. `docs/RAILWAY_QUICKSTART.md` - Quick start
6. `src/main/resources/application.yml` - Atualizado com variáveis

#### Resultado

✅ Backend 100% pronto para deploy no Railway
✅ Configuração profissional com profiles
✅ Documentação completa para a equipe
✅ Suporte a variáveis de ambiente
✅ Deploy automático configurado
✅ Guias passo a passo (completo + rápido)

---

---

## 🔧 FEATURE DE DEBUG - 18/11/2025

### ✅ Endpoint Temporário: Desbloquear Badges Manualmente

**Objetivo:** Facilitar testes e demonstração na banca do TCC, permitindo desbloquear badges rapidamente sem precisar acesso SQL ao Railway.

#### Problema Identificado

- Railway não possui interface de query SQL integrada
- Difícil acessar banco de dados remotamente para testes
- Necessidade de demonstrar sistema de badges funcionando na apresentação

#### Solução Implementada

**1. Endpoint de Debug** (`POST /api/gamification/debug/unlock`)

```java
// GamificationController.java
@PostMapping("/debug/unlock")
public ResponseEntity<String> unlockBadgeDebug(@Valid @RequestBody UnlockBadgeDebugRequest request)
```

**Request Body:**
```json
{
  "username": "joaozinho",
  "badgeName": "Primeira Tarefa"
}
```

**Funcionalidades:**
- ✅ Busca criança pelo username
- ✅ Busca badge pelo nome (case-insensitive)
- ✅ Verifica se já possui a badge (evita duplicatas)
- ✅ Desbloqueia badge manualmente
- ✅ Adiciona XP bônus automaticamente
- ✅ Cria notificação no app
- ✅ Pode causar level up
- ❌ Não requer autenticação JWT (facilita testes)

**Respostas possíveis:**
```
✅ Badge 'Primeira Tarefa' desbloqueada com sucesso para João Silva (+25 XP)!
⚠️ Criança já possui a badge: Primeira Tarefa
❌ Criança não encontrada com username: joaozinho
❌ Badge não encontrada: Primeira. Badges disponíveis: ...
```

**2. Service Layer** (`GamificationService.unlockBadgeForTest()`)

Método transacional que:
1. Busca usuário por username
2. Busca badge por nome
3. Valida se já possui
4. Cria registro `UserBadge`
5. Adiciona XP bônus (chama `addXP()`)
6. Cria notificação
7. Retorna mensagem de sucesso/erro

**3. Scripts Automatizados**

**PowerShell** (`scripts/unlock-all-badges.ps1`):
```powershell
.\unlock-all-badges.ps1 -Username "joaozinho" -ApiUrl "https://sua-api.railway.app"
```

**Bash** (`scripts/unlock-all-badges.sh`):
```bash
./unlock-all-badges.sh joaozinho https://sua-api.railway.app
```

**O que os scripts fazem:**
- Desbloqueiam **TODAS as 8 badges** automaticamente
- Adiciona **+725 XP total** (soma dos bônus)
- Mostra progresso em tempo real
- Exibe resumo ao final (sucessos/falhas/XP ganho)

#### Arquivos Criados (5 arquivos)

1. `UnlockBadgeDebugRequest.java` - DTO de request (18 linhas)
2. `GamificationService.java` - Método `unlockBadgeForTest()` (+53 linhas)
3. `GamificationController.java` - Endpoint `/debug/unlock` (+13 linhas)
4. `scripts/unlock-all-badges.ps1` - Script PowerShell (72 linhas)
5. `scripts/unlock-all-badges.sh` - Script Bash (56 linhas)

#### Documentação

1. `docs/DEBUG_UNLOCK_BADGES.md` - Guia completo de uso (180 linhas)
2. `scripts/README.md` - Instruções dos scripts (120 linhas)

#### Exemplo de Uso

**Desbloquear 1 badge via Postman:**
```
POST https://kidscoin-api.up.railway.app/api/gamification/debug/unlock
Content-Type: application/json

{
  "username": "maria",
  "badgeName": "Milionário"
}
```

**Desbloquear TODAS via script:**
```powershell
cd scripts
.\unlock-all-badges.ps1 -Username "maria" -ApiUrl "https://kidscoin-api.up.railway.app"
```

**Resultado no mobile:**
- 8 badges desbloqueadas ✅
- 8 notificações criadas 🔔
- +725 XP ganho ✨
- Possível level up 📈

#### ⚠️ Lembrete de Produção

**Antes da entrega final do TCC:**

Remover/comentar:
- [ ] `UnlockBadgeDebugRequest.java`
- [ ] Endpoint `@PostMapping("/debug/unlock")`
- [ ] Método `unlockBadgeForTest()`
- [ ] Pasta `scripts/`
- [ ] `docs/DEBUG_UNLOCK_BADGES.md`

Ou simplesmente comentar o endpoint:
```java
// @PostMapping("/debug/unlock")
```

#### Compilação

```
[INFO] Compiling 96 source files
[INFO] BUILD SUCCESS
```

✅ Projeto compila sem erros

---

---

## 🐛 CORREÇÕES E MELHORIAS - 18/11/2025 (Sessão 2)

### ✅ Correção #1: Spring Security Bloqueando Endpoint de Debug

**Problema:** Endpoint `/api/gamification/debug/unlock` retornava **403 Forbidden**.

**Causa:** Spring Security exigia autenticação JWT para todos os endpoints exceto `/api/auth/**`.

**Solução:**
```java
// SecurityConfig.java
.requestMatchers("/api/gamification/debug/**").permitAll()
```

**Commit:** `13a7510 - fix: libera endpoint de debug de badges no Spring Security`

---

### ✅ Correção #2: Seeds de Badges Não Executavam

**Problema:** Railway não tinha badges no banco. Endpoint retornava "Badges disponíveis: " (vazio).

**Causa:** Spring Boot 3+ não executa `data.sql` automaticamente com Hibernate.

**Solução:**
```yaml
# application.yml
spring:
  sql:
    init:
      mode: always # Executa data.sql na inicialização
      continue-on-error: true # Continua se badges já existem
```

**Commit:** `63adf66 - fix: ativa execucao automatica do data.sql para criar badges`

**Resultado:** As 8 badges foram criadas automaticamente no Railway após redeploy.

---

### ✅ Correção #3: Problema de Encoding UTF-8 na Badge "Milionário"

**Problema Identificado:**

1. No Railway, badge aparecia como "Milio??río" (caractere corrompido)
2. No frontend mobile, aparecia "Milio??rio"
3. Script PowerShell falhava ao tentar desbloquear "Milionario" (sem acento)

**Causa Raiz:**

O arquivo `data.sql` tinha encoding incorreto ao salvar o acento "á" em "Milionário". O PostgreSQL do Railway salvou com caracteres corrompidos.

**Solução Implementada:**

**1. Removido acento do `data.sql`:**
```sql
-- Antes:
'Milionário',

-- Depois:
'Milionario',
```

**2. Criado endpoint de correção para banco existente:**
```java
// GamificationController.java
@GetMapping("/debug/fix-milionario")
public ResponseEntity<String> fixMilionarioBadge()

// GamificationService.java
public String fixMilionarioBadge() {
    // Busca badge com nome contendo "Milion"
    // Renomeia para "Milionario" (sem acento)
    // Salva no banco
}
```

**3. Atualizado script PowerShell:**
```powershell
# Antes:
"Milionário"

# Depois:
"Milionario"
```

**Como Usar o Endpoint de Correção:**
```powershell
# Executar UMA VEZ após deploy (corrige registro existente):
Invoke-RestMethod -Uri "https://sua-api.railway.app/api/gamification/debug/fix-milionario"

# Resposta:
✅ Badge corrigida! Nome antigo: 'Milio??rio' → Novo: 'Milionario'
```

**Commit:** `bad158c - fix: corrige encoding UTF-8 da badge Milionario`

**Resultado:**
- ✅ Badge salva corretamente no banco (sem caracteres corrompidos)
- ✅ Frontend exibe "Milionario" sem "??"
- ✅ Script desbloqueia com sucesso

---

### 📊 Resumo da Sessão

**Problemas Resolvidos:**
1. ✅ Endpoint bloqueado por Spring Security (403)
2. ✅ Badges não eram criadas no Railway (data.sql não executava)
3. ✅ Encoding UTF-8 corrompido na badge "Milionário"

**Commits:**
- `004bdad` - feat: adiciona endpoint de debug para desbloquear badges manualmente
- `13a7510` - fix: libera endpoint de debug de badges no Spring Security
- `63adf66` - fix: ativa execucao automatica do data.sql para criar badges
- `bad158c` - fix: corrige encoding UTF-8 da badge Milionario

**Arquivos Modificados:**
- `SecurityConfig.java` - Liberado `/api/gamification/debug/**`
- `application.yml` - Configurado `sql.init.mode: always`
- `data.sql` - Removido acento de "Milionário"
- `unlock-all-badges.ps1` - Atualizado para "Milionario"
- `GamificationController.java` - Adicionado endpoint `/debug/fix-milionario`
- `GamificationService.java` - Adicionado método `fixMilionarioBadge()`

---

### 🎯 Endpoint de Debug em Produção

**Status:** ✅ **FUNCIONANDO** no Railway

**Uso:**

**1. Desbloquear 1 badge:**
```powershell
Invoke-RestMethod `
  -Uri "https://kidscoin-api-production.up.railway.app/api/gamification/debug/unlock" `
  -Method POST `
  -ContentType "application/json" `
  -Body '{"username":"yugi123","badgeName":"Primeira Tarefa"}'
```

**2. Desbloquear TODAS as 8 badges:**
```powershell
cd F:\Dev\kidscoin\kidscoin-api\scripts
.\unlock-all-badges.ps1 `
  -Username "yugi123" `
  -ApiUrl "https://kidscoin-api-production.up.railway.app/api"
```

**3. Corrigir badge com encoding corrompido:**
```powershell
Invoke-RestMethod `
  -Uri "https://kidscoin-api-production.up.railway.app/api/gamification/debug/fix-milionario"
```

**Resultado Esperado:**
```
Desbloqueando: Primeira Tarefa... OK
Desbloqueando: Poupador Iniciante... OK
Desbloqueando: Trabalhador Dedicado... OK
Desbloqueando: Dia Produtivo... OK
Desbloqueando: Consistente... OK
Desbloqueando: Planejador... OK
Desbloqueando: Comprador Consciente... OK
Desbloqueando: Milionario... OK

==================================================
Badges desbloqueadas: 8
XP Total Ganho: +725 XP
==================================================
```

---

### ⚠️ Lembrete: Remover Antes da Entrega Final

Endpoints e arquivos temporários para **REMOVER ou COMENTAR** antes da apresentação do TCC:

**Backend:**
- [ ] `GamificationController.java` - Endpoints `/debug/unlock` e `/debug/fix-milionario`
- [ ] `GamificationService.java` - Métodos `unlockBadgeForTest()` e `fixMilionarioBadge()`
- [ ] `UnlockBadgeDebugRequest.java` - DTO de debug
- [ ] `SecurityConfig.java` - Linha `.requestMatchers("/api/gamification/debug/**").permitAll()`

**Scripts:**
- [ ] `scripts/unlock-all-badges.ps1`
- [ ] `scripts/unlock-all-badges.sh`
- [ ] `scripts/README.md`

**Documentação:**
- [ ] `docs/DEBUG_UNLOCK_BADGES.md`

**Forma rápida:** Apenas comentar os endpoints:
```java
// @PostMapping("/debug/unlock")
// @GetMapping("/debug/fix-milionario")
```

---

**Última atualização:** 18/11/2025 - Correções de encoding e configuração
**Status:** ✅ **Sistema 100% FUNCIONAL EM PRODUÇÃO**
**Compilação:** 96 arquivos | BUILD SUCCESS
**Commits totais:** 42 commits (12 Parte 1 + 30 Parte 2)
**Deploy:** 🚀 **ATIVO no Railway** (commit `bad158c`)
**Badges:** ✅ Todas as 8 funcionando (incluindo correção de encoding)
**GitHub:** ✅ Sincronizado
