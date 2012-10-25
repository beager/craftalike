package game;

public class PerlinNoise2D {
	
	private static float noise(int x, int y) {
		int n = x + y * 57;
		n = (n << 13) ^ n;
	    return (1.0f - ((n * (n * n * 15731 + 789221) + 1376312589) & 0x7fffffff) / 1073741824.0f);
	}
	
	private static float smoothNoise(float x, float y) {
		float corners = (noise((int)x - 1, (int)y - 1) + noise((int)x - 1, (int)y + 1) + noise((int)x + 1, (int)y + 1)) / 16;
		float sides = (noise((int)x - 1, (int)y) + noise((int)x + 1, (int)y) + noise((int)x, (int)y + 1)) / 8;
		float center = noise((int)x, (int)y) / 4;
		return corners + sides + center;
	}
	
	private static float cosInterpolate(float a, float b, float x) {
		float angle = (float) (x * Math.PI);
		float prc = (float) ((1.0f - Math.cos(angle)) * 0.5f);
		return a * (1.0f - prc) + b * prc;
	}
	
	public static float perlin2D(int x, int y, int width, int height, int seed, float noiseSize, float persistence, int octaves) {
		float total = 0.0f;
		
		for(int i = 0; i < octaves; i++) {
			// Calculate frequency and amplitude
			float freq = (float) Math.pow(2, i);
			float amp = (float) Math.pow(persistence, i);
			
			// Calculate x and y noise coordinates
			float tx = x * freq;
			float ty = y * freq;
			float txInt = (int) tx;
			float tyInt = (int) ty;
			
			// Calculate fractions of x and y
			float fracX = tx - txInt;
			float fracY = ty - tyInt;
			
			// Get noise
			float v1 = smoothNoise(txInt + seed, tyInt + seed);
			float v2 = smoothNoise(txInt + 1 + seed, tyInt + seed);
			float v3 = smoothNoise(txInt + seed, tyInt + 1 + seed);
			float v4 = smoothNoise(txInt + 1 + seed, tyInt + 1 + seed);
			
			// Smooth noise in the X-axis
			float i1 = cosInterpolate(v1, v2, fracX);
			float i2 = cosInterpolate(v3, v4, fracX);
			
			// Smooth in the Y-axis
			total += cosInterpolate(i1, i2, fracY) * amp;
		}
		
		return total;
	}
}
