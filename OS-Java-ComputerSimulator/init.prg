data:       equ   100
stack_size: equ   100
data_segments: equ 1
main:       MOV   cx, 5
            PUSH  10
            MOV   ax, data
loop:       MOV   [ax],cx
            ADD   ax, 4
		    DEC   cx
    		CMP   cx, 0
		    JNZ   loop
            log   "THIS IS PROCESS"
            getpid
            PUSH  AX
            log   AX
            log   "MY PARENT IS"
            getppid
            PUSH  AX
            log   AX
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
            JNZ   end
            exec  "prog.prg"
            log   "CANNOT EXEC prog.prg"
            exit  3
end:        exit  0