import java.net.URI;

public class Config {
    private final String url;
    private final int port;

    public Config(String url, int port) {
            if (url == null || url.isBlank()) {
            throw new IllegalArgumentException(
                "URL cannot be null or blank: " + url
            );
        }

        try {
            URI.create(url);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                "Invalid URL: " + url, e
            );
        }


        if (port < 1 || port > 65535) {
            throw new IllegalArgumentException("Port out of range: " + port);
        }

        this.url = url;
        this.port = port;
    }
     @Override
    public String toString() {
        return "Config{url='" + url + "', port=" + port + "}";
    }

    public String getUrl()
    {
        return url;
    }

    public int getPort()
    {
        return port;
    }
}