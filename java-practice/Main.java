public class Main {
    public static void main(String[] args)
            throws InvalidConfigValueException {

        throw new InvalidConfigValueException(
            "database.url",
            "not-a-real-url",
            new IllegalArgumentException(
                "Illegal character in scheme name at index 0"
            )
        );
    }
}