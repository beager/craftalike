package game;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

public class Lighting {
	
	public static void initLighting() {
		FloatBuffer lightAmbient = BufferUtils.createFloatBuffer(4);
		FloatBuffer lightDiffuse = BufferUtils.createFloatBuffer(4);
		FloatBuffer lightPosition = BufferUtils.createFloatBuffer(4);
		FloatBuffer lightDir = BufferUtils.createFloatBuffer(4);
		
		/* Set up the light */
	    // Ambient Light
	    lightAmbient.put(0, 1.0f);
	    lightAmbient.put(1, 1.0f);
	    lightAmbient.put(2, 1.0f);
	    lightAmbient.put(3, 1.0f);

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
	    GL11.glLight(GL11.GL_LIGHT1, GL11.GL_SPOT_DIRECTION, lightDir);
	    GL11.glLight(GL11.GL_LIGHT1, GL11.GL_POSITION, lightPosition); // Position The Light
	    GL11.glEnable(GL11.GL_LIGHT1); // Enable Light One
	}

	public static void initFog() {
		GL11.glEnable(GL11.GL_FOG);
		FloatBuffer fogColor = BufferUtils.createFloatBuffer(4);
		fogColor.put(Game.AMBIENCE_COLOR.x).put(Game.AMBIENCE_COLOR.y).put(Game.AMBIENCE_COLOR.z).put(Game.AMBIENCE_COLOR.a).flip();
		GL11.glFogi(GL11.GL_FOG_MODE, GL11.GL_EXP2);
		GL11.glFog(GL11.GL_FOG_COLOR, fogColor);
		GL11.glFogf(GL11.GL_FOG_DENSITY, 0.08f);
		GL11.glHint(GL11.GL_FOG_HINT, GL11.GL_DONT_CARE);
		//GL11.glFogf(GL11.GL_FOG_START, 10000.0f);
		//GL11.glFogf(GL11.GL_FOG_END, 50000.0f);
	}

}
