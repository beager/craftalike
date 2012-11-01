package game;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;

public class Chunk {
	
	private static final int WATER_LEVEL = 6;

	// Size of the terrain measured in cubes
	public Vector3 arraySize;
	
	// Size of each cube
	public Vector3f cubeSize;
	
	// Optional translation
	private Vector3f translation;
	
	private int smoothLevel;
	
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
	private Texture sandTexture;
	private Texture woodPlanksTexture;
	
	private int[][] heightData;
	
	private int[][][] simplexData;
	
	private int displayList;

	public boolean released = false;
	
	public boolean isChanged = true;

	private Texture brickTexture;

	private Texture snowTexture;

	private Texture concreteTexture;
	
	public Chunk(int x, int z, Vector3 arraySize, Vector3f cubeSize, Vector3f translation) {
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
		sandTexture = TextureStore.getTexture("res/pix-sand.png");
		woodPlanksTexture = TextureStore.getTexture("res/pix-planks.png");
		brickTexture = TextureStore.getTexture("res/pix-brick.png");
		snowTexture = TextureStore.getTexture("res/pix-snow.png");
		concreteTexture = TextureStore.getTexture("res/pix-concrete.png");
	}
	
	public void generateTerrain(int maxHeight, int minHeight, int smoothLevel, int seed, float noiseSize, float persistence, int octaves, boolean textures) {
		// Stores the height of each x, z coordinate
		this.smoothLevel = smoothLevel;
		int perlinSizeZ = arraySize.z + smoothLevel * 2;
		int perlinSizeX = arraySize.x + smoothLevel * 2;
		heightData = new int[perlinSizeX][perlinSizeZ];
		
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
		for(int z = 0; z < perlinSizeZ; z++) {
			for(int x = 0; x < perlinSizeX; x++) {
					heightData[x][z] = (int) (PerlinNoise2D.perlin2D(x + (int) translation.x, z + (int) translation.z, arraySize.x, arraySize.z, seed, noiseSize, persistence, octaves) * (maxHeight - minHeight) + minHeight);
					if (heightData[x][z] > Game.CHUNK_HEIGHT - 1) heightData[x][z] = Game.CHUNK_HEIGHT - 1;
					if (heightData[x][z] < 1) heightData[x][z] = 1;
			}
		}
		
		
		
		
		
		int smoothDecrementor = smoothLevel;
		
		// Smoothen the terrain
		while(smoothDecrementor > 0) {
			for(int z = 1; z < perlinSizeZ; z += 1) {
				for(int x = 1; x < perlinSizeX; x += 1) {
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
			
			smoothDecrementor--;
		}
		
		// Create the cubes
		for(int z = smoothLevel; z < smoothLevel + arraySize.z; z++) {
			for(int x = smoothLevel; x < smoothLevel + arraySize.x; x++) {
				for(int y = heightData[x][z]; y >= 0; y--) {
					terrain[x - smoothLevel][y][z - smoothLevel] = createCube(new Vector3(x - smoothLevel, y, z - smoothLevel), textures);
				}
				
				if (heightData[x][z] < WATER_LEVEL) {
					for (int y = WATER_LEVEL; y > heightData[x][z]; y--) {
						terrain[x - smoothLevel][y][z - smoothLevel] = createWaterCube(new Vector3(x - smoothLevel, y, z - smoothLevel), textures);
					}
					heightData[x][z] = WATER_LEVEL;
				}
			}
		}
		
		for(int z = 0; z < arraySize.z; z++) {
			for(int y = 1; y < arraySize.y; y++) {
				for (int x = 0; x < arraySize.x; x++) {
					double level = SimplexNoise.noise(
							(float) (x + translation.x) / 30f, 
							(float) (y + translation.y) / 30f, 
							(float) (z + translation.z) / 30f);
					if (level > 0.8d) {
						if ((terrain[x][y][z] != null) && (terrain[x][y][z].type != Cube.TYPE_WATER))
							terrain[x][y][z] = null;
					}
					
					if ((heightData[x][z] - y) > 3) {
						level = SimplexNoise.noise(
								(float) (x + translation.x) / 14f, 
								(float) (y + translation.y) / 7f, 
								(float) (z + translation.z) / 14f);
						if (level > (
								(SimplexNoise.noise(
									((float) x + translation.x) / 100,
									((float) z + translation.z) / 100
								) + 1) / 2 
							)
						) {
							if ((terrain[x][y][z] != null) && (terrain[x][y][z].type != Cube.TYPE_WATER))
								terrain[x][y][z] = null;
						}
					}
				}
			}
		}
		
		calculateVisibleSides();
	}
		
	private void calculateVisibleSides() {

		// Calculate which sides each cube needs to render
		for(int z = 0; z < arraySize.z; z++) {
			for(int x = 0; x < arraySize.x; x++) {
				for(int y = heightData[x + smoothLevel][z + smoothLevel]; y >= 0; y--) {
					boolean renderTop = false;
					boolean renderBottom = false;
					
					boolean renderFront = false;
					boolean renderBack = false;
					boolean renderRight = false;
					boolean renderLeft = false;
					if (terrain[x][y][z] != null)
					{
						if (terrain[x][y][z].type == Cube.TYPE_WATER) {
							if (y == WATER_LEVEL) {
								renderTop = true;
							}
						} else {
							if (y == arraySize.y - 1) {
								renderTop = true;
							} else if ((terrain[x][y + 1][z] != null) && (terrain[x][y+1][z].type == Cube.TYPE_WATER)) {
								renderTop = true;
							} else if (terrain[x][y+1][z] != null) {
								renderTop = (terrain[x][y + 1][z].type == Cube.TYPE_WATER);
							} else {
								renderTop = true;
							}
							
							if (y == 0) {
								renderBottom = true;
							} else if ((terrain[x][y - 1][z] != null) && (terrain[x][y - 1][z].type == Cube.TYPE_WATER)) {
								renderBottom = true;
							} else if (terrain[x][y - 1][z] != null) {
								renderBottom = (terrain[x][y - 1][z].type == Cube.TYPE_WATER);
							} else {
								renderBottom = true;
							}
							
							if (z == arraySize.z - 1) {
								renderFront = true;
							} else if (terrain[x][y][z + 1] != null) {
								renderFront = (terrain[x][y][z + 1].type == Cube.TYPE_WATER);
							} else {
								renderFront = true;
							}
							
							if (z == 0) {
								renderBack = true;
							} else if (terrain[x][y][z - 1] != null) {
								renderBack = (terrain[x][y][z - 1].type == Cube.TYPE_WATER);
							} else {
								renderBack = true;
							}
							
							if (x == arraySize.x - 1) {
								renderRight = true;
							} else if (terrain[x + 1][y][z] != null) {
								renderRight = (terrain[x + 1][y][z].type == Cube.TYPE_WATER);
							} else {
								renderRight = true;
							}
							
							if (x == 0) {
								renderLeft = true;
							} else if (terrain[x - 1][y][z] != null) {
								renderLeft = (terrain[x - 1][y][z].type == Cube.TYPE_WATER);
							} else {
								renderLeft = true;
							}
						}
						
						terrain[x][y][z].setVisibleSides(renderTop, renderBottom, renderFront, renderBack, renderRight, renderLeft);
					}
				}
			}
		}
		

	}
	
	private Cube createCube(Vector3 arrayPosition, boolean textures) {
		// Calculate the coordinates
		Vector3f pos1 = new Vector3f(arrayPosition.x * cubeSize.x + translation.x, arrayPosition.y * cubeSize.y + translation.y, arrayPosition.z * cubeSize.z + translation.z);
		Vector3f pos2 = Vector3f.add(pos1, cubeSize);
		
		// Set texture depending on y
		Vector4f color = null;
		Texture texture = null;
		int type = 0;
		
		if ((arrayPosition.y < 7) && (heightData[arrayPosition.x][arrayPosition.z] - arrayPosition.y < 3)) {
				// Sand
				color = new Vector4f(0.3f, 0.3f, 0.3f, 1.0f);
				texture = sandTexture;
				type = Cube.TYPE_SAND;

		} else if (heightData[arrayPosition.x][arrayPosition.z] - arrayPosition.y < 3) {
			if (arrayPosition.y == heightData[arrayPosition.x][arrayPosition.z]) {
				color = new Vector4f(0.3f, 0.3f, 0.3f, 1.0f);
				texture = grassTexture;
				type = Cube.TYPE_GRASS;
				return new MultiTextureCube(pos1, pos2, color, type, grassTexture, dirtTexture,
						dirtSideTexture, dirtSideTexture, dirtSideTexture, dirtSideTexture);
			} else {
				color = new Vector4f(0.3f, 0.3f, 0.3f, 1.0f);
				texture = dirtTexture;
				type = Cube.TYPE_DIRT;
			}
		} else {
			color = new Vector4f(0.3f, 0.3f, 0.3f, 1.0f);
			texture = concreteTexture;
			type = Cube.TYPE_STONE;
		}
		
		if(!textures)
			texture = null;
		
		return new Cube(pos1, pos2, color, type, texture);
	}
	
	private Cube createWaterCube(Vector3 arrayPosition, boolean textures) {
		Vector3f pos1 = new Vector3f(arrayPosition.x * cubeSize.x + translation.x, arrayPosition.y * cubeSize.y * 31 / 32 + translation.y, arrayPosition.z * cubeSize.z + translation.z);
		Vector3f pos2 = Vector3f.add(pos1, cubeSize);
		
		return new Cube(pos1, pos2, new Vector4f(0.3f, 0.3f, 0.3f, 1.0f), Cube.TYPE_WATER, waterTexture);
	}
	
	private Cube createSpecificCube(Vector3 arrayPosition, Texture texture) {
		Vector3f pos1 = new Vector3f(arrayPosition.x * cubeSize.x + translation.x, arrayPosition.y * cubeSize.y + translation.y, arrayPosition.z * cubeSize.z + translation.z);
		Vector3f pos2 = Vector3f.add(pos1, cubeSize);
		
		return new Cube(pos1, pos2, new Vector4f(0.3f, 0.3f, 0.3f, 1.0f), 0, texture);
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
			if((terrain[arrayCoordinates.x][arrayCoordinates.y][arrayCoordinates.z] != null)
					&& (terrain[arrayCoordinates.x][arrayCoordinates.y][arrayCoordinates.z].type != Cube.TYPE_WATER)) {
				return true;
			}
		}
		
		return false;
	}
	
	public void render() {
		// Call the display list
		released = false;
		if (isChanged)
		{
			isChanged = false;
			release();
			released = false;
		
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
		if (!released)
			GL11.glCallList(displayList);
	}
	
	public void release() {
		GL11.glDeleteLists(displayList, 1);
		released = true;
	}

	public void deleteBlockAt(Vector3f pos) {
		isChanged = true;
		Vector3 index = new Vector3(
			(int) Math.floor(((pos.x % Game.CHUNK_SIZE) + Game.CHUNK_SIZE) % Game.CHUNK_SIZE),
			(int) Math.floor(((pos.y % Game.CHUNK_HEIGHT) + Game.CHUNK_HEIGHT) % Game.CHUNK_HEIGHT),
			(int) Math.floor(((pos.z % Game.CHUNK_SIZE) + Game.CHUNK_SIZE) % Game.CHUNK_SIZE)
		);
		
		if ((index.x >= 0) && (index.y >= 0) && (index.z >= 0)) {
			terrain[index.x][index.y][index.z] = null;
			release();
			calculateVisibleSides();
			render();
		}
	}

	public void createBlockAt(Vector3f pos) {
		// TODO Auto-generated method stub
		isChanged = true;
		Vector3 index = new Vector3(
			(int) Math.floor(((pos.x % Game.CHUNK_SIZE) + Game.CHUNK_SIZE) % Game.CHUNK_SIZE),
			(int) Math.floor(((pos.y % Game.CHUNK_HEIGHT) + Game.CHUNK_HEIGHT) % Game.CHUNK_HEIGHT),
			(int) Math.floor(((pos.z % Game.CHUNK_SIZE) + Game.CHUNK_SIZE) % Game.CHUNK_SIZE)
		);

		if ((index.x >= 0) && (index.y >= 0) && (index.z >= 0)) {
			terrain[index.x][index.y][index.z] = createSpecificCube(index, brickTexture);
			release();
			calculateVisibleSides();
			render();
		}
	}

	public int getCubeTypeAtVector(Vector3f pos) {
		Vector3 index = new Vector3(
			(int) Math.floor(((pos.x % Game.CHUNK_SIZE) + Game.CHUNK_SIZE) % Game.CHUNK_SIZE),
			(int) Math.floor(((pos.y % Game.CHUNK_HEIGHT) + Game.CHUNK_HEIGHT) % Game.CHUNK_HEIGHT),
			(int) Math.floor(((pos.z % Game.CHUNK_SIZE) + Game.CHUNK_SIZE) % Game.CHUNK_SIZE)
		);

		return ((index.x >= 0) && (index.y >= 0) && (index.z >= 0) && (terrain[index.x][index.y][index.z] != null)) ?
				terrain[index.x][index.y][index.z].type : 0;
	}
	
}


