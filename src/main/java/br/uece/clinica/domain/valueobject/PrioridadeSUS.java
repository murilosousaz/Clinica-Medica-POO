package br.uece.clinica.domain.valueobject;

public enum PrioridadeSUS {
    VERMELHO("Emergência", 0, "Imediato", "#E24B4A"),
    LARANJA("Muito urgente", 10, "10 minutos", "#BA7517"),
    AMARELO("Urgente", 30, "30 minutos", "#EF9F27"),
    VERDE("Pouco urgente", 120, "120 minutos", "#3B6D11"),
    AZUL("Não urgente", 240, "Eletivo", "#185FA5");

    private final String descricao;
    private final int tempoMaximoMinutos;
    private final String label;
    private final String corHexadecimal;

    PrioridadeSUS(String descricao, int tempoMaximoMinutos, String label, String corHexadecimal) {
        this.descricao = descricao;
        this.tempoMaximoMinutos = tempoMaximoMinutos;
        this.label = label;
        this.corHexadecimal = corHexadecimal;
    }

    public String getDescricao() {
        return descricao;
    }

    public int getTempoMaximoMinutos() {
        return tempoMaximoMinutos;
    }

    public String getLabel() {
        return label;
    }

    public String getCorHexadecimal() {
        return corHexadecimal;
    }

    public boolean isEmergencia() {
        return this == VERMELHO || this == LARANJA;
    }
}