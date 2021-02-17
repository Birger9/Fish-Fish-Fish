package game;

import javax.swing.*;

/**
 * The Main class is the main class of the program
 */
public class Main {

    private JFrame frame = new JFrame();
    /**
     * Constructor that initializes AppPanel and sets title
     */
    public Main() {
	AppPanel panel = new AppPanel();
	frame.add(panel);
	frame.pack();
	frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	frame.setResizable(false);
	frame.setTitle("FishFishFish");
	frame.setVisible(true);
    }

    public static void main(String[] args) {
        Main main = new Main();
    }
}
