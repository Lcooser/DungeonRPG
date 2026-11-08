package com.dungeonrpg.game.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Disposable;

import java.util.EnumMap;
import java.util.Map;

/**
 * Plays sound effects and music. Every file is optional: a missing file means silence for that sound,
 * so audio can be added later by dropping files into assets/sounds and assets/music.
 */
public class AudioManager implements Disposable {

    private static final String[] EXTENSIONS = {".ogg", ".wav", ".mp3"};
    private static final float SOUND_VOLUME = 0.7f;
    private static final float MUSIC_VOLUME = 0.4f;
    // Small random pitch change so repeated hits do not sound identical.
    private static final float PITCH_VARIATION = 0.06f;

    private final Map<SoundId, Sound> sounds = new EnumMap<>(SoundId.class);
    private Music music;

    public AudioManager() {
        for (SoundId id : SoundId.values()) {
            FileHandle file = find("sounds/" + id.getFileName());

            if (file != null) {
                sounds.put(id, Gdx.audio.newSound(file));
            }
        }
    }

    public void play(SoundId id) {
        Sound sound = sounds.get(id);

        if (sound == null) return;

        sound.play(SOUND_VOLUME, 1f + MathUtils.random(-PITCH_VARIATION, PITCH_VARIATION), 0f);
    }

    /** Starts looping the music file (without extension, e.g. "music/dungeon"), if it exists. */
    public void playMusic(String path) {
        stopMusic();

        FileHandle file = find(path);

        if (file == null) return;

        music = Gdx.audio.newMusic(file);
        music.setLooping(true);
        music.setVolume(MUSIC_VOLUME);
        music.play();
    }

    public void stopMusic() {
        if (music != null) {
            music.stop();
            music.dispose();
            music = null;
        }
    }

    private FileHandle find(String pathWithoutExtension) {
        for (String extension : EXTENSIONS) {
            FileHandle file = Gdx.files.internal(pathWithoutExtension + extension);

            if (file.exists()) return file;
        }

        return null;
    }

    @Override
    public void dispose() {
        stopMusic();

        for (Sound sound : sounds.values()) {
            sound.dispose();
        }
    }
}
