package game;
import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import java.awt.Font;
import java.awt.FontFormatException;

import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.util.ResourceLoader;

import profiling.Profiling;
import profiling.ProfilingPart;


public class Game {
	
	private static final float MOUSE_SPEED_SCALE = 0.1f;
	private static final float MOVEMENT_SPEED = 0.085f;
	private static final float MOVEMENT_SPEED_FLYMODE = 0.17f;
	private static final float FALSE_GRAVITY_SPEED = 0.035f;
	
	private static final int CHUNK_SIZE = 64;
	
	private static final boolean FULLSCREEN = false;
	private static final boolean VSYNC = true;
	private static final boolean TEXTURES = true;
	
	private long lastFrame;
	
	// Game components
	private Camera camera;
	private CubeTerrain terrain;
	private Skybox skybox;
	private TextureStore textureStore;
	
	// Toggles
	private boolean flyMode = false;
	private boolean doCollisionChecking = true;
	private boolean renderSkybox = false;
	private boolean wireframe = false;
	
	private boolean closeRequested = false;
	
	private TrueTypeFont font;
	
	// Profiling
	private Profiling profiling = new Profiling();
	private ProfilingPart displayUpdate = new ProfilingPart("DISPLAY UPDATE");
	private ProfilingPart inputHandling = new ProfilingPart("INPUT HANDLING");
	
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
		
		int width = Display.getDesktopDisplayMode().getWidth();
		int height = Display.getDesktopDisplayMode().getHeight();
		
