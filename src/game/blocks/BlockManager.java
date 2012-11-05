package game.blocks;

import game.Chunk;
import game.Vector3;

public class BlockManager {
	
	public static Block[] blocks;
	
	public static final int TYPE_NONE = -1;
	public static final int TYPE_DIRT = 0;
	public static final int TYPE_GRASS = 1;
	public static final int TYPE_WATER = 2;
	public static final int TYPE_STONE = 3;
	public static final int TYPE_SAND = 4;
	public static final int TYPE_BRICK = 5;
	public static final int TYPE_PLANKS = 6;
	public static final int TYPE_CONCRETE = 7;
	public static final int TYPE_SNOW = 8;
	
	public BlockManager() {
		blocks = new Block[]{
			new DirtBlock(),
			new GrassBlock(),
			new WaterBlock(),
			new StoneBlock(),
			new SandBlock(),
			new BrickBlock(),
			new WoodPlankBlock(),
			new ConcreteBlock(),
			new SnowBlock()
		};
	}
	
	public static void renderType(Chunk[] thisChunk, int type, Vector3 offset) {
		if (blocks[type] != null) {
			blocks[type].render(thisChunk, offset);
		}
	}
}
