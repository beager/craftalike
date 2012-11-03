package game;

import game.blocks.Block;
import game.blocks.BlockManager;

import org.lwjgl.opengl.GL11;

public class Chunk {
	
	private static final int WATER_LEVEL = 64;

	// Size of the terrain measured in cubes
	public Vector3 arraySize;
	
	// Size of each cube
	public Vector3f cubeSize;
	
	// Optional translation
	public Vector3f translation;
	
	// The 3d array containing the cubes
	public int[][][] terrain;
	
	public int x;
	public int z;
	
	private int[][] heightData;
	
	private int displayList;

	public boolean released = false;
	
	public boolean isChanged = true;
	
	public Chunk(int x, int z, Vector3 arraySize, Vector3f cubeSize, Vector3f translation) {
		this.x = x;
		this.z = z;
		this.arraySize = arraySize;
		this.cubeSize = cubeSize;
		this.translation = translation;
		
		// Create the cube array
		terrain = new int[arraySize.x][arraySize.y][arraySize.z];
		
		for(int zz = 0; zz < arraySize.z; zz++) {
			for(int yy = 0; yy < arraySize.y; yy++) {
				for(int xx = 0; xx < arraySize.x; xx++) {
					terrain[xx][yy][zz] = -1;
				}
			}
		}
	}
	
	public void generateTerrain(int maxHeight, int minHeight, int seed, float noiseSize, float persistence, int octaves, boolean textures) {
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
					heightData[x][z] = (int) ((PerlinNoise2D.perlin2D(x + (int) translation.x, z + (int) translation.z, arraySize.x, arraySize.z, seed, noiseSize, persistence, octaves) * (maxHeight - minHeight) + minHeight)) + 56;
					if (heightData[x][z] > Game.CHUNK_HEIGHT - 1) heightData[x][z] = Game.CHUNK_HEIGHT - 1;
					if (heightData[x][z] < 1) heightData[x][z] = 1;
			}
		}
		// Create the cubes
		for(int z = 0; z < arraySize.z; z++) {
			for(int x = 0; x < arraySize.x; x++) {
				for(int y = heightData[x][z]; y >= 0; y--) {
					if (heightData[x][z] >= WATER_LEVEL + 1)
					{
						if (y == heightData[x][z]) {
							terrain[x][y][z] = BlockManager.TYPE_GRASS;
						} else if (y > heightData[x][z] - 3) {
							terrain[x][y][z] = BlockManager.TYPE_DIRT;
						} else {
							terrain[x][y][z] = BlockManager.TYPE_STONE;
						}	
					} else {
						if (y == heightData[x][z]) {
							terrain[x][y][z] = BlockManager.TYPE_SAND;
						} else if (y > heightData[x][z] - 3) {
							terrain[x][y][z] = BlockManager.TYPE_SAND;
						} else {
							terrain[x][y][z] = BlockManager.TYPE_STONE;
						}
					}
					
				}
				
				if (heightData[x][z] < WATER_LEVEL) {
					for (int y = WATER_LEVEL; y > heightData[x][z]; y--) {
						terrain[x][y][z] = BlockManager.TYPE_WATER;
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
						if ((terrain[x][y][z] > -1) && (terrain[x][y][z] != Block.TYPE_WATER))
							terrain[x][y][z] = -1;
					}
					
					if ((heightData[x][z] - y) > 3) {
						level = SimplexNoise.noise(
								(float) (x + translation.x) / 14f, 
								(float) (y + translation.y) / 7f, 
								(float) (z + translation.z) / 14f);
						if (level > (
								(SimplexNoise.noise(
									((float) x + translation.x) / 50,
									((float) z + translation.z) / 50
								) + 0.7d) 
							)
						) {
							if ((terrain[x][y][z] > -1) && (terrain[x][y][z] != Block.TYPE_WATER))
								terrain[x][y][z] = -1;
						}
					}
				}
			}
		}
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
			if((terrain[arrayCoordinates.x][arrayCoordinates.y][arrayCoordinates.z] > -1)
					&& (terrain[arrayCoordinates.x][arrayCoordinates.y][arrayCoordinates.z] != Block.TYPE_WATER)) {
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
			
			Chunk[] chunkArray = new Chunk[]{this};
			for(int z = 0; z < arraySize.z; z++) {
				for(int x = 0; x < arraySize.x; x++) {
					for(int y = 0; y < arraySize.y; y++) {
						if(terrain[x][y][z] > -1) {
							Game.blockManager.renderType(chunkArray, terrain[x][y][z], new Vector3(x,y,z));
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
			terrain[index.x][index.y][index.z] = -1;
			release();
			render();
		}
	}

	public void createBlockAt(Vector3f pos) {
		isChanged = true;
		Vector3 index = new Vector3(
			(int) Math.floor(((pos.x % Game.CHUNK_SIZE) + Game.CHUNK_SIZE) % Game.CHUNK_SIZE),
			(int) Math.floor(((pos.y % Game.CHUNK_HEIGHT) + Game.CHUNK_HEIGHT) % Game.CHUNK_HEIGHT),
			(int) Math.floor(((pos.z % Game.CHUNK_SIZE) + Game.CHUNK_SIZE) % Game.CHUNK_SIZE)
		);

		if ((index.x >= 0) && (index.y >= 0) && (index.z >= 0)) {
			terrain[index.x][index.y][index.z] = 1; // todo change
			release();
			render();
		}
	}

	public int getCubeTypeAtVector(Vector3f pos) {
		Vector3 index = new Vector3(
			(int) Math.floor(((pos.x % Game.CHUNK_SIZE) + Game.CHUNK_SIZE) % Game.CHUNK_SIZE),
			(int) Math.floor((pos.y + Game.CHUNK_HEIGHT) % Game.CHUNK_HEIGHT),
			(int) Math.floor(((pos.z % Game.CHUNK_SIZE) + Game.CHUNK_SIZE) % Game.CHUNK_SIZE)
		);

		return ((index.x >= 0) && (index.y >= 0) && (index.z >= 0) && (terrain[index.x][index.y][index.z] < 0)) ?
				terrain[index.x][index.y][index.z] : 0;
	}
	
}


