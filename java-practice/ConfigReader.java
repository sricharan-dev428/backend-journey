import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class ConfigReader {



    public Config readConfig(Map<String, String> config)
            throws InvalidConfigValueException, MissingConfigKeyException {
        
    }


    public static void main(String[] args) {
        System.out.println(
            readConfig("/The/path does not exist.md")
        );
    }
}