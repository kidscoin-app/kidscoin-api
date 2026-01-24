-- Migration: Adiciona colunas de streak na tabela user_xp
-- Execute este script manualmente se as colunas não forem criadas automaticamente

-- Adicionar coluna current_streak com default 0
ALTER TABLE user_xp ADD COLUMN IF NOT EXISTS current_streak INTEGER NOT NULL DEFAULT 0;

-- Adicionar coluna longest_streak com default 0
ALTER TABLE user_xp ADD COLUMN IF NOT EXISTS longest_streak INTEGER NOT NULL DEFAULT 0;

-- Adicionar coluna last_task_completed_date (nullable)
ALTER TABLE user_xp ADD COLUMN IF NOT EXISTS last_task_completed_date DATE;
