package game;

import org.newdawn.slick.opengl.Texture;

public class TransparentCube extends Cube {
	
	public static boolean isSolid = false;

	public TransparentCube(Vector3f pos1, Vector3f pos2, Vector4f color,
			Texture texture) {
		super(pos1, pos2, color, texture);
		// TODO Auto-generated constructor stub
	}

}