		// Initialize OpenGL
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		
		GLU.gluPerspective(70.0f, (float)width / (float)height, 0.1f, 200.0f);
		
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
		// Set OpenGL options
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_LIGHTING);
		
		GL11.glShadeModel(GL11.GL_SMOOTH); 
		
		GL11.glBlendFunc (GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glEnable(GL11.GL_BLEND);
		
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		
		FloatBuffer lightAmbient = BufferUtils.createFloatBuffer(4);
		FloatBuffer lightDiffuse = BufferUtils.createFloatBuffer(4);
		FloatBuffer lightPosition = BufferUtils.createFloatBuffer(4);
		FloatBuffer lightDir = BufferUtils.createFloatBuffer(4);
		
		/* Set up the light */
	    // Ambient Light
	    lightAmbient.put(0, 0.7f);
	    lightAmbient.put(1, 0.7f);
	    lightAmbient.put(2, 0.7f);
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
	    lightDir.put(1, 0.0f);
	    lightDir.put(2, 0.0f);
	    lightDir.put(3, 1.0f);

	    GL11.glLight(GL11.GL_LIGHT1, GL11.GL_AMBIENT, lightAmbient); // Setup The Ambient Light
	    //GL11.glLight(GL11.GL_LIGHT1, GL11.GL_DIFFUSE, lightDiffuse); // Setup The Diffuse Light
	    GL11.glLight(GL11.GL_LIGHT1, GL11.GL_SPOT_DIRECTION, lightDir);
	    GL11.glLight(GL11.GL_LIGHT1, GL11.GL_POSITION, lightPosition); // Position The Light
	    GL11.glEnable(GL11.GL_LIGHT1); // Enable Light One
		
		// Hide the mouse
		Mouse.setGrabbed(true);
		
		textureStore = new TextureStore();
		
		Texture skyboxTexture = textureStore.getTexture("res/clouds.png");
		
		// Create the terrain
		terrain = new CubeTerrain(new Vector3(CHUNK_SIZE, 25, CHUNK_SIZE), new Vector3f(1.0f, 1.0f, 1.0f), new Vector3f(-25.0f, -40.0f, -25.0f), textureStore);
		
		final int TERRAIN_MAX_HEIGHT = 40;
		final int TERRAIN_MIN_HEIGHT = 8;
		final int TERRAIN_SMOOTH_LEVEL = 9;
		
		final int TERRAIN_GEN_SEED = 1024;
		final float TERRAIN_GEN_NOISE_SIZE = 2.0f;
		final float TERRAIN_GEN_PERSISTENCE = 0.25f;
		final int TERRAIN_GEN_OCTAVES = 1;
		
		terrain.generateTerrain(TERRAIN_MAX_HEIGHT, TERRAIN_MIN_HEIGHT, TERRAIN_SMOOTH_LEVEL,
								TERRAIN_GEN_SEED, TERRAIN_GEN_NOISE_SIZE, TERRAIN_GEN_PERSISTENCE, TERRAIN_GEN_OCTAVES, TEXTURES);
		
		// Create the camera
		camera = new Camera(new Vector3f(0.0f, 2.0f, 0.0f), new Vector3f(0.0f, 0.0f, 0.0f), terrain);
		
		// Create the skybox
		skybox = new Skybox(new Vector3f(-50.0f, -50.0f, -50.0f), new Vector3f(50.0f, 50.0f, 50.0f), null, skyboxTexture);
		
		InputStream inputStream = ResourceLoader.getResourceAsStream("res/fonts/Minecraftia.ttf");
		Font awtFont = null;
		
		try {
			awtFont = Font.createFont(Font.TRUETYPE_FONT, inputStream);
		} catch (FontFormatException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		awtFont = awtFont.deriveFont(24f);
		font = new TrueTypeFont(awtFont, false);
		
		// Fog, experimental
		GL11.glEnable(GL11.GL_FOG);
		FloatBuffer fogColor = BufferUtils.createFloatBuffer(4);
		fogColor.put(1.0f).put(1.0f).put(1.0f).put(1.0f).flip();
		GL11.glFogi(GL11.GL_FOG_MODE, GL11.GL_LINEAR);
		GL11.glFog(GL11.GL_FOG_COLOR, fogColor);
		//GL11.glFogf(GL11.GL_FOG_DENSITY, 0.1f);
		GL11.glHint(GL11.GL_FOG_HINT, GL11.GL_DONT_CARE);
		GL11.glFogf(GL11.GL_FOG_START, 10.0f);
		GL11.glFogf(GL11.GL_FOG_END, 50.0f);
		
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
			
		// Main loop
		lastFrame = System.currentTimeMillis();
		
		float frameTime = 0.0f;
		
		float frameRes = 1000.0f / 60.0f;
		
		while(!Display.isCloseRequested()) {
			// Calculate delta time
			
			long t = System.currentTimeMillis();
			float deltaTime = (t - lastFrame);
			
			//profiling.frameBegin();
			frameTime += deltaTime;
			if (frameTime > frameRes)
			{
				update();
				frameTime %= frameRes;
			}
			render();
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
		
		profiling.partBegin(inputHandling);
		
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
				camera.move(movementSpeed, Camera.LEFT, 0, doCollisionChecking, flyMode);
			if(Keyboard.isKeyDown(Keyboard.KEY_D))
				camera.move(movementSpeed, Camera.RIGHT, 0, doCollisionChecking, flyMode);
			if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
				if (camera.grounded) {
					camera.gravitySpeed = -0.25f;
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
		profiling.partEnd(inputHandling);
			
		// Apply gravity
		if(!flyMode) {
//			camera.move(0, Camera.FORWARD, FALSE_GRAVITY_SPEED, doCollisionChecking, flyMode);
			camera.move(0, Camera.FORWARD, camera.gravitySpeed, doCollisionChecking, flyMode);
			camera.updateGravity();
		}
		
		//profiling.frameEnd();
	
	}
	
	public void render() {
		// Clear the screen
		GL11.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glLoadIdentity();
		
		if(wireframe)
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
		else 
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
		
		// Apply the camera matrix
		camera.applyMatrix();
		
		// Render the terrain
		terrain.render();
			
		// Render the skybox
		if(renderSkybox)
			skybox.render();
		
		GL11.glPushMatrix();
		GL11.glColorMaterial(GL11.GL_FRONT_AND_BACK, GL11.GL_AMBIENT_AND_DIFFUSE);
		GL11.glEnable(GL11.GL_COLOR_MATERIAL);
		
		GL11.glColor3f(0.6f, 0.2f, 0.3f);
		
		GL11.glColor3f(1.0f, 1.0f, 1.0f);
		GL11.glDisable(GL11.GL_COLOR_MATERIAL);
		GL11.glPopMatrix();
		
		// Set title to debug info
		Display.setTitle("x: " + camera.coordinates.x + " y: " + camera.coordinates.y + " z: " + camera.coordinates.z +
				" xRot: " + camera.rotation.x + " yRot: " + camera.rotation.y + " zRot: " + camera.rotation.z);
		
		// Updates the display, also polls the mouse and keyboard
		profiling.partBegin(displayUpdate);
			Display.update();
		profiling.partEnd(displayUpdate);
		
		
	}

}
