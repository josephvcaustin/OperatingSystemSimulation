/* Joseph Austin
CS4323 - Operating Systems
Phase 2
4/27/2017
REPLACER: */
public class REPLACER
{
	public int[] replace(PCB job)
	{
		int frameNum = 0;
		int replacedPageNum=-1;
		boolean foundReplacement=false;
		//Find a page to replace
		//Mark it as not resident
		//reset all page references to zero
		for(int i=0;i<job.pageTable.size();i++){
			PageTableEntry page = job.pageTable.get(i);
			if((page.vi==0)||(page.resident==0)) continue;
			if(page.reference==0&&page.modified==0){
				System.out.println("Clean replaced page "+i);
				replacedPageNum=i;
				page.resident=0;
				job.cleanReplace++;
				replacedPageNum=i;
				frameNum=page.frameNum;
				foundReplacement=true;
			break;}
		}
		if(!foundReplacement){
			for(int i=0;i<job.pageTable.size();i++){
				PageTableEntry page = job.pageTable.get(i);
				if((page.vi==0)||(page.resident==0)) continue;
				if(page.reference==0&&page.modified==1){
					System.out.println("Dirty replaced page "+i);
					replacedPageNum=i;
					page.resident=0;
					job.dirtyReplace++;
					frameNum=page.frameNum;
					foundReplacement=true;
				break;}
			}
		}
		if(!foundReplacement){
			for(int i=0;i<job.pageTable.size();i++){
				PageTableEntry page = job.pageTable.get(i);
				if((page.vi==0)||(page.resident==0)) continue;
				if(page.reference==1&&page.modified==0){
					System.out.println("Clean replaced page "+i);
					replacedPageNum=i;
					page.resident=0;
					job.cleanReplace++;
					replacedPageNum=i;
					frameNum=page.frameNum;
					foundReplacement=true;
				break;}
			}
		}
		if(!foundReplacement){
			for(int i=0;i<job.pageTable.size();i++){
				PageTableEntry page = job.pageTable.get(i);
				if((page.vi==0)||(page.resident==0)) continue;
				if(page.reference==1&&page.modified==1){
					System.out.println("Dirty replaced page "+i);
					replacedPageNum=i;
					page.resident=0;
					job.dirtyReplace++;
					replacedPageNum=i;
					frameNum=page.frameNum;
					foundReplacement=true;
				break;}
			}
		}
		for(PageTableEntry page: job.pageTable) {page.reference=0;}
		return new int[]{frameNum, replacedPageNum};
	}
	
}