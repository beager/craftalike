package game;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;


public class Skybox extends Cube {

	public Skybox(Vector3f pos1, Vector3f pos2, Vector4f color, Texture texture) {
		super(pos1, pos2, color, texture);
	}

	@Override
	public void render() {
		if(texture != null) {
			// Set the texture
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getTextureID());
		} else {
			// Set the color
			GL11.glColorMaterial(GL11.GL_FRONT_AND_BACK, GL11.GL_AMBIENT_AND_DIFFUSE);
			GL11.glEnable(GL11.GL_COLOR_MATERIAL);
			GL11.glColor4f(color.x, color.y, color.z, color.a);
		}
		
		GL11.glBegin(GL11.GL_QUADS);
		
		// Top
		if(renderTop) {
			GL11.glNormal3f(0.0f, -1.0f, 0.0f);
			GL11.glTexCoord2f(1.0f, 0.0f);
			GL11.glVertex3f(pos2.x, pos2.y, pos2.z);
			GL11.glTexCoord2f(0.0f, 0.0f);
			GL11.glVertex3f(pos1.x, pos2.y, pos2.z);
			GL11.glTexCoord2f(0.0f, 1.0f);
			GL11.glVertex3f(pos1.x, pos2.y, pos1.z);
			GL11.glTexCoord2f(1.0f, 1.0f);
			GL11.glVertex3f(pos2.x, pos2.y, pos1.z);
		}
		
		// Bottom
		if(renderBottom) {
			GL11.glNormal3f(0.0f, 1.0f, 0.0f);
			GL11.glTexCoord2f(1.0f, 0.0f);
			GL11.glVertex3f(pos2.x, pos1.y, pos1.z);
			GL11.glTexCoord2f(0.0f, 0.0f);
			GL11.glVertex3f(pos1.x, pos1.y, pos1.z);
			GL11.glTexCoord2f(0.0f, 1.0f);
			GL11.glVertex3f(pos1.x, pos1.y, pos2.z);
			GL11.glTexCoord2f(1.0f, 1.0f);
			GL11.glVertex3f(pos2.x, pos1.y, pos2.z);
		}
		
		// Front
		if(renderFront) {
			GL11.glNormal3f(0.0f, 0.0f, -1.0f);
			GL11.glTexCoord2f(1.0f, 0.0f);
			GL11.glVertex3f(pos1.x, pos2.y, pos2.z);
			GL11.glTexCoord2f(0.0f, 0.0f);
			GL11.glVertex3f(pos2.x, pos2.y, pos2.z);
			GL11.glTexCoord2f(0.0f, 1.0f);
			GL11.glVertex3f(pos2.x, pos1.y, pos2.z);
			GL11.glTexCoord2f(1.0f, 1.0f);
			GL11.glVertex3f(pos1.x, pos1.y, pos2.z);
		}
		
		// Back
		if(renderBack) {
			GL11.glNormal3f(0.0f, 0.0f, 1.0f);
			GL11.glTexCoord2f(1.0f, 0.0f);
			GL11.glVertex3f(pos2.x, pos2.y, pos1.z);
			GL11.glTexCoord2f(0.0f, 0.0f);
			GL11.glVertex3f(pos1.x, pos2.y, pos1.z);
			GL11.glTexCoord2f(0.0f, 1.0f);
			GL11.glVertex3f(pos1.x, pos1.y, pos1.z);
			GL11.glTexCoord2f(1.0f, 1.0f);
			GL11.glVertex3f(pos2.x, pos1.y, pos1.z);
		}
		
		// Right
		if(renderRight) {
			GL11.glNormal3f(-1.0f, 0.0f, 0.0f);
			GL11.glTexCoord2f(1.0f, 0.0f);
			GL11.glVertex3f(pos2.x, pos2.y, pos2.z);
			GL11.glTexCoord2f(0.0f, 0.0f);
			GL11.glVertex3f(pos2.x, pos2.y, pos1.z);
			GL11.glTexCoord2f(0.0f, 1.0f);
			GL11.glVertex3f(pos2.x, pos1.y, pos1.z);
			GL11.glTexCoord2f(1.0f, 1.0f);
			GL11.glVertex3f(pos2.x, pos1.y, pos2.z);
		}
		
		// Left
		if(renderLeft) {
			GL11.glNormal3f(1.0f, 0.0f, 0.0f);
			GL11.glTexCoord2f(1.0f, 0.0f);
			GL11.glVertex3f(pos1.x, pos2.y, pos1.z);
			GL11.glTexCoord2f(0.0f, 0.0f);
			GL11.glVertex3f(pos1.x, pos2.y, pos2.z);
			GL11.glTexCoord2f(0.0f, 1.0f);
			GL11.glVertex3f(pos1.x, pos1.y, pos2.z);
			GL11.glTexCoord2f(1.0f, 1.0f);
			GL11.glVertex3f(pos1.x, pos1.y, pos1.z);
		}
		
		GL11.glEnd();
		
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_COLOR_MATERIAL);
	}
	
}
