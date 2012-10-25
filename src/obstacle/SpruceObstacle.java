package obstacle;

import org.newdawn.slick.opengl.Texture;

import game.Cube;
import game.CubeTerrain;
import game.TextureStore;
import game.Vector4f;

public class SpruceObstacle extends Obstacle {
	
	private Texture trunkTexture;
	private Texture leavesTexture;

	public SpruceObstacle(CubeTerrain terrain, TextureStore textureStore) {
		super(terrain, textureStore);
	}
	
	public void createSpruce(boolean textures) {
		// Specify size
		xLength = 7;
		zLength = 7;
		yLength = 8;
		
		// Create array
		obstacleArray = new Cube[xLength][yLength][zLength];
		
		trunkTexture = textureStore.getTexture("res/pix-wood.png");
		leavesTexture = textureStore.getTexture("res/pix-leaves.png");
		
		for(int x = 0; x < xLength; x++) {
			for(int y = 0; y < yLength; y++) {
				for(int z = 0; z < zLength; z++) {
					obstacleArray[x][y][z] = null;
				}
			}
		}
		
		// Create spruce "crown"
		for(int y = 3; y < yLength; y++) {
			for(int x = y - 3; x < xLength - (y - 3); x++) {
				for(int z = y - 3; z < zLength - (y - 3); z++) {
					obstacleArray[x][y][z] = new Cube(null, null, new Vector4f(0.0f, 0.20f, 0.04f, 1.0f), leavesTexture);
				}
			}
		}
		
		// Create stem
		for(int y = 0; y < yLength - 2; y++) {
			obstacleArray[xLength/2][y][zLength/2] = new Cube(null, null, new Vector4f(0.25f, 0.125f, 0.0f, 1.0f), trunkTexture);
		}
	}
	
}
