package Backend.exceptions;

public class NomeRazaoSocialInvalidaException extends RuntimeException {
    public NomeRazaoSocialInvalidaException(String message) {
        super(message);
    }
}