package game;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBVertexBufferObject;
import org.lwjgl.opengl.GLContext;
import org.newdawn.slick.opengl.Texture;

public class Cube {

	/* pos1 contains the lowest x, y, z. pos2 contains the heighest x, y, z */
	public Vector3f pos1, pos2;
	
	// Color to use if no texture is present
	protected Vector4f color;	
	
	// Texture class from Slick-Util library
	protected Texture texture;
	
	public int type;
	
	public static int TYPE_DIRT = 0;
	public static int TYPE_GRASS = 1;
	public static int TYPE_WATER = 2;
	public static int TYPE_STONE = 3;
	public static int TYPE_SAND = 4;
	
	// Determines which sides to draw
	protected boolean renderTop, renderBottom, renderFront, renderBack, renderRight, renderLeft;
	
	// Properties
	public static boolean isSolid = true;
	
	public void setPos1(Vector3f pos1) {
		this.pos1 = pos1;
	}
	
	public void setPos2(Vector3f pos2) {
		this.pos2 = pos2;
	}
	
	public Cube(Vector3f pos1, Vector3f pos2, Vector4f color, int type, Texture texture) {
		this.pos1 = pos1;
		this.pos2 = pos2;
		this.color = color;
		this.texture = texture;
		this.type = type;
		
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
		if(renderTop) {
			Vector3f[] vertices = new Vector3f[4];
			vertices[0] = new Vector3f(pos2.x, pos2.y, pos1.z);
			vertices[1] = new Vector3f(pos1.x, pos2.y, pos1.z);
			vertices[2] = new Vector3f(pos1.x, pos2.y, pos2.z);
			vertices[3] = new Vector3f(pos2.x, pos2.y, pos2.z);
			QuadQueue.add(texture.getTextureID(), vertices);
		}
		
		if(renderBottom) {
			Vector3f[] vertices = new Vector3f[4];
			vertices[0] = new Vector3f(pos2.x, pos1.y, pos2.z);
			vertices[1] = new Vector3f(pos1.x, pos1.y, pos2.z);
			vertices[2] = new Vector3f(pos1.x, pos1.y, pos1.z);
			vertices[3] = new Vector3f(pos2.x, pos1.y, pos1.z);
			QuadQueue.add(texture.getTextureID(), vertices);
		}
		
		if(renderFront) {
			Vector3f[] vertices = new Vector3f[4];
			vertices[0] = new Vector3f(pos2.x, pos2.y, pos2.z);
			vertices[1] = new Vector3f(pos1.x, pos2.y, pos2.z);
			vertices[2] = new Vector3f(pos1.x, pos1.y, pos2.z);
			vertices[3] = new Vector3f(pos2.x, pos1.y, pos2.z);
			QuadQueue.add(texture.getTextureID(), vertices);
		}
		
		if(renderBack) {
			Vector3f[] vertices = new Vector3f[4];
			vertices[0] = new Vector3f(pos1.x, pos2.y, pos1.z);
			vertices[1] = new Vector3f(pos2.x, pos2.y, pos1.z);
			vertices[2] = new Vector3f(pos2.x, pos1.y, pos1.z);
			vertices[3] = new Vector3f(pos1.x, pos1.y, pos1.z);
			QuadQueue.add(texture.getTextureID(), vertices);
		}

		if(renderRight) {
			Vector3f[] vertices = new Vector3f[4];
			vertices[0] = new Vector3f(pos2.x, pos2.y, pos1.z);
			vertices[1] = new Vector3f(pos2.x, pos2.y, pos2.z);
			vertices[2] = new Vector3f(pos2.x, pos1.y, pos2.z);
			vertices[3] = new Vector3f(pos2.x, pos1.y, pos1.z);
			QuadQueue.add(texture.getTextureID(), vertices);
		}
		
		if(renderLeft) {
			Vector3f[] vertices = new Vector3f[4];
			vertices[0] = new Vector3f(pos1.x, pos2.y, pos2.z);
			vertices[1] = new Vector3f(pos1.x, pos2.y, pos1.z);
			vertices[2] = new Vector3f(pos1.x, pos1.y, pos1.z);
			vertices[3] = new Vector3f(pos1.x, pos1.y, pos2.z);
			QuadQueue.add(texture.getTextureID(), vertices);
		}
	}

	public void setPos(Vector3f target) {
		setPos1(target);
		setPos2(new Vector3f(target.x + 1f, target.y + 1f, target.z + 1f));
	}


}

