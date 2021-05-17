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
            MOV   CX, DX
loop:       CMP   CX,0
            JZ    fork
            DEC   CX
            PUSH  DX
            PUSH  [me]
            PUSH  [parent]
            JMP   loop
fork:       fork
            CMP   AX, 0
            JNZ   busyWait
            log   "me ="   // Child
            MOV   AX, [me]
            log   AX
            MOV   DS, 5
            MOV   AX, [me]
            log   AX
            MOV   DS, 5
            MOV   AX, [me] // Will get a segmentation fault
            exit  [me]
busyWait:   MOV   CX, 10   // Parent process busy waits
loop2:      DEC   CX
            JNZ   loop2
exit:       CMP   DX,4
            JZ    shutdown
            exit  [me]
shutdown:   shutdown