package game.blocks;

import game.Chunk;
import game.Game;
import game.QuadQueue;
import game.TextureStore;
import game.Vector3;
import game.Vector3f;

import org.newdawn.slick.opengl.Texture;

public class Block {
	
	public static int type = Block.TYPE_NONE;
	
	public boolean isSolid = true;
	
	public static int TYPE_NONE = -1;
	public static int TYPE_DIRT = 0;
	public static int TYPE_GRASS = 1;
	public static int TYPE_WATER = 2;
	public static int TYPE_STONE = 3;
	public static int TYPE_SAND = 4;
	
	public String[] getTexturePaths() {
		return new String[]{
				"res/pix-concrete.png", 
				"res/pix-concrete.png", 
				"res/pix-concrete.png", 
				"res/pix-concrete.png", 
				"res/pix-concrete.png", 
				"res/pix-concrete.png"
				};
	}

	protected Block() {

	}
	
	public static boolean[] determineSolidSides(Chunk[] chunk, Vector3 offset) {
		boolean[] b = new boolean[6]; // top bottom front back left right
		
		int blockId = -1;
		
		if (offset.y == Game.CHUNK_HEIGHT - 1) {
			b[0] = true;
		} else {
			b[0] = chunk[0].terrain[offset.x][offset.y + 1][offset.z] < 0;
		}
		
		if (offset.y == 0) {
			b[1] = true;
		} else {
			b[1] = chunk[0].terrain[offset.x][offset.y - 1][offset.z] < 0;
		}
		
		/**
		 * Major TODO from here on out is to make the b[n] = true blocks look
		 * inside adjacent chunks to determine if they should be on or off.
		 * This should trigger lazy generation of the adjacent chunk if it doesn't
		 * already exist, but make sure that generation and rendering are separate
		 * steps, so calling this won't cause an interminable chunk-load death. 
		 */
		if (offset.x == Game.CHUNK_SIZE - 1) {
			b[4] = true;
		} else {
			b[4] = chunk[0].terrain[offset.x + 1][offset.y][offset.z] < 0;
			//b[4] = false;
		}
		
		if (offset.x == 0) {
			b[5] = true;
		} else {
			b[5] = chunk[0].terrain[offset.x - 1][offset.y][offset.z] < 0;
			//b[5] = blockId != -1 ? BlockManager.blocks[blockId].isSolid() : false;
			//b[5] = false;
		}
		
		if (offset.z == Game.CHUNK_SIZE - 1) {
			b[2] = true;
		} else {
			b[2] = chunk[0].terrain[offset.x][offset.y][offset.z + 1] < 0;
			//b[2] = blockId != -1 ? BlockManager.blocks[blockId].isSolid() : false;
			//b[2] = false;
			}
		
		if (offset.z == 0) {
			b[3] = true;
		} else {
			b[3] = chunk[0].terrain[offset.x][offset.y][offset.z - 1] < 0;
			//b[3] = blockId != -1 ? BlockManager.blocks[blockId].isSolid() : false;
			//b[3] = false;
		}
		
		return b;
	}


	/* Renders the cube. */
	public void render(Chunk[] thisChunk, Vector3 offset) {
		boolean b[] = Block.determineSolidSides(thisChunk, offset);
		
		Vector3f pos1 = new Vector3f((float) thisChunk[0].translation.x + offset.x, (float) offset.y, (float) thisChunk[0].translation.z + offset.z);
		Vector3f pos2 = new Vector3f(pos1.x + 1f, pos1.y + 1f, pos1.z + 1f);
		
		String[] paths = getTexturePaths();
		Texture[] texture = {
				TextureStore.getTexture(paths[0]),
				TextureStore.getTexture(paths[1]),
				TextureStore.getTexture(paths[2]),
				TextureStore.getTexture(paths[3]),
				TextureStore.getTexture(paths[4]),
				TextureStore.getTexture(paths[5]),
				};
		
		
		if(b[0]) {
			Vector3f[] vertices = new Vector3f[4];
			vertices[0] = new Vector3f(pos2.x, pos2.y, pos1.z);
			vertices[1] = new Vector3f(pos1.x, pos2.y, pos1.z);
			vertices[2] = new Vector3f(pos1.x, pos2.y, pos2.z);
			vertices[3] = new Vector3f(pos2.x, pos2.y, pos2.z);
			QuadQueue.add(texture[0].getTextureID(), vertices);
		}
		
		if(b[1]) {
			Vector3f[] vertices = new Vector3f[4];
			vertices[0] = new Vector3f(pos2.x, pos1.y, pos2.z);
			vertices[1] = new Vector3f(pos1.x, pos1.y, pos2.z);
			vertices[2] = new Vector3f(pos1.x, pos1.y, pos1.z);
			vertices[3] = new Vector3f(pos2.x, pos1.y, pos1.z);
			QuadQueue.add(texture[1].getTextureID(), vertices);
		}
		
		if(b[2]) {
			Vector3f[] vertices = new Vector3f[4];
			vertices[0] = new Vector3f(pos2.x, pos2.y, pos2.z);
			vertices[1] = new Vector3f(pos1.x, pos2.y, pos2.z);
			vertices[2] = new Vector3f(pos1.x, pos1.y, pos2.z);
			vertices[3] = new Vector3f(pos2.x, pos1.y, pos2.z);
			QuadQueue.add(texture[2].getTextureID(), vertices);
		}
		
		if(b[3]) {
			Vector3f[] vertices = new Vector3f[4];
			vertices[0] = new Vector3f(pos1.x, pos2.y, pos1.z);
			vertices[1] = new Vector3f(pos2.x, pos2.y, pos1.z);
			vertices[2] = new Vector3f(pos2.x, pos1.y, pos1.z);
			vertices[3] = new Vector3f(pos1.x, pos1.y, pos1.z);
			QuadQueue.add(texture[3].getTextureID(), vertices);
		}

		if(b[4]) {
			Vector3f[] vertices = new Vector3f[4];
			vertices[0] = new Vector3f(pos2.x, pos2.y, pos1.z);
			vertices[1] = new Vector3f(pos2.x, pos2.y, pos2.z);
			vertices[2] = new Vector3f(pos2.x, pos1.y, pos2.z);
			vertices[3] = new Vector3f(pos2.x, pos1.y, pos1.z);
			QuadQueue.add(texture[4].getTextureID(), vertices);
		}
		
		if(b[5]) {
			Vector3f[] vertices = new Vector3f[4];
			vertices[0] = new Vector3f(pos1.x, pos2.y, pos2.z);
			vertices[1] = new Vector3f(pos1.x, pos2.y, pos1.z);
			vertices[2] = new Vector3f(pos1.x, pos1.y, pos1.z);
			vertices[3] = new Vector3f(pos1.x, pos1.y, pos2.z);
			QuadQueue.add(texture[5].getTextureID(), vertices);
		}
	}
}

