# 🧪 Scripts SQL para Testes da Poupança

Este documento contém scripts SQL úteis para testar o sistema de poupança em ambiente de desenvolvimento/staging, sem precisar esperar dias reais.

---

## 📋 Pré-requisitos

Antes de executar os scripts, você precisa do `savings_id` da criança:

```sql
-- Encontrar o savings_id
SELECT
    s.id as savings_id,
    u.id as child_id,
    u.full_name,
    s.balance
FROM savings s
JOIN users u ON s.child_id = u.id
WHERE u.role = 'CHILD';
```

**Substitua `'SEU_SAVINGS_ID'` nos scripts abaixo pelo ID encontrado.**

---

## 🧹 Limpar Dados de Teste

```sql
-- Remover todos os depósitos de teste
DELETE FROM savings_deposits
WHERE savings_id = 'SEU_SAVINGS_ID';

-- Resetar saldo da poupança
UPDATE savings
SET balance = 0,
    total_deposited = 0,
    total_earned = 0
WHERE id = 'SEU_SAVINGS_ID';
```

---

## 💰 Criar Depósitos com Datas Antigas

### Depósito de 90 dias (Diamante - 150%)

```sql
INSERT INTO savings_deposits (id, savings_id, amount, status, deposited_at, created_at, updated_at)
VALUES (
    gen_random_uuid(),
    'SEU_SAVINGS_ID',
    400,
    'ACTIVE',
    NOW() - INTERVAL '90 days',
    NOW() - INTERVAL '90 days',
    NOW()
);

UPDATE savings
SET balance = balance + 400,
    total_deposited = total_deposited + 400
WHERE id = 'SEU_SAVINGS_ID';
```

**Juros esperados:** ~185 moedas (400 × 0,4% × 90 dias × bônus progressivo)

---

### Depósito de 45 dias (Ouro - 130%)

```sql
INSERT INTO savings_deposits (id, savings_id, amount, status, deposited_at, created_at, updated_at)
VALUES (
    gen_random_uuid(),
    'SEU_SAVINGS_ID',
    400,
    'ACTIVE',
    NOW() - INTERVAL '45 days',
    NOW() - INTERVAL '45 days',
    NOW()
);

UPDATE savings
SET balance = balance + 400,
    total_deposited = total_deposited + 400
WHERE id = 'SEU_SAVINGS_ID';
```

**Juros esperados:** ~94 moedas

---

### Depósito de 20 dias (Prata - 120%)

```sql
INSERT INTO savings_deposits (id, savings_id, amount, status, deposited_at, created_at, updated_at)
VALUES (
    gen_random_uuid(),
    'SEU_SAVINGS_ID',
    200,
    'ACTIVE',
    NOW() - INTERVAL '20 days',
    NOW() - INTERVAL '20 days',
    NOW()
);

UPDATE savings
SET balance = balance + 200,
    total_deposited = total_deposited + 200
WHERE id = 'SEU_SAVINGS_ID';
```

**Juros esperados:** ~18 moedas

---

### Depósito de 10 dias (Bronze - 110%)

```sql
INSERT INTO savings_deposits (id, savings_id, amount, status, deposited_at, created_at, updated_at)
VALUES (
    gen_random_uuid(),
    'SEU_SAVINGS_ID',
    100,
    'ACTIVE',
    NOW() - INTERVAL '10 days',
    NOW() - INTERVAL '10 days',
    NOW()
);

UPDATE savings
SET balance = balance + 100,
    total_deposited = total_deposited + 100
WHERE id = 'SEU_SAVINGS_ID';
```

**Juros esperados:** ~4 moedas

---

### Depósito de 5 dias (Base - 100%)

```sql
INSERT INTO savings_deposits (id, savings_id, amount, status, deposited_at, created_at, updated_at)
VALUES (
    gen_random_uuid(),
    'SEU_SAVINGS_ID',
    100,
    'ACTIVE',
    NOW() - INTERVAL '5 days',
    NOW() - INTERVAL '5 days',
    NOW()
);

UPDATE savings
SET balance = balance + 100,
    total_deposited = total_deposited + 100
WHERE id = 'SEU_SAVINGS_ID';
```

**Juros esperados:** ~2 moedas

---

## 🎯 Cenário Completo: Múltiplos Depósitos

Este script cria 3 depósitos em diferentes faixas de tempo:

