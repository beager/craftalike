package game;

public class Vector3f {

	public float x, y, z;
	
	public Vector3f(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public void multiply(Vector3f a) {
		x *= a.x;
		y *= a.y;
		z *= a.z;
	}
	
	public void add(Vector3f a) {
		x += a.x;
		y += a.y;
		z += a.z;
	}
	
	public static Vector3f multiply(Vector3f a, Vector3f b) {
		return new Vector3f(a.x * b.x, a.y * b.y, a.z * b.z);
	}
	
	public static Vector3f add(Vector3f a, Vector3f b) {
		return new Vector3f(a.x + b.x, a.y + b.y, a.z + b.z);
	}
}
