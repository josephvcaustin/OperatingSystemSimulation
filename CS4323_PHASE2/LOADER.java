import java.util.*;
import java.io.*;
/* Joseph Austin
CS4323 - Operating Systems
Phase 2
4/27/2017
LOADER: This component or module is responsible for initializing new jobs and loading
the "demanded" pages.
1) Initializing new jobs: you will proceed in the same fashion as in Phase 1.


2) Loading the Demanded Pages: */ 
public class LOADER{
	/*Queue of jobs, stored on infinite disk */
	public static ArrayList<String[]> Job_Q = new ArrayList<>();
	/* There are a total of at most 15 PCBs in this system. */
	private static final int MAX_PCBS = 15;
	public void load(String[] job)
	{
		if(job[1].equals("0")) return;
		if(SYSTEM.scheduler.currentPCBs == MAX_PCBS) {Job_Q.add(job); System.out.println("Job "+job[0]+" to jobQ (max pcb's)"); return;}
		if(Job_Q.size() > 0){Job_Q.add(job);
							 for(int i=0;i<Job_Q.size();i++){
								 job=Job_Q.get(i);
								 schedule(job);}}
		else schedule(job);
	}
	/* Load jobs from disk (Job_Q) because there are no arrivals */
	public void load() {
		if(SYSTEM.scheduler.currentPCBs == MAX_PCBS) return;
		else {for(int i=0;i<Job_Q.size();i++){
				String[] job=Job_Q.get(i);
				schedule(job);}
		}
	}
	
	public void schedule(String[] job) {
		/* The loader will call mem_manager.admit() to acquire memory.
			If enough memory is available, the loader will call scheduler.setup()
			to have a PCB created and inserted in the ready queue. */
		int id=Integer.parseInt(job[0]);
		int size=Integer.parseInt(job[1]);
		if(SYSTEM.mem_manager.admit(size)){
			Job_Q.remove(job);
			try{
				String fn = (SYSTEM.filePath + job[2]);
				File PC=new File(fn); 
				System.out.println("Making a PCB");
				PCB pcb = SYSTEM.scheduler.setup(id, size, PC);
				initializePageTable(pcb);}
			catch(Exception e){System.out.println("File " + job[2] + "not found."); return;}
		}
		else {System.out.println("Job "+job[0]+" to jobQ (no more frames)");}
	}
	
	/* The LOADER is also responsible for initializing page tables. The process of initializing 
	a page table for a new job involves two steps:
	a) Setting the v/i bit of as many page table entries as the job contains
	b) Clearing the resident, referenced, and modified bits of the page table. */
	public void initializePageTable(PCB pcb) {
		for(int i=0; i<pcb.PAGE_TABLE_SIZE; i++){
			if(i<pcb.numPages){pcb.pageTable.add(new PageTableEntry(1,0,0,0,0));}
			else pcb.pageTable.add(new PageTableEntry(0,0,0,0,0));
		}
		System.out.println("Job "+pcb.ID+" table init. "+pcb.numPages+" needed.");
	}
	
	/* Once a page fault occurs, the SYSTEM will obtain and allocate
	a free frame for the newly-referenced page. The LOADER is responsible for loading the new page
	in the designated frame (in this simulation, loading a page consists of writing a mesage in the trace file)
	and redefining the appropriate entry in the page table of the job and the FFT. Handling a page fault is
	considered DISK I/O. 
	*/
	public void loadDemandedPage(PageTableEntry page, int frameNum) {
		page.frameNum = frameNum;
		page.resident = 1;
		//Frame was already removed from FFT in mem_manager
	}
	/* Once the loader has finished loading jobs, control is returned to the SYSTEM
	which calls the process scheduler for dispatching jobs. */
}