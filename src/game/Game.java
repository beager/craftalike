package game;

import java.awt.Font;
import java.io.InputStream;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.util.ResourceLoader;

public class Game {
	
	private static final float MOUSE_SPEED_SCALE = 0.1f;
	private static final float MOVEMENT_SPEED = 0.085f;
	private static final float MOVEMENT_SPEED_FLYMODE = 0.17f;
	private static final float FALSE_GRAVITY_SPEED = 0.035f;
	
	public static final int CHUNK_SIZE = 16;
	public static final int CHUNK_HEIGHT = 64;
	
	private static final boolean FULLSCREEN = false;
	private static final boolean VSYNC = true;
	public static final boolean TEXTURES = true;
	
	private TrueTypeFont font;
	
	public static Vector4f AMBIENCE_COLOR = new Vector4f(0.05f, 0.05f, 0.05f, 1.0f);
	
	private long lastFrame;
	
	private long startTime;
	
	// Game components
	private Camera camera;
	private ChunkManager terrain;
	
	// Toggles
	private boolean flyMode = false;
	private boolean doCollisionChecking = true;
	private boolean renderSkybox = false;
	private boolean wireframe = false;
	
	private boolean closeRequested = false;
	public static long deltaTime;
	
	public void start() {
		// Create the display
		try {
			Display.setDisplayMode(Display.getDesktopDisplayMode());
			Display.setFullscreen(FULLSCREEN);
			Display.setVSyncEnabled(VSYNC);
			Display.create();
		} catch(LWJGLException e) {
			e.printStackTrace();
			System.exit(0);
		}
		
		Display.setTitle("Craftalike");
		
		initGl();

		// Hide the mouse
		Mouse.setGrabbed(true);
		
		// Create the terrain
		terrain = new ChunkManager();
		
		Vector3f startPos = new Vector3f(20000.0f, 80.0f, 20000.0f);
		
		terrain.generate(startPos);

		// Create the camera
		camera = new Camera(startPos, new Vector3f(0.0f, 0.0f, 0.0f), terrain);

		// load font from a .ttf file
		try {
			InputStream inputStream	= ResourceLoader.getResourceAsStream("res/fonts/Minecraftia.ttf");
			
			Font awtFont = Font.createFont(Font.TRUETYPE_FONT, inputStream);
			awtFont = awtFont.deriveFont(24f); // set font size
			font = new TrueTypeFont(awtFont, false);
				
		} catch (Exception e) {
			e.printStackTrace();
		}	
		
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
			
		// Main loop
		lastFrame = System.currentTimeMillis();
		
		startTime = System.currentTimeMillis();
		
		float frameTime = 0.0f;
		
		float frameRes = 1000.0f / 60.0f;
		
		while(!Display.isCloseRequested()) {
			// Calculate delta time
			long t = System.currentTimeMillis();
			deltaTime = (t - lastFrame);
			
			frameTime += deltaTime;
			if (frameTime > frameRes)
			{
				update();
				frameTime %= frameRes;
			}
			if (deltaTime < (1000.0f / 30.0f)) render();
			lastFrame = t;
			
			if (closeRequested) break;
		}
		
		// Cleanup
		terrain.release();
		Display.destroy();
	}

	public static void main(String[] args) {
		Game cubeGame = new Game();
		cubeGame.start();
	}
	
