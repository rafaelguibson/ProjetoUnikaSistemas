package Backend.enums;

public enum TipoPessoa {
    PF("Física"),
    PJ("Jurídica");

    private final String descricao;

    TipoPessoa(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
