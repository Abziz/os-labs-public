me:         equ   96
myParent:   equ   100
children:   equ   6
SIGHUP:     equ   1
SIGKILL:    equ   9
SIGTERM:    equ   15
SIGCHLD:    equ   17
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
            signal SIGCHLD, handle
            MOV   CX, children
forkLoop:   CMP   CX, 0
            JZ    kill
			fork
            CMP   AX, 0
            JNZ   parent
exec:       exec  "prog.prg"
            log   "CANNOT EXEC prog.prg"
            exit  1
parent:     DEC   CX
            JMP   forkLoop
kill:       kill  2, SIGKILL
            kill  3, SIGHUP
            kill  4, SIGTERM
            kill  5, SIGKILL
            kill  6, SIGHUP
            kill  7, SIGTERM
finalLoop:  JMP   finalLoop

//
//
//
handle:             LOG   "CHILD TERMINATED"
                    POP   AX
                    RET