-- Migração: Adicionar rastreamento individual de depósitos na poupança
-- Execute este script se o banco de dados já existir

-- Criar tabela savings_deposits
CREATE TABLE IF NOT EXISTS savings_deposits (
    id UUID PRIMARY KEY,
    savings_id UUID NOT NULL REFERENCES savings(id) ON DELETE CASCADE,
    amount INTEGER NOT NULL CHECK (amount > 0),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    deposited_at TIMESTAMP NOT NULL,
    withdrawn_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- Criar índices para melhor performance
CREATE INDEX IF NOT EXISTS idx_savings_deposits_savings_id ON savings_deposits(savings_id);
CREATE INDEX IF NOT EXISTS idx_savings_deposits_status ON savings_deposits(savings_id, status);
CREATE INDEX IF NOT EXISTS idx_savings_deposits_deposited_at ON savings_deposits(deposited_at);

-- Comentários
COMMENT ON TABLE savings_deposits IS 'Rastreia cada depósito individual na poupança para calcular rendimento por tempo guardado';
COMMENT ON COLUMN savings_deposits.amount IS 'Valor depositado (em moedas)';
COMMENT ON COLUMN savings_deposits.status IS 'Status do depósito: ACTIVE ou WITHDRAWN';
COMMENT ON COLUMN savings_deposits.deposited_at IS 'Data e hora do depósito';
COMMENT ON COLUMN savings_deposits.withdrawn_at IS 'Data e hora do saque (se aplicável)';
