package game;

import java.io.IOException;

import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.AudioLoader;
import org.newdawn.slick.util.ResourceLoader;

public class Music {

	private static Audio oggStream;
	
	public static void playMusic() {
		try {
			oggStream = AudioLoader.getStreamingAudio("OGG", ResourceLoader.getResource("/res/audio/music/song1.ogg"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		oggStream.playAsMusic(1.0f, 1.0f, true);
	}

}
