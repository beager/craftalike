package game.blocks;

public class GrassBlock extends Block {

	@Override
	public String[] getTexturePaths() {
		return new String[]{
				"res/pix-grass.png", 
				"res/pix-dirt.png", 
				"res/pix-grass-side.png", 
				"res/pix-grass-side.png", 
				"res/pix-grass-side.png", 
				"res/pix-grass-side.png"
				};
	}
}
