me:         equ   100
parent:     equ   104
stack_size: equ   100
data_segments: equ 1
main:       getpid
            log   "THIS IS PROCESS"
            MOV   DX, AX
            log   DX
            MOV   me, AX
            getppid
            log   "MY PARENT IS"
            log   AX
            MOV   parent, AX
fork1:      fork
            CMP   AX, 0
            JNZ   fork2
            exec  "prog.prg"
            log   "CANNOT EXEC prog.prg"
            exit  1
fork2:      fork
            CMP   AX, 0
            JNZ   fork3
            exec  "prog.prg"
            log   "CANNOT EXEC prog.prg"
            exit  2
fork3:      fork
            CMP   AX, 0
            JNZ   finalloop
            exec  "prog.prg"
            log   "CANNOT EXEC prog.prg"
            exit  3
finalloop:  JMP   finalloop