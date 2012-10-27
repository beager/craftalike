package game;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import org.newdawn.slick.Color;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;
import org.newdawn.slick.opengl.Texture;

import profiling.Profiling;
import profiling.ProfilingPart;


public class Game {
	
	private static final float MOUSE_SPEED_SCALE = 0.1f;
	private static final float MOVEMENT_SPEED = 0.085f;
	private static final float MOVEMENT_SPEED_FLYMODE = 0.17f;
	private static final float FALSE_GRAVITY_SPEED = 0.035f;
	
	private static final int CHUNK_SIZE = 16;
	
	private static final boolean FULLSCREEN = false;
	private static final boolean VSYNC = true;
	private static final boolean TEXTURES = true;
	
	private UnicodeFont font;
	
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
		final int TERRAIN_SMOOTH_LEVEL = 10;
		
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
		try {
			font = new UnicodeFont("res/fonts/Minecraftia.ttf", 1, false, false);
		} catch (SlickException e1) {
			e1.printStackTrace();
		}
		font.addAsciiGlyphs();
		font.addGlyphs(400,400);
		font.getEffects().add(new ColorEffect(java.awt.Color.WHITE));
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
		AMBIENCE_COLOR.x = sinPos * 0.8f;
		AMBIENCE_COLOR.y = sinPos * 0.6f;
		AMBIENCE_COLOR.z = sinPos * 0.6f;
	}
	
	public void initGl() {
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glShadeModel(GL11.GL_SMOOTH); 
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc (GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
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
		font.drawString(10, 10, "GetDunkedOn", Color.green);
	}
	
	public void render() {
		Lighting.initLighting();
		Lighting.initFog();
		int width = Display.getDesktopDisplayMode().getWidth();
		int height = Display.getDesktopDisplayMode().getHeight();
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(true);
		
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		
		GL11.glLoadIdentity();
		GLU.gluPerspective(70.0f, (float)width / (float)height, 0.1f, 200.0f);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		
		GL11.glClearColor(AMBIENCE_COLOR.x, AMBIENCE_COLOR.y, AMBIENCE_COLOR.z + 0.2f, AMBIENCE_COLOR.a);
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
        GL11.glDepthMask(false);
        GL11.glPushMatrix();

        // set the color of the quad (R,G,B,A)
        GL11.glColor4f(0.5f,0.5f,1.0f,0.5f);
         
        // draw quad
        GL11.glBegin(GL11.GL_QUADS);
            GL11.glVertex2f(100,100);
            GL11.glVertex2f(100+200,100);
            GL11.glVertex2f(100+200,100+200);
            GL11.glVertex2f(100,100+200);
        GL11.glEnd();
        
	    render2d();
	    GL11.glPopMatrix();
	    GL11.glEnable(GL11.GL_LIGHTING);
		profiling.partBegin(displayUpdate);
		Display.update();
		profiling.partEnd(displayUpdate);
	}
}
