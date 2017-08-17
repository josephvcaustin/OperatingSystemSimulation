/* MEMORY MANAGER: This subprogram mem_manager simulates the memory management functions of the system.
This subprogram is to have four entry points. */

public class MEM_MANAGER {
	/* For phase I, the memory is viewed as 512 allocatable units. */
	public static final int SIZE = 512;
	public static final int MINIMUM_AVAILABLE = 51; //Minimum 10% available
	private int mem_available = SIZE;
	
	/* allocate: Which is given the required allocation length, and, depending on whether or not
		the request is honored, it will return a 1 to indicate a success and 0 overwise. */
	public int allocate(int length) {
		if ((mem_available >= MINIMUM_AVAILABLE) && (mem_available >= length)) {
			mem_available -= length;
			System.out.println("Available memory: " + mem_available);
			return 1; }
		else return 0;
	}
	
	/* release: Which is given the size of a deallocation and. as a result, the total available 
	memory will be incremented by the given size. */
	public void release(int size) { 
		mem_available += size; 
		System.out.println("Available memory: " + mem_available);
		}
		
	/* setup: Which is used for the initialization purposes. */
	public void setup() {
		//Setup called using default constructor.
	}
	
	/* stats: Which outputs the memory stats every 200 virtual time units to the system output
	file called SYS_LOG */
	public String stats() {
		return String.format("%9d|%15d|%10d|%15d|%17d|%18d|%20d", SYSTEM.CPU, (SIZE-mem_available), mem_available, 
								SYSTEM.loader.Job_Q.size(), (SYSTEM.scheduler.currentPCBs-SYSTEM.scheduler.blocked_Q.size()), SYSTEM.scheduler.blocked_Q.size(), SYSTEM.completedJobs.size());
	}
}