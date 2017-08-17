import java.util.*;
import java.io.*;
/* Joseph Austin
CS4323 - Operating Systems
Phase 2
4/27/2017
SCHEDULER: After the loader places jobs in memory, the scheduler
allocates CPU time for jobs and shifts jobs between ready queues and
the blocked queue. */
public class SCHEDULER {
	public ArrayList<PCB> subqueue1 = new ArrayList<>();
	/* You are to use the following subqueue quantum sizes for the scheduler in
	phase 2 of the project: 10, 16, 24, and 40. */
	public final int SUBQUEUE1_TQ = 10; //Time quantum for SQ1
	public final int SUBQUEUE1_TURNS = 3; //# of turns allowed in SQ1
	public ArrayList<PCB> subqueue2 = new ArrayList<>();
	public final int SUBQUEUE2_TQ = 16; //Time quantum for SQ2
	public final int SUBQUEUE2_TURNS = 5; //# of turns allowed in SQ2
	public ArrayList<PCB> subqueue3 = new ArrayList<>();
	public final int SUBQUEUE3_TQ = 24; //Time quantum for SQ3
	public final int SUBQUEUE3_TURNS = 6; //# of turns allowed in SQ3
	public ArrayList<PCB> subqueue4 = new ArrayList<>(); 
	public final int SUBQUEUE4_TQ = 40; //Time quantum for SQ4
	public final int SUBQUEUE4_TURNS = Integer.MAX_VALUE; //Infinitely many turns in subqueue4.
	public ArrayList<PCB> blocked_Q = new ArrayList<>();
	public final int BLOCKED_Q = 0; //Integer associated with blocked_Q (the 0th queue)
	public final int IO_TIME = 10; //Burst time for I/O
	public int currentPCBs = 0; //# of active PCB's
	
	public PCB setup(int ID, int size, File PC) throws Exception{
		/*If enough memory is available, the loader will call scheduler.setup()
		to have a PCB created and inserted in the top subqueue (subqueue1). */
		PCB pcb = new PCB(ID, size, PC);
		subqueue1.add(pcb);
		currentPCBs++; 
		System.out.println("Job "+ID+" scheduled. CurrentPCBs="+currentPCBs);
		return pcb;}

	public void dispatch() throws Exception {
		/* The scheduler will dispatch the job at the head of the highest priority non-empty subqueue. */
		if(!(subqueue1.isEmpty())) execute(subqueue1, 1, SUBQUEUE1_TQ, SUBQUEUE1_TURNS, subqueue1.get(0));
		else if(!(subqueue2.isEmpty())) execute(subqueue2, 2, SUBQUEUE2_TQ, SUBQUEUE2_TURNS, subqueue2.get(0));
		else if(!(subqueue3.isEmpty())) execute(subqueue3, 3, SUBQUEUE3_TQ, SUBQUEUE3_TURNS, subqueue3.get(0));
		else if(!(subqueue4.isEmpty())) execute(subqueue4, 4, SUBQUEUE4_TQ, SUBQUEUE4_TURNS, subqueue4.get(0));
		/* After allocating time, check the blocked_Q for jobs that finished I/O */
		checkBlocked(); }
	
