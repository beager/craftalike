package game.blocks;

public class WaterBlock extends Block {
	
	@Override
	public String[] getTexturePaths() {
		return new String[]{
				"res/pix-water.png", 
				"res/pix-water.png", 
				"res/pix-water.png", 
				"res/pix-water.png", 
				"res/pix-water.png", 
				"res/pix-water.png"
				};
	}
	
	public boolean isSolid() {
		return false;
	}
}
