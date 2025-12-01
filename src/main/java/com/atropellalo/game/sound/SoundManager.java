package com.atropellalo.game.sound;

import com.atropellalo.game.config.GameConfig;
import com.atropellalo.game.weapon.WeaponType;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.EnumMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Gestor de sonidos del juego.
 * Maneja la carga, reproducción y control de volumen de efectos de sonido.
 * Soporta reproducción en loop para armas continuas.
 */
public class SoundManager {
    
    private static final Logger LOGGER = Logger.getLogger(SoundManager.class.getName());
    
    /** Instancia singleton */
    private static SoundManager instance;
    
    /** Clips de sonido por tipo de arma */
    private final Map<WeaponType, Clip> weaponClips;
    
    /** Estado de reproducción de cada arma */
    private final Map<WeaponType, Boolean> playingState;
    
    /** Volumen maestro (0.0 - 1.0) */
    private float masterVolume;
    
    /** Volumen de efectos de armas (0.0 - 1.0) */
    private float weaponVolume;
    
    /** Indica si el sonido está habilitado */
    private boolean soundEnabled;
    
    /**
     * Constructor privado (singleton).
     */
    private SoundManager() {
        this.weaponClips = new EnumMap<>(WeaponType.class);
        this.playingState = new EnumMap<>(WeaponType.class);
        this.masterVolume = GameConfig.SOUND_MASTER_VOLUME;
        this.weaponVolume = GameConfig.SOUND_WEAPON_VOLUME;
        this.soundEnabled = GameConfig.SOUND_ENABLED;
        
        loadAllSounds();
    }
    
