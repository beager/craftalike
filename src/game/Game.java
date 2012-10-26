package game;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;
import org.newdawn.slick.opengl.Texture;

import profiling.Profiling;
import profiling.ProfilingPart;


public class Game {
	
	private UnicodeFont font;
	
	private static final float MOUSE_SPEED_SCALE = 0.1f;
	private static final float MOVEMENT_SPEED = 0.085f;
	private static final float MOVEMENT_SPEED_FLYMODE = 0.17f;
	private static final float FALSE_GRAVITY_SPEED = 0.035f;
	
	private static final int CHUNK_SIZE = 128;
	
	private static final boolean FULLSCREEN = false;
	private static final boolean VSYNC = true;
	private static final boolean TEXTURES = true;
	
	public static Vector4f AMBIENCE_COLOR = new Vector4f(0.05f, 0.05f, 0.05f, 1.0f);
	
	private long lastFrame;
	
	private long startTime;
	
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
	
	// Profiling
	private Profiling profiling = new Profiling();
	private ProfilingPart displayUpdate = new ProfilingPart("DISPLAY UPDATE");
	private ProfilingPart inputHandling = new ProfilingPart("INPUT HANDLING");
	
	@SuppressWarnings("unchecked")
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
		
		textureStore = new TextureStore();
		
		Texture skyboxTexture = textureStore.getTexture("res/clouds.png");
		
		// Create the terrain
		terrain = new CubeTerrain(new Vector3(CHUNK_SIZE, CHUNK_SIZE, CHUNK_SIZE), new Vector3f(1.0f, 1.0f, 1.0f), new Vector3f(-25.0f, -40.0f, -25.0f), textureStore);
		
		final int TERRAIN_MAX_HEIGHT = 40;
		final int TERRAIN_MIN_HEIGHT = 8;
		final int TERRAIN_SMOOTH_LEVEL = 5;
		
		final int TERRAIN_GEN_SEED = 1024;
		final float TERRAIN_GEN_NOISE_SIZE = 2.0f;
		final float TERRAIN_GEN_PERSISTENCE = 0.25f;
		final int TERRAIN_GEN_OCTAVES = 4;
		
		terrain.generateTerrain(TERRAIN_MAX_HEIGHT, TERRAIN_MIN_HEIGHT, TERRAIN_SMOOTH_LEVEL,
								TERRAIN_GEN_SEED, TERRAIN_GEN_NOISE_SIZE, TERRAIN_GEN_PERSISTENCE, TERRAIN_GEN_OCTAVES, TEXTURES);
		
		// Create the camera
		camera = new Camera(new Vector3f(-20.0f, -5.0f, -20.0f), new Vector3f(0.0f, 0.0f, 0.0f), terrain);
		
		// Create the skybox
		skybox = new Skybox(new Vector3f(-50.0f, -50.0f, -50.0f), new Vector3f(50.0f, 50.0f, 50.0f), null, skyboxTexture);
		
		//fonts
		java.awt.Font awtFont = new java.awt.Font("Courier New", java.awt.Font.BOLD, 18);
        font = new UnicodeFont(awtFont);
        font.getEffects().add(new ColorEffect(java.awt.Color.white));
        font.addAsciiGlyphs();
        try {
            font.loadGlyphs();
        } catch (SlickException e) {
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
			float deltaTime = (t - lastFrame);
			
			//profiling.frameBegin();
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
		profiling.partEnd(inputHandling);
			
		// Apply gravity
		if(!flyMode) {
			camera.move(0, Camera.FORWARD, camera.gravitySpeed, doCollisionChecking, flyMode);
			camera.updateGravity();
		}
		
		long timeElapsed = System.currentTimeMillis() - startTime;
		float sinPos = (float) Math.sin((timeElapsed / 80000.0f) % (2.0f * Math.PI) );
		sinPos = (	(sinPos + 1) / 2);
		
		if (sinPos < 0.4f) sinPos = 0.4f;
		if (sinPos > 0.6f) sinPos = 0.6f;
		sinPos = (sinPos - 0.4f) * 4.0f + 0.1f;
		AMBIENCE_COLOR.x = sinPos;
		AMBIENCE_COLOR.y = sinPos;
		AMBIENCE_COLOR.z = sinPos;
		//profiling.frameEnd();
	}
	
	public void initGl() {
		int width = Display.getDesktopDisplayMode().getWidth();
		int height = Display.getDesktopDisplayMode().getHeight();
		
		// Initialize OpenGL
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		
		// Set FOV
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
	}
	
	public void render3d() {
		Lighting.initLighting();
		Lighting.initFog();
		
		GL11.glClearColor(AMBIENCE_COLOR.x, AMBIENCE_COLOR.y, AMBIENCE_COLOR.z + 0.2f, AMBIENCE_COLOR.a);
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
		
		GL11.glColor3f(AMBIENCE_COLOR.x, AMBIENCE_COLOR.y, AMBIENCE_COLOR.z);
		
		//GL11.glColor3f(1.0f, 1.0f, 1.0f);
		GL11.glDisable(GL11.GL_COLOR_MATERIAL);
		
		
		// Set title to debug info
		//Display.setTitle("x: " + camera.coordinates.x + " y: " + camera.coordinates.y + " z: " + camera.coordinates.z +
		//		" xRot: " + camera.rotation.x + " yRot: " + camera.rotation.y + " zRot: " + camera.rotation.z);
		
		// Updates the display, also polls the mouse and keyboard
		
		
	}
	
	public void render2d() {
		int width = Display.getDesktopDisplayMode().getWidth();
		int height = Display.getDesktopDisplayMode().getHeight();
		GL11.glOrtho(0, width, 0, height, 0, 300);
		font.drawString(10, 10, "GetDunkedOn");	
		
	}
	
	public void render() {
		render3d();
		
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		//GL11.glLoadMatrix();
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		GL11.glDisable(GL11.GL_LIGHTING);
		// do things here with 2D
		GL11.glEnable(GL11.GL_BLEND); 
	    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA); 

	    render2d();
	    
	    GL11.glDisable(GL11.GL_BLEND);
		GL11.glPushMatrix();
		GL11.glPopMatrix();
		// end do things here with 2D
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glPopMatrix();
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		
		GL11.glPopMatrix();
		
		
		
		profiling.partBegin(displayUpdate);
		Display.update();
		profiling.partEnd(displayUpdate);
	}

}
