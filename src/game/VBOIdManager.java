package game;

import org.lwjgl.opengl.ARBVertexBufferObject;

public class VBOIdManager {
	public static int getNextId() {
		return ARBVertexBufferObject.glGenBuffersARB();
	}
}
