/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package settings;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TableView;

/**
 *
 * @author 'Aaron Lomba'
 */
public class Settings implements Observable {

    public static final String LN = System.getProperty("line.separator");
    public static final Map<String, String> SETTINGS = new HashMap<>();
    /**
     * The directory to the Documents/Synesthesia Direct/ folder.  <em> INCLUDES
     * THE
     * TRAILING SLASH </em>
     */
    public static String docDir;
    public static String home;
    /**
     * The directory to the AppData/Local/SynesthesiaDirect/ folder.  <em>
     * INCLUDES
     * THE
     * TRAILING SLASH </em>
     */
    public static String resDir;
    private static boolean wasSetup = false;

    static {
        setup();
    }

    public static boolean getBoolean(String k) throws ValueConversionException {
        if (!wasSetup) {
            throw new DatabaseNotInitializedException("The setting database was never setup!");
        }
        try {
            if (SETTINGS.get(k) == null) {
                throw new ValueConversionException("Could not find value. this should be deprecated");
            }
            return Boolean.valueOf(SETTINGS.get(k));
        } catch (NumberFormatException e) {
            throw new ValueConversionException("Couldn't convert to boolean!");
        }
    }

    public static boolean getBoolean(String k, boolean def) {
        if (!wasSetup) {
            throw new DatabaseNotInitializedException("The setting database was never setup!");
        }

        try {
            if (SETTINGS.get(k) == null) {
                return def;
            }
            return Boolean.valueOf(SETTINGS.get(k));
        } catch (Exception e) {
            return def;
        }
    }

    public static double getDouble(String k) throws ValueConversionException {
        if (!wasSetup) {
            throw new DatabaseNotInitializedException("The setting database was never setup!");
        }
        try {
            return Double.parseDouble(SETTINGS.get(k));
        } catch (NumberFormatException e) {
            throw new ValueConversionException("Couldn't convert to double!");
        }
    }

    public static double getDouble(String k, double def) {
        if (!wasSetup) {
            throw new DatabaseNotInitializedException("The setting database was never setup!");
        }
        try {
            return Double.parseDouble(SETTINGS.get(k));
        } catch (NumberFormatException e) {
            return def;
        }
    }

    public static int getInt(String k) throws ValueConversionException {
        if (!wasSetup) {
            throw new DatabaseNotInitializedException("The setting database was never setup!");
        }
        try {
            return Integer.parseInt(SETTINGS.get(k));
        } catch (NumberFormatException e) {
            throw new ValueConversionException("Couldn't convert to integer!");
        }
    }

    public static int getInt(String k, int def) {
        if (!wasSetup) {
            throw new DatabaseNotInitializedException("The setting database was never setup!");
        }
        try {
            return Integer.parseInt(SETTINGS.get(k));
        } catch (NumberFormatException e) {
            return def;
        }
    }

    public static Long getLong(String k) throws ValueConversionException {
        if (!wasSetup) {
            throw new DatabaseNotInitializedException("The setting database was never setup!");
        }
        try {
            return Long.parseLong(SETTINGS.get(k));
        } catch (NumberFormatException e) {
            throw new ValueConversionException("Couldn't convert to integer!");
        }
    }

    public static int getLong(String k, int def) {
        if (!wasSetup) {
            throw new DatabaseNotInitializedException("The setting database was never setup!");
        }
        try {
            return Integer.parseInt(SETTINGS.get(k));
        } catch (NumberFormatException e) {
            return def;
        }
    }

    public static String getString(String k, String def) {
        if (!wasSetup) {
            throw new DatabaseNotInitializedException("The setting database was never setup!");
        }
        try {
            return SETTINGS.get(k) == null ? def : SETTINGS.get(k);
        } catch (Exception e) {
            return def;
        }
    }

    public static String getString(String k) throws KeyException {
        if (!wasSetup) {
            throw new DatabaseNotInitializedException("The setting database was never setup!");
        }
        try {
            return SETTINGS.get(k);
        } catch (Exception e) {
            throw new KeyException("No such key");
        }
    }

    public static void pushSetting(String key, int value) {
        if (!wasSetup) {
            throw new DatabaseNotInitializedException("The setting database was never setup!");
        }
        pushSetting(key, Integer.toString(value));
    }

    public static void pushSetting(String key, double value) {
        if (!wasSetup) {
            throw new DatabaseNotInitializedException("The setting database was never setup!");
        }
        pushSetting(key, Double.toString(value));
    }

    public static void pushSetting(String key, boolean value) {
        if (!wasSetup) {
            throw new DatabaseNotInitializedException("The setting database was never setup!");
        }
        pushSetting(key, Boolean.toString(value));
    }

    public static void pushSetting(String key, Long value) {
        if (!wasSetup) {
            throw new DatabaseNotInitializedException("The setting database was never setup!");
        }
        pushSetting(key, Long.toString(value));
    }

    public static void pushSetting(String key, String value) {
        if (!wasSetup) {
            throw new DatabaseNotInitializedException("The setting database was never setup!");
        }
        SETTINGS.put(key, value);
    }

    public static void put(String key, String value) {
        SETTINGS.put(key, value);
    }

    public static void readSettings() throws FileNotFoundException {
        //String setPath = "Settings.txt";
        File file = new File(resDir + "Settings.txt");
        if (!file.exists()) {
            try {
                file.createNewFile();
                System.out.println("new file created");
            } catch (IOException ex) {
            }
            return;
        }

        System.out.println(file.toString());

        //System.out.println("here");
        try (Scanner reader = new Scanner(file)) {
            //System.out.println("here");
            String line;
            int c;
            //System.out.println("n " + reader.next());
            while (reader.hasNextLine()) {
                line = reader.nextLine();
                c = line.indexOf("=");
                //System.out.println(line.substring(0, c).trim() + " " + line.substring(c + 1).trim());
                try {
                    SETTINGS.put(line.substring(0, c).trim(), line.substring(c + 1).trim());
                } catch (Exception e) {

                }
            }
        }
        System.out.println("settings read: " + SETTINGS.size());

    }

    public static void setup() {
        String v = System.getProperty("user.home").replace("\\", "/");
        home = v;
        resDir = v + "/AppData/Local/SynesthesiaDirect/";
        docDir = v + "/Documents/Synesthesia Direct/";
        try {
            readSettings();
            wasSetup = true;
        } catch (FileNotFoundException ex) {
            return;
        }
        //SETTINGS.put("test", "apple");

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                writeAll();
            }
        });
    }

    public static void writeAll() {
        if (!wasSetup) {
            throw new DatabaseNotInitializedException("The setting database was never setup!");
        }
        File file = new File(resDir + "Settings.txt");
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException ex) {
                return;
            }
        }

        try (FileWriter writer = new FileWriter(file)) {
            writer.write("");
            SETTINGS.forEach(( String k, String v ) -> {
                try {
                    //System.out.println(k);
                    writer.append(k + "=" + v + LN);
                } catch (IOException ex) {
                }
            });
            writer.close();
        } catch (IOException e) {

        }
    }

    @Override
    public void addListener(InvalidationListener listener) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
