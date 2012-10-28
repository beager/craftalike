package game;

import org.newdawn.slick.opengl.Texture;

public class MultiTextureCube extends Cube {
	
	private Texture texture2, texture3, texture4, texture5, texture6;

	public MultiTextureCube(Vector3f pos1, Vector3f pos2, Vector4f color, int type,
			Texture texture1, Texture texture2, Texture texture3, Texture texture4, Texture texture5, Texture texture6) {
		super(pos1, pos2, color, type, texture1);
		this.texture2 = texture2;
		this.texture3 = texture3;
		this.texture4 = texture4;
		this.texture5 = texture5;
		this.texture6 = texture6;
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
			QuadQueue.add(texture2.getTextureID(), vertices);
		}
		
		// Front
		if(renderFront) {
			Vector3f[] vertices = new Vector3f[4];
			vertices[0] = new Vector3f(pos2.x, pos2.y, pos2.z);
			vertices[1] = new Vector3f(pos1.x, pos2.y, pos2.z);
			vertices[2] = new Vector3f(pos1.x, pos1.y, pos2.z);
			vertices[3] = new Vector3f(pos2.x, pos1.y, pos2.z);
			QuadQueue.add(texture3.getTextureID(), vertices);
		}
		
		// Back
		if(renderBack) {
			Vector3f[] vertices = new Vector3f[4];
			vertices[0] = new Vector3f(pos1.x, pos2.y, pos1.z);
			vertices[1] = new Vector3f(pos2.x, pos2.y, pos1.z);
			vertices[2] = new Vector3f(pos2.x, pos1.y, pos1.z);
			vertices[3] = new Vector3f(pos1.x, pos1.y, pos1.z);
			QuadQueue.add(texture4.getTextureID(), vertices);
		}
		
		// Right
		if(renderRight) {
			Vector3f[] vertices = new Vector3f[4];
			vertices[0] = new Vector3f(pos2.x, pos2.y, pos1.z);
			vertices[1] = new Vector3f(pos2.x, pos2.y, pos2.z);
			vertices[2] = new Vector3f(pos2.x, pos1.y, pos2.z);
			vertices[3] = new Vector3f(pos2.x, pos1.y, pos1.z);
			QuadQueue.add(texture5.getTextureID(), vertices);
		}
		
		// Left
		if(renderLeft) {
			Vector3f[] vertices = new Vector3f[4];
			vertices[0] = new Vector3f(pos1.x, pos2.y, pos2.z);
			vertices[1] = new Vector3f(pos1.x, pos2.y, pos1.z);
			vertices[2] = new Vector3f(pos1.x, pos1.y, pos1.z);
			vertices[3] = new Vector3f(pos1.x, pos1.y, pos2.z);	
			QuadQueue.add(texture6.getTextureID(), vertices);
		}
	}

}
