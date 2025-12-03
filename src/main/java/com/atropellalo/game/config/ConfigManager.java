package com.atropellalo.game.config;

import java.io.*;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Gestor de configuración persistente del juego.
 * Guarda y carga la configuración del usuario (volúmenes, etc.).
 */
public class ConfigManager {
    
    private static final Logger LOGGER = Logger.getLogger(ConfigManager.class.getName());
    private static final String CONFIG_FILE = "atropellalo.config";
    
    private static ConfigManager instance;
    private Properties properties;
    private File configFile;
    
    // Claves de configuración
    private static final String KEY_MASTER_VOLUME = "masterVolume";
    private static final String KEY_WEAPON_VOLUME = "weaponVolume";
    private static final String KEY_MUSIC_VOLUME = "musicVolume";
    private static final String KEY_SOUND_ENABLED = "soundEnabled";
    
    // Valores por defecto
    private static final float DEFAULT_MASTER_VOLUME = 1.0f;
    private static final float DEFAULT_WEAPON_VOLUME = 0.7f;
    private static final float DEFAULT_MUSIC_VOLUME = 0.5f;
    private static final boolean DEFAULT_SOUND_ENABLED = true;
    
    private ConfigManager() {
        properties = new Properties();
        
        // Buscar archivo de configuración en el directorio del usuario
        String userHome = System.getProperty("user.home");
        File configDir = new File(userHome, ".atropellalo");
        
        // Crear directorio si no existe
        if (!configDir.exists()) {
            configDir.mkdirs();
        }
        
        configFile = new File(configDir, CONFIG_FILE);
        loadConfig();
    }
    
    public static ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }
    
    /**
     * Carga la configuración desde el archivo.
     */
    private void loadConfig() {
        if (!configFile.exists()) {
            LOGGER.info("Archivo de configuración no encontrado. Usando valores por defecto.");
            setDefaults();
            saveConfig();
            return;
        }
        
        try (FileInputStream fis = new FileInputStream(configFile)) {
            properties.load(fis);
            LOGGER.info("Configuración cargada desde: " + configFile.getAbsolutePath());
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Error al cargar configuración. Usando valores por defecto.", e);
            setDefaults();
        }
    }
    
    /**
     * Guarda la configuración en el archivo.
     */
    public void saveConfig() {
        try (FileOutputStream fos = new FileOutputStream(configFile)) {
            properties.store(fos, "Atropellalo Game Configuration");
            LOGGER.info("Configuración guardada en: " + configFile.getAbsolutePath());
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error al guardar configuración", e);
        }
    }
    
    /**
     * Establece los valores por defecto.
     */
    private void setDefaults() {
        properties.setProperty(KEY_MASTER_VOLUME, String.valueOf(DEFAULT_MASTER_VOLUME));
        properties.setProperty(KEY_WEAPON_VOLUME, String.valueOf(DEFAULT_WEAPON_VOLUME));
        properties.setProperty(KEY_MUSIC_VOLUME, String.valueOf(DEFAULT_MUSIC_VOLUME));
        properties.setProperty(KEY_SOUND_ENABLED, String.valueOf(DEFAULT_SOUND_ENABLED));
    }
    
    // Getters
    
    public float getMasterVolume() {
        return Float.parseFloat(properties.getProperty(KEY_MASTER_VOLUME, String.valueOf(DEFAULT_MASTER_VOLUME)));
    }
    
    public float getWeaponVolume() {
        return Float.parseFloat(properties.getProperty(KEY_WEAPON_VOLUME, String.valueOf(DEFAULT_WEAPON_VOLUME)));
    }
    
    public float getMusicVolume() {
        return Float.parseFloat(properties.getProperty(KEY_MUSIC_VOLUME, String.valueOf(DEFAULT_MUSIC_VOLUME)));
    }
    
    public boolean isSoundEnabled() {
        return Boolean.parseBoolean(properties.getProperty(KEY_SOUND_ENABLED, String.valueOf(DEFAULT_SOUND_ENABLED)));
    }
    
    // Setters
    
    public void setMasterVolume(float volume) {
        properties.setProperty(KEY_MASTER_VOLUME, String.valueOf(volume));
        saveConfig();
    }
    
    public void setWeaponVolume(float volume) {
        properties.setProperty(KEY_WEAPON_VOLUME, String.valueOf(volume));
        saveConfig();
    }
    
    public void setMusicVolume(float volume) {
        properties.setProperty(KEY_MUSIC_VOLUME, String.valueOf(volume));
        saveConfig();
    }
    
    public void setSoundEnabled(boolean enabled) {
        properties.setProperty(KEY_SOUND_ENABLED, String.valueOf(enabled));
        saveConfig();
    }
}
