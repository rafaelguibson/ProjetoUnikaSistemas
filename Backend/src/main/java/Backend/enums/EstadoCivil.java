package Backend.enums;

public enum  EstadoCivil {
    SOLTEIRO("Solteiro(a)"),
    CASADO("Casado(a)"),
    DIVORCIADO("Divorciado(a)"),
    VIUVO("Viuvo(a)");

    private final String descricao;

    EstadoCivil(String descricao) {
        this.descricao = descricao;
    }
    public String getDescricao() {
        return descricao;
    }
}
