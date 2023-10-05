package com.example.curatorBot;

import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class configParser {
    public static Properties properties;
    public static String propertiesFilePath = "src/main/resources/config.properties";

    static {
        properties = new Properties();
        load();
    }

    public static void load() {
        FileInputStream fis;
        try {
            fis = new FileInputStream(propertiesFilePath);
            properties.load(fis);
        } catch (FileNotFoundException e) {
            System.err.printf("[ERROR] File %s not exists \n", propertiesFilePath);
            System.err.println("[INFO] Creating BUDDY properties");
            createBuddyProperties();
        } catch (IOException e) {
            System.err.println("[ERROR] Failed loading properties");
        }
    }

    private static void createBuddyProperties() {
        FileOutputStream fileOutputStream;
        try {
            fileOutputStream = new FileOutputStream(propertiesFilePath);
            // add properties here
            properties.store(fileOutputStream, null);
        } catch (SecurityException | IOException exception) {
            System.err.println(exception.getMessage());
        }
    }
}
