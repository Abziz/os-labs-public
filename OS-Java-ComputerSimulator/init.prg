me:         equ   96
myParent:   equ   100
children:   equ   4
delay:      equ   10
stack_size: equ   100
data_segments: equ 1
main:       getpid
            log   "THIS IS PROCESS"
            MOV   me, AX
            log   [me]
            getppid
            log   "MY PARENT IS"
            MOV   myParent, AX
            log   [myParent]
            MOV   CX, children
forkLoop:   CMP   CX, 0
            JZ    finalLoop
			fork
            CMP   AX, 0
            JNZ   parent
            MOV   CX,delay
childBWait: CMP   CX,0        // Child Busy Waits so that init can fork all children
            JZ    exec        // before anything is done (thus getting consecutive pids)
            DEC   CX
            JMP   childBWait   
exec:       exec  "prog.prg"
            log   "CANNOT EXEC prog.prg"
            exit  1
parent:     DEC   CX
            JMP   forkLoop
finalLoop:  JMP   finalLoop