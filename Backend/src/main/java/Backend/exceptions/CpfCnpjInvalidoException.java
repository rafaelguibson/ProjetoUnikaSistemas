package Backend.exceptions;

public class CpfCnpjInvalidoException extends RuntimeException {
    public CpfCnpjInvalidoException(String message) {
        super(message);
    }
}