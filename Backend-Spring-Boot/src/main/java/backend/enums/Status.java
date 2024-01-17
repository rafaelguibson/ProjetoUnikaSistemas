package backend.enums;

public enum Status {
    ATIVO("Ativo"),
    INATIVO("Inativo");

    private final String status;

    Status(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}

