package game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.lwjgl.opengl.GL11;

public class QuadQueue {
	
	private static HashMap<String, ArrayList<Vector3f[]>> quadQueues = new HashMap<String, ArrayList<Vector3f[]>>();

	public QuadQueue() {
	}
	
	public static void add(int textureId, Vector3f[] v) {
		if (!quadQueues.containsKey(String.valueOf(textureId))) {
			quadQueues.put(String.valueOf(textureId), new ArrayList<Vector3f[]>());
		}
		quadQueues.get(String.valueOf(textureId)).add(v);
	}
	
	public static void renderAll() {
		for (Iterator<String> tids = quadQueues.keySet().iterator(); tids.hasNext();) {
			String tid = tids.next();

			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_MODULATE);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, Integer.parseInt(tid)); 
			GL11.glBegin(GL11.GL_QUADS);
			GL11.glColor4f(0f, 0f, 0f, .5f);
			
			while (!quadQueues.get(tid).isEmpty()) {
				Vector3f v[] = quadQueues.get(tid).remove(0);
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
		}
	}

}
