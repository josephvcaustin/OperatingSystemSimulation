import java.util.*;
public class SCHEDULER {
	private ArrayList<PCB> subqueue1 = new ArrayList<>();
	private final int SUBQUEUE1_TQ = 20;
	private final int SUBQUEUE1_TURNS = 3;
	private ArrayList<PCB> subqueue2 = new ArrayList<>();
	private final int SUBQUEUE2_TQ = 30;
	private final int SUBQUEUE2_TURNS = 5;
	private ArrayList<PCB> subqueue3 = new ArrayList<>();
	private final int SUBQUEUE3_TQ = 50;
	private final int SUBQUEUE3_TURNS = 6;
	private ArrayList<PCB> subqueue4 = new ArrayList<>(); 
	private final int SUBQUEUE4_TQ = 80; 
	private final int SUBQUEUE4_TURNS = Integer.MAX_VALUE; //Infinitely many turns in subqueue4.
	
	public ArrayList<PCB> blocked_Q = new ArrayList<>();
	private final int BLOCKED_Q = 0; //Integer associated with blocked_Q (the 0th queue)
	private final int IO_TIME = 10;
	public int currentPCBs = 0;
	
	public void setup(int ID, int size, int[] bursts) {
		/*If enough memory is available, the loader will call scheduler.setup()
		to have a PCB created and inserted in the top subqueue (subqueue1). */
		PCB pcb = new PCB(ID, size, bursts, SYSTEM.CPU);
		subqueue1.add(pcb);
		currentPCBs++;
	}
	public void update(PCB pcb) {
		
	}
	public String stats(PCB stat) {
		//return String.format("
		return "";
	}
	
	public void dispatch() {
		System.out.println("Active PCBs: " +currentPCBs);
		System.out.println("On Subqueue1: " +subqueue1.size());
		System.out.println("On Subqueue2: " +subqueue2.size());
		System.out.println("On Subqueue3: " +subqueue3.size());
		System.out.println("On Subqueue4: " +subqueue4.size());
		System.out.println("Blocked: " +blocked_Q.size());
		
		/* The scheduler will dispatch the job at the head of the highest priority non-empty subqueue. */
		if(!(subqueue1.isEmpty())) {
			execute(subqueue1, 1, SUBQUEUE1_TQ, SUBQUEUE1_TURNS, subqueue1.get(0));
		}
		else if(!(subqueue2.isEmpty())) {
			execute(subqueue2, 2, SUBQUEUE2_TQ, SUBQUEUE2_TURNS, subqueue2.get(0));
		}
		else if(!(subqueue3.isEmpty())) {
			execute(subqueue3, 3, SUBQUEUE3_TQ, SUBQUEUE3_TURNS, subqueue3.get(0));
		}
		else if(!(subqueue4.isEmpty())) {
			execute(subqueue4, 4, SUBQUEUE4_TQ, SUBQUEUE4_TURNS, subqueue4.get(0));
		}
		
		checkBlocked();
		
	}
	public void execute(ArrayList<PCB> Q, int qCur, int TQ, int TURNS, PCB dis) {
		dis.cpuShots++;
		dis.qTurns++;
		dis.currentQ = qCur;
		System.out.println("Job " + dis.ID + " dispatched from subqueue" + qCur + ". " + dis.qTurns + " turns on this queue, " + dis.cpuShots + " CPU shots total.");
		if(dis.bursts[dis.currentBurst] <= TQ) {
			/* The residency rules specify that if a process places an I/O request before its time quantum expires, the
			process remains in the same subqueue and the count of turns in that particular subqueue will be reset to zero. */
			if(dis.bursts[dis.currentBurst] < TQ) {
				System.out.println("Job " + dis.ID + " to blocked_Q, will remain in subqueue" + qCur);
				dis.qTurns = 0;
			}
			
			SYSTEM.CPU += dis.bursts[dis.currentBurst]; //Increment system clock by the time the job used.
			System.out.println("Job " + dis.ID + " used " + dis.bursts[dis.currentBurst] + " time on the CPU.");
			dis.bursts[dis.currentBurst] = 0;
			dis.currentBurst++; //Done with this burst, go to next
			
			/* If a job terminates, the CPU is released and control is transferred to the SYSTEM.
			The SYSTEM will call scheduler.stats() to output job termination statistics to the 
			JOB_LOG file. The SYSTEM will then call mem_manager.release(). */
			if(dis.currentBurst == dis.bursts.length) {
				System.out.println("Job " + dis.ID + " completed at " + SYSTEM.CPU);
				Q.remove(dis);
				currentPCBs--;
				SYSTEM.terminate(dis);
			}
			
			/* If a job requests I/O, it releases the CPU and places the PCB in the blocked_Q.
			The job will remain on the blocked list for 10 virtual time units (time for I/O completion). */
			else { //Send to blocked_Q for I/O
				System.out.println("Job " + dis.ID + " to blocked_Q, next burst (" + dis.currentBurst + ") has length " + dis.bursts[dis.currentBurst]);
				dis.timeIOCompleted = SYSTEM.CPU + IO_TIME; //Done blocking at time = CPU + IO_TIME
				transfer(dis.currentQ, BLOCKED_Q, dis);
				
			}
		}
		/* If a job uses all its quantum and still needs more CPU time tom complete its 
		current CPU burst, the job loses the CPU and the scheduler will append the job
		that lost the CPU to the appropriate subqueue within the ready_Q. */
		else
		{
			System.out.println("Job " + dis.ID + " used " + TQ + " time on the CPU.");
			dis.bursts[dis.currentBurst] -= TQ;
			System.out.println("Job " + dis.ID + " exceeded quantum, burst " + dis.currentBurst + " has " + dis.bursts[dis.currentBurst] + " remaining.");
			SYSTEM.CPU += TQ;
			if(dis.qTurns > TURNS) { //Turns are up, place on the next queue.
				dis.qTurns = 0;
				transfer(qCur, (qCur+1), dis);
			}
			else transfer(qCur, qCur, dis); //Place at the back of current queue.
		}
	}
	public void transfer(int qFrom, int qTo, PCB dis) {
		System.out.println("Job " + dis.ID + " transfered from subqueue" + qFrom + " to subqueue" + qTo);
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
	public void block(PCB dis)
	{
		transfer(dis.currentQ, BLOCKED_Q, dis);
	}
	public void checkBlocked()
	{
		for(int i = 0; i<blocked_Q.size(); i++)
		{
			PCB job = blocked_Q.get(i);
			if(SYSTEM.CPU > job.timeIOCompleted) { //If it is done waiting for I/O
				/* A process will remain in the last subqueue (subqueue4) until it terminates, or until it 
				places an I/O request. When an I/O request is placed, the process will be moved at once to the 
				top subqueue (subqueue1). */
				if(job.currentQ == 4) transfer(BLOCKED_Q, 1, job);
				else transfer(BLOCKED_Q, job.currentQ, job);
				System.out.println("Job " + job.ID + " finished blocking at time " + job.timeIOCompleted);
				i--;
			}
		}
	}
	public void unblock(PCB dis)
	{
		/* A process will remain in the last subqueue (subqueue4) until it terminates, or until it 
		places an I/O request. When an I/O request is placed, the process will be moved at once to the 
		top subqueue (subqueue1). */
		if(dis.currentQ == 4) transfer(BLOCKED_Q, 1, dis);
		else transfer(BLOCKED_Q, dis.currentQ, dis);
	}
}