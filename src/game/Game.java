package game;

import game.blocks.BlockManager;

import java.awt.Font;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.lwjgl.LWJGLException;
import org.lwjgl.LWJGLUtil;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.openal.AL;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.openal.SoundStore;
import org.newdawn.slick.opengl.TextureImpl;
import org.newdawn.slick.util.ResourceLoader;

public class Game {
	
	private static final float MOUSE_SPEED_SCALE = 0.1f;
	private static final float MOVEMENT_SPEED = 0.085f;
	private static final float MOVEMENT_SPEED_FLYMODE = 0.17f;
	private static final float FALSE_GRAVITY_SPEED = 0.035f;
	private static final float FOV = 90.0f;
	
	public static final int CHUNK_SIZE = 16;
	public static final int CHUNK_HEIGHT = 128;
	
	public static final int TEXT_LINE_HEIGHT = 18;
	
	public static int fps;
	public static int ups;
	
	private static final boolean FULLSCREEN = false;
	private static final boolean VSYNC = false;
	public static final boolean TEXTURES = true;
	
	private TrueTypeFont font;
	
	public static BlockManager blockManager = new BlockManager();
	
	public static Vector4f AMBIENCE_COLOR = new Vector4f(0.05f, 0.05f, 0.05f, 1.0f);
	
	private long startTime;
	
	// Game components
	private Camera camera;
	private ChunkManager terrain;
	
	// Toggles
	private boolean flyMode = false;
	private boolean doCollisionChecking = true;
	private boolean renderSkybox = false;
	private boolean wireframe = false;
	private boolean isPaused = false;
	
	private boolean closeRequested = false;
	private int displayFps;
	private int displayUps;
	private boolean debugScreen = false;
	public static long deltaTime;
	private boolean playMusic = true;
	private boolean pickBlock = false;
	private boolean placeBlock = false;
	private int mouseDelayer = 0;
	
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
		
		Vector3f startPos = new Vector3f(8.0f, 129.0f, 8.0f);

		// Create the camera
		camera = new Camera(startPos, new Vector3f(0.0f, 0.0f, 0.0f), terrain);

		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		
		// load font from a .ttf file
		try {
			InputStream inputStream	= ResourceLoader.getResourceAsStream("res/fonts/Minecraftia.ttf");
			
			Font awtFont = Font.createFont(Font.TRUETYPE_FONT, inputStream);
			awtFont = awtFont.deriveFont(16f); // set font size
			font = new TrueTypeFont(awtFont, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (playMusic) Music.playMusic();

		startTime = System.currentTimeMillis();

		long lastTime = System.nanoTime();
		long timer = System.currentTimeMillis();
		final double ns = 1000000000.0 / 60.0;
		double delta = 0;
		fps = 0;
		ups = 0;
		displayUps = 0;
		displayFps = 0;
		
		while(!Display.isCloseRequested()) {
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			while (delta >= 1) {
				if (!isPaused) {
					update();
				}

				ups++;
				SoundStore.get().poll(0);
				delta--;
			}
			render();
			fps++;
			
			if (System.currentTimeMillis() - timer > 1000) {
				timer += 1000;
				displayFps = fps;
				displayUps = ups;
				//frame.setTitle(Game.title + " | " + updates + " ups, " + frames + " fps");
				ups = 0;
				fps = 0;	
			}
			
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
					} else if (Keyboard.getEventKey() == Keyboard.KEY_ESCAPE) {
						isPaused = !isPaused;
						Mouse.setGrabbed(!Mouse.isGrabbed());
					} else if (Keyboard.getEventKey() == Keyboard.KEY_P) {
						debugScreen  = !debugScreen;
					} else if (Keyboard.getEventKey() == Keyboard.KEY_1) {
						ItemBox.setSelectedIndex(0);
					} else if (Keyboard.getEventKey() == Keyboard.KEY_2) {
						ItemBox.setSelectedIndex(1);
					} else if (Keyboard.getEventKey() == Keyboard.KEY_3) {
						ItemBox.setSelectedIndex(2);
					} else if (Keyboard.getEventKey() == Keyboard.KEY_4) {
						ItemBox.setSelectedIndex(3);
					} else if (Keyboard.getEventKey() == Keyboard.KEY_5) {
						ItemBox.setSelectedIndex(4);
					} else if (Keyboard.getEventKey() == Keyboard.KEY_6) {
						ItemBox.setSelectedIndex(5);
					} else if (Keyboard.getEventKey() == Keyboard.KEY_7) {
						ItemBox.setSelectedIndex(6);
					} else if (Keyboard.getEventKey() == Keyboard.KEY_8) {
						ItemBox.setSelectedIndex(7);
					} else if (Keyboard.getEventKey() == Keyboard.KEY_9) {
						ItemBox.setSelectedIndex(8);
					}
				}
			}
			
