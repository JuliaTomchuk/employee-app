package by.andersen.employee.exception;

public class DataConflictException extends RuntimeException {
    public DataConflictException(ErrorMessage errorMessage) {
        super(errorMessage.name());
    }
}