    /**
     * Obtiene la instancia del SoundManager.
     * @return Instancia singleton
     */
    public static synchronized SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }
    
    /**
     * Carga todos los sonidos de armas.
     */
    private void loadAllSounds() {
        loadWeaponSound(WeaponType.PISTOL, "/sounds/pistol.mp3");
        loadWeaponSound(WeaponType.LIGHT_MACHINE_GUN, "/sounds/machinegun.mp3");
        loadWeaponSound(WeaponType.GRENADE_LAUNCHER, "/sounds/grenade-launcher.mp3");
        loadWeaponSound(WeaponType.FLAMETHROWER, "/sounds/fireflammer.mp3");
        loadWeaponSound(WeaponType.SHOTGUN, "/sounds/shotgun.mp3");
        loadWeaponSound(WeaponType.SNIPER_RAILGUN, "/sounds/sniperrifle.mp3");
    }
    
    /**
     * Carga un sonido de arma desde los recursos.
     * @param weaponType Tipo de arma
     * @param resourcePath Ruta al recurso de audio
     */
    private void loadWeaponSound(WeaponType weaponType, String resourcePath) {
        try {
            InputStream inputStream = getClass().getResourceAsStream(resourcePath);
            if (inputStream == null) {
                LOGGER.warning("No se encontró el archivo de sonido: " + resourcePath);
                return;
            }
            
            // Convertir MP3 a formato compatible (necesita biblioteca externa)
            // Por ahora intentamos cargarlo directamente
            BufferedInputStream bufferedIn = new BufferedInputStream(inputStream);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(bufferedIn);
            
            // Convertir a formato PCM si es necesario
            AudioFormat baseFormat = audioIn.getFormat();
            AudioFormat decodedFormat = new AudioFormat(
                AudioFormat.Encoding.PCM_SIGNED,
                baseFormat.getSampleRate(),
                16,
                baseFormat.getChannels(),
                baseFormat.getChannels() * 2,
                baseFormat.getSampleRate(),
                false
            );
            
            AudioInputStream decodedAudioIn = AudioSystem.getAudioInputStream(decodedFormat, audioIn);
            
            Clip clip = AudioSystem.getClip();
            clip.open(decodedAudioIn);
            
            weaponClips.put(weaponType, clip);
            playingState.put(weaponType, false);
            
            LOGGER.info("Sonido cargado para: " + weaponType.name());
            
        } catch (UnsupportedAudioFileException e) {
            LOGGER.log(Level.WARNING, "Formato de audio no soportado para " + weaponType + ". MP3 requiere biblioteca adicional.", e);
        } catch (IOException | LineUnavailableException e) {
            LOGGER.log(Level.WARNING, "Error cargando sonido para " + weaponType, e);
        }
    }
    
    /**
     * Reproduce el sonido de un arma una vez.
     * @param weaponType Tipo de arma
     */
    public void playWeaponSound(WeaponType weaponType) {
        if (!soundEnabled || !weaponClips.containsKey(weaponType)) {
            return;
        }
        
        Clip clip = weaponClips.get(weaponType);
        if (clip != null) {
            clip.stop();
            clip.setFramePosition(0);
            setClipVolume(clip, masterVolume * weaponVolume);
            clip.start();
        }
    }
    
    /**
     * Inicia la reproducción en loop del sonido de un arma.
     * Útil para armas continuas como el lanzallamas.
     * @param weaponType Tipo de arma
     */
    public void startWeaponLoop(WeaponType weaponType) {
        if (!soundEnabled || !weaponClips.containsKey(weaponType)) {
            return;
        }
        
        // Evitar reiniciar si ya está en loop
        if (Boolean.TRUE.equals(playingState.get(weaponType))) {
            return;
        }
        
        Clip clip = weaponClips.get(weaponType);
        if (clip != null) {
            clip.setFramePosition(0);
            setClipVolume(clip, masterVolume * weaponVolume);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            playingState.put(weaponType, true);
        }
    }
    
    /**
     * Detiene la reproducción en loop del sonido de un arma.
     * @param weaponType Tipo de arma
     */
    public void stopWeaponLoop(WeaponType weaponType) {
        if (!weaponClips.containsKey(weaponType)) {
            return;
        }
        
        Clip clip = weaponClips.get(weaponType);
        if (clip != null && clip.isRunning()) {
            clip.stop();
            playingState.put(weaponType, false);
        }
    }
    
    /**
     * Verifica si un arma está reproduciendo sonido en loop.
     * @param weaponType Tipo de arma
     * @return true si está en loop
     */
    public boolean isLooping(WeaponType weaponType) {
        return Boolean.TRUE.equals(playingState.get(weaponType));
    }
    
    /**
     * Establece el volumen de un clip.
     * @param clip Clip de audio
     * @param volume Volumen (0.0 - 1.0)
     */
    private void setClipVolume(Clip clip, float volume) {
        if (clip == null) {
            return;
        }
        
        try {
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            // Convertir volumen lineal (0-1) a decibelios
            float dB = (float) (Math.log10(Math.max(0.0001, volume)) * 20.0);
            dB = Math.max(gainControl.getMinimum(), Math.min(gainControl.getMaximum(), dB));
            gainControl.setValue(dB);
        } catch (IllegalArgumentException e) {
            LOGGER.fine("Control de volumen no disponible para este clip");
        }
    }
    
    /**
     * Establece el volumen maestro.
     * @param volume Volumen (0.0 - 1.0)
     */
    public void setMasterVolume(float volume) {
        this.masterVolume = Math.max(0, Math.min(1, volume));
        updateAllVolumes();
    }
    
    /**
     * Establece el volumen de armas.
     * @param volume Volumen (0.0 - 1.0)
     */
    public void setWeaponVolume(float volume) {
        this.weaponVolume = Math.max(0, Math.min(1, volume));
        updateAllVolumes();
    }
    
    /**
     * Actualiza el volumen de todos los clips activos.
     */
    private void updateAllVolumes() {
        for (Map.Entry<WeaponType, Clip> entry : weaponClips.entrySet()) {
            Clip clip = entry.getValue();
            if (clip != null && clip.isRunning()) {
                setClipVolume(clip, masterVolume * weaponVolume);
            }
        }
    }
    
    /**
     * Habilita o deshabilita el sonido.
     * @param enabled true para habilitar
     */
    public void setSoundEnabled(boolean enabled) {
        this.soundEnabled = enabled;
        if (!enabled) {
            stopAllSounds();
        }
    }
    
    /**
     * Detiene todos los sonidos.
     */
    public void stopAllSounds() {
        for (Map.Entry<WeaponType, Clip> entry : weaponClips.entrySet()) {
            Clip clip = entry.getValue();
            if (clip != null && clip.isRunning()) {
                clip.stop();
            }
            playingState.put(entry.getKey(), false);
        }
    }
    
    /**
     * Libera todos los recursos de audio.
     */
    public void dispose() {
        stopAllSounds();
        for (Clip clip : weaponClips.values()) {
            if (clip != null) {
                clip.close();
            }
        }
        weaponClips.clear();
        playingState.clear();
    }
    
    // Getters
    
    public float getMasterVolume() {
        return masterVolume;
    }
    
    public float getWeaponVolume() {
        return weaponVolume;
    }
    
    public boolean isSoundEnabled() {
        return soundEnabled;
    }
}
