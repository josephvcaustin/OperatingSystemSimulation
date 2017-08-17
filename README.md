# OperatingSystemSimulation
This repository hosts code for the simulation of a rudimentary operating system. The assignment was designed to help develop a better understanding of schedulers, queues, job processing, and resource allocation.

Phase 1 of the project involved developing a series of objects to:
1) Read job information from a text file (job ID, memory allocation requirements, and CPU burst times)
2) Place a job in the ready queue if enough memory is available.
3) Give a job time on the CPU if its turn comes up.
4) Place the job in the blocked queue if it finishes a burst, or reschedule it if it does not.
5) Release memory once a job completes.

In phase 1 of the project, memory was viewed as 512 allocatable blocks without regard to memory location or paging.

Phase 2 of the project involved expanding upon phase 1 to implement a paging scheme.
In Phase 2, each job was instead viewed as a series of reads, writes, or executes on a series of pages allocated to it.
If a page was referenced out of the range allocated to the job, or a page was referenced that had been swapped out of memory, a page fault would occur and the replacer would seek a candidate for replacement that would minimize the chances of another fault occurring. 
