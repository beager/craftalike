package game;
import java.util.Random;

import obstacle.SpruceObstacle;
import obstacle.TreeObstacle;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;


public class CubeTerrain {
	
	// Size of the terrain measured in cubes
	public Vector3 arraySize;
	
	// Size of each cube
	public Vector3f cubeSize;
	
	// Optional translation
	private Vector3f translation;
	
	// The 3d array containing the cubes
	public Cube[][][] terrain;
	
	// Textures
	private Texture stoneTexture;
	private Texture grassTexture;
	private Texture waterTexture;
	private Texture dirtTexture;
	private Texture dirtSideTexture;
	
	// Display list
	private int displayList;
	
	private TextureStore textureStore;
	private int[][] heightData;
	
	public CubeTerrain(Vector3 arraySize, Vector3f cubeSize, Vector3f translation, TextureStore textureStore) {
		this.arraySize = arraySize;
		this.cubeSize = cubeSize;
		this.translation = translation;
		this.textureStore = textureStore;
		
		// Create the cube array
		terrain = new Cube[arraySize.x][arraySize.y][arraySize.z];
		
		for(int z = 0; z < arraySize.z; z++) {
			for(int y = 0; y < arraySize.y; y++) {
				for(int x = 0; x < arraySize.x; x++) {
					terrain[x][y][z] = null;
				}
			}
		}
		
		stoneTexture = textureStore.getTexture("res/pix-stone.png");
		grassTexture = textureStore.getTexture("res/pix-grass.png");
		waterTexture = textureStore.getTexture("res/pix-water.png");
		dirtTexture = textureStore.getTexture("res/pix-dirt.png");
		dirtSideTexture = textureStore.getTexture("res/pix-grass-side.png");
	}
	
	public void generateTerrain(int maxHeight, int minHeight, int smoothLevel, int seed, float noiseSize, float persistence, int octaves, boolean textures) {
		// Stores the height of each x, z coordinate
		heightData = new int[arraySize.x][arraySize.z];
		
		// Make sure maxHeight and minHeight are within bounds of the cube array
		if(maxHeight > arraySize.y)
			maxHeight = arraySize.y;
		
		if(maxHeight < 0)
			maxHeight = 0;
		
		if(minHeight > arraySize.y)
			minHeight = arraySize.y;
		
		if(minHeight < 0)
			minHeight = 0;
		
		// Randomize the heights using Perlin noise
		for(int z = 0; z < arraySize.z; z++) {
			for(int x = 0; x < arraySize.x; x++) {
					heightData[x][z] = (int) (PerlinNoise2D.perlin2D(x, z, arraySize.x, arraySize.z, seed, 100.0f, 0.0001f, octaves) * (maxHeight - minHeight) + minHeight);
			}
		}
		
		// Smoothen the terrain
		while(smoothLevel > 0) {
			for(int z = 1; z < arraySize.z; z += 1) {
				for(int x = 1; x < arraySize.x; x += 1) {
					float totalHeight = 0.0f;
					float count = 0;
					
					if(z > 0) {
						totalHeight += heightData[x][z - 1];
						count++;
					}
					
					if(z < arraySize.z - 1) {
						totalHeight += heightData[x][z + 1];
						count++;
					}
					
					if(x > 0) {
						totalHeight += heightData[x - 1][z];
						count++;
					}
					
					if(x < arraySize.x - 1) {
						totalHeight += heightData[x + 1][z];
						count++;
					}
					
					heightData[x][z] = Math.round(totalHeight / count);
				}
			}
			
			smoothLevel--;
		}
		
		// Create the cubes
		for(int z = 0; z < arraySize.z; z++) {
			for(int x = 0; x < arraySize.x; x++) {
				for(int y = heightData[x][z]; y >= 0; y--) {
					terrain[x][y][z] = createCube(new Vector3(x, y, z), textures);
				}
			}
		}
		
		Random rand = new Random();
		
		// Create tree obstacles
		TreeObstacle treeGen = new TreeObstacle(this, textureStore);
		int treeCount = 5;
		
		for(int treeIndex = 0; treeIndex < treeCount; treeIndex++) {
			// Select a random position on the terrain
			int x = rand.nextInt(arraySize.x);
			int z = rand.nextInt(arraySize.z);
			int y = heightData[x][z];
			
			// Create the tree
			treeGen.createTree(textures);
			treeGen.placeObstacle(new Vector3(x, y, z), false);
		}
		
		// Create spruce obstacles
		SpruceObstacle spruceGen = new SpruceObstacle(this, textureStore);
		int spruceCount = 5;
		
		for(int spruceIndex = 0; spruceIndex < spruceCount; spruceIndex++) {
			// Select a random position on the terrain
			int x = rand.nextInt(arraySize.x);
			int z = rand.nextInt(arraySize.z);
			int y = heightData[x][z];
			
			// Create the spruce
			spruceGen.createSpruce(textures);
			spruceGen.placeObstacle(new Vector3(x, y, z), false);
		}
		
		// Calculate which sides each cube needs to render
		for(int z = 0; z < arraySize.z; z++) {
			for(int x = 0; x < arraySize.x; x++) {
				for(int y = heightData[x][z]; y >= 0; y--) {
					boolean renderTop = (y == heightData[x][z]) || (y == 0);
					boolean renderBottom = (y == 0) || (y == 3);
					boolean renderFront = (z == arraySize.z - 1) || (terrain[x][y][z + 1] == null);
					boolean renderBack = (z == 0) || (terrain[x][y][z - 1] == null);
					boolean renderRight = (x == arraySize.x - 1) || (terrain[x + 1][y][z] == null);
					boolean renderLeft = (x == 0) || (terrain[x - 1][y][z] == null);
					
					terrain[x][y][z].setVisibleSides(renderTop, renderBottom, renderFront, renderBack, renderRight, renderLeft);
				}
			}
		}
		
		// Create the display list
		displayList = GL11.glGenLists(1);
		GL11.glNewList(displayList, GL11.GL_COMPILE);
		
		for(int z = 0; z < arraySize.z; z++) {
			for(int x = 0; x < arraySize.x; x++) {
				for(int y = 0; y < arraySize.y; y++) {
					if(terrain[x][y][z] != null)
						terrain[x][y][z].render();
				}
			}
		}
		
		QuadQueue.renderAll();
		
		GL11.glEndList();
	}
	
