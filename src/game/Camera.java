package game;
import org.lwjgl.opengl.GL11;

public class Camera {

	public static final int FORWARD = 0;
	public static final int BACKWARD = 1;
	public static final int RIGHT = 2;
	public static final int LEFT = 3;
	
	public static final float MAX_GRAVITY_SPEED = 1.0f;
	public static final float GRAVITY_ACCEL = 0.01f;
	
	public float gravitySpeed = 0.0f;
	public boolean grounded = false;
	public boolean hasGravitiedThisFrame = false;
	
	public Vector3f coordinates;
	public Vector3f rotation;
	
	private ChunkManager terrain;
	
	public Camera(Vector3f coordinates, Vector3f rotation, ChunkManager terrain) {
		this.coordinates = coordinates;
		this.rotation = rotation;
		this.terrain = terrain;
	}
	
	public void move(float delta, int direction, float gravityDelta, boolean collisionChecking, boolean flyMode) {
		Vector3f newCoordinates = new Vector3f(coordinates.x, coordinates.y, coordinates.z);
		
		if (newCoordinates.y < -0.0f)
			newCoordinates.y = 80.0f;
		
		if(direction == FORWARD) {
			if(flyMode) {
				// Includes moving in the Y-direction
				newCoordinates.x += Math.cos(Math.toRadians(rotation.x)) * -Math.sin(Math.toRadians(rotation.y)) * delta;
				newCoordinates.y += Math.sin(Math.toRadians(rotation.x)) * delta;
				newCoordinates.z += Math.cos(Math.toRadians(rotation.x)) * -Math.cos(Math.toRadians(rotation.y)) * delta;
			} else {
				// No moving in the Y-direction. (2D version, use if flying is forbidden)
				newCoordinates.x += -Math.sin(Math.toRadians(rotation.y)) * delta;
				newCoordinates.z += -Math.cos(Math.toRadians(rotation.y)) * delta;
			}
		} else if(direction == BACKWARD) {
			if(flyMode) {
				// Includes moving in the Y-direction
				newCoordinates.x -= Math.cos(Math.toRadians(rotation.x)) * -Math.sin(Math.toRadians(rotation.y)) * delta;
				newCoordinates.y -= Math.sin(Math.toRadians(rotation.x)) * delta;
				newCoordinates.z -= Math.cos(Math.toRadians(rotation.x)) * -Math.cos(Math.toRadians(rotation.y)) * delta;
			} else {
				// No moving in the Y-direction. (2D version, use if flying is forbidden)
				newCoordinates.x -= -Math.sin(Math.toRadians(rotation.y)) * delta;
				newCoordinates.z -= -Math.cos(Math.toRadians(rotation.y)) * delta;
			}
		} else if(direction == RIGHT) {
			// Only move in the XZ-directions
			newCoordinates.x += Math.sin(Math.toRadians(rotation.y + 90)) * delta;
			newCoordinates.z += Math.sin(Math.toRadians(-rotation.y)) * delta;
		} else if(direction == LEFT) {
			// Only move in the XZ-directions
			newCoordinates.x -= Math.sin(Math.toRadians(rotation.y + 90)) * delta;
			newCoordinates.z -= Math.sin(Math.toRadians(-rotation.y)) * delta;
		}
		
		// Add false gravity
		//newCoordinates.y -= gravityDelta;
		if (!hasGravitiedThisFrame) {
			newCoordinates.y -= gravitySpeed;
			hasGravitiedThisFrame = true;
		}
		
		// Collision detection
		if(collisionChecking) {
			if(!collision(newCoordinates.x, coordinates.y, coordinates.z)) {
				coordinates.x = newCoordinates.x;
			}
			
			if(!collision(coordinates.x, newCoordinates.y, coordinates.z)) {
				coordinates.y = newCoordinates.y;
			}
			else
			{
				gravitySpeed = 0.0f;
				grounded = true;
			}
			
			if(!collision(coordinates.x, coordinates.y, newCoordinates.z)) {
				coordinates.z = newCoordinates.z;
			}
			
		} else {
			coordinates.x = newCoordinates.x;
			coordinates.y = newCoordinates.y;
			coordinates.z = newCoordinates.z;
		}
	}
	
	public boolean collision(float x, float y, float z) {
		// Simulate a cube cross around the point
		float cubeSize = 0.8f;
		
		Vector3f c1 = new Vector3f(x - cubeSize / 2, y, z);
		Vector3f c2 = new Vector3f(x + cubeSize / 2, y, z);
		Vector3f c3 = new Vector3f(x, y - 1.5f, z);				// This is 1.5f to simulate the proportions of a human (head/camera at top of body)
		Vector3f c4 = new Vector3f(x, y + cubeSize / 2, z);
		Vector3f c5 = new Vector3f(x, y, z - cubeSize / 2);
		Vector3f c6 = new Vector3f(x, y, z + cubeSize / 2);
		Vector3f c7 = new Vector3f(x - cubeSize / 2, y - 1.5f, z);
		Vector3f c8 = new Vector3f(x + cubeSize / 2, y - 1.5f, z);
		Vector3f c9 = new Vector3f(x, y - 1.5f, z - cubeSize / 2);
		Vector3f c10 = new Vector3f(x, y - 1.5f, z + cubeSize / 2);
		
		if(!terrain.solidAt(c1) && 
				!terrain.solidAt(c2) && 
				!terrain.solidAt(c3) && 
				!terrain.solidAt(c4) && 
				!terrain.solidAt(c5) && 
				!terrain.solidAt(c6) &&
				!terrain.solidAt(c7) && 
				!terrain.solidAt(c8) && 
				!terrain.solidAt(c9) && 
				!terrain.solidAt(c10)) {
			return false;
		}
		
		return true;
	}
	
	/* Use this when adding rotation instead of Vector3f.add since this function makes the numbers stay under 360. */
	public void addRotation(Vector3f rot) {
		rotation.x += rot.x;
		rotation.y += rot.y;
		rotation.z += rot.z;
		
		if(rotation.x >= 360.0f || rotation.x <= -360.0f)
			rotation.x = rotation.x % 360.0f;
		
		if(rotation.y >= 360.0f || rotation.y <= -360.0f)
			rotation.y = rotation.y % 360.0f;
		
		if(rotation.z >= 360.0f || rotation.z <= -360.0f)
			rotation.z = rotation.z % 360.0f;
		
		// Gimbal lock
		if(rotation.x <= -90.0f)
			rotation.x = -90.0f;
		else if(rotation.x >= 90.0f)
			rotation.x = 90.0f;
	}
	
	public void applyMatrix() {
		// Rotate
		GL11.glRotatef(-rotation.x, 1.0f, 0.0f, 0.0f);
		GL11.glRotatef(-rotation.y, 0.0f, 1.0f, 0.0f);
		GL11.glRotatef(-rotation.z, 0.0f, 0.0f, 1.0f);
		
		// Translate
		GL11.glTranslatef(-coordinates.x, -coordinates.y, -coordinates.z);
	}

	public void updateGravity() {
		if (gravitySpeed < MAX_GRAVITY_SPEED) gravitySpeed += GRAVITY_ACCEL;
		// TODO Auto-generated method stub
		
	}
	
}
