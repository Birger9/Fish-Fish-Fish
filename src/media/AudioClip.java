package media;

import game.AppPanel;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
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

    public AudioClip(String fileName){
	fileNames = new String[1];
	fileNames[0] = fileName;

	minIndex = AppPanel.getAudioManager().getNum(); // Get sample index before adding audio files
	try {
	    AppPanel.getAudioManager().addClip(fileName);
	} catch (IOException | UnsupportedAudioFileException | LineUnavailableException ex) {
	    Logger.getLogger(AudioClip.class.getName()).log(Level.SEVERE, null, ex);
	    System.out.println("WARNING: media.AudioClip " + fileName + " not found");
	}
	maxIndex = AppPanel.getAudioManager().getNum(); // Get sample index after adding audio files
    }

    public AudioClip(String[] fileNames){
	this.fileNames = fileNames;

	// Sample index = sample count - 1
	minIndex = AppPanel.getAudioManager().getNum(); // Get sample index before adding audio files
	try {
	    for(String file : fileNames)
		AppPanel.getAudioManager().addClip(file);
	} catch (IOException | UnsupportedAudioFileException | LineUnavailableException ex) {
	    Logger.getLogger(AudioClip.class.getName()).log(Level.SEVERE, null, ex);
	    System.out.println("WARNING: media.AudioClip not found");
	}
	maxIndex = AppPanel.getAudioManager().getNum(); // Get sample index after adding audio files
    }

    /**
     * Method play plays audio file.
     * If there are several files in the audio clip, pick a random file
     * @param Nothing.
     * @return Nothing.
     */
    public void play(){
	int randomIndex = (int)Math.floor(Math.random() * (maxIndex - minIndex) + minIndex); // Fetch a random number between minIndex and maxIndex
	AppPanel.getAudioManager().playSound(randomIndex);
    }

    /**
     * Method loop loops audio file continuously.
     * If there are several files in the audio clip, pick a random file
     * @param double Time to wait in seconds before the clip starts playing again after it is done playing.
     * @return Nothing.
     */
    public void loop(double timeBetweenSamples){
	loop = true;
	int randomIndex = (int)(Math.random() * (maxIndex - minIndex));
	double intervalInSeconds = AppPanel.getAudioManager().getClipLength(fileNames[randomIndex]) + timeBetweenSamples;
	ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
	final Runnable play = new Runnable(){
	    @Override
	    public void run() {
		if(loop)
		    AppPanel.getAudioManager().playSound(randomIndex + minIndex); // Play audio clip
		else
		    scheduler.shutdown();
	    }
	};
	scheduler.scheduleAtFixedRate(play, 0, (long)(intervalInSeconds * 1000), TimeUnit.MILLISECONDS);
    }
    /**
     * Method play sets loop to false, stops after current loop is done
     * @param Nothing.
     * @return Nothing.
     */
    public void stop(){
	loop = false;
    }
}
