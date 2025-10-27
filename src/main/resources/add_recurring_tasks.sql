-- Migração: Adicionar suporte a tarefas recorrentes
-- Execute este script se o banco de dados já existir

-- Adicionar coluna is_recurring (obrigatória, padrão false)
ALTER TABLE tasks ADD COLUMN IF NOT EXISTS is_recurring BOOLEAN NOT NULL DEFAULT false;

-- Adicionar coluna recurrence_type (opcional)
ALTER TABLE tasks ADD COLUMN IF NOT EXISTS recurrence_type VARCHAR(50);

-- Adicionar coluna recurrence_days (opcional)
ALTER TABLE tasks ADD COLUMN IF NOT EXISTS recurrence_days VARCHAR(100);

-- Adicionar coluna recurrence_end_date (opcional)
ALTER TABLE tasks ADD COLUMN IF NOT EXISTS recurrence_end_date DATE;

-- Atualizar tarefas existentes para não serem recorrentes
UPDATE tasks SET is_recurring = false WHERE is_recurring IS NULL;
