package backend.enums;

public enum TipoPessoa {
    PF("Física"),
    PJ("Jurídica");

    private final String tipoPessoa;

    TipoPessoa(String tipoPessoa) {
        this.tipoPessoa = tipoPessoa;
    }

    public String getTipoPessoa() {
        return tipoPessoa;
    }
}