```sql
-- Limpar tudo primeiro
DELETE FROM savings_deposits WHERE savings_id = 'SEU_SAVINGS_ID';
UPDATE savings SET balance = 0, total_deposited = 0 WHERE id = 'SEU_SAVINGS_ID';

-- Depósito 1: 400 moedas há 90 dias (Diamante)
INSERT INTO savings_deposits (id, savings_id, amount, status, deposited_at, created_at, updated_at)
VALUES (gen_random_uuid(), 'SEU_SAVINGS_ID', 400, 'ACTIVE',
        NOW() - INTERVAL '90 days', NOW() - INTERVAL '90 days', NOW());

-- Depósito 2: 200 moedas há 45 dias (Ouro)
INSERT INTO savings_deposits (id, savings_id, amount, status, deposited_at, created_at, updated_at)
VALUES (gen_random_uuid(), 'SEU_SAVINGS_ID', 200, 'ACTIVE',
        NOW() - INTERVAL '45 days', NOW() - INTERVAL '45 days', NOW());

-- Depósito 3: 100 moedas há 10 dias (Bronze)
INSERT INTO savings_deposits (id, savings_id, amount, status, deposited_at, created_at, updated_at)
VALUES (gen_random_uuid(), 'SEU_SAVINGS_ID', 100, 'ACTIVE',
        NOW() - INTERVAL '10 days', NOW() - INTERVAL '10 days', NOW());

-- Atualizar saldo total
UPDATE savings
SET balance = 700,
    total_deposited = 700
WHERE id = 'SEU_SAVINGS_ID';
```

**Resultado esperado:**
- Principal: 700 moedas
- Juros acumulados: ~233 moedas
- Total disponível: ~933 moedas

---

## 🔍 Queries de Verificação

### Ver todos os depósitos com cálculo de dias e nível

```sql
SELECT
    amount as principal,
    EXTRACT(DAY FROM (NOW() - deposited_at))::INT as dias,
    CASE
        WHEN EXTRACT(DAY FROM (NOW() - deposited_at)) >= 90 THEN 'Diamante (150%)'
        WHEN EXTRACT(DAY FROM (NOW() - deposited_at)) >= 60 THEN 'Platina (140%)'
        WHEN EXTRACT(DAY FROM (NOW() - deposited_at)) >= 30 THEN 'Ouro (130%)'
        WHEN EXTRACT(DAY FROM (NOW() - deposited_at)) >= 15 THEN 'Prata (120%)'
        WHEN EXTRACT(DAY FROM (NOW() - deposited_at)) >= 7 THEN 'Bronze (110%)'
        ELSE 'Base (100%)'
    END as nivel,
    status,
    deposited_at
FROM savings_deposits
WHERE savings_id = 'SEU_SAVINGS_ID'
  AND status = 'ACTIVE'
ORDER BY deposited_at;
```

### Ver saldo da poupança

```sql
SELECT
    balance as principal,
    total_deposited,
    total_earned as juros_realizados
FROM savings
WHERE id = 'SEU_SAVINGS_ID';
```

### Contar depósitos ativos

```sql
SELECT
    COUNT(*) as num_depositos,
    SUM(amount) as total_principal
FROM savings_deposits
WHERE savings_id = 'SEU_SAVINGS_ID'
  AND status = 'ACTIVE';
```

---

## 📊 Tabela de Referência: Faixas de Bônus

| Dias Guardado | Multiplicador | Bônus | Taxa Efetiva | Nível     |
|---------------|---------------|-------|--------------|-----------|
| 1-6 dias      | 1.0x          | 0%    | 0,4%/dia     | Base      |
| 7-14 dias     | 1.1x          | +10%  | 0,44%/dia    | Bronze 🥉 |
| 15-29 dias    | 1.2x          | +20%  | 0,48%/dia    | Prata 🥈  |
| 30-59 dias    | 1.3x          | +30%  | 0,52%/dia    | Ouro 🥇   |
| 60-89 dias    | 1.4x          | +40%  | 0,56%/dia    | Platina 💎|
| 90+ dias      | 1.5x          | +50%  | 0,6%/dia     | Diamante ✨|

---

## ⚠️ Notas Importantes

1. **Ambiente:** Use estes scripts apenas em **desenvolvimento** ou **staging**
2. **Backup:** Sempre faça backup antes de manipular dados
3. **FIFO:** O sistema usa FIFO (First In, First Out) nos saques
4. **Data Original:** Saques parciais mantêm a data original do depósito restante
5. **Arredondamento:** Juros são arredondados para cima (beneficia a criança)

---

## 🎯 Casos de Teste Recomendados

### 1. Testar FIFO
```sql
-- Criar 2 depósitos em datas diferentes
-- Sacar parcialmente
-- Verificar que o mais antigo foi sacado primeiro
```

### 2. Testar Manutenção de Data
```sql
-- Criar depósito de 90 dias
-- Sacar metade
-- Verificar que o restante ainda tem 90 dias
```

### 3. Testar Múltiplos Depósitos
```sql
-- Criar 3+ depósitos com datas diferentes
-- Sacar valor que atravessa múltiplos depósitos
-- Verificar cálculo correto de juros de cada um
```

---

**Última atualização:** Janeiro 2026
