package game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import org.lwjgl.opengl.GL11;

public class ChunkManager {
	private HashMap<String, Chunk> chunkMap = new HashMap<String, Chunk>();
	private HashMap<String, Chunk> sleepingChunks = new HashMap<String, Chunk>();
	private int seed;

	private static int chunkNum = 8;

	public ChunkManager() {
		Random rand = new Random();
		rand.setSeed(System.nanoTime());
		seed = rand.nextInt() & 0xFFFF;
	}

	public void generate(Vector3f pos) {
		int inChunkX, inChunkZ;
		
		inChunkX = pos.x < 0 ? (int) (pos.x - Game.CHUNK_SIZE) / Game.CHUNK_SIZE : (int) pos.x / Game.CHUNK_SIZE;
		inChunkZ = pos.z < 0 ? (int) (pos.z - Game.CHUNK_SIZE) / Game.CHUNK_SIZE : (int) pos.z / Game.CHUNK_SIZE;
		
		for (int x = inChunkX - chunkNum; x <= inChunkX + chunkNum - 1; x++) {
			for (int z = inChunkZ - chunkNum; z <= inChunkZ + chunkNum - 1; z++) {
				getChunk(x, z);
			}
		}
	}

	public void updateVisibleChunks(Vector3f pos) {
		// we want to check if a chunk is near the player.
		int inChunkX, inChunkZ;
		
		inChunkX = pos.x < 0 ? (int) (pos.x - Game.CHUNK_SIZE) / Game.CHUNK_SIZE : (int) pos.x / Game.CHUNK_SIZE;
		inChunkZ = pos.z < 0 ? (int) (pos.z - Game.CHUNK_SIZE) / Game.CHUNK_SIZE : (int) pos.z / Game.CHUNK_SIZE;
		
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
			sleepingChunks.put(deletion, chunkMap.get(deletion));
			chunkMap.remove(deletion);
		}
		
		String hashKey = String.valueOf(inChunkX) + "," + String.valueOf(inChunkZ);
		if (!chunkMap.containsKey(hashKey)
				&& (Game.deltaTime < 1000.0f / 60.0f)) // lol throttling, actually a TODO
		{
			getChunk(inChunkX, inChunkZ);
			return;
		}
		
		for (int x = inChunkX - chunkNum; x <= inChunkX + chunkNum; x++) {
			for (int z = inChunkZ - chunkNum; z <= inChunkZ + chunkNum; z++) {
				hashKey = String.valueOf(x) + "," + String.valueOf(z);
				if (!chunkMap.containsKey(hashKey)
						&& (Game.deltaTime < 1000.0f / 60.0f)) // lol throttling, actually a TODO eventing
				{
					getChunk(x, z);
					return;
				}
			}
		}
	}
	
	public void getChunk(int offsetX, int offsetZ) {
		String hashKey = String.valueOf(offsetX) + "," + String.valueOf(offsetZ);
		if (sleepingChunks.containsKey(hashKey))
		{
			chunkMap.put(hashKey, sleepingChunks.get(hashKey));
			sleepingChunks.remove(hashKey);
		}
		else
		{
			generateChunk(offsetX, offsetZ);
		}
	}

	public void generateChunk(int offsetX, int offsetZ) {
		// Create the terrain

		String hashKey = String.valueOf(offsetX) + ","
				+ String.valueOf(offsetZ);

		chunkMap.put(hashKey, new Chunk(offsetX, offsetZ, new Vector3(
				Game.CHUNK_SIZE, Game.CHUNK_HEIGHT, Game.CHUNK_SIZE),
				new Vector3f(1.0f, 1.0f, 1.0f), new Vector3f((float) offsetX
						* Game.CHUNK_SIZE, 0.0f, (float) offsetZ
						* Game.CHUNK_SIZE)));

		final int TERRAIN_MAX_HEIGHT = 32;
		final int TERRAIN_MIN_HEIGHT = 12;
		final int TERRAIN_SMOOTH_LEVEL = 0;

		final int TERRAIN_GEN_SEED = seed;
		final float TERRAIN_GEN_NOISE_SIZE = 0.01f;
		final float TERRAIN_GEN_PERSISTENCE = 0.9f;
		final int TERRAIN_GEN_OCTAVES = 6;

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

	public void deleteBlockAt(Vector3f pos) {
		// TODO Auto-generated method stub
		int inChunkX, inChunkZ;
		
		inChunkX = pos.x < 0 ? (int) (pos.x - Game.CHUNK_SIZE) / Game.CHUNK_SIZE : (int) pos.x / Game.CHUNK_SIZE;
		inChunkZ = pos.z < 0 ? (int) (pos.z - Game.CHUNK_SIZE) / Game.CHUNK_SIZE : (int) pos.z / Game.CHUNK_SIZE;
		
		String hashKey = String.valueOf(inChunkX) + "," + String.valueOf(inChunkZ);
		if (chunkMap.containsKey(hashKey)) // lol throttling, actually a TODO
		{
			chunkMap.get(hashKey).deleteBlockAt(pos);
			return;
		}
	}

	public void placeBlockAt(Vector3f coordinates, Vector3f target) {
		// find block intersection
		Vector3f midpoint;
		
		for (int i = 0; i < 10; i++) { // ten passes of precision;
			midpoint = Vector3f.midpoint(coordinates, target);
			if (solidAt(midpoint))
			{
				//still solid, back it up
				target = midpoint;
			} else {
				coordinates = midpoint;
			}
		}
		
		int inChunkX = coordinates.x < 0 ? (int) (coordinates.x - Game.CHUNK_SIZE) / Game.CHUNK_SIZE : (int) coordinates.x / Game.CHUNK_SIZE;
		int inChunkZ = coordinates.z < 0 ? (int) (coordinates.z - Game.CHUNK_SIZE) / Game.CHUNK_SIZE : (int) coordinates.z / Game.CHUNK_SIZE;
		
		String hashKey = String.valueOf(inChunkX) + "," + String.valueOf(inChunkZ);
		if (chunkMap.containsKey(hashKey)) // lol throttling, actually a TODO
		{
			chunkMap.get(hashKey).createBlockAt(coordinates);
			return;
		}
		
	}

	public int getCubeTypeAtVector(Vector3f pos) {
		// TODO Auto-generated method stub
		int inChunkX, inChunkZ;
		
		inChunkX = pos.x < 0 ? (int) (pos.x - Game.CHUNK_SIZE) / Game.CHUNK_SIZE : (int) pos.x / Game.CHUNK_SIZE;
		inChunkZ = pos.z < 0 ? (int) (pos.z - Game.CHUNK_SIZE) / Game.CHUNK_SIZE : (int) pos.z / Game.CHUNK_SIZE;
		
		String hashKey = String.valueOf(inChunkX) + "," + String.valueOf(inChunkZ);
		if (chunkMap.containsKey(hashKey)) // lol throttling, actually a TODO
		{
			return chunkMap.get(hashKey).getCubeTypeAtVector(pos);
		}
		return 0;
	}

}