			if (closeRequested) break;
		}
		
		// Cleanup
		terrain.release();
		Display.destroy();
	}


	public static void main(String[] args) {
	        switch (LWJGLUtil.getPlatform()) {
	            case LWJGLUtil.PLATFORM_MACOSX:
	                addLibraryPath(new File("libs/lwjgl-2.8.4/native/macosx"));
	                break;
	            case LWJGLUtil.PLATFORM_LINUX:
	                addLibraryPath(new File("libs/lwjgl-2.8.4/native/linux"));
	                if (System.getProperty("os.arch").contains("64")) {
	                    System.loadLibrary("openal64");
	                } else {
	                    System.loadLibrary("openal");
	                }
	                break;
	            case LWJGLUtil.PLATFORM_WINDOWS:
	                addLibraryPath(new File("libs/lwjgl-2.8.4/native/windows"));

	                if (System.getProperty("os.arch").contains("64")) {
	                    System.loadLibrary("OpenAL64");
	                } else {
	                    System.loadLibrary("OpenAL32");
	                }
	                break;
	            default:
	                System.out.println("Unsupported operating system: {}");
	                System.exit(1);
	        }
		Game cubeGame = new Game();
		cubeGame.start();
	}
	
	public void finalize() {
		terrain.release();
		AL.destroy();
		Display.destroy();
	}
	
	public void update() {
		camera.hasGravitiedThisFrame = false;
		Lighting.setIsUnderwater(terrain.getCubeTypeAtVector(camera.coordinates) == BlockManager.TYPE_WATER);
		float movementSpeed = flyMode ? MOVEMENT_SPEED_FLYMODE : MOVEMENT_SPEED;
		if (Lighting.isUnderwater) movementSpeed *= 0.3;
		
			// Handle mouse movement
				camera.addRotation(new Vector3f(Mouse.getDY() * MOUSE_SPEED_SCALE, -Mouse.getDX() * MOUSE_SPEED_SCALE, 0.0f));
				
			// Handle keypresses
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
			
			// handle mouse clicks
			if (Mouse.isButtonDown(0) && (mouseDelayer == 0)) {
				pickBlock = true;
				mouseDelayer = 20;
			} else if (Mouse.isButtonDown(1) && (mouseDelayer == 0)) {
				placeBlock = true;
				mouseDelayer = 20;
			}
			
			//System.out.println(Mouse.getDWheel());
			
			int dwheel = Mouse.getDWheel();
			if (dwheel < 0) {
				ItemBox.setSelectedIndex(ItemBox.getSelectedIndex() + 1);
			} else if (dwheel > 0) {
				ItemBox.setSelectedIndex(ItemBox.getSelectedIndex() - 1);
			}
			if (mouseDelayer  > 0) mouseDelayer--;
			
		// Apply gravity
		if(!flyMode) {
			camera.move(0, Camera.FORWARD, camera.gravitySpeed, doCollisionChecking, flyMode);
			camera.updateGravity();
		}
		
		camera.updateLookingAt();
		
		terrain.updateVisibleChunks(camera.coordinates);
		
		long timeElapsed = System.currentTimeMillis() - startTime;
		float sinPos = (float) Math.sin((timeElapsed / 80000.0f) % (2.0f * Math.PI) );
		sinPos = (	(sinPos + 1) / 2);
		
		if (sinPos < 0.4f) sinPos = 0.4f;
		if (sinPos > 0.6f) sinPos = 0.6f;
		sinPos = (sinPos - 0.4f) * 4.0f + 0.2f;
		AMBIENCE_COLOR.x = sinPos - 0.4f;
		AMBIENCE_COLOR.y = sinPos - 0.4f;
		AMBIENCE_COLOR.z = sinPos - 0.2f;
		
		if (pickBlock) {
			pickBlock = false;
			if (camera.hasTarget)
				terrain.deleteBlockAt(camera.target);
		}
		
		if (placeBlock ) {
			placeBlock = false;
			if (camera.hasTarget)
				terrain.placeBlockAt(camera.coordinates, camera.target);
		}
	}
	
	public void initGl() {
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glShadeModel(GL11.GL_SMOOTH); 
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
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
	
	public void renderTextLine(int lineNum, String string) {
		renderTextLine(lineNum, string, 0);
	}
	
	public void renderTextLine(int lineNum, String string, int offsetX) {
		font.drawString(offsetX + 2, lineNum * TEXT_LINE_HEIGHT + 2, string, Color.black);
		font.drawString(offsetX, lineNum * TEXT_LINE_HEIGHT, string, Color.white);
	}
	
	public void render2d() {
		if (isPaused) {
			// set the color of the quad (R,G,B,A)
			GL11.glEnable(GL11.GL_BLEND);
	        GL11.glColor4f(0.3f, 0.3f, 0.3f,0.7f);
	         
	        // draw quad
	        GL11.glBegin(GL11.GL_QUADS);
	            GL11.glVertex2f(0,0);
	            GL11.glVertex2f(Display.getDesktopDisplayMode().getWidth(),0);
	            GL11.glVertex2f(Display.getDesktopDisplayMode().getWidth(),Display.getDesktopDisplayMode().getHeight());
	            GL11.glVertex2f(0,Display.getDesktopDisplayMode().getHeight());
	        GL11.glEnd();
	        
	        
	        
	        GL11.glDisable(GL11.GL_BLEND);
		}
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glShadeModel(GL11.GL_SMOOTH);
		
		ItemBox.render();
		
		TextureImpl.unbind();
		if (debugScreen) {
			int i = 0;
			renderTextLine(i++, "x: " + camera.coordinates.x );
			renderTextLine(i++, "y: " + camera.coordinates.y );
			renderTextLine(i++, "z: " + camera.coordinates.z );
			renderTextLine(i++, displayFps + " fps, " + displayUps + " ups");
			int totalMem = (int) Runtime.getRuntime().totalMemory() / (1000000);
			int usedMem = totalMem - ((int) Runtime.getRuntime().freeMemory() / (1000000));
			renderTextLine(i++, "mem/total: " + usedMem + "/" + totalMem);
			
		}
		
		
		
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		
		
	}
	
	public void render() {
		Lighting.initLighting();
		
		Lighting.initFog();
		int width = Display.getDesktopDisplayMode().getWidth();
		int height = Display.getDesktopDisplayMode().getHeight();
		
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		
		GL11.glLoadIdentity();
		
		float ratio = ((float)Display.getDisplayMode().getWidth())/((float)Display.getDisplayMode().getHeight());

		GLU.gluPerspective(FOV, ratio, 0.1f, 160f);
		
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		
		if (!Lighting.isUnderwater) {
			GL11.glClearColor(AMBIENCE_COLOR.x, AMBIENCE_COLOR.y, AMBIENCE_COLOR.z, AMBIENCE_COLOR.a);
		} else {
			GL11.glClearColor(AMBIENCE_COLOR.x - 0.7f, AMBIENCE_COLOR.y - 0.7f, AMBIENCE_COLOR.z - 0.6f, AMBIENCE_COLOR.a);
		}
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glPushMatrix();
		render3d();
		GL11.glPopMatrix();
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();

		GL11.glOrtho(0, width, height, 0, 1, -1);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		
        GL11.glDisable( GL11.GL_DEPTH_TEST );
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glPushMatrix();

	    render2d();
	    
	    GL11.glPopMatrix();
	    GL11.glEnable(GL11.GL_CULL_FACE);
	    GL11.glEnable(GL11.GL_LIGHTING);
	    GL11.glEnable(GL11.GL_DEPTH_TEST);
		Display.update();
	}
	
    private static void addLibraryPath(File libPath) {
        try {
            String envPath = System.getProperty("java.library.path");
            if (envPath == null || envPath.isEmpty()) {
                System.setProperty("java.library.path", libPath.getAbsolutePath());
            } else {
                System.setProperty("java.library.path", envPath + File.pathSeparator + libPath.getAbsolutePath());
            }

            final Field usrPathsField = ClassLoader.class.getDeclaredField("usr_paths");
            usrPathsField.setAccessible(true);

            List<String> paths = new ArrayList<String>(Arrays.asList((String[]) usrPathsField.get(null)));

            if (paths.contains(libPath.getAbsolutePath())) {
                return;
            }
            paths.add(0, libPath.getAbsolutePath()); // Add to beginning, to override system libraries

            usrPathsField.set(null, paths.toArray(new String[paths.size()]));
        } catch (Exception e) {
            System.out.println("Couldn't link static libraries.");
            System.exit(1);
        }
    }
}
