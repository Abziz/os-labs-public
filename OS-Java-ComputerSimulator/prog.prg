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
            JZ    exit
            DEC   CX
            PUSH  DX
            PUSH  [me]
            PUSH  [parent]
            JMP   loop
exit:       CMP   DX,4
            JZ    shutdown
            exit  [me]
shutdown:   shutdown