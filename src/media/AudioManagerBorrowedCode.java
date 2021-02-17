package media;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

// unable to open file '/etc/dconf/db/site': /etc/dconf/db/site: invalid gvdb header; expect degraded performance
// This file is missing (at least on whatever distro ThinLinc uses), which likely causes a bunch of exceptions and audio delay.
// This issue could be the reason why the audio stops working after a few seconds. This has not previously been a problem on Windows.

/**
 * The class media.AudioManager manages information about the audio.
 * such as size, format and so on and is responsible for actually playing the sounds in the game.
 * Once you add a clip, its index is saved as an integer. The first clip is for example played by calling playSound(0).
 * This is done automatically by utilizing AudioClip
 *
 * source: http://www.java-gaming.org/index.php?topic=1948.0
 * Some modifications have been made.
 */

public class AudioManagerBorrowedCode
{
    private static final int SAMPLE_INBITS = 16;
    private static final float SAMPLE_RATE = 8000.0F;
    private static final int BUFFER_SIZE = 1024;
    private List<AudioFormat> audioformatList = new ArrayList<>();
    private List<Integer> sizeList = new ArrayList<>();
    private List<DataLine.Info> infoList = new ArrayList<>();
    private List<byte[]> audioList = new ArrayList<>();
    private int num = 0;

    // Audio clips, file path information etc
    private static final String AUDIO_FOLDER_FILE_PATH = "/audio/";

    public AudioManagerBorrowedCode()
    {
    }

    /**
     * addClip adds an audio clip
     * @param String of the filename.
     * @exception UnsupportedAudioFileException,LineUnavailableException
     * @see IOException,Exception
     */
    public void addClip(String fileName)
	    throws UnsupportedAudioFileException, LineUnavailableException
    {
	URL url = getClass().getResource(AUDIO_FOLDER_FILE_PATH + fileName);
	AudioInputStream audioInputStream = null;
	try (AudioInputStream inputStream = AudioSystem.getAudioInputStream(loadStream(url.openStream()))) {
	    audioInputStream = inputStream;
	} catch (IOException e) {
	    e.printStackTrace();
	}
	//AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(loadStream(url.openStream()));
	AudioFormat af = audioInputStream.getFormat();
	int size = (int) (af.getFrameSize() * audioInputStream.getFrameLength());
	byte[] audio = new byte[size];
	DataLine.Info info = new DataLine.Info(Clip.class, af, size);
	try {
	    audioInputStream.read(audio, 0, size); // Result of read() is purposely ignored since we don't need it.
	} catch (IOException e) {
	    e.printStackTrace();
	}

	audioformatList.add(af);
	sizeList.add(size);
	infoList.add(info);
	audioList.add(audio);

	num++;
    }

    /**
     * loadStream loads an input stream
     * @param InputStream the input stream to load.
     * @return ByteArrayInputStream the resulting ByteArrayInputStream.
     * @exception IOException
     * @see IOException
     */
    private ByteArrayInputStream loadStream(InputStream inputstream)
	    throws IOException
    {
        ByteArrayOutputStream byteArrayOutputStream;
	try (ByteArrayOutputStream byteArrOutputStream = new ByteArrayOutputStream()) {
	    byteArrayOutputStream = byteArrOutputStream;
	}
	byte data[] = new byte[BUFFER_SIZE];
	for(int i = inputstream.read(data); i != -1; i = inputstream.read(data))
	    byteArrayOutputStream.write(data, 0, i);

	inputstream.close();
	byteArrayOutputStream.close();
	data = byteArrayOutputStream.toByteArray();
	return new ByteArrayInputStream(data);
    }

    /**
     * playSound plays a selected media.AudioClip
     * @param clipIndex index of the audioClip to play.
     */
    public void playSound(int clipIndex) {
	if(clipIndex > num) {
	    System.out.println("playSound: sample " + clipIndex + " is not available");
	}
	else {
	    try {
	        Clip clip;
		try (Clip c = (Clip) AudioSystem.getLine(infoList.get(clipIndex))) { clip = c; }
		clip.open(audioformatList.get(clipIndex), audioList.get(clipIndex), 0, (sizeList.get(clipIndex)).intValue());
		clip.start();
	    } catch(LineUnavailableException | IllegalArgumentException ex){ // IllegalArgumentException is caught because of a
		ex.printStackTrace();					     // problem with the IDE (or something). Sometimes
	    }								     // an IllegalArgumentException is thrown by getLine.
	}								     // While the exact cause is unknown, it is reasonable
    }									     // to assume that it is unrelated to the code itself,
    									     // seeing as it works properly on windows netbeans.
    /**
     * getClipLength gets the length of a clip, in seconds.
     * @param fileName the name of the file.
     * @return durationInSeconds, the duration of the clip in seconds.
     */
    public double getClipLength(String fileName){
	double durationInSeconds;
	try{
	    File file = new File(System.getProperty("user.dir") + "/src/audio/" + fileName);
	    AudioInputStream audioInputStream;
	    try (AudioInputStream audioInStream = AudioSystem.getAudioInputStream(file)) {
	        audioInputStream = audioInStream;
	    }
	    AudioFormat format = getAudioFormat();
	    long frames = audioInputStream.getFrameLength();
	    durationInSeconds = (frames+0.0) / format.getFrameRate();
	} catch(IOException | UnsupportedAudioFileException ex){
	    durationInSeconds = 0;
	    ex.printStackTrace();
	}
	return durationInSeconds;
    }

    /**
     * getAudioFormat returns a new AudioFormat object and sets its parameters (such as sample rate and number of channels).
     * @return an AudioFormat object
     */
    private AudioFormat getAudioFormat() {
	int channels = 1;
	boolean signed = true;
	boolean bigEndian = false;
	return new AudioFormat(SAMPLE_RATE, SAMPLE_INBITS, channels, signed, bigEndian);
    }

    public int getNum(){
	return num;
    }

}
