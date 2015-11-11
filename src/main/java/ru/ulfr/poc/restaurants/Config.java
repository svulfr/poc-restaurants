package ru.ulfr.poc.restaurants;

/**
 * Site configuration
 * <p>
 * Created by ulfr on 22.10.15.
 */
public class Config {
    public static final String DS_DRIVER = "com.mysql.jdbc.Driver";
    public static final String DS_URL = "jdbc:mysql://127.0.0.1:3306/pocrest?useUnicode=true&characterEncoding=UTF-8";
    public static final String DS_USER = "root";
    public static final String DS_PASS = "root";

    private static Config instance = new Config();

    public static Config getInstance() {
        return instance;
    }

}
