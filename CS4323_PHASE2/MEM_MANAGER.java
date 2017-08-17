/* Joseph Austin
CS4323 - Operating Systems
Phase 2
4/27/2017
MEMORY MANAGER: This component is responsible for maintaining the
free frame table as well as allocating and releasing memory */
import java.util.*;
public class MEM_MANAGER {
	/* Paged memory with 128 allocatable page frames */
	public static final int NUMBER_OF_FRAMES = 128;
	/* Each page frame is 256 bytes long. */
	public static final int PAGE_FRAME_SIZE = 256;
	/* As with phase 1, you will define your own limit as to when memory is "used up" */
	public static final int MIN_FRAMES = 13; //Leave 10% of mem free
	
	/*FFT is to be maintained in memory as a linked list. 
	Each free page frame will have a pointer field pointing to the next free frame. 
	The free frame table stores the frame numbers of all available frames.*/
	public static LinkedList<Integer> freeFrameTable = new LinkedList<>();
	
	/* init: Invoked at the system start-up time, it initializes the free frame table. */
	/* Initially, frames 0 through 127 are free. */
	public MEM_MANAGER(){init();}
	public void init() {for(int i = 0; i<NUMBER_OF_FRAMES; i++) {freeFrameTable.add(i);}}
	
	/* admit: Invoked by LOADER, it will return a boolean value indicating
	whether a request to initialize a new job has been accepted. A false
	flag indicates that there is not enough memory available to load the 
	current request. However, the LOADER will keep trying to initialize
	new requests until MEM_MANAGER sends a flag indicating that it has completely run out of memory. 
	It is not necessary to load any pages into any frames at the time of load. Actual page loading into
	frames takes place when a page is actually referenced. */
	public boolean admit(int length) {
		if(freeFrameTable.size() < MIN_FRAMES) {System.out.println("No more free frames."); return false;} //Already full
		//Take length and see how many pages that translates to
		//If there's space for 1/4 of those pages, then go for it
		double pagesNeeded = Math.ceil(length/PAGE_FRAME_SIZE);
		int oneFourthPagesNeeded = (int)Math.ceil(pagesNeeded/4);
		if(freeFrameTable.size() > oneFourthPagesNeeded) {System.out.println("Job will fit in memory (" +oneFourthPagesNeeded+" pages needed."); return true;}
		else {System.out.println("Job will not fit in memory (" +oneFourthPagesNeeded+" pages needed."); return false;}
	}
	/* allocate:  The SYSTEM will call MEM_MANAGER.allocate() for the purpose of obtaining a free frame.
	The allocate entry will return the address of the free frame at the head of the free frame table.*/
	public int[] allocate() {
		if(freeFrameTable.size()==0){/*Mem full!*/return new int[]{-1,-1};}
		int freeFrameNum=freeFrameTable.getFirst();//Grab the element at the head of the list
		freeFrameTable.remove(); //It's no longer free!
		return new int[]{freeFrameNum, -1};
	}
	/* release:  Given a pointer that points to the page table of a job, MEM_MANAGER will release the allocated
	frames. Before memory is released, usage statistics should be logged to the mem_stat file.*/
	public void release(ArrayList<PageTableEntry> pcbPageTable) {
		for(PageTableEntry entry:pcbPageTable){
			if(entry.resident == 1) freeFrameTable.add(entry.frameNum);} //Add resident frames back into the FFT
	}
	
	/* stats:  Invoked by the SYSTEM, this entry will give the utilization status of the memory in terms of the number
	of free frames divided by the total number of frames, and the number of allocated or occupied frames divided
	by the total number of frames.*/
	public String stats() {
		return "---------------------------------------------------------------------------------------------------------\nFree Frames/Total Frames = " + ((double)freeFrameTable.size()/NUMBER_OF_FRAMES) + ", Used Frames/Total Frames = " + ((double)(NUMBER_OF_FRAMES-freeFrameTable.size())/NUMBER_OF_FRAMES) + "\n---------------------------------------------------------------------------------------------------------\n";
	}
}