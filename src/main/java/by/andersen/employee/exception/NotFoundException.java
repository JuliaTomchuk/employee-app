package by.andersen.employee.exception;


public class NotFoundException extends RuntimeException {
    public NotFoundException(ErrorMessage errorMessage) {
        super(errorMessage.name());
    }
}
