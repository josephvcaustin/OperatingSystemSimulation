/* Joseph Austin
CS4323 - Operating Systems
Phase 2
4/27/2017
CPU: This component is in charge of fetching, decoding, 
and executing instructions and updating the reference bits
according to the type of reference encountered. The CPU 
clock will be incremented by 2 time units for the execution 
of each instruction. */
public class CPU
{
	public static int TIME = 0;
	public static final int EXEC_TIME = 2;
	/* execute(): Used for giving a process time on the CPU
	Read/Write requests will cause a job to get blocked */
	public boolean execute(PageTableEntry page, char refType) throws Exception {
		TIME+=EXEC_TIME; //Execute the instruction
		if ((refType!='p')&&(refType!='r')&&(refType!='w')){SYSTEM.errorMsg=("RefType Exception: refType "+refType+" invalid."); throw new Exception();}
		else if(page.resident==0){return true;} //Page fault
		else if(refType=='w') {page.reference=1; page.modified=1;}
		else if(refType=='r') {page.reference=1;}
		else {page.reference=1;}
		return false; //No page fault
	}
}