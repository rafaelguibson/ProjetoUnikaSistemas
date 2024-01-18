package backend.validators;

public class CpfCnpjInvalidoException extends RuntimeException {
    public CpfCnpjInvalidoException(String message) {
        super(message);
    }
}