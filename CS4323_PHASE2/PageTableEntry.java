/* V/I bit: The V/I bit is necessary to indicate which portion of the page table is actually occupied by the job.
Resident bit: Shows whether the page is currently in memory (1) or on disk (0)
Reference bit: Set every time a page is referenced.
Modified bit: set only if the page is reference for a "write" */
public class PageTableEntry {
	public int vi = 0; public int resident = 0;
	public int reference = 0; public int modified = 0;
	public int frameNum = 0;
	public PageTableEntry(int v, int rs, int rf, int m, int f){
	vi=v; resident=rs; reference=rf; modified=m; frameNum=f; }
}