package game;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

public class Lighting {
	
	public static boolean isUnderwater = false;
	
	public static void initLighting() {
		FloatBuffer lightAmbient = BufferUtils.createFloatBuffer(4);

		if (isUnderwater) {
			lightAmbient.put(0, Game.AMBIENCE_COLOR.x - 0.7f);
		    lightAmbient.put(1, Game.AMBIENCE_COLOR.y - 0.7f);
		    lightAmbient.put(2, Game.AMBIENCE_COLOR.z - 0.6f);
		    lightAmbient.put(3, Game.AMBIENCE_COLOR.a);
		} else {
		    lightAmbient.put(0, (Game.AMBIENCE_COLOR.x * 2.0f) + 2f);
		    lightAmbient.put(1, (Game.AMBIENCE_COLOR.y * 2.0f) + 2f);
		    lightAmbient.put(2, (Game.AMBIENCE_COLOR.z * 2.0f) + 2f);
		    lightAmbient.put(3, 1f);
		}
		
	    GL11.glLight(GL11.GL_LIGHT1, GL11.GL_AMBIENT, lightAmbient); // Setup The Ambient Light
	    GL11.glEnable(GL11.GL_LIGHT1); // Enable Light One
	}

	public static void initFog() {
		if (isUnderwater) {
			GL11.glEnable(GL11.GL_FOG);
			FloatBuffer fogColor = BufferUtils.createFloatBuffer(4);
			fogColor.put(Game.AMBIENCE_COLOR.x - 0.7f).put(Game.AMBIENCE_COLOR.y - 0.7f).put(Game.AMBIENCE_COLOR.z - 0.6f).put(Game.AMBIENCE_COLOR.a).flip();
			GL11.glFogi(GL11.GL_FOG_MODE, GL11.GL_EXP2);
			GL11.glFog(GL11.GL_FOG_COLOR, fogColor);
			GL11.glFogf(GL11.GL_FOG_DENSITY, 0.2f);
			GL11.glHint(GL11.GL_FOG_HINT, GL11.GL_DONT_CARE);
		} else {
			GL11.glEnable(GL11.GL_FOG);
			FloatBuffer fogColor = BufferUtils.createFloatBuffer(4);
			fogColor.put(Game.AMBIENCE_COLOR.x).put(Game.AMBIENCE_COLOR.y).put(Game.AMBIENCE_COLOR.z).put(Game.AMBIENCE_COLOR.a).flip();
			GL11.glFogi(GL11.GL_FOG_MODE, GL11.GL_EXP2);
			GL11.glFog(GL11.GL_FOG_COLOR, fogColor);
			GL11.glFogf(GL11.GL_FOG_DENSITY, 0.01f); // was 0.08
			GL11.glHint(GL11.GL_FOG_HINT, GL11.GL_DONT_CARE);
		}
	}

	public static void setIsUnderwater(boolean b) {
		isUnderwater = b;
	}

}
