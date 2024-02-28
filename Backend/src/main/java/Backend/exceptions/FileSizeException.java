package Backend.exceptions;

public class FileSizeException extends RuntimeException {
    public FileSizeException(String arquivo) {
        super("O arquivo " + arquivo + " é maior que 20MB.");
    }
}