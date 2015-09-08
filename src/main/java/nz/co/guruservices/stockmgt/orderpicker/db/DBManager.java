package nz.co.guruservices.stockmgt.orderpicker.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import nz.co.guruservices.stockmgt.orderpicker.AppProperties;

public class DBManager {

    private final AppProperties appProperties;

    private final String dbUrl;
    private final String dbUser;
    private final String dbPassword;

    public DBManager(final AppProperties appProperties) throws Exception {
        this.appProperties = appProperties;
        Class.forName(appProperties.getDbDriver());
        dbUrl = appProperties.getDbUrl();
        dbUser = appProperties.getDbUser();
        dbPassword = appProperties.getDbPassword();
    }

    public Connection getConnection()
            throws SQLException {
        return DriverManager.getConnection(dbUrl, dbUser, dbPassword);
    }
}
