CREATE TABLE IF NOT EXISTS medico (
                                      id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                      nome VARCHAR(120) NOT NULL,
                                      crm VARCHAR(20) UNIQUE NOT NULL,
                                      especialidade VARCHAR(60) NOT NULL,
                                      planos_saude TEXT,
                                      valor_consulta_particular NUMERIC(10,2) NOT NULL,
                                      telefone VARCHAR(20),
                                      email VARCHAR(100),
                                      ativo BOOLEAN DEFAULT true,
                                      data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                      data_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_medico_especialidade ON medico(especialidade);
CREATE INDEX idx_medico_ativo ON medico(ativo);

-- Tabela de Planos de Saúde vinculados a Médicos
CREATE TABLE IF NOT EXISTS medico_planos_saude (
                                                   medico_id UUID NOT NULL REFERENCES medico(id) ON DELETE CASCADE,
                                                   plano_saude VARCHAR(100) NOT NULL,
                                                   PRIMARY KEY (medico_id, plano_saude)
);

-- Tabela de Pacientes
CREATE TABLE IF NOT EXISTS paciente (
                                        id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                        nome VARCHAR(120) NOT NULL,
                                        idade INT NOT NULL,
                                        cpf VARCHAR(11) UNIQUE,
                                        telefone VARCHAR(20),
                                        email VARCHAR(100),
                                        plano_saude_nome VARCHAR(100),
                                        plano_numero_carnetizacao VARCHAR(50),
                                        plano_ativo BOOLEAN DEFAULT false,
                                        ativo BOOLEAN DEFAULT true,
                                        data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                        data_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_paciente_cpf ON paciente(cpf);
CREATE INDEX idx_paciente_ativo ON paciente(ativo);
CREATE INDEX idx_paciente_plano_saude ON paciente(plano_saude_nome);

-- Tabela de Enfermeiros (NOVA)
CREATE TABLE IF NOT EXISTS enfermeiro (
                                          id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                          nome VARCHAR(120) NOT NULL,
                                          coren VARCHAR(30) UNIQUE NOT NULL,
                                          telefone VARCHAR(20),
                                          email VARCHAR(100),
                                          especialidade VARCHAR(100),
                                          turno VARCHAR(20) NOT NULL,
                                          total_triagens_realizadas INT DEFAULT 0,
                                          ultimo_acesso TIMESTAMP,
                                          ativo BOOLEAN DEFAULT true,
                                          data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                          data_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_enfermeiro_coren ON enfermeiro(coren);
CREATE INDEX idx_enfermeiro_turno ON enfermeiro(turno);
CREATE INDEX idx_enfermeiro_especialidade ON enfermeiro(especialidade);
CREATE INDEX idx_enfermeiro_ativo ON enfermeiro(ativo);

-- Tabela de Triagens (NOVA)
CREATE TABLE IF NOT EXISTS triagem (
                                       id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                       paciente_id UUID NOT NULL REFERENCES paciente(id),
                                       enfermeiro_id UUID NOT NULL REFERENCES enfermeiro(id),
                                       prioridade VARCHAR(30) NOT NULL,
                                       pressao_arterial VARCHAR(20),
                                       frequencia_cardiaca INT,
                                       temperatura DOUBLE PRECISION,
                                       frequencia_respiratoria INT,
                                       peso DOUBLE PRECISION,
                                       altura DOUBLE PRECISION,
                                       queixa_principal VARCHAR(500),
                                       observacoes TEXT,
                                       ativo BOOLEAN DEFAULT true,
                                       data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                       data_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_triagem_paciente ON triagem(paciente_id);
CREATE INDEX idx_triagem_enfermeiro ON triagem(enfermeiro_id);
CREATE INDEX idx_triagem_prioridade ON triagem(prioridade);
CREATE INDEX idx_triagem_data ON triagem(data_criacao);

-- Tabela de Consultas
CREATE TABLE IF NOT EXISTS consulta (
                                        id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                        medico_id UUID NOT NULL REFERENCES medico(id),
                                        paciente_id UUID NOT NULL REFERENCES paciente(id),
                                        data_consulta DATE NOT NULL,
                                        horario VARCHAR(10),
                                        diag_sintomas VARCHAR(500),
                                        diag_texto TEXT,
                                        diag_tratamento TEXT,
                                        diag_observacoes TEXT,
                                        receita TEXT,
                                        valor_pago NUMERIC(10,2) NOT NULL DEFAULT 0,
                                        status VARCHAR(20) NOT NULL DEFAULT 'AGENDADA',
                                        ativo BOOLEAN DEFAULT true,
                                        data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                        data_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_consulta_medico ON consulta(medico_id);
CREATE INDEX idx_consulta_paciente ON consulta(paciente_id);
CREATE INDEX idx_consulta_data ON consulta(data_consulta);
CREATE INDEX idx_consulta_status ON consulta(status);

-- Tabela de Exames Solicitados
CREATE TABLE IF NOT EXISTS consulta_exames (
                                               consulta_id UUID NOT NULL REFERENCES consulta(id) ON DELETE CASCADE,
                                               exame VARCHAR(255) NOT NULL
);

-- Tabela de Avaliações
CREATE TABLE IF NOT EXISTS avaliacao (
                                         id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                         consulta_id UUID UNIQUE NOT NULL REFERENCES consulta(id) ON DELETE CASCADE,
                                         texto TEXT,
                                         estrelas INT NOT NULL CHECK (estrelas BETWEEN 1 AND 5),
                                         ativo BOOLEAN DEFAULT true,
                                         data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                         data_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_avaliacao_consulta ON avaliacao(consulta_id);
CREATE INDEX idx_avaliacao_estrelas ON avaliacao(estrelas);

-- Tabela de Contas/Faturas
CREATE TABLE IF NOT EXISTS conta (
                                     id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                     paciente_id UUID NOT NULL REFERENCES paciente(id),
                                     consulta_id UUID REFERENCES consulta(id),
                                     valor NUMERIC(10,2) NOT NULL,
                                     descricao VARCHAR(500),
                                     data_vencimento DATE NOT NULL,
                                     data_pagamento DATE,
                                     situacao VARCHAR(30) NOT NULL DEFAULT 'PENDENTE',
                                     ativo BOOLEAN DEFAULT true,
                                     data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                     data_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_conta_paciente ON conta(paciente_id);
CREATE INDEX idx_conta_situacao ON conta(situacao);
CREATE INDEX idx_conta_vencimento ON conta(data_vencimento);

-- Dados Iniciais (Exemplo)

-- Inserir Médicos Exemplo
INSERT INTO medico (nome, crm, especialidade, valor_consulta_particular, telefone, email)
VALUES
    ('Dr. Carlos Mendes', 'CRM12345SP', 'CARDIOLOGISTA', 150.00, '85987654321', 'carlos@clinica.com'),
    ('Dra. Ana Silva', 'CRM12346SP', 'PEDIATRA', 100.00, '85987654322', 'ana@clinica.com'),
    ('Dr. João Santos', 'CRM12347SP', 'DERMATOLOGISTA', 120.00, '85987654323', 'joao@clinica.com')
ON CONFLICT DO NOTHING;

-- Inserir Enfermeiros Exemplo
INSERT INTO enfermeiro (nome, coren, especialidade, turno, telefone, email)
VALUES
    ('Maria Silva', 'RN1234567', 'Triagem', 'MATUTINO', '85987654300', 'maria@clinica.com'),
    ('João Costa', 'RN1234568', 'Triagem', 'VESPERTINO', '85987654301', 'joao_enf@clinica.com'),
    ('Fernanda Lima', 'RN1234569', 'Urgência', 'NOTURNO', '85987654302', 'fernanda@clinica.com')
ON CONFLICT DO NOTHING;

-- Inserir Pacientes Exemplo
INSERT INTO paciente (nome, idade, cpf, telefone, email, plano_saude_nome, plano_ativo)
VALUES
    ('Pedro Santos', 45, '12345678901', '85988776655', 'pedro@email.com', 'Unimed', true),
    ('Julia Oliveira', 28, '98765432100', '85988776656', 'julia@email.com', 'Bradesco Saúde', true),
    ('Roberto Costa', 60, '11223344556', '85988776657', 'roberto@email.com', 'Não tenho', false)
ON CONFLICT DO NOTHING;

-- Tabelas de Sequências para IDs (se necessário)
-- PostgreSQL usa UUID, então não é necessário

-- Trigger para atualizar data_atualizacao (exemplo)
CREATE OR REPLACE FUNCTION update_data_atualizacao()
    RETURNS TRIGGER AS $$
BEGIN
    NEW.data_atualizacao = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Aplicar trigger a todas as tabelas principais
DO $$
    BEGIN
        DROP TRIGGER IF EXISTS update_medico ON medico;
        CREATE TRIGGER update_medico BEFORE UPDATE ON medico
            FOR EACH ROW EXECUTE FUNCTION update_data_atualizacao();

        DROP TRIGGER IF EXISTS update_paciente ON paciente;
        CREATE TRIGGER update_paciente BEFORE UPDATE ON paciente
            FOR EACH ROW EXECUTE FUNCTION update_data_atualizacao();

        DROP TRIGGER IF EXISTS update_enfermeiro ON enfermeiro;
        CREATE TRIGGER update_enfermeiro BEFORE UPDATE ON enfermeiro
            FOR EACH ROW EXECUTE FUNCTION update_data_atualizacao();

        DROP TRIGGER IF EXISTS update_triagem ON triagem;
        CREATE TRIGGER update_triagem BEFORE UPDATE ON triagem
            FOR EACH ROW EXECUTE FUNCTION update_data_atualizacao();

        DROP TRIGGER IF EXISTS update_consulta ON consulta;
        CREATE TRIGGER update_consulta BEFORE UPDATE ON consulta
            FOR EACH ROW EXECUTE FUNCTION update_data_atualizacao();

        DROP TRIGGER IF EXISTS update_avaliacao ON avaliacao;
        CREATE TRIGGER update_avaliacao BEFORE UPDATE ON avaliacao
            FOR EACH ROW EXECUTE FUNCTION update_data_atualizacao();

        DROP TRIGGER IF EXISTS update_conta ON conta;
        CREATE TRIGGER update_conta BEFORE UPDATE ON conta
            FOR EACH ROW EXECUTE FUNCTION update_data_atualizacao();
    END$$;

-- Views úteis para relatórios
CREATE OR REPLACE VIEW vw_medico_avaliacoes AS
SELECT
    m.id as medico_id,
    m.nome,
    m.especialidade,
    COUNT(a.id) as total_avaliacoes,
    ROUND(AVG(a.estrelas)::numeric, 2) as media_estrelas
FROM medico m
         LEFT JOIN consulta c ON m.id = c.medico_id
         LEFT JOIN avaliacao a ON c.id = a.consulta_id
WHERE m.ativo = true
GROUP BY m.id, m.nome, m.especialidade;

CREATE OR REPLACE VIEW vw_enfermeiro_triagens AS
SELECT
    e.id as enfermeiro_id,
    e.nome,
    e.especialidade,
    e.turno,
    e.total_triagens_realizadas,
    COUNT(t.id) as triagens_hoje
FROM enfermeiro e
         LEFT JOIN triagem t ON e.id = t.enfermeiro_id
    AND CAST(t.data_criacao AS DATE) = CURRENT_DATE
WHERE e.ativo = true
GROUP BY e.id, e.nome, e.especialidade, e.turno, e.total_triagens_realizadas;

CREATE OR REPLACE VIEW vw_contas_pendentes AS
SELECT
    c.id as conta_id,
    p.nome as paciente_nome,
    c.valor,
    c.data_vencimento,
    CURRENT_DATE - c.data_vencimento as dias_atraso
FROM conta c
         JOIN paciente p ON c.paciente_id = p.id
WHERE c.situacao IN ('PENDENTE', 'VENCIDA')
ORDER BY c.data_vencimento ASC;

GRANT SELECT ON ALL TABLES IN SCHEMA public TO clinica_user;
GRANT INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO clinica_user;