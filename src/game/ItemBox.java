package game;

import game.blocks.BlockManager;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;

public class ItemBox {

	private static int selectedIndex = 0;
	
	private static int[] blockIndices = new int[]{0,1,3,4,5,6,7,8,2};

	public ItemBox() {
		// TODO Auto-generated constructor stub
	}
	
	public static int getSelectedIndex() {
		return selectedIndex;
	}

	public static void setSelectedIndex(int selectedIndex) {
		ItemBox.selectedIndex = (selectedIndex < 0) ? 8 : selectedIndex % 9;
	}
	
	public static int getSelectedBlock() {
		return blockIndices[selectedIndex];
	}

	public static void render() {
		int startPosY = Display.getDesktopDisplayMode().getHeight() - 48 - 24;
		int startPosX = (Display.getDesktopDisplayMode().getWidth() / 2) - (24 * 9);
		
		for (int i = 0; i < 9; i++) {
			if (getSelectedIndex()  == i)
				GL11.glColor4f(1f,1f,1f,1f);
			else
				GL11.glColor4f(1f,1f,1f,.5f);
			
			Texture tex = TextureStore.getTexture("res/gui-itembox.png");
			tex.bind();
			GL11.glBegin(GL11.GL_QUADS);
			GL11.glTexCoord2f(0, 0);
		    GL11.glVertex2f(startPosX,startPosY);
		    GL11.glTexCoord2f(0, tex.getHeight());
		    GL11.glVertex2f(startPosX+48,startPosY);
		    GL11.glTexCoord2f(tex.getWidth(), tex.getHeight());
		    GL11.glVertex2f(startPosX+48,startPosY+48);
		    GL11.glTexCoord2f(tex.getWidth(), 0);
		    GL11.glVertex2f(startPosX,startPosY+48);
		    GL11.glEnd();
		    
		    if (blockIndices[i] > -1)
		    	ItemBox.setBlock(i);

			startPosX += 48;
		}
	}
	
	public static void setBlock(int i) {
		String[] textures = BlockManager.blocks[blockIndices[i]].getTexturePaths();
		
		int startPosY = Display.getDesktopDisplayMode().getHeight() - 48 - 24 + 6;
		int startPosX = (Display.getDesktopDisplayMode().getWidth() / 2) - (24 * 9) + (48 * i) + 6;
		
		Texture tex;
		
		// Draw top of cube
		tex = TextureStore.getTexture(textures[0]);
		tex.bind();
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2f(0, 0);
	    GL11.glVertex2f(startPosX,startPosY + 9);
	    GL11.glTexCoord2f(0, tex.getHeight());
	    GL11.glVertex2f(startPosX+18,startPosY);
	    GL11.glTexCoord2f(tex.getWidth(), tex.getHeight());
	    GL11.glVertex2f(startPosX+36,startPosY+9);
	    GL11.glTexCoord2f(tex.getWidth(), 0);
	    GL11.glVertex2f(startPosX+18,startPosY+18);
	    GL11.glEnd();
	    
	    // Draw left of cube
	    tex = TextureStore.getTexture(textures[2]);
		tex.bind();
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2f(0, 0);
	    GL11.glVertex2f(startPosX+18,startPosY+18);	    
	    GL11.glTexCoord2f(0, tex.getHeight());
	    GL11.glVertex2f(startPosX+18,startPosY+36);
	    GL11.glTexCoord2f(tex.getWidth(), tex.getHeight());
	    GL11.glVertex2f(startPosX,startPosY+27);
	    GL11.glTexCoord2f(tex.getWidth(), 0);
	    GL11.glVertex2f(startPosX,startPosY + 9);
	    GL11.glEnd();
	    
	    tex = TextureStore.getTexture(textures[4]);
		tex.bind();
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2f(0, 0);   

	    GL11.glVertex2f(startPosX+36,startPosY+ 9);
	    GL11.glTexCoord2f(0, tex.getHeight());

	    GL11.glVertex2f(startPosX+36,startPosY+27);
	    GL11.glTexCoord2f(tex.getWidth(), tex.getHeight());
	    GL11.glVertex2f(startPosX+18,startPosY + 36);
	    GL11.glTexCoord2f(tex.getWidth(), 0);
	    GL11.glVertex2f(startPosX+18,startPosY+18);	 
	    GL11.glEnd();
	    /*tex = TextureStore.getTexture(textures[4]);
		tex.bind();
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2f(0, 0);
	    GL11.glVertex2f(startPosX,startPosY + 9);
	    GL11.glTexCoord2f(0, tex.getHeight());
	    GL11.glVertex2f(startPosX+18,startPosY);
	    GL11.glTexCoord2f(tex.getWidth(), tex.getHeight());
	    GL11.glVertex2f(startPosX+36,startPosY+9);
	    GL11.glTexCoord2f(tex.getWidth(), 0);
	    GL11.glVertex2f(startPosX+18,startPosY+18);
	    GL11.glEnd();*/
	}

}