	/* After dispatching a process, give it time on the CPU and reevaluate
	its queue assignment. */
	public void execute(ArrayList<PCB> Q, int qCur, int TQ, int TURNS, PCB dis) throws Exception {
		System.out.println("Dispatch job "+dis.ID+" from Q"+qCur);
		dis.cpuShots++;
		dis.qTurns++;
		dis.currentQ = qCur;
		int timeUsed = 0;
		boolean blocked=false;
		while(timeUsed < TQ){
			/* If a job terminates, the CPU is released and control is transferred to the SYSTEM.*/
			if(dis.currentRef==dis.refs.size()){
				Q.remove(dis);
				SYSTEM.terminate(dis);
				blocked=true;
				break;}
			timeUsed+=SYSTEM.cpu.EXEC_TIME; //Add time used
			dis.totalCPUTime+=SYSTEM.cpu.EXEC_TIME; //Add time used
			String ref=dis.refs.get(dis.currentRef);
			char refType = ref.charAt(0);
			int pageNum = Integer.parseInt(ref.substring(1).trim());
			System.out.println("Ref "+refType+pageNum);
			boolean pageFault = SYSTEM.cpu.execute(dis.pageTable.get(pageNum), refType); //Exec the instruction and give the page
			/* If a job requests I/O, it releases the CPU and places the PCB in the blocked_Q.
			The job will remain on the blocked list for 10 virtual time units (time for I/O completion). */
			/* The residency rules specify that if a process places an I/O request before its time quantum expires, the
			process remains in the same subqueue and the count of turns in that particular subqueue will be reset to zero. */
			/* Once a page fault occurs, the SYSTEM will obtain and allocate a free frame for the newly-referenced page. */
			if(pageFault) {
				dis.faults++; SYSTEM.handleFault(dis, pageNum); 
				System.out.println("Job "+dis.ID+" to blockedQ."); /*dis.qTurns = 0;*/ block(dis); blocked=true; break;}
			else {
				dis.currentRef++; //Point to next instruction
			}
			
			if((ref.charAt(0)=='w')||(ref.charAt(0)=='r')) {System.out.println("Job "+dis.ID+" to blockedQ."); /*dis.qTurns = 0;*/ block(dis); blocked=true; break;}
			
		}
		if(blocked) return;
		/* If a job uses all its quantum and still needs more CPU time to complete its 
		current CPU burst, the job loses the CPU and the scheduler will append the job
		that lost the CPU to the appropriate subqueue within the ready_Q. */
		if(dis.qTurns > TURNS) { //Turns are up, place on the next queue.
			System.out.println("Job "+dis.ID+" used up turns on Q"+qCur);
			dis.qTurns = 0;
			transfer(qCur, (qCur+1), dis);}
		else {System.out.println("Job "+dis.ID+" used up time on Q"+qCur); transfer(qCur, qCur, dis);} //Place at the back of current queue.
	}
	
	/* If a process needs to switch queues, transfer it. */
	public void transfer(int qFrom, int qTo, PCB dis) {
		switch(qTo) {
			case BLOCKED_Q: blocked_Q.add(dis); break;
			case 1: subqueue1.add(dis); break;
			case 2: subqueue2.add(dis); break;
			case 3: subqueue3.add(dis); break;
			case 4: subqueue4.add(dis); break;}
		switch(qFrom) {
			case BLOCKED_Q: blocked_Q.remove(dis); break;
			case 1: subqueue1.remove(dis); break;
			case 2: subqueue2.remove(dis); break;
			case 3: subqueue3.remove(dis); break;
			case 4: subqueue4.remove(dis); break;}
	}
	
	/* If a process finishes a burst and requests I/O, transfer to blocked_Q. */
	public void block(PCB dis) { 
		dis.totalCPUTime += IO_TIME;
		dis.timeIOCompleted = SYSTEM.cpu.TIME + IO_TIME;
		transfer(dis.currentQ, BLOCKED_Q, dis);}
		
	public void unblock(PCB dis){
		System.out.println("Job "+dis.ID+" done blocking at time "+dis.timeIOCompleted);
		if(dis.currentQ == 4) transfer(BLOCKED_Q, 1, dis);
		else transfer(BLOCKED_Q, dis.currentQ, dis);}
	
	/* Check if any processes in blocked_Q have finished I/O. */
	public void checkBlocked() {
		if((blocked_Q.size() == currentPCBs)||(blocked_Q.size()==15)) { SYSTEM.cpu.TIME+=1; } //If ready queue is empty, increment CPU
		for(int i = 0; i<blocked_Q.size(); i++) {
			PCB job = blocked_Q.get(i);
			if(SYSTEM.cpu.TIME > job.timeIOCompleted) { unblock(job); i--;}
		}
	}
}