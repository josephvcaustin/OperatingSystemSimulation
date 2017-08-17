public class PCB {
	
	/* A PCB includes at least the following information:
		a. job ID
		b. job size
		c. number of CPU bursts predicted at the start of the job
		d. current burst length
		e. time the job entered the SYSTEM
		f. cumulative CPU time used by the job
		g. time of completion of the current I/O operation
	*/
	
	public int ID = 0;
	public int size = 0;
	public int[] bursts = new int[0];
	public int timeIn = 0;
	public int currentBurst = 0; //Index in bursts[] array of current burst
	public int totalCPUTime = 0;
	public int timeIOCompleted = 0; //10 units after being placed in blocked_Q
	public int cpuShots = 0;
	public int qTurns = 0;
	public int currentQ = -1;
	public PCB(int ID, int size, int[] bursts, int timeIn) {
		this.ID = ID; this.size = size; this.bursts = bursts; this.timeIn = timeIn;
	}
}