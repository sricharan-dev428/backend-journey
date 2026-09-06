import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigReader {

    static String readConfig(String pathString) {
        Path path = Path.of(pathString);

        try {
            var content = Files.readAllLines(path);
            if (content.isEmpty()) {
            return "default";
        }

        return content.get(0);
        } catch (IOException e) {
            System.err.println("Config not found, using default");
            return "default";
        }
    }

    public static void main(String[] args) {
        System.out.println(
            readConfig("/The/path does not exist.md")
        );
    }
}