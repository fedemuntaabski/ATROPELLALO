package com.atropellalo.game.sound;

import com.atropellalo.game.config.GameConfig;
import com.atropellalo.game.weapon.WeaponType;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.EnumMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
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
    
    /** Pool de clips para armas de disparo rápido (3 clips por arma) */
    private final Map<WeaponType, List<Clip>> weaponClipPools;
    
    /** Índice del siguiente clip a usar en el pool */
    private final Map<WeaponType, Integer> poolIndexes;
    
    /** Estado de reproducción de cada arma */
    private final Map<WeaponType, Boolean> playingState;
    
    /** Volumen maestro (0.0 - 1.0) */
    private float masterVolume;
    
    /** Volumen de efectos de armas (0.0 - 1.0) */
    private float weaponVolume;
    
    /** Indica si el sonido está habilitado */
    private boolean soundEnabled;
    
    /** Clip de música de fondo actual */
    private Clip musicClip;
    
    /** Lista de pistas de música para gameplay */
    private final List<String> gameplayMusicTracks;
    
    /** Índice de la pista actual en gameplay */
    private int currentTrackIndex;
    
    /** Volumen de música (0.0 - 1.0) */
    private float musicVolume;
    
    /** Path de la música actual para evitar reinicios */
    private String currentMusicPath;
    
    /** Volumen temporal para atenuación */
    private float tempMusicVolume;
    
    /** Indica si la música está atenuada */
    private boolean musicDimmed;
    
    /** LineListener para la música de gameplay */
    private LineListener gameplayMusicListener;
    
    /**
     * Constructor privado (singleton).
     */
    private SoundManager() {
        this.weaponClips = new EnumMap<>(WeaponType.class);
        this.weaponClipPools = new EnumMap<>(WeaponType.class);
        this.poolIndexes = new EnumMap<>(WeaponType.class);
        this.playingState = new EnumMap<>(WeaponType.class);
        this.masterVolume = GameConfig.SOUND_MASTER_VOLUME;
        this.weaponVolume = GameConfig.SOUND_WEAPON_VOLUME;
        this.musicVolume = 0.5f; // Volumen de música por defecto
        this.soundEnabled = GameConfig.SOUND_ENABLED;
        this.gameplayMusicTracks = new ArrayList<>();
        this.currentTrackIndex = 0;
        this.currentMusicPath = null;
        this.tempMusicVolume = -1;
        this.musicDimmed = false;
        
        // Configurar lista de pistas de gameplay (todas excepto sombras_del_fin.mp3)
        gameplayMusicTracks.add("/music/danza_de_los_muertos.mp3");
        gameplayMusicTracks.add("/music/danza_de_los_muertos_v2.mp3");
        gameplayMusicTracks.add("/music/the_last_breath.mp3");
        
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
        // Determinar si el arma necesita pool de clips (armas de disparo rápido)
        boolean needsPool = weaponType == WeaponType.PISTOL || 
                           weaponType == WeaponType.SHOTGUN || 
                           weaponType == WeaponType.SNIPER_RAILGUN ||
                           weaponType == WeaponType.GRENADE_LAUNCHER;
        
        if (needsPool) {
            // Crear pool de 3 clips para esta arma
            List<Clip> pool = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                Clip clip = loadSingleClip(resourcePath);
                if (clip != null) {
                    pool.add(clip);
                }
            }
            if (!pool.isEmpty()) {
                weaponClipPools.put(weaponType, pool);
                poolIndexes.put(weaponType, 0);
                LOGGER.info("Pool de sonidos cargado para: " + weaponType.name() + " (" + pool.size() + " clips)");
            }
        } else {
            // Cargar un solo clip para armas de loop
            Clip clip = loadSingleClip(resourcePath);
            if (clip != null) {
                weaponClips.put(weaponType, clip);
                playingState.put(weaponType, false);
                LOGGER.info("Sonido cargado para: " + weaponType.name());
            }
        }
    }
    
    /**
     * Carga un clip individual desde un recurso.
     * @param resourcePath Ruta al recurso de audio
     * @return Clip cargado o null si hubo error
     */
    private Clip loadSingleClip(String resourcePath) {
        try {
            InputStream inputStream = getClass().getResourceAsStream(resourcePath);
            if (inputStream == null) {
                LOGGER.warning("No se encontró el archivo de sonido: " + resourcePath);
                return null;
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
            return clip;
            
        } catch (UnsupportedAudioFileException e) {
            LOGGER.log(Level.WARNING, "Formato de audio no soportado. MP3 requiere biblioteca adicional.", e);
        } catch (IOException | LineUnavailableException e) {
            LOGGER.log(Level.WARNING, "Error cargando sonido", e);
        }
        return null;
    }
    
    /**
     * Reproduce el sonido de un arma una vez.
     * @param weaponType Tipo de arma
     */
    public void playWeaponSound(WeaponType weaponType) {
        if (!soundEnabled) {
            return;
        }
        
        // Usar pool de clips si existe (para armas de disparo rápido)
        if (weaponClipPools.containsKey(weaponType)) {
            List<Clip> pool = weaponClipPools.get(weaponType);
            int index = poolIndexes.getOrDefault(weaponType, 0);
            
            Clip clip = pool.get(index);
            if (clip != null) {
                // Detener si está corriendo
                if (clip.isRunning()) {
                    clip.stop();
                }
                clip.flush();
                clip.setFramePosition(0);
                setClipVolume(clip, masterVolume * weaponVolume);
                clip.start();
                
                // Rotar al siguiente clip del pool
                poolIndexes.put(weaponType, (index + 1) % pool.size());
            }
            return;
        }
        
        // Usar clip único para armas que no necesitan pool
        if (!weaponClips.containsKey(weaponType)) {
            return;
        }
        
        Clip clip = weaponClips.get(weaponType);
        if (clip != null) {
            // Detener y limpiar cualquier reproducción anterior
            if (clip.isRunning()) {
                clip.stop();
            }
            clip.flush();
            clip.setLoopPoints(0, -1); // Asegurar que no está en loop
            clip.setFramePosition(0);
            playingState.put(weaponType, false);
            
            // Configurar volumen e iniciar
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
        
        Clip clip = weaponClips.get(weaponType);
        if (clip != null) {
            // Si ya está sonando, no hacer nada
            if (clip.isRunning() && Boolean.TRUE.equals(playingState.get(weaponType))) {
                return;
            }
            
            // Detener cualquier sonido anterior
            clip.stop();
            clip.flush();
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
            clip.loop(0); // Cancelar loop primero
            clip.stop();
            clip.flush(); // Detener abruptamente el sonido
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
            
            // Aplicar cambio inmediatamente
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
        
        // Actualizar volumen de música también
        if (musicClip != null) {
            setClipVolume(musicClip, masterVolume * musicVolume);
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
            stopMusic();
        }
    }
    
    /**
     * Detiene todos los sonidos.
     */
    public void stopAllSounds() {
        // Detener clips únicos (armas de loop como LMG y Flamethrower)
        for (Map.Entry<WeaponType, Clip> entry : weaponClips.entrySet()) {
            Clip clip = entry.getValue();
            if (clip != null) {
                if (clip.isRunning()) {
                    clip.loop(0); // Cancelar loop primero
                    clip.stop();
                    clip.flush(); // Detener abruptamente
                }
                playingState.put(entry.getKey(), false);
            }
        }
        
        // Detener también todos los clips de los pools por seguridad
        for (Map.Entry<WeaponType, List<Clip>> entry : weaponClipPools.entrySet()) {
            List<Clip> pool = entry.getValue();
            if (pool != null) {
                for (Clip clip : pool) {
                    if (clip != null && clip.isRunning()) {
                        clip.stop();
                        clip.flush();
                    }
                }
            }
        }
    }
    
    /**
     * Reproduce música de fondo desde un recurso.
     * @param resourcePath Ruta al recurso de música
     * @param loop true para reproducir en loop
     */
    public void playMusic(String resourcePath, boolean loop) {
        if (!soundEnabled) {
            return;
        }
        
        // No reiniciar si ya está sonando la misma música
        if (currentMusicPath != null && currentMusicPath.equals(resourcePath) && 
            musicClip != null && musicClip.isRunning()) {
            return;
        }
        
        stopMusic();
        currentMusicPath = resourcePath;
        
        try {
            InputStream inputStream = getClass().getResourceAsStream(resourcePath);
            if (inputStream == null) {
                LOGGER.warning("No se encontró el archivo de música: " + resourcePath);
                return;
            }
            
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
            
            musicClip = AudioSystem.getClip();
            musicClip.open(decodedAudioIn);
            setClipVolume(musicClip, masterVolume * musicVolume);
            
            if (loop) {
                musicClip.loop(Clip.LOOP_CONTINUOUSLY);
            } else {
                // Crear y guardar listener para reproducir la siguiente pista
                gameplayMusicListener = event -> {
                    if (event.getType() == LineEvent.Type.STOP && !musicClip.isRunning()) {
                        playNextGameplayTrack();
                    }
                };
                musicClip.addLineListener(gameplayMusicListener);
                musicClip.start();
            }
            
            LOGGER.info("Música iniciada: " + resourcePath);
            
        } catch (UnsupportedAudioFileException e) {
            LOGGER.log(Level.WARNING, "Formato de audio no soportado para música: " + resourcePath, e);
        } catch (IOException | LineUnavailableException e) {
            LOGGER.log(Level.WARNING, "Error cargando música: " + resourcePath, e);
        }
    }
    
    /**
     * Reproduce música del menú principal (sombras_del_fin.mp3 en loop).
     */
    public void playMenuMusic() {
        playMusic("/music/sombras_del_fin.mp3", true);
    }
    
    /**
     * Inicia la reproducción de música de gameplay.
     * Las pistas se reproducen en secuencia y luego se repiten.
     */
    public void playGameplayMusic() {
        if (!soundEnabled || gameplayMusicTracks.isEmpty()) {
            return;
        }
        
        currentTrackIndex = 0;
        playMusic(gameplayMusicTracks.get(currentTrackIndex), false);
    }
    
    /**
     * Reproduce la siguiente pista de gameplay.
     */
    private void playNextGameplayTrack() {
        if (!soundEnabled || gameplayMusicTracks.isEmpty()) {
            return;
        }
        
        // Verificar que currentMusicPath sea una pista de gameplay
        // Si es null o es la música del menú, no reproducir nada
        if (currentMusicPath == null || currentMusicPath.equals("/music/sombras_del_fin.mp3")) {
            return;
        }
        
        currentTrackIndex = (currentTrackIndex + 1) % gameplayMusicTracks.size();
        playMusic(gameplayMusicTracks.get(currentTrackIndex), false);
    }
    
    /**
     * Detiene la música de fondo.
     */
    public void stopMusic() {
        if (musicClip != null) {
            // Remover listener si existe para evitar reproducciones automáticas
            if (gameplayMusicListener != null) {
                musicClip.removeLineListener(gameplayMusicListener);
                gameplayMusicListener = null;
            }
            
            if (musicClip.isRunning()) {
                musicClip.stop();
            }
            musicClip.close();
            musicClip = null;
        }
        currentMusicPath = null;
        musicDimmed = false;
        tempMusicVolume = -1;
    }
    
    /**
     * Establece el volumen de la música.
     * @param volume Volumen (0.0 - 1.0)
     */
    public void setMusicVolume(float volume) {
        this.musicVolume = Math.max(0, Math.min(1, volume));
        if (musicClip != null && musicClip.isRunning()) {
            // Aplicar inmediatamente sin delay
            float actualVolume = musicDimmed ? musicVolume * 0.3f : musicVolume;
            setClipVolume(musicClip, masterVolume * actualVolume);
        }
    }
    
    /**
     * Atenúa la música (reduce el volumen al 30%).
     */
    public void dimMusic() {
        if (!musicDimmed && musicClip != null && musicClip.isRunning()) {
            musicDimmed = true;
            setClipVolume(musicClip, masterVolume * musicVolume * 0.3f);
        }
    }
    
    /**
     * Restaura el volumen normal de la música.
     */
    public void undimMusic() {
        if (musicDimmed && musicClip != null && musicClip.isRunning()) {
            musicDimmed = false;
            setClipVolume(musicClip, masterVolume * musicVolume);
        }
    }
    
    /**
     * Libera todos los recursos de audio.
     */
    public void dispose() {
        stopAllSounds();
        stopMusic();
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
    
    public float getMusicVolume() {
        return musicVolume;
    }
    
    public boolean isSoundEnabled() {
        return soundEnabled;
    }
    
    public String getCurrentMusicPath() {
        return currentMusicPath;
    }
}
