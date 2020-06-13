package media;

import java.util.HashMap;

/**
 * The class AudioLoader is used to load audio into the game by adding entries into a hashmap that contains a name/id
 * and an AudioClip which specifies which file(s) should be played
 */
public class AudioLoader
{
    private AudioLoader() {}

    private static HashMap<String, AudioClip> audioClipHashMap = new HashMap<>();

    /**
     * Method createAudioClips adds audio clips to the audio clip hashmap
     * @param Nothing.
     * @return Nothing.
     */
    public static void createAudioClips() {
        audioClipHashMap.put("BITE", new AudioClip(new String[]{ "bite1.wav", "bite2.wav", "bite3.wav",
		"bite4.wav", "bite5.wav", "bite6.wav" }));
	audioClipHashMap.put("DASH", new AudioClip(new String[]{ "dash1.wav", "dash2.wav" }));
	audioClipHashMap.put("MUSIC", new AudioClip(new String[] { "music1.wav", "music2.wav", "music3.wav",
		"music4.wav" }));
	audioClipHashMap.put("WARNING", new AudioClip(new String[] { "warning.wav" }));
    }

    /**
     * Method loopClip plays a specified audio clip continuously
     * @param name The name of the clip to play
     * @param timeBetweenSamples The time to wait between each loop
     * @return Nothing.
     */
    public static void loopClip(String name, double timeBetweenSamples) {
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
     * @return Nothing.
     */
    public static void playClip(String name) {
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
    public static AudioClip getClip(String name) {
        if (!audioClipHashMap.containsKey(name)) {
	    System.out.println("WARNING: Clip \"" + name + "\" does not exist");
	}
        return audioClipHashMap.get(name);
    }
}
