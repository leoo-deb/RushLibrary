package exceptions;

public class LivroExistenteException extends RuntimeException {
    public LivroExistenteException(String message) {
        super(message);
    }
}
