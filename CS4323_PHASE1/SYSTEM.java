import java.util.*; import java.io.*;
public class SYSTEM {
	public static int CPU = 0; //Clock cycles of CPU (current time)
	
	public static LOADER loader = new LOADER();
	public static MEM_MANAGER mem_manager = new MEM_MANAGER();
	public static SCHEDULER scheduler = new SCHEDULER();
	
	public static ArrayList<Integer> completedJobs = new ArrayList<>();
	
	public static PrintWriter sys_log = null;
	public static PrintWriter job_log = null;
	public static final int SYS_LOG_INTERVAL = 200;
	public static int nextSysLog = SYS_LOG_INTERVAL;
	
	public static void main(String[] args) {
		Scanner s = null;
		try { 
			s = new Scanner(new File(args[0])); 
			sys_log = new PrintWriter(new File("SYS_LOG.txt"));
			job_log = new PrintWriter(new File("JOB_LOG.txt"));
		}
		catch(FileNotFoundException e) { System.exit(0); }
		
		sys_log.println("CPU Time | Mem_Allocated | Mem_Free | Jobs in Job_Q | Jobs in ready_Q | Jobs in blocked_Q| # of Jobs Completed");
		sys_log.println("---------|---------------|----------|---------------|-----------------|------------------|--------------------");
		
		while(s.hasNextLine() || (scheduler.currentPCBs > 0) || (loader.Job_Q.size() > 0)) {
			System.out.println("CPU = " + CPU);
			if(s.hasNextLine()) { //While not EOF
				String[] tokens = s.nextLine().split(" ");
				int[] job = new int[tokens.length];
				for(int i=0; i<tokens.length; i++) { job[i] = Integer.parseInt(tokens[i]); }
				loader.load(job);
			}
			else if(loader.Job_Q.size() > 0){ //Jobs sitting on disk
				loader.load();
			}
			
			/* Once the loader has finished loading jobs, control is returned to the SYSTEM
			which calls the process scheduler for dispatching jobs. */
			scheduler.dispatch();
			
			if(CPU >= nextSysLog) {
				sys_log.println(mem_manager.stats());
				nextSysLog+=SYS_LOG_INTERVAL;
			}
			System.out.println();
		}
		System.out.println(Arrays.toString(completedJobs.toArray()));
		System.out.println(completedJobs.size() + " jobs completed.");
		sys_log.println(mem_manager.stats());
		sys_log.println("Final CPU time: " + CPU);
		sys_log.close();
		job_log.close();
	}
	/* If a job terminates, the CPU is released and control is transferred to the SYSTEM.
	The SYSTEM will call scheduler.stats() to output job termination statistics to the 
	JOB_LOG file. The SYSTEM will then call mem_manager.release(). */
	public static void terminate(PCB term) {
		completedJobs.add(term.ID);
		job_log.println(scheduler.stats(term));
		mem_manager.release(term.size);
	}
}