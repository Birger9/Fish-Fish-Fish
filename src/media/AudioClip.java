package media;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 * The media.AudioClip class is used to play back audio. It gets initialized through the clip's file name, and can be played,
 * looped, and stopped.
 */
public class AudioClip {

    private String[] fileNames;
    private int minIndex, maxIndex;
    private boolean loop = true;
    private AudioManagerBorrowedCode audioManager = new AudioManagerBorrowedCode();

    public AudioClip(String fileName, AudioManagerBorrowedCode audioManager){
	fileNames = new String[1];
	fileNames[0] = fileName;
	this.audioManager = audioManager;

	minIndex = audioManager.getNum(); // Get sample index before adding audio files
	try {
	    audioManager.addClip(fileName);
	} catch (UnsupportedAudioFileException | LineUnavailableException ex) {
	    Logger.getLogger(AudioClip.class.getName()).log(Level.SEVERE, null, ex);
	    System.out.println("WARNING: media.AudioClip " + fileName + " not found");
	}
	maxIndex = audioManager.getNum(); // Get sample index after adding audio files
    }

    public AudioClip(String[] fileNames, AudioManagerBorrowedCode audioManager){
	this.fileNames = fileNames;
	this.audioManager = audioManager;

	// Sample index = sample count - 1
	minIndex = audioManager.getNum(); // Get sample index before adding audio files
	try {
	    for(String file : fileNames) {
		audioManager.addClip(file);
	    }
	} catch (UnsupportedAudioFileException | LineUnavailableException ex) {
	    Logger.getLogger(AudioClip.class.getName()).log(Level.SEVERE, null, ex);
	    System.out.println("WARNING: media.AudioClip not found");
	}
	maxIndex = audioManager.getNum(); // Get sample index after adding audio files
    }

    /**
     * Play an audio file.
     * If there are several files in the audio clip, pick a random file
     */
    public void play(){
	int randomIndex = (int)Math.floor(Math.random() * (maxIndex - minIndex) + minIndex); // Fetch a random number between minIndex and maxIndex
	audioManager.playSound(randomIndex);
    }

    /**
     * Loop an audio file continuously.
     * If there are several files in the audio clip, pick a random file
     * @param double Time to wait in seconds before the clip starts playing again after it is done playing.
     */
    public void loop(double timeBetweenSamples){
	loop = true;
	int randomIndex = (int)(Math.random() * (maxIndex - minIndex));
	double intervalInSeconds = audioManager.getClipLength(fileNames[randomIndex]) + timeBetweenSamples;
	ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
	final Runnable play = new Runnable(){
	    @Override
	    public void run() {
		if(loop)
		    audioManager.playSound(randomIndex + minIndex); // Play audio clip
		else
		    scheduler.shutdown();
	    }
	};
	scheduler.scheduleAtFixedRate(play, 0, (long)(intervalInSeconds * 1000), TimeUnit.MILLISECONDS);
    }
    /**
     * Set loop to false, and stop the clip after the current loop is done
     */
    public void stop(){
	loop = false;
    }
}
