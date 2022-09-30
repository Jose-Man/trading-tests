package name.lattuada.trading.tests.helpers;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class ConfigurationFileReader {
    private static Properties properties;

    public static String getBaseUrl() {
        try(InputStream input = ConfigurationFileReader.class.getClassLoader().getResourceAsStream("configuration.properties")) {
            properties = new Properties();
            properties.load(input);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        return properties.getProperty("baseUrl");
    }

}
