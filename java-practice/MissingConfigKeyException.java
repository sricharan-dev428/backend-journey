public class MissingConfigKeyException extends Exception {
    private final String key;

    public MissingConfigKeyException(String key) {
        super("Missing required config key: " + key);
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}