-- Seeds de Badges (8 badges)
-- Inserir apenas se a tabela estiver vazia

INSERT INTO badges (id, name, description, icon_name, criteria_type, criteria_value, xp_bonus, created_at)
SELECT * FROM (
    SELECT
        gen_random_uuid() as id,
        'Primeira Tarefa' as name,
        'Complete sua primeira tarefa' as description,
        'star' as icon_name,
        'TASK_COUNT' as criteria_type,
        1 as criteria_value,
        25 as xp_bonus,
        NOW() as created_at

    UNION ALL

    SELECT
        gen_random_uuid(),
        'Poupador Iniciante',
        'Acumule 100 moedas na carteira',
        'piggy-bank',
        'CURRENT_BALANCE',
        100,
        50,
        NOW()

    UNION ALL

    SELECT
        gen_random_uuid(),
        'Trabalhador Dedicado',
        'Complete 10 tarefas',
        'trophy',
        'TASK_COUNT',
        10,
        75,
        NOW()

    UNION ALL

    SELECT
        gen_random_uuid(),
        'Dia Produtivo',
        'Complete 5 tarefas em um dia',
        'fire',
        'TASKS_IN_ONE_DAY',
        5,
        100,
        NOW()

    UNION ALL

    SELECT
        gen_random_uuid(),
        'Consistente',
        'Complete tarefas por 7 dias seguidos',
        'calendar',
        'STREAK_DAYS',
        7,
        150,
        NOW()

    UNION ALL

    SELECT
        gen_random_uuid(),
        'Planejador',
        'Guarde 200 moedas na poupança',
        'vault',
        'SAVINGS_AMOUNT',
        200,
        100,
        NOW()

    UNION ALL

    SELECT
        gen_random_uuid(),
        'Comprador Consciente',
        'Resgate sua primeira recompensa',
        'gift',
        'REDEMPTION_COUNT',
        1,
        50,
        NOW()

    UNION ALL

    SELECT
        gen_random_uuid(),
        'Milionário',
        'Ganhe 1000 moedas no total',
        'crown',
        'TOTAL_COINS_EARNED',
        1000,
        200,
        NOW()
) AS new_badges
WHERE NOT EXISTS (SELECT 1 FROM badges);
