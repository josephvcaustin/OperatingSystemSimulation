/* Joseph Austin
CS4323 - Operating Systems
Phase 2
4/27/2017
PCB: Object representing a process control block (PCB).
Stores information associated with a process to be loaded
and scheduled.
*/
import java.io.*;
import java.util.*;
public class PCB {
	/* A PCB includes at least the following information:
		a. job ID
		b. job size
		c. number of CPU bursts predicted at the start of the job
		d. current burst length
		e. time the job entered the SYSTEM
		f. cumulative CPU time used by the job
		g. time of completion of the current I/O operation*/
	public int ID = 0; //Job number
	public int size = 0; //Memory required
	public int timeIn = 0; //Time arrived
	public int totalCPUTime = 0; //Total time used up on CPU
	public int timeIOCompleted = 0; //10 units after being placed in blocked_Q
	public int cpuShots = 0; //Number of tries at CPU
	public int qTurns = 0; //Number of turns in current queue
	public int currentQ = -1; //Number of current queue (1, 2, 3, or 4)
	/* PHASE 2 */
	/* The PCB maintains, where necessary, such information as:
		a. jobId
		b. PC value
		c. size in bytes
		d. size in pages
		e. page table base addess
		f. number of page faults
		g. number of replacements (clean/dirty) */
	/* The reference string file name will be used to open a file with that 
	name in the aforementioned directory. This file pointer will be copied 
	into the PC field of the relevant PCB. */
	public File PC;
	public ArrayList<String> refs = new ArrayList<>();;
	public int currentRef=0;
	public int numPages = 0;
	public int oneFourthPagesNeeded = 0;
	public static final int PAGE_TABLE_SIZE = 128;
	public static final int PAGE_SIZE = 256;
	public ArrayList<PageTableEntry> pageTable = new ArrayList<>(PAGE_TABLE_SIZE);
	public int fragmentation = 0;
	public int residentPages = 0;
	public int faults=0;
	public int cleanReplace=0;
	public int dirtyReplace=0;
	
	public PCB(int id, int s, File p) throws Exception {
		ID=id; size=s; PC=p; timeIn=SYSTEM.cpu.TIME; 
		numPages = (int)Math.ceil((double)(size/PAGE_SIZE))+1; 
		//Take length and see how many pages that translates to
		//If there's space for 1/4 of those pages, then go for it
		oneFourthPagesNeeded = (int)Math.ceil((double)numPages/4);
		if(numPages > PAGE_TABLE_SIZE){SYSTEM.errorMsg=("Job "+id+" is too big for its page table."); SYSTEM.errorPCB = this; throw new Exception();}
		fragmentation = size%PAGE_SIZE;
		try{ Scanner scan=new Scanner(PC);
			while(scan.hasNextLine()){
				String ref=scan.nextLine().trim();
				refs.add(ref);
			}
		}
		catch(FileNotFoundException e) {System.out.println("File not found."); System.exit(0);}
	}
}