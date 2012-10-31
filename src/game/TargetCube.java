package game;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;

public class TargetCube extends Cube {

	public TargetCube(Vector3f pos1, Vector3f pos2, Vector4f color, int type,
			Texture texture) {
		super(pos1, pos2, color, type, texture);
		// TODO Auto-generated constructor stub
	}
	
	/* Renders the cube. */
	public void render() {

			Vector3f[][] vertices = new Vector3f[6][4];
			vertices[0][0] = new Vector3f(pos2.x, pos2.y, pos1.z);
			vertices[0][1] = new Vector3f(pos1.x, pos2.y, pos1.z);
			vertices[0][2] = new Vector3f(pos1.x, pos2.y, pos2.z);
			vertices[0][3] = new Vector3f(pos2.x, pos2.y, pos2.z);
	
			vertices[1][0] = new Vector3f(pos2.x, pos1.y, pos2.z);
			vertices[1][1] = new Vector3f(pos1.x, pos1.y, pos2.z);
			vertices[1][2] = new Vector3f(pos1.x, pos1.y, pos1.z);
			vertices[1][3] = new Vector3f(pos2.x, pos1.y, pos1.z);
		
			vertices[2][0] = new Vector3f(pos2.x, pos2.y, pos2.z);
			vertices[2][1] = new Vector3f(pos1.x, pos2.y, pos2.z);
			vertices[2][2] = new Vector3f(pos1.x, pos1.y, pos2.z);
			vertices[2][3] = new Vector3f(pos2.x, pos1.y, pos2.z);
		
			vertices[3][0] = new Vector3f(pos1.x, pos2.y, pos1.z);
			vertices[3][1] = new Vector3f(pos2.x, pos2.y, pos1.z);
			vertices[3][2] = new Vector3f(pos2.x, pos1.y, pos1.z);
			vertices[3][3] = new Vector3f(pos1.x, pos1.y, pos1.z);

			vertices[4][0] = new Vector3f(pos2.x, pos2.y, pos1.z);
			vertices[4][1] = new Vector3f(pos2.x, pos2.y, pos2.z);
			vertices[4][2] = new Vector3f(pos2.x, pos1.y, pos2.z);
			vertices[4][3] = new Vector3f(pos2.x, pos1.y, pos1.z);
		
			vertices[5][0] = new Vector3f(pos1.x, pos2.y, pos2.z);
			vertices[5][1] = new Vector3f(pos1.x, pos2.y, pos1.z);
			vertices[5][2] = new Vector3f(pos1.x, pos1.y, pos1.z);
			vertices[5][3] = new Vector3f(pos1.x, pos1.y, pos2.z);
		
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, 4);
			
			GL11.glEnable(GL11.GL_COLOR_MATERIAL);
			
			int displayList = GL11.glGenLists(1);
			GL11.glNewList(displayList, GL11.GL_COMPILE);
			GL11.glPushMatrix();
			
			GL11.glBegin(GL11.GL_QUADS);
			
			GL11.glColor4f(0.3f, 0.3f, 0.3f,0.7f);
			
			for (int i = 0; i < 6; i++) {
				Vector3f v[] = vertices[i];
				GL11.glNormal3f(1.0f, -1.0f, 1.0f);	
				GL11.glTexCoord2f(1.0f, 0.0f);
				GL11.glVertex3f(v[0].x, v[0].y, v[0].z);
				
				GL11.glNormal3f(-1.0f, -1.0f, 1.0f);
				GL11.glTexCoord2f(0.0f, 0.0f);
				GL11.glVertex3f(v[1].x, v[1].y, v[1].z);
				
				GL11.glNormal3f(-1.0f, -1.0f, -1.0f);
				GL11.glTexCoord2f(0.0f, 1.0f);
				GL11.glVertex3f(v[2].x, v[2].y, v[2].z);
				
				GL11.glNormal3f(1.0f, -1.0f, -1.0f);
				GL11.glTexCoord2f(1.0f, 1.0f);
				GL11.glVertex3f(v[3].x, v[3].y, v[3].z);
			}
			
			GL11.glEnd();
			
			
			
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_COLOR_MATERIAL);
			
GL11.glPopMatrix();
			
			GL11.glEndList();
			
			GL11.glCallList(displayList);
			
			GL11.glDeleteLists(displayList, 1);
	}
	
	public void setPos(Vector3f target) {
		setPos1(new Vector3f(target.x - 0.1f, target.y - 0.1f, target.z - 0.1f));
		setPos2(new Vector3f(target.x + 1.1f, target.y + 1.1f, target.z + 1.1f));
	}

}
