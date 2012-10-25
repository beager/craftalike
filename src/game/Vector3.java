package game;

public class Vector3 {
	public int x, y, z;
	
	public Vector3(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public void multiply(Vector3 a) {
		x *= a.x;
		y *= a.y;
		z *= a.z;
	}
	
	public void add(Vector3 a) {
		x += a.x;
		y += a.y;
		z += a.z;
	}
	
	public static Vector3 multiply(Vector3 a, Vector3 b) {
		return new Vector3(a.x * b.x, a.y * b.y, a.z * b.z);
	}
	
	public static Vector3 add(Vector3 a, Vector3 b) {
		return new Vector3(a.x + b.x, a.y + b.y, a.z + b.z);
	}
}
