package game;

public class Vector4f {

	public float x, y, z, a;
	
	public Vector4f(float x, float y, float z, float a) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.a = a;
	}
	
	public void multiply(Vector4f b) {
		x *= b.x;
		y *= b.y;
		z *= b.z;
		a *= b.a;
	}
	
	public void add(Vector4f b) {
		x += b.x;
		y += b.y;
		z += b.z;
		a += b.a;
	}
	
	public static Vector4f multiply(Vector4f a, Vector4f b) {
		return new Vector4f(a.x * b.x, a.y * b.y, a.z * b.z, a.a * b.a);
	}
	
	public static Vector4f add(Vector4f a, Vector4f b) {
		return new Vector4f(a.x + b.x, a.y + b.y, a.z + b.z, a.a + b.a);
	}
}
