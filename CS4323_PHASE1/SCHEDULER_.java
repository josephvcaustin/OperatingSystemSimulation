import java.util.*;
public class SCHEDULER {
	private ArrayList<PCB> subqueue1 = new ArrayList<>();
	private final int SUBQUEUE1_TQ = 20;
	private final int SUBQUEUE1_TURNS = 3;
	private ArrayList<PCB> subqueue2 = new ArrayList<>();
	private final int SUBQUEUE2_TQ = 30;
	private final int SUBQUEUE1_TURNS = 5;
	private ArrayList<PCB> subqueue3 = new ArrayList<>();
	private final int SUBQUEUE3_TQ = 50;
	private final int SUBQUEUE1_TURNS = 6;
	private ArrayList<PCB> subqueue4 = new ArrayList<>(); 
	private final int SUBQUEUE4_TQ = 80;
	
	private ArrayList<PCB> blocked_Q = new ArrayList<>();
	private final int IO_TIME = 10;
	public int currentPCBs = 0;
	
	public void setup(int ID, int size, int[] bursts) {
		/*If enough memory is available, the loader will call scheduler.setup()
		to have a PCB created and inserted in the ready queue. */
		PCB p = new PCB(ID, size, bursts, SYSTEM.CPU);
		subqueue1.add(p);
		currentPCBs++;
	}
	public void update(PCB pcb ) {
		
	}
	public String stats(PCB stat) {
		return String.format("
	}
	
	public void dispatch() {
		System.out.println("Active PCBs: " +currentPCBs);
		System.out.println("On Subqueue1: " +subqueue1.size());
		System.out.println("On Subqueue2: " +subqueue2.size());
		System.out.println("On Subqueue3: " +subqueue3.size());
		System.out.println("On Subqueue4: " +subqueue4.size());
		System.out.println("Blocked: " +blocked_Q.size());
		if(!(subqueue1.isEmpty())) {
			
			PCB dis = subqueue1.get(0);
			dis.cpuShots++;
			dis.qTurns++;
			dis.currentQ = 1;
			
			System.out.println("Dispatch job " + dis.ID + " from subqueue1");
			if(dis.bursts[dis.currentBurst] <= SUBQUEUE1_TQ) { //Current burst less than quantum
				SYSTEM.CPU+=dis.bursts[dis.currentBurst];
				
				/* The residency rules specify that if a process places an I/O request before its time quantum expires, the
				process remains in the same subqueue and the count of turns in that particular subqueue will be reset to zero. */
				if(dis.bursts[dis.currentBurst] < SUBQUEUE1_TQ) dis.qTurns = 0;
				
				dis.bursts[dis.currentBurst] = 0;
				dis.currentBurst++; //Done with this burst, go to next
				if(dis.currentBurst == dis.bursts.length) { //Done with this process, terminate
					System.out.println("Job " + dis.ID + " completed at " + SYSTEM.CPU);
					subqueue1.remove(dis);
					currentPCBs--;
					SYSTEM.terminate(dis);
				}
				else { //Send to blocked_Q for I/O
					System.out.println("Job " + dis.ID + " to blocked_Q, burst " + dis.currentBurst + " of length " + dis.bursts[dis.currentBurst]);
					blocked_Q.add(dis);
					dis.timeIOCompleted = SYSTEM.CPU + IO_TIME; //Current time + IO time = point in time to move out of blocked_Q
					subqueue1.remove(dis);
				}
			}
			else //Send to subqueue2 (exceeded time quota)
			{
				dis.bursts[dis.currentBurst] -= SUBQUEUE1_TQ;
				System.out.println("Job " + dis.ID + " to subqueue2, burst " + dis.currentBurst + " of remaining length " + dis.bursts[dis.currentBurst]);
				SYSTEM.CPU+=SUBQUEUE1_TQ;
				subqueue2.add(dis);
				subqueue1.remove(dis);	
			}
		}
		
		else if(!(subqueue2.isEmpty())) {
			PCB dis = subqueue2.get(0);
			dis.cpuShots++;
			dis.qTurns++;
			dis.currentQ = 2;
			
			System.out.println("Dispatch job " + dis.ID + " from subqueue2");
			if(dis.bursts[dis.currentBurst] <= SUBQUEUE2_TQ) {
				SYSTEM.CPU+=dis.bursts[dis.currentBurst];
				
				/* The residency rules specify that if a process places an I/O request before its time quantum expires, the
				process remains in the same subqueue and the count of turns in that particular subqueue will be reset to zero. */
				if(dis.bursts[dis.currentBurst] < SUBQUEUE2_TQ) dis.qTurns = 0;
				
				dis.bursts[dis.currentBurst] = 0;
				dis.currentBurst++; //Done with this burst
				if(dis.currentBurst == dis.bursts.length) { //Done with this process
					System.out.println("Job " + dis.ID + " completed at " + SYSTEM.CPU);
					subqueue2.remove(dis);
					currentPCBs--;
					SYSTEM.terminate(dis);
				}
				else { //Send to blocked_Q for I/O
					System.out.println("Job " + dis.ID + " to blocked_Q, burst " + dis.currentBurst + " of length " + dis.bursts[dis.currentBurst]);
					blocked_Q.add(dis);
					dis.timeIOCompleted = SYSTEM.CPU + IO_TIME; //Current time + IO time = point in time to move out of blocked_Q
					subqueue2.remove(dis);
				}
			}
			else //Send to subqueue3 (exceeded time quota)
			{
				dis.bursts[dis.currentBurst] -= SUBQUEUE2_TQ;
				SYSTEM.CPU+=SUBQUEUE2_TQ;
				subqueue3.add(dis);
				subqueue2.remove(dis);
			}
		}
		
		else if(!(subqueue3.isEmpty())) {
			PCB dis = subqueue3.get(0);
			dis.cpuShots++;
			dis.qTurns++;
			dis.currentQ = 3;
			
			System.out.println("Dispatch job " + dis.ID + " from subqueue3");
			if(dis.bursts[dis.currentBurst] <= SUBQUEUE3_TQ) {
				SYSTEM.CPU+=dis.bursts[dis.currentBurst];
				
				/* The residency rules specify that if a process places an I/O request before its time quantum expires, the
				process remains in the same subqueue and the count of turns in that particular subqueue will be reset to zero. */
				if(dis.bursts[dis.currentBurst] < SUBQUEUE3_TQ) dis.qTurns = 0;
				
				dis.bursts[dis.currentBurst] = 0;
				dis.currentBurst++; //Done with this burst
				if(dis.currentBurst == dis.bursts.length) { //Done with this process
					System.out.println("Job " + dis.ID + " completed at " + SYSTEM.CPU);
					subqueue3.remove(dis);
					currentPCBs--;
					SYSTEM.terminate(dis);
				}
				else { //Send to blocked_Q for I/O
					System.out.println("Job " + dis.ID + " to blocked_Q, burst " + dis.currentBurst + " of length " + dis.bursts[dis.currentBurst]);
					blocked_Q.add(dis);
					dis.timeIOCompleted = SYSTEM.CPU + IO_TIME; //Current time + IO time = point in time to move out of blocked_Q
					subqueue3.remove(dis);
				}
			}
			else //Send to subqueue4 (exceeded time quota)
			{
				dis.bursts[dis.currentBurst] -= SUBQUEUE3_TQ;
				SYSTEM.CPU+=SUBQUEUE3_TQ;
				subqueue4.add(dis);
				subqueue3.remove(dis);
			}
		}
		else if(!(subqueue4.isEmpty())) {
			PCB dis = subqueue4.get(0);
			dis.cpuShots++;
			dis.qTurns++;
			dis.currentQ = 4;
			
			System.out.println("Dispatch job " + dis.ID + " from subqueue4");
			dis.bursts[dis.currentBurst] = 0;
			SYSTEM.CPU+= dis.bursts[dis.currentBurst];
			dis.currentBurst++;
			if(dis.currentBurst == dis.bursts.length) { //Done with this process
				System.out.println("Job " + dis.ID + " completed at " + SYSTEM.CPU);
				subqueue4.remove(dis);
				currentPCBs--;
				SYSTEM.terminate(dis);
			}
			else { //Send to blocked_Q for I/O
				System.out.println("Job " + dis.ID + " to blocked_Q, burst " + dis.currentBurst + " of length " + dis.bursts[dis.currentBurst]);
				blocked_Q.add(dis);
				dis.timeIOCompleted = SYSTEM.CPU + IO_TIME; //Current time + IO time = point in time to move out of blocked_Q
				subqueue4.remove(dis);
			}
		}
		else { //All queues are empty except possibly the blocked queue
			SYSTEM.CPU+=IO_TIME;
		}
		/* Whenever a job relinquishes the CPU and appropriate actions have been carried out, the scheduler
		will check to see if any job in the blocked_Q has completed its I/O and is ready to run again. If
		such a job is found, it is taken from the blocked_Q and placed in the subqueue1. */
		for(int i=0; i<blocked_Q.size(); i++) {
			PCB job = blocked_Q.get(i);
			if(job.timeIOCompleted <= SYSTEM.CPU){
				System.out.println("Job " + job.ID + " finished IO, burst " + job.currentBurst + " of length " + job.bursts[job.currentBurst]);
				subqueue1.add(job);
				blocked_Q.remove(job);
			}
		}
	}
}