package game;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;

public class Cube {

	/* pos1 contains the lowest x, y, z. pos2 contains the heighest x, y, z */
	public Vector3f pos1, pos2;
	
	// Color to use if no texture is present
	protected Vector4f color;	
	
	// Texture class from Slick-Util library
	protected Texture texture;
	
	// Determines which sides to draw
	protected boolean renderTop, renderBottom, renderFront, renderBack, renderRight, renderLeft;
	
	// Properties
	public static boolean isSolid = true;
	
	public Cube(Vector3f pos1, Vector3f pos2, Vector4f color, Texture texture) {
		this.pos1 = pos1;
		this.pos2 = pos2;
		this.color = color;
		this.texture = texture;
		
		// Default is to draw all sides
		this.renderTop = true;
		this.renderBottom = true;
		this.renderFront = true;
		this.renderBack = true;
		this.renderRight = true;
		this.renderLeft = true;
	}
	
	/* Sets information about which sides to draw. */
	public void setVisibleSides(boolean drawTop, boolean drawBottom, boolean drawFront, boolean drawBack, boolean drawRight, boolean drawLeft) {
		this.renderTop = drawTop;
		this.renderBottom = drawBottom;
		this.renderFront = drawFront;
		this.renderBack = drawBack;
		this.renderRight = drawRight;
		this.renderLeft = drawLeft;
	}
	
	/* Renders the cube. */
	public void render() {		
		// Top
		if(renderTop) {
			Vector3f[] vertices = new Vector3f[4];
			
			vertices[0] = new Vector3f(pos2.x, pos2.y, pos1.z);

			vertices[1] = new Vector3f(pos1.x, pos2.y, pos1.z);

			vertices[2] = new Vector3f(pos1.x, pos2.y, pos2.z);

			vertices[3] = new Vector3f(pos2.x, pos2.y, pos2.z);
			
			QuadQueue.add(texture.getTextureID(), vertices);
		}
		
		// Bottom
		if(renderBottom) {
			Vector3f[] vertices = new Vector3f[4];

			vertices[0] = new Vector3f(pos2.x, pos1.y, pos2.z);
			
			vertices[1] = new Vector3f(pos1.x, pos1.y, pos2.z);
			
			vertices[2] = new Vector3f(pos1.x, pos1.y, pos1.z);
			
			vertices[3] = new Vector3f(pos2.x, pos1.y, pos1.z);
			
			QuadQueue.add(texture.getTextureID(), vertices);
		}
		
		// Front
		if(renderFront) {
			Vector3f[] vertices = new Vector3f[4];

			vertices[0] = new Vector3f(pos2.x, pos2.y, pos2.z);

			vertices[1] = new Vector3f(pos1.x, pos2.y, pos2.z);

			vertices[2] = new Vector3f(pos1.x, pos1.y, pos2.z);

			vertices[3] = new Vector3f(pos2.x, pos1.y, pos2.z);
			
			QuadQueue.add(texture.getTextureID(), vertices);
		}
		
		// Back
		if(renderBack) {
			Vector3f[] vertices = new Vector3f[4];

			vertices[0] = new Vector3f(pos1.x, pos2.y, pos1.z);

			vertices[1] = new Vector3f(pos2.x, pos2.y, pos1.z);

			vertices[2] = new Vector3f(pos2.x, pos1.y, pos1.z);

			vertices[3] = new Vector3f(pos1.x, pos1.y, pos1.z);
			
			QuadQueue.add(texture.getTextureID(), vertices);
		}
		
		// Right
		if(renderRight) {
			Vector3f[] vertices = new Vector3f[4];

			vertices[0] = new Vector3f(pos2.x, pos2.y, pos1.z);

			vertices[1] = new Vector3f(pos2.x, pos2.y, pos2.z);

			vertices[2] = new Vector3f(pos2.x, pos1.y, pos2.z);

			vertices[3] = new Vector3f(pos2.x, pos1.y, pos1.z);
			
			QuadQueue.add(texture.getTextureID(), vertices);
		}
		
		// Left
		if(renderLeft) {
			Vector3f[] vertices = new Vector3f[4];

			vertices[0] = new Vector3f(pos1.x, pos2.y, pos2.z);

			vertices[1] = new Vector3f(pos1.x, pos2.y, pos1.z);

			vertices[2] = new Vector3f(pos1.x, pos1.y, pos1.z);

			vertices[3] = new Vector3f(pos1.x, pos1.y, pos2.z);
			
			QuadQueue.add(texture.getTextureID(), vertices);
		}
		
		// Reset color if color was used
		//GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

		//GL11.glDisable(GL11.GL_TEXTURE_2D);
		//GL11.glDisable(GL11.GL_COLOR_MATERIAL);
	}
}

