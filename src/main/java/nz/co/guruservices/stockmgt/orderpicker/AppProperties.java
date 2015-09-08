package nz.co.guruservices.stockmgt.orderpicker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;

public class AppProperties {

    private Properties properties;

    public void loaddProperties()
            throws Exception {
        properties = new Properties();

        final File file = new File("conf/app.properties");

        if (!file.canRead()) {
            throw new FileNotFoundException("Property file conf/app.properties not found");
        }

        try (InputStream inputStream = new FileInputStream(file);) {
            properties.load(inputStream);
        }
    }

    public String getDbDriver() {
        return properties.getProperty("db.driver");
    }

    public String getDbUrl() {
        return properties.getProperty("db.url");
    }

    public String getDbUser() {
        return properties.getProperty("db.user");
    }

    public String getDbPassword() {
        return properties.getProperty("db.password");
    }
}
