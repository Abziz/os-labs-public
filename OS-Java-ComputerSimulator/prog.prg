data:       equ   100
iterations: equ   20
stack_size: equ   100
data_segments: equ 1
main:	    getpid
		    PUSH  AX
		    log   AX
		    getppid
		    PUSH  AX
		    log   AX
            MOV   cx, iterations
loop:       CMP   cx,0
            JZ    exit
            log   "THIS IS PROCESS"
            getpid
            log   AX
            log   "MY PARENT IS"
            getppid
            log   AX
            DEC   cx
            JMP   loop
exit:       getpid
            CMP   AX,4
            JZ    shutdown
            exit  AX
shutdown:   shutdown