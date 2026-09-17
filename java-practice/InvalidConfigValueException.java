public class InvalidConfigValueException extends Exception {
    private final String key;
    private final String value;

    public InvalidConfigValueException(String key, String value) {
        this(key, value, null);
    }

    public InvalidConfigValueException(
            String key,
            String value,
            Throwable cause) {
        super("Invalid value for config key '" + key + "': " + value, cause);
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}