	public void update() {
		camera.hasGravitiedThisFrame = false;
		float movementSpeed = flyMode ? MOVEMENT_SPEED_FLYMODE : MOVEMENT_SPEED;
		
			// Handle mouse movement
				camera.addRotation(new Vector3f(Mouse.getDY() * MOUSE_SPEED_SCALE, -Mouse.getDX() * MOUSE_SPEED_SCALE, 0.0f));
				
			// Handle keypresses
			if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE))
				closeRequested = true;
			if(Keyboard.isKeyDown(Keyboard.KEY_W))
				camera.move(movementSpeed, Camera.FORWARD, 0, doCollisionChecking, flyMode);
			if(Keyboard.isKeyDown(Keyboard.KEY_S))
				camera.move(movementSpeed, Camera.BACKWARD, 0, doCollisionChecking, flyMode);
			if(Keyboard.isKeyDown(Keyboard.KEY_A))
				camera.move(movementSpeed / 2, Camera.LEFT, 0, doCollisionChecking, flyMode);
			if(Keyboard.isKeyDown(Keyboard.KEY_D))
				camera.move(movementSpeed / 2, Camera.RIGHT, 0, doCollisionChecking, flyMode);
			if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
				if (camera.grounded) {
					camera.gravitySpeed = -0.18f;
					camera.grounded = false;
				}
			} else {
				if (camera.gravitySpeed < 0.0f) camera.gravitySpeed = 0.0f;
			}
			if(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL))
				camera.move(0, Camera.RIGHT, FALSE_GRAVITY_SPEED * 2, doCollisionChecking, flyMode);
			
			
			// Check for pressed keys
			while (Keyboard.next()) {
				if (Keyboard.getEventKeyState()) {
					if (Keyboard.getEventKey() == Keyboard.KEY_F) {
					    	flyMode = !flyMode;
					} else if (Keyboard.getEventKey() == Keyboard.KEY_C) {
					    	doCollisionChecking = !doCollisionChecking;
					} else if (Keyboard.getEventKey() == Keyboard.KEY_B) {
					    	renderSkybox = !renderSkybox;
					} else if (Keyboard.getEventKey() == Keyboard.KEY_V) {
					    	wireframe = !wireframe;
					}
				}
			}
			
		// Apply gravity
		if(!flyMode) {
			camera.move(0, Camera.FORWARD, camera.gravitySpeed, doCollisionChecking, flyMode);
			camera.updateGravity();
		}
		
		terrain.updateVisibleChunks(camera.coordinates.x, camera.coordinates.z);
		
		long timeElapsed = System.currentTimeMillis() - startTime;
		float sinPos = (float) Math.sin((timeElapsed / 80000.0f) % (2.0f * Math.PI) );
		sinPos = (	(sinPos + 1) / 2);
		
		if (sinPos < 0.4f) sinPos = 0.4f;
		if (sinPos > 0.6f) sinPos = 0.6f;
		sinPos = (sinPos - 0.4f) * 4.0f + 0.2f;
		AMBIENCE_COLOR.x = sinPos - 0.4f;
		AMBIENCE_COLOR.y = sinPos - 0.4f;
		AMBIENCE_COLOR.z = sinPos - 0.2f;
	}
	
	public void initGl() {
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		//GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glShadeModel(GL11.GL_SMOOTH); 
		//GL11.glEnable(GL11.GL_BLEND);
		//GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
	}
	
	public void render3d() {
		if(wireframe)
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
		else 
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
		
		// Apply the camera matrix
		camera.applyMatrix();
		
		// Render the terrain
		terrain.render();
	}
	
	public void render2d() {
		
		Color.white.bind();
		font.drawString(10, 100, "NICE LOOKING FONTS!", Color.black);
	}
	
	public void render() {
		
		Lighting.initLighting();
		Lighting.initFog();
		int width = Display.getDesktopDisplayMode().getWidth();
		int height = Display.getDesktopDisplayMode().getHeight();
		
		//GL11.glDepthMask(true);
		
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		
		GL11.glLoadIdentity();
		GLU.gluPerspective(70.0f, (float)width / (float)height, 0.1f, 200.0f);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		
		GL11.glClearColor(AMBIENCE_COLOR.x, AMBIENCE_COLOR.y, AMBIENCE_COLOR.z, AMBIENCE_COLOR.a);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glPushMatrix();
		render3d();
		GL11.glPopMatrix();
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();

		GL11.glOrtho(0, width, 0, height, -1, 1);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		
        GL11.glDisable( GL11.GL_DEPTH_TEST );
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_CULL_FACE);
        //GL11.glDepthMask(false);
        GL11.glPushMatrix();

        // set the color of the quad (R,G,B,A)
        GL11.glColor4f(0.5f,0.5f,1.0f,0.5f);
         
        // draw quad
        /*GL11.glBegin(GL11.GL_QUADS);
            GL11.glVertex2f(100,100);
            GL11.glVertex2f(100+200,100);
            GL11.glVertex2f(100+200,100+200);
            GL11.glVertex2f(100,100+200);
        GL11.glEnd();*/
        
	    //render2d();
	    
	    GL11.glPopMatrix();
	    GL11.glEnable(GL11.GL_CULL_FACE);
	    GL11.glEnable(GL11.GL_LIGHTING);
	    GL11.glEnable(GL11.GL_DEPTH_TEST);
		Display.update();
	}
}
