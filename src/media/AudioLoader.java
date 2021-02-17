package media;

import java.util.HashMap;
import java.util.Map;

/**
 * The class AudioLoader is used to load audio into the game by adding entries into a hashmap that contains a name/id
 * and an AudioClip which specifies which file(s) should be played
 */
public class AudioLoader
{
    private AudioManagerBorrowedCode audioManager = new AudioManagerBorrowedCode();

    public AudioLoader(AudioManagerBorrowedCode audioManager) {
        this.audioManager = audioManager;
    }

    private final Map<String, AudioClip> audioClipHashMap = new HashMap<>();

    /**
     * Method createAudioClips adds audio clips to the audio clip hashmap
     */
    public void createAudioClips() {
        audioClipHashMap.put("BITE", new AudioClip(new String[]{ "bite1.wav", "bite2.wav", "bite3.wav",
		"bite4.wav", "bite5.wav", "bite6.wav" }, audioManager));
	audioClipHashMap.put("DASH", new AudioClip(new String[]{ "dash1.wav", "dash2.wav" }, audioManager));
	audioClipHashMap.put("MUSIC", new AudioClip(new String[] { "music1.wav", "music2.wav", "music3.wav",
		"music4.wav" }, audioManager));
	audioClipHashMap.put("WARNING", new AudioClip(new String[] { "warning.wav" }, audioManager));
    }

    /**
     * Method loopClip plays a specified audio clip continuously
     * @param name The name of the clip to play
     * @param timeBetweenSamples The time to wait between each loop
     */
    public void loopClip(String name, double timeBetweenSamples) {
	AudioClip clip = getClip(name);
	if (clip == null) {
	    System.out.println("Could not play audioClip with name " + name);
	}
	else {
	    clip.loop(timeBetweenSamples);
	}
    }

    /**
     * Method loopClip plays a specified audio clip
     * @param name The name of the clip to play
     */
    public void playClip(String name) {
        AudioClip clip = getClip(name);
        if (clip == null) {
	    System.out.println("Could not play audioClip with name " + name);
	}
        else {
            clip.play();
	}
    }

    /**
     * Method getClip retrieves appropriate audio clip from the hash map
     * @param String, name of the audio that should be played
     * @return media.AudioClip object
     * @exception RuntimeException,NullPointerException
     * @see RuntimeException
     */
    public AudioClip getClip(String name) {
        if (!audioClipHashMap.containsKey(name)) {
	    System.out.println("WARNING: Clip \"" + name + "\" does not exist");
	}
        return audioClipHashMap.get(name);
    }
}
