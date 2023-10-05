package com.example.curatorBot;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class configParser {
    public static Properties properties;
    public static String propertiesFilePath = "src/main/resources/config.properties";

    static {
        load();
    }

    public static void load() {
        FileInputStream fis;
        properties = new Properties();

        try {
            fis = new FileInputStream(propertiesFilePath);
            properties.load(fis);
        } catch (FileNotFoundException e) {
            System.err.printf("[ERROR] File %s not exists \n", propertiesFilePath);
        } catch (IOException e) {
            System.err.println("[ERROR] Failed loading properties");
        }
    }
}
