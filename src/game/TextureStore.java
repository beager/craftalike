package game;

import java.io.IOException;
import java.util.HashMap;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

public class TextureStore {

	private static HashMap<String, Texture> textureMap = new HashMap<String, Texture>();
	
	public static Texture getTexture(String path) {
		// Return the texture if it already exists in the map
		if(textureMap.containsKey(path)) {
			return textureMap.get(path);
		} else {
			try {
				Texture tex = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream(path));
				GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
				GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
				textureMap.put(path, tex);
				return tex;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}
}
