package game;

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
	
	public int x;
	public int z;
	
	// Textures
	private Texture stoneTexture;
	private Texture grassTexture;
	private Texture waterTexture;
	private Texture dirtTexture;
	private Texture dirtSideTexture;
	
	private TextureStore textureStore;
	private int[][] heightData;
	
	private int displayList;

	public boolean released = false;
	
	public CubeTerrain(int x, int z, Vector3 arraySize, Vector3f cubeSize, Vector3f translation) {
		this.x = x;
		this.z = z;
		this.arraySize = arraySize;
		this.cubeSize = cubeSize;
		this.translation = translation;
		
		// Create the cube array
		terrain = new Cube[arraySize.x][arraySize.y][arraySize.z];
		
		for(int zz = 0; zz < arraySize.z; zz++) {
			for(int yy = 0; yy < arraySize.y; yy++) {
				for(int xx = 0; xx < arraySize.x; xx++) {
					terrain[xx][yy][zz] = null;
				}
			}
		}
		
		stoneTexture = TextureStore.getTexture("res/pix-stone.png");
		grassTexture = TextureStore.getTexture("res/pix-grass.png");
		waterTexture = TextureStore.getTexture("res/pix-water.png");
		dirtTexture = TextureStore.getTexture("res/pix-dirt.png");
		dirtSideTexture = TextureStore.getTexture("res/pix-grass-side.png");
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
					heightData[x][z] = (int) (PerlinNoise2D.perlin2D(x + (int) translation.x, z + (int) translation.z, arraySize.x, arraySize.z, seed, 100.0f, 0.0001f, octaves) * (maxHeight - minHeight) + minHeight);
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
					} else {
						totalHeight += heightData[x][z];
						count++;
					}
					
					if(z < arraySize.z - 1) {
						totalHeight += heightData[x][z + 1];
						count++;
					} else {
						totalHeight += heightData[x][z];
						count++;
					}
					
					if(x > 0) {
						totalHeight += heightData[x - 1][z];
						count++;
					} else {
						totalHeight += heightData[x][z];
						count++;
					}
					
					if(x < arraySize.x - 1) {
						totalHeight += heightData[x + 1][z];
						count++;
					} else {
						totalHeight += heightData[x][z];
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
		
		displayList = GL11.glGenLists(1);
		GL11.glNewList(displayList, GL11.GL_COMPILE);
		
		for(int z = 0; z < arraySize.z; z++) {
			for(int x = 0; x < arraySize.x; x++) {
				for(int y = 0; y < arraySize.y; y++) {
					if(terrain[x][y][z] != null) {
						terrain[x][y][z].render();
					}
				}
			}
		}
		
		QuadQueue.renderAll();
		GL11.glEndList();
	}
	
	private Cube createCube(Vector3 arrayPosition, boolean textures) {
		// Calculate the coordinates
		Vector3f pos1 = new Vector3f(arrayPosition.x * cubeSize.x + translation.x, arrayPosition.y * cubeSize.y + translation.y, arrayPosition.z * cubeSize.z + translation.z);
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
			color = new Vector4f(0.3f, 0.3f, 0.3f, 1.0f);
			texture = grassTexture;
		}
		
		if(!textures)
			texture = null;
		
		return new Cube(pos1, pos2, color, texture);
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
	
	public void render() {
		// Call the display list
		if (!released)
			GL11.glCallList(displayList);
	}
	
	public void release() {
		GL11.glDeleteLists(displayList, 1);
		released = true;
	}
	
}


