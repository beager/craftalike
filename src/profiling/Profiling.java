package profiling;

import java.util.LinkedList;
import java.util.List;

public class Profiling {

	private long frameBeginTime = 0;
	private long frameEndTime = 0;
	
	private List<ProfilingPart> parts = new LinkedList<ProfilingPart>();
	
	public Profiling() {
		
	}
	
	/* Should be called in the beginning of a frame. */
	public void frameBegin() {
		parts.clear();
		frameBeginTime = System.currentTimeMillis();
	}
	
	/* Should be called to register the start of a new part of the program. */
	public void partBegin(ProfilingPart pp) {
		pp.partBeginTime = System.currentTimeMillis();
	}
	
	/* Should be called to register the end of a part of the program. */
	public void partEnd(ProfilingPart pp) {
		parts.add(pp);
		pp.partEndTime = System.currentTimeMillis();
	}
	
	/* Should be called in the end of a frame. */
	public void frameEnd() {
		frameEndTime = System.currentTimeMillis();
		
		long frameTime = frameEndTime - frameBeginTime;
		System.out.println("-- NEW FRAME: " + frameTime + " MS (" + fps() + "FPS --");
		
		float otherPercent = 100.0f;
		long otherTime = frameTime;
		
		for(ProfilingPart pp : parts) {
			long partTime = pp.partEndTime - pp.partBeginTime;
			float percent = (((float)partTime) / ((float)frameTime)) * 100.0f;
			System.out.println("\t" + pp.name + " - " + partTime + " MS (" + percent + " %)");
			otherPercent -= percent;
			otherTime -= partTime;
		}
		
		System.out.println("\tOTHER - " + otherTime + " MS (" + otherPercent + " %)");
	}
	
	public float fps() {
		return 1000.0f / (frameEndTime - frameBeginTime);
	}
}
