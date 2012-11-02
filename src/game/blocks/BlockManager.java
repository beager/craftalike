package game.blocks;

import game.Chunk;
import game.Vector3;

public class BlockManager {
	
	public Block[] blocks;
	
	public static final int TYPE_NONE = -1;
	public static final int TYPE_DIRT = 0;
	public static final int TYPE_GRASS = 1;
	public static final int TYPE_WATER = 2;
	public static final int TYPE_STONE = 3;
	public static final int TYPE_SAND = 4;
	
	public BlockManager() {
		blocks = new Block[]{
			new DirtBlock(),
			new GrassBlock(),
			new WaterBlock(),
			new StoneBlock(),
			new SandBlock()
		};
	}
	
	public void renderType(Chunk[] thisChunk, int type, Vector3 offset) {
		if (blocks[type] != null) {
			blocks[type].render(thisChunk, offset);
		}
	}
}
