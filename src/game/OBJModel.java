package game;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import org.lwjgl.opengl.GL11;


public class OBJModel {

	private int displayList = 0;
	
	public OBJModel() {
		
	}
	
	public void loadModel(InputStream in) throws Exception {
		// Create an OpenGL display list and start recording
		displayList = GL11.glGenLists(1);
		GL11.glNewList(displayList, GL11.GL_COMPILE);
		
		// OBJ file must only contain triangle faces
		int mode = GL11.GL_TRIANGLES;
		GL11.glBegin(mode);
		
		try {
			// Temporary lists holding vertices, normals and texture coordinates
			List<Vector3f> vertexList = new LinkedList<Vector3f>();
			List<Vector2f> textureCoordList = new LinkedList<Vector2f>();
			List<Vector3f> normalList = new LinkedList<Vector3f>();
			
			// Create a BufferedReader
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			
			// Begin parsing lines
			String line = null;
			while((line = reader.readLine()) != null) {
				StringTokenizer st = new StringTokenizer(line);
				
				if(!st.hasMoreTokens())
					continue;
				
				// Get the first token of the line
				String firstToken = st.nextToken();
				
				if(firstToken.equals("v")) {
					// Parse and save the vertex
					vertexList.add(new Vector3f(Float.parseFloat(st.nextToken()), Float.parseFloat(st.nextToken()), Float.parseFloat(st.nextToken())));
				}  else if(firstToken.equals("vt")) {
					// Parse and save the texture coordinate
					textureCoordList.add(new Vector2f(Float.parseFloat(st.nextToken()), Float.parseFloat(st.nextToken())));
				} else if(firstToken.equals("vn")) {
					// Parse and save the normal
					normalList.add(new Vector3f(Float.parseFloat(st.nextToken()), Float.parseFloat(st.nextToken()), Float.parseFloat(st.nextToken())));
				} else if(firstToken.equals("f")) {
					int numberOfVertices = st.countTokens();
					
					if(st.countTokens() == 3 && mode != GL11.GL_TRIANGLES) {
						GL11.glEnd();
						mode = GL11.GL_TRIANGLES;
						GL11.glBegin(mode);
					} else if(st.countTokens() == 4 && mode != GL11.GL_QUADS) {
						GL11.glEnd();
						mode = GL11.GL_QUADS;
						GL11.glBegin(mode);
					}
					
					for(int v = 0; v < numberOfVertices; v++) {
						String vs = st.nextToken();
						String vsArray[] = vs.split("/");
						
						// Extract vertex, texture coordinate and normal
						Vector3f vertex = vertexList.get(Integer.parseInt(vsArray[0]) - 1);
						Vector2f textureCoord = null;
						Vector3f normal = null;
						
						if(vsArray.length > 1)
							textureCoord = vsArray[1].length() > 0 ? textureCoordList.get(Integer.parseInt(vsArray[1]) - 1) : null;
						
						if(vsArray.length > 2)
							normal = vsArray[2].length() > 0 ? normalList.get(Integer.parseInt(vsArray[2]) - 1) : null;
							
						// Call OpenGL methods to render (which will be recorded and saved to the display list)
						if(normal != null)
							GL11.glNormal3f(normal.x, normal.y, normal.z);
							
						if(textureCoord != null)
							GL11.glTexCoord2f(textureCoord.x, textureCoord.y);
						
						GL11.glVertex3f(vertex.x, vertex.y, vertex.z);
					}
				}
			}
		} catch(Exception e) {
			// This catch is only needed to call glEndList if something goes wrong
			GL11.glEndList();
			
			// Just pass the exception to the function caller
			throw e;
		}
		
		GL11.glEnd();
		GL11.glEndList();
	}
	
	public void render(Vector3f translation, Vector3f rotation, Vector3f scale) {
		GL11.glPushMatrix();
		
		// Set the translation
		GL11.glTranslatef(translation.x, translation.y, translation.z);
		
		// Set the rotation
		GL11.glRotatef(rotation.x, 1.0f, 0.0f, 0.0f);
		GL11.glRotatef(rotation.y, 0.0f, 1.0f, 0.0f);
		GL11.glRotatef(rotation.z, 0.0f, 0.0f, 1.0f);
		
		// Set scale
		GL11.glScalef(scale.x, scale.y, scale.z);
		
		// Render the model
		if(displayList != 0)
			GL11.glCallList(displayList);
		
		GL11.glPopMatrix();
	}
	
	public void release() {
		if(displayList != 0)
			GL11.glDeleteLists(displayList, 1);
	}
	
}
