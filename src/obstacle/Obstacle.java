package obstacle;

import game.Cube;
import game.CubeTerrain;
import game.TextureStore;
import game.Vector3;
import game.Vector3f;

public abstract class Obstacle {

	protected CubeTerrain terrain;
	protected TextureStore textureStore;
	protected Cube[][][] obstacleArray;
	protected int xLength, yLength, zLength;
	
	public Obstacle(CubeTerrain terrain, TextureStore textureStore) {
		this.terrain = terrain;
		this.textureStore = textureStore;
	}
	
	public boolean placeObstacle(Vector3 position, boolean spaceMustBeEmpty) {
		// Make sure it fits the terrain bounds
		if(position.x >= 0 && position.x + xLength < terrain.arraySize.x &&
		   position.y >= 0 && position.y + yLength < terrain.arraySize.y &&
		   position.z >= 0 && position.z + zLength < terrain.arraySize.z) {
			
			if(spaceMustBeEmpty) {
				// Make sure the space where it should be put is empty
				for(int x = 0; x < xLength; x++) {
					for(int y = 0; y < yLength; y++) {
						for(int z = 0; z < zLength; z++) {
							if(obstacleArray[x][y][z] != null && terrain.terrain[position.x + x][position.y + y][position.z + z] != null) {
								return false;
							}
						}
					}
				}
			}
			
			// Place the obstacle
			for(int x = 0; x < xLength; x++) {
				for(int y = 0; y < yLength; y++) {
					for(int z = 0; z < zLength; z++) {
						if(obstacleArray[x][y][z] != null) {
							terrain.terrain[position.x + x][position.y + y][position.z + z] = obstacleArray[x][y][z];
							terrain.terrain[position.x + x][position.y + y][position.z + z].pos1 = new Vector3f((position.x + x) * terrain.cubeSize.x, (position.y + y) * terrain.cubeSize.y, (position.z + z) * terrain.cubeSize.z);
							terrain.terrain[position.x + x][position.y + y][position.z + z].pos2 = Vector3f.add(terrain.terrain[position.x + x][position.y + y][position.z + z].pos1, terrain.cubeSize);
						}
					}
				}
			}
			
			return true;
		}

		return false;
	}
}