	private Cube createCube(Vector3 arrayPosition, boolean textures) {
		// Calculate the coordinates
		Vector3f pos1 = new Vector3f(arrayPosition.x * cubeSize.x, arrayPosition.y * cubeSize.y, arrayPosition.z * cubeSize.z);
		Vector3f pos2 = Vector3f.add(pos1, cubeSize);
		
		// Set texture depending on y
		Vector4f color = null;
		Texture texture = null;
		
		if(arrayPosition.y == 0) {
			// Dirt
			color = new Vector4f(0.3f, 0.3f, 0.3f, 1.0f);
			texture = stoneTexture;
		} else if(arrayPosition.y < 3) {
			// Water
			color = new Vector4f(0.0f, 0.2f, 0.7f, 0.6f);
			texture = waterTexture;
		} else if (arrayPosition.y < 7) {
			// Stone
			color = new Vector4f(0.3f, 0.3f, 0.3f, 1.0f);
			texture = stoneTexture;
		} else {
			if (heightData[arrayPosition.x][arrayPosition.z] > arrayPosition.y){
				// Dirt
				color = new Vector4f(0.3f, 0.3f, 0.3f, 1.0f);
				texture = dirtTexture;
			} else {
				color = new Vector4f(0.2f, 0.4f, 0.1f, 1.0f);
				texture = grassTexture;
				return new MultiTextureCube(pos1, pos2, color, texture, dirtTexture, dirtSideTexture, dirtSideTexture, dirtSideTexture, dirtSideTexture);
			}
		}
		
		if(!textures)
			texture = null;
		
		return new Cube(pos1, pos2, color, texture);
	}
	
	public void render() {
		// Save the current matrix
		GL11.glPushMatrix();
		
		// Add the translation matrix
		GL11.glTranslatef(translation.x, translation.y, translation.z);
		
		// Call the display list
		GL11.glCallList(displayList);
		
		// Restore the matrix
		GL11.glPopMatrix();
	}
	
	/* Returns true if there is a solid cube at the given coordinates. */
	public boolean solidAt(Vector3f coordinates) {
		// Get the cube coordinates in the array
		Vector3 arrayCoordinates = new Vector3((int)((coordinates.x - translation.x) / cubeSize.x), (int)((coordinates.y - translation.y) / cubeSize.y), (int)((coordinates.z - translation.z) / cubeSize.z));
		
		// Is this within the array bounds?
		if(arrayCoordinates.x >= 0 && arrayCoordinates.x < arraySize.x &&
			arrayCoordinates.y >= 0 && arrayCoordinates.y < arraySize.y &&
			arrayCoordinates.z >= 0 && arrayCoordinates.z < arraySize.z) {
			// Is there a cube at this coordinate?
			if(terrain[arrayCoordinates.x][arrayCoordinates.y][arrayCoordinates.z] != null) {
				return true;
			}
		}
		
		return false;
	}
	
	public void release() {
		GL11.glDeleteLists(displayList, 1);
	}
}


