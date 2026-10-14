import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class ConfigReader {
    



    public Config readConfig(Map<String, String> config)
            throws InvalidConfigValueException, MissingConfigKeyException {
                String url=config.get("database.url");
                String portValue=config.get("server.port");
                if (url == null) {
                    throw new MissingConfigKeyException("database.url");
                }

                if (portValue == null) {
                    throw new MissingConfigKeyException("server.port");
                }
                
                int port;
                try{
                    port=Integer.parseInt(portValue);
                }
                catch(NumberFormatException e)//figured out after clicking on parseInt method where it showrd throws numberformatexception
                {
                    throw new InvalidConfigValueException("server.port", portValue, e);

                }

                URI uri;
                try{
                    uri=URI.create(url);
                    
                }
                catch(IllegalArgumentException e)
                {
                    throw new InvalidConfigValueException("database.url", url,e);
                }
                if(!uri.isAbsolute())   
                    {
                        throw new InvalidConfigValueException("database.url",url);
                    }

                if (port < 1 || port > 65535) {
            throw new InvalidConfigValueException("server.port",portValue);
        }
        return new Config(url, port);
    }


    public static void main(String[] args) {
         ConfigReader reader = new ConfigReader();

        //Test1-valid one
        Map<String, String> valid = Map.of(
            "database.url", "postgres://localhost:5432/mydb",
            "server.port", "8080"
        );

        //missing key should throw MissingConfigKeyException
        Map<String, String> missingKey = Map.of(
    "database.url", "postgres://localhost:5432/mydb");

        //test 3 invalid port
        Map<String, String> invalidPort = Map.of(
            "database.url", "postgres://localhost:5432/mydb",
            "server.port", "abc"
        );

        //test 4: port out of range
        Map<String, String> portOutOfRange = Map.of(
            "database.url", "postgres://localhost:5432/mydb",
            "server.port", "70000"
        );

        List<Map<String, String>> tests = List.of(valid, missingKey, invalidPort, portOutOfRange);
        for(Map<String,String> test:tests)
        {
                try {
            Config result = reader.readConfig(test);
            System.out.println("Success: " + result);
        }
        catch (MissingConfigKeyException e) {
            System.out.println("Error: " + e.getMessage());
            System.out.println("Key: " + e.getKey());
        }

        catch (InvalidConfigValueException e) {
            System.out.println("Error: " + e.getMessage());
            System.out.println("Key: " + e.getKey());
        }
        }
        //prediction: 
        /* test 1 success
        test 2: A MissingConfigKeyException is thrown immediately.
        test 3: throws NumberFormatException, catch wraps it in InvalidConfigValueException and preserves the cause.
        test 4: range check fails and throws InvalidConfigValueException.
         */
        

    }
}