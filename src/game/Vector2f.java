package game;

public class Vector2f {
	public float x, y;
	
	public Vector2f(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public void multiply(Vector2f a) {
		x *= a.x;
		y *= a.y;
	}
	
	public void add(Vector2f a) {
		x += a.x;
		y += a.y;
	}
	
	public static Vector2f multiply(Vector2f a, Vector2f b) {
		return new Vector2f(a.x * b.x, a.y * b.y);
	}
	
	public static Vector2f add(Vector2f a, Vector2f b) {
		return new Vector2f(a.x + b.x, a.y + b.y);
	}
}
