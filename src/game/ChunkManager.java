package game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import org.lwjgl.opengl.GL11;

public class ChunkManager {
	private HashMap<String, CubeTerrain> chunkMap = new HashMap<String, CubeTerrain>();
	private int seed;

	private static int chunkNum = 5;

	public ChunkManager() {
		
	}

	public void generate(Vector3f startPos) {
		Random rand = new Random();
		seed = rand.nextInt() & 0xFFFF;
		
		int inChunkX, inChunkZ;
		inChunkX = (int) startPos.x / Game.CHUNK_SIZE;
		inChunkZ = (int) startPos.z / Game.CHUNK_SIZE;

		for (int x = inChunkX - chunkNum; x <= inChunkX + chunkNum; x++) {
			for (int z = inChunkZ - chunkNum; z <= inChunkZ + chunkNum; z++) {
				generateChunk(x, z);
			}
		}
	}

	public void updateVisibleChunks(float posX, float posZ) {
		// we want to check if a chunk is near the player.
		int inChunkX, inChunkZ;
		inChunkX = (int) posX / Game.CHUNK_SIZE;
		inChunkZ = (int) posZ / Game.CHUNK_SIZE;

		// check existing chunks
		// if chunk is too far, remove it
		// if chunk is near enough, it's ok.
		ArrayList<String> deletions = new ArrayList<String>();
		for (Iterator<String> chunkIds = chunkMap.keySet().iterator(); chunkIds
				.hasNext();) {
			String chunkId = chunkIds.next();
			String[] coords = chunkId.split(",");
			if ((Math.abs(inChunkX - Integer.parseInt(coords[0])) > chunkNum)
					|| (Math.abs(inChunkZ - Integer.parseInt(coords[1])) > chunkNum)) {
				deletions.add(chunkId);
			}
		}

		for (Iterator<String> toDelete = deletions.iterator(); toDelete
				.hasNext();) {
			String deletion = toDelete.next();
			chunkMap.get(deletion).release();
			chunkMap.remove(deletion);
		}

		// check positions near the player
		// if it is near, and doesnt exist, create it.
		for (int x = inChunkX - chunkNum; x <= inChunkX + chunkNum; x++) {
			for (int z = inChunkZ - chunkNum; z <= inChunkZ + chunkNum; z++) {
				String hashKey = String.valueOf(x) + "," + String.valueOf(z);
				if (!chunkMap.containsKey(hashKey)
						&& (Game.deltaTime < 1000.0f / 60.0f)) // lol throttling, actually a TODO
				{
					generateChunk(x, z);
					return;
				}
			}
		}
	}

	public void generateChunk(int offsetX, int offsetZ) {
		// Create the terrain

		String hashKey = String.valueOf(offsetX) + ","
				+ String.valueOf(offsetZ);

		chunkMap.put(hashKey, new CubeTerrain(offsetX, offsetZ, new Vector3(
				Game.CHUNK_SIZE, Game.CHUNK_HEIGHT, Game.CHUNK_SIZE),
				new Vector3f(1.0f, 1.0f, 1.0f), new Vector3f((float) offsetX
						* Game.CHUNK_SIZE, 0.0f, (float) offsetZ
						* Game.CHUNK_SIZE)));

		final int TERRAIN_MAX_HEIGHT = 20;
		final int TERRAIN_MIN_HEIGHT = 7;
		final int TERRAIN_SMOOTH_LEVEL = 0;

		final int TERRAIN_GEN_SEED = seed;
		final float TERRAIN_GEN_NOISE_SIZE = 0.06f;
		final float TERRAIN_GEN_PERSISTENCE = 0.9f;
		final int TERRAIN_GEN_OCTAVES = 3;

		chunkMap.get(hashKey).generateTerrain(TERRAIN_MAX_HEIGHT,
				TERRAIN_MIN_HEIGHT, TERRAIN_SMOOTH_LEVEL, TERRAIN_GEN_SEED,
				TERRAIN_GEN_NOISE_SIZE, TERRAIN_GEN_PERSISTENCE,
				TERRAIN_GEN_OCTAVES, Game.TEXTURES);
	}

	public void render() {
		// Save the current matrix
		GL11.glPushMatrix();

		// Add the translation matrix
		GL11.glTranslatef(0.0f, 0.0f, 0.0f);

		for (Iterator<String> chunkIds = chunkMap.keySet().iterator(); chunkIds
				.hasNext();) {
			String chunkId = chunkIds.next();
			chunkMap.get(chunkId).render();
		}
		// Restore the matrix
		GL11.glPopMatrix();
	}

	public void release() {
		for (Iterator<String> chunkIds = chunkMap.keySet().iterator(); chunkIds
				.hasNext();) {
			String chunkId = chunkIds.next();
			chunkMap.get(chunkId).release();
		}
	}

	/* Returns true if there is a solid cube at the given coordinates. */
	public boolean solidAt(Vector3f coordinates) {
		int xIndex, zIndex;
		xIndex = (int) Math.floor(coordinates.x / Game.CHUNK_SIZE);
		zIndex = (int) Math.floor(coordinates.z / Game.CHUNK_SIZE);

		String hashKey = String.valueOf(xIndex) + "," + String.valueOf(zIndex);

		if (chunkMap.containsKey(hashKey)) {
			return chunkMap.get(hashKey).solidAt(coordinates);
		}

		return false;
	}

}
