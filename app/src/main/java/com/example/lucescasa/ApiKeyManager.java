package com.example.lucescasa;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ApiKeyManager {
    Properties properties = new Properties();
    public  ApiKeyManager(){
        try{
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("config.properties");
            properties.load(inputStream);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public String getApiKey() {
        return properties.getProperty("api_key");
    }
}
