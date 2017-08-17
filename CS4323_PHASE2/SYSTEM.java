import java.util.*; import java.io.*;
/* Joseph Austin
CS4323 - Operating Systems
Phase 1
3/23/2017
SYSTEM: Main driver of the simulation. Hosts each module
(LOADER, MEM_MANAGER, and SCHEDULER) and orchestrates information
transfer between each module. 
usage: java SYSTEM input.txt > output.txt */
public class SYSTEM {
	public static LOADER loader = new LOADER();
	public static MEM_MANAGER mem_manager = new MEM_MANAGER();
	public static SCHEDULER scheduler = new SCHEDULER();
	public static CPU cpu = new CPU();
	public static REPLACER replacer = new REPLACER();
	public static ArrayList<Integer> completedJobs = new ArrayList<>(); //Completed job ID's
	public static PrintWriter mem_stat;
	public static PrintWriter trace;
	public static PCB errorPCB = null;
	public static String errorMsg = "";
	public static String filePath = "";
	public static void main(String[] args) {
			Scanner s = null; //Used for reading from job input file
			try { 
				File f = new File(args[0]);
				String path = f.getAbsolutePath();
				filePath = path.substring(0, (path.length()-8));
				s = new Scanner(f); 
				mem_stat = new PrintWriter(new File("mem_stat_file"));
				trace = new PrintWriter(new File("trace_file")); }
			catch(FileNotFoundException e) { System.exit(0); }
			mem_stat.println("|JOB_ID|JOB_SIZE|# of Frames|Internal Frag|Ref String Length|Page Faults|# Clean Replace|# Dirty Replace|");
			mem_stat.println("|------|--------|-----------|-------------|-----------------|-----------|---------------|---------------|");
			trace.println("|Job ID|Placed/Replaced Page|Frame#|Referenced?|Modified?|");
			trace.println("|------|--------------------|------|-----------|---------|");
			//While (not EOF) or (jobs remain to be completed) or (jobs remain in Job_Q)
			while(s.hasNextLine() || (scheduler.currentPCBs > 0) || (loader.Job_Q.size() > 0) ||(scheduler.blocked_Q.size()>0)) {
				try{
					System.out.println("Time: " +SYSTEM.cpu.TIME);
					if(s.hasNextLine()) { //If not EOF
						String[] job = new String[3];
						System.out.println("Job arrival ");
						String line = s.nextLine();
						job[0] = line.substring(0, 4).trim();
						System.out.println(job[0]);
						if(!job[0].equals("0")){
							job[1] = line.substring(4, 12).trim();
							System.out.println(job[1]);
							job[2] = line.substring(12).trim();
							System.out.println(job[2]);}
						else{job[1] = "0"; job[2] = "0";}
						loader.load(job);
					}
					else if(loader.Job_Q.size() > 0){loader.load();}
					/* Once the loader has finished loading jobs, control is returned to the SYSTEM
					which calls the process scheduler for dispatching jobs. */
					scheduler.dispatch();
					/*Dispatching a job may lead to a fault. SCHEDULER will call SYSTEM.handleFault()*/
				}
				catch(Exception e){
					System.out.println("ABNORMAL TERMINATION: Job "+errorPCB.ID);
					mem_stat.println("ABNORMAL TERMINATION: Job "+errorPCB.ID+", error: "+errorMsg);
					terminate(errorPCB);
					continue;
				}
			}
			System.out.println("Completed "+completedJobs.size()+" jobs.");
			mem_stat.close(); trace.close(); 
		
			
	}
	public static void handleFault(PCB job, int pageNum) throws Exception {
		System.out.println("Page "+pageNum+" from job "+job.ID+" faulted (ref'd but not resident)");
		PageTableEntry page = job.pageTable.get(pageNum);
		int[] ret = new int[2];
		int frameNum = 0;
		int reference = page.reference;
		int modified = page.modified;
		if (page.vi == 0) {errorMsg=("Address exception, page "+pageNum+" invalid."); errorPCB=job; throw new Exception();}
		//If the job is fully in memory, call the replacer
		if(job.residentPages >= job.oneFourthPagesNeeded) ret = replacer.replace(job);
		//Otherwise, call mem_manager
		else {ret = mem_manager.allocate(); 
			if(ret[0] == -1) ret = replacer.replace(job);
			else job.residentPages++;}
		/*Once the system finds a replacement page, it will call the LOADER and pass
		to it the page table, the page number of page to be loaded, and the page number of the replacement page*/
		frameNum = ret[0];
		int replacedPage = ret[1];
		System.out.println("Load demanded: page "+pageNum+" from job "+job.ID+" moved to frame "+frameNum);
		trace.printf("|%6d|P:%7d R:%7d |%6d|%11d|%9d|%n", job.ID, pageNum, replacedPage, frameNum, reference, modified);
		loader.loadDemandedPage(page, frameNum);
	}	
	/* If a job terminates, the CPU is released and control is transferred to the SYSTEM.
	The SYSTEM will call scheduler.stats() to output job termination statistics to the 
	JOB_LOG file. The SYSTEM will then call mem_manager.release() */
	public static void terminate(PCB term) {
		scheduler.currentPCBs--;
		completedJobs.add(term.ID);
		scheduler.subqueue1.remove(term);
		scheduler.subqueue2.remove(term);
		scheduler.subqueue3.remove(term);
		scheduler.subqueue4.remove(term);
		scheduler.blocked_Q.remove(term);
		System.out.println("Job "+term.ID+" finished at "+cpu.TIME);
		System.out.println("CurrentPCBs="+scheduler.currentPCBs);
		//Memory utilization will also be written at set intervals every 4th job delivered.
		//This information will be written by the stats entry of MEM_MANAGER
		mem_stat.printf("|%6d|%8d|%11d|%13d|%17d|%11d|%15d|%15d|%n", term.ID, term.size, term.residentPages, term.fragmentation, term.refs.size(), term.faults, term.cleanReplace, term.dirtyReplace);
		if (completedJobs.size()%4==0) {
			mem_stat.println(mem_manager.stats());
			mem_stat.println("|JOB_ID|JOB_SIZE|# of Frames|Internal Frag|Ref String Length|Page Faults|# Clean Replace|# Dirty Replace|");
			mem_stat.println("|------|--------|-----------|-------------|-----------------|-----------|---------------|---------------|");}
		mem_manager.release(term.pageTable);
		term.currentQ = -1;
	}
		
}