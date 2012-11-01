package game;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

public class Lighting {
	
	public static boolean isUnderwater = false;
	
	public static void initLighting() {
		FloatBuffer lightAmbient = BufferUtils.createFloatBuffer(4);
		FloatBuffer lightDiffuse = BufferUtils.createFloatBuffer(4);
		FloatBuffer lightPosition = BufferUtils.createFloatBuffer(4);
		FloatBuffer lightDir = BufferUtils.createFloatBuffer(4);
		
		/* Set up the light */
	    // Ambient Light
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
	    // Diffuse Light
	    lightDiffuse.put(0, 1.0f);
	    lightDiffuse.put(1, 1.0f);
	    lightDiffuse.put(2, 1.0f);
	    lightDiffuse.put(3, 1.0f);

	    // Light Position
	    lightPosition.put(0, 64.0f);
	    lightPosition.put(1, 64.0f);
	    lightPosition.put(2, 50.0f);
	    lightPosition.put(3, 1.0f);

	    lightDir.put(0, 0.0f);
	    lightDir.put(1, -100.0f);
	    lightDir.put(2, 0.0f);
	    lightDir.put(3, 1.0f);

	    GL11.glLight(GL11.GL_LIGHT1, GL11.GL_AMBIENT, lightAmbient); // Setup The Ambient Light
	    //GL11.glLight(GL11.GL_LIGHT1, GL11.GL_DIFFUSE, lightDiffuse); // Setup The Diffuse Light
	    //GL11.glLight(GL11.GL_LIGHT1, GL11.GL_SPOT_DIRECTION, lightDir);
	    //GL11.glLight(GL11.GL_LIGHT1, GL11.GL_POSITION, lightPosition); // Position The Light
	    GL11.glEnable(GL11.GL_LIGHT1); // Enable Light One
	}

	public static void initFog() {
		if (isUnderwater) {
			GL11.glEnable(GL11.GL_FOG);
			FloatBuffer fogColor = BufferUtils.createFloatBuffer(4);
			fogColor.put(Game.AMBIENCE_COLOR.x - 0.7f).put(Game.AMBIENCE_COLOR.y - 0.7f).put(Game.AMBIENCE_COLOR.z - 0.6f).put(Game.AMBIENCE_COLOR.a).flip();
			GL11.glFogi(GL11.GL_FOG_MODE, GL11.GL_EXP2);
			GL11.glFog(GL11.GL_FOG_COLOR, fogColor);
			GL11.glFogf(GL11.GL_FOG_DENSITY, 0.2f); // was 0.08
			GL11.glHint(GL11.GL_FOG_HINT, GL11.GL_DONT_CARE);
		} else {
			GL11.glEnable(GL11.GL_FOG);
			FloatBuffer fogColor = BufferUtils.createFloatBuffer(4);
			fogColor.put(Game.AMBIENCE_COLOR.x).put(Game.AMBIENCE_COLOR.y).put(Game.AMBIENCE_COLOR.z).put(Game.AMBIENCE_COLOR.a).flip();
			GL11.glFogi(GL11.GL_FOG_MODE, GL11.GL_EXP2);
			GL11.glFog(GL11.GL_FOG_COLOR, fogColor);
			GL11.glFogf(GL11.GL_FOG_DENSITY, 0.01f); // was 0.08
			GL11.glHint(GL11.GL_FOG_HINT, GL11.GL_DONT_CARE);
			//GL11.glFogf(GL11.GL_FOG_START, 100.0f);
			//GL11.glFogf(GL11.GL_FOG_END, 500.0f);
		}
	}

	public static void setIsUnderwater(boolean b) {
		isUnderwater = b;
	}

}
