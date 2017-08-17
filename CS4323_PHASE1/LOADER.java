import java.util.*;

/* The loader will be responsible for loading jobs into memory. */

public class LOADER{
	public ArrayList<int[]> Job_Q = new ArrayList<>();
	/* There are a total of at most 15 PCBs in this system. */
	private final int MAX_PCBS = 15;
	private final int SUCCESSFUL = 1;
	
	/* The loader will try to load jobs i) as long as there is enough memory available 
	and the number of PCBs is fewer than 15, and ii) as long as there are incoming job
	requests. */
	public void load(int[] jobArrival) {
		System.out.println("Jobs on queue: " + Job_Q.size());
		if(jobArrival[1] == 0) return; //Null job
		if(SYSTEM.scheduler.currentPCBs == MAX_PCBS) {
			/* If the loader encounters a new arrival that cannot be loaded immediately,
			the request will be queued (placed on the Job_Q) */
			System.out.println("Job " + jobArrival[0] + " placed on Job_Q (too many PCBs).");
			Job_Q.add(jobArrival);
			return;
		}
		/* Every time the loader is called to load a job, it will first consider the
		load requests on the Job_Q before considering any new arrivals. */
		else if(Job_Q.size() > 0)
		{
			Job_Q.add(jobArrival);
			System.out.println("Job " + jobArrival[0] + " placed on Job_Q (not enough memory).");
			for(int i=0; i<Job_Q.size(); i++) {
				int[] job = Job_Q.get(i);
				int ID = job[0];
				int size = job[1];
				
				/* The loader will call mem_manager.allocate() to acquire memory.
				If enough memory is available, the loader will call scheduler.setup()
				to have a PCB created and inserted in the ready queue. */
				if(SYSTEM.mem_manager.allocate(size) == SUCCESSFUL) {
					int[] bursts = Arrays.copyOfRange(job, 2, job.length);
					SYSTEM.scheduler.setup(ID, size, bursts);
					Job_Q.remove(job);
					System.out.println("Job " + ID + " loaded into memory from Job_Q.");
					if(SYSTEM.scheduler.currentPCBs == MAX_PCBS) break;
				}
			}
		}
		else {
			int ID = jobArrival[0];
			int size = jobArrival[1];
			
			/* The loader will call mem_manager.allocate() to acquire memory.
			If enough memory is available, the loader will call scheduler.setup()
			to have a PCB created and inserted in the ready queue. */
			if(SYSTEM.mem_manager.allocate(size) == SUCCESSFUL) {
				int[] bursts = Arrays.copyOfRange(jobArrival, 2, jobArrival.length);
				SYSTEM.scheduler.setup(ID, size, bursts);
				System.out.println("Job " + ID + " loaded into memory from arrivals.");
			}
			else {
				Job_Q.add(jobArrival);
				System.out.println("Job " + jobArrival[0] + " placed on Job_Q (not enough memory).");
			}
		}
	}
	public void load() {
		System.out.println("Jobs on queue: " + Job_Q.size());
		if(SYSTEM.scheduler.currentPCBs == MAX_PCBS) {
			/* If the loader encounters a new arrival that cannot be loaded immediately,
			the request will be queued (placed on the Job_Q) */
			System.out.println("No job loaded (too many PCBs).");
			return;
		}
		/* Every time the loader is called to load a job, it will first consider the
		load requests on the Job_Q before considering any new arrivals. */
		else
		{
			for(int i=0; i<Job_Q.size(); i++) {
				int[] job = Job_Q.get(i);
				int ID = job[0];
				int size = job[1];
				/*If enough memory is available, the loader will call scheduler.setup()
				to have a PCB created and inserted in the ready queue. */
				if(SYSTEM.mem_manager.allocate(size) == SUCCESSFUL) {
					int[] bursts = Arrays.copyOfRange(job, 2, job.length);
					SYSTEM.scheduler.setup(ID, size, bursts);
					Job_Q.remove(job);
					System.out.println("Job " + ID + " loaded into memory from Job_Q.");
					if(SYSTEM.scheduler.currentPCBs == MAX_PCBS) break;
				}
			}
		}
	}
	/* Once the loader has finished loading jobs, control is returned to the SYSTEM
	which calls the process scheduler for dispatching jobs. */
}