import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class classProperties {
private Properties properties = new Properties();
    public classProperties(){
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("env.properties");

    }
    public String getApiKey(){
        return properties.getProperty("api_key");
    }
}
