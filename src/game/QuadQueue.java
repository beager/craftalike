package game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.lwjgl.opengl.GL11;

public class QuadQueue {
	
	private static HashMap<String, ArrayList<Vector3f[]>> quadQueues = new HashMap<String, ArrayList<Vector3f[]>>();

	public QuadQueue() {
		// TODO Auto-generated constructor stub
	}
	
	public static void add(int textureId, Vector3f[] v) {
		if (!quadQueues.containsKey(String.valueOf(textureId))) {
			quadQueues.put(String.valueOf(textureId), new ArrayList<Vector3f[]>());
		}
		quadQueues.get(String.valueOf(textureId)).add(v);
	}
	
	public static void renderAll() {
		for (Iterator<String> tids = quadQueues.keySet().iterator(); tids.hasNext();) {
			//TODO: Render quads here!
			
			String tid = tids.next();
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			System.out.println(Integer.parseInt(tid));
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, Integer.parseInt(tid));
			//GL11.glBindTexture(GL11.GL_TEXTURE_2D, 4);
			
			GL11.glBegin(GL11.GL_QUADS);
			
			
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
