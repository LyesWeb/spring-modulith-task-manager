package ma.lyes.task.common.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }

    public static ResourceNotFoundException of(String resourceName, String fieldName, Object fieldValue) {
        return new ResourceNotFoundException(String.format("%s not found with %s: '%s'", resourceName, fieldName, fieldValue));
    }
}
