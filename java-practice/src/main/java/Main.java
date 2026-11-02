public class Main {
    public static void main(String[] args) {

        String[] urls = {
            "postgres://localhost:5432/mydb",
            "hello world",
            ""
        };

        for (String url : urls) {
            try {
                Config config = new Config(url, 8080);
                System.out.println("SUCCESS: " + url);
            } catch (IllegalArgumentException e) {
                System.out.println("FAILED: " + e.getMessage());
            }
        }
    }
}