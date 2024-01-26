package backend.exceptions;

public class CpfCnpjInvalidoException extends RuntimeException {
    public CpfCnpjInvalidoException(String message) {
        super(message);
    }
}