-- ========================================
-- MIGRAÇÃO: Adicionar Tarefas Recorrentes
-- ========================================
-- Execute este script no seu cliente PostgreSQL
-- (DBeaver, pgAdmin, psql, etc.)

-- Conectar ao banco: educacao_financeira

-- 1. Adicionar coluna is_recurring (obrigatória, padrão false)
ALTER TABLE tasks ADD COLUMN IF NOT EXISTS is_recurring BOOLEAN NOT NULL DEFAULT false;

-- 2. Adicionar coluna recurrence_type (opcional)
ALTER TABLE tasks ADD COLUMN IF NOT EXISTS recurrence_type VARCHAR(50);

-- 3. Adicionar coluna recurrence_days (opcional)
ALTER TABLE tasks ADD COLUMN IF NOT EXISTS recurrence_days VARCHAR(100);

-- 4. Adicionar coluna recurrence_end_date (opcional)
ALTER TABLE tasks ADD COLUMN IF NOT EXISTS recurrence_end_date DATE;

-- 5. Atualizar tarefas existentes para não serem recorrentes
UPDATE tasks SET is_recurring = false WHERE is_recurring IS NULL;

-- ========================================
-- Verificar resultado
-- ========================================
SELECT column_name, data_type, is_nullable, column_default
FROM information_schema.columns
WHERE table_name = 'tasks'
AND column_name IN ('is_recurring', 'recurrence_type', 'recurrence_days', 'recurrence_end_date')
ORDER BY column_name;

-- Deve retornar 4 linhas:
-- is_recurring       | boolean             | NO  | false
-- recurrence_days    | character varying   | YES | NULL
-- recurrence_end_date| date                | YES | NULL
-- recurrence_type    | character varying   | YES | NULL
