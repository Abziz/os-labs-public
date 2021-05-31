stack_size: 		equ   200
data_segments: 		equ   1
me:     			equ   96
parent:     		equ   100
childWrites: 		equ   104
grandChildWrites:	equ   108
dummyCell:          equ   112
dummyCell2:         equ   116
sleepTime:          equ   10


main:               CALL  getAndLogPIDs
forkChildren:       fork
            		CMP   AX, 0
            		JZ    child1
                    fork
                    CMP   AX, 0
                    JZ    child2
end:                exit  0    


child1:             fork
                    CMP   AX, 0
                    JZ    grandChild11
                    fork
                    CMP   AX, 0
                    JZ    grandChild12
                    CMP   childWrites, 0
                    JZ    child1End
                    MOV   dummyCell, 1         
child1End:			exit  0


child2:             fork
                    CMP   AX, 0
                    JZ    grandChild21
                    fork
                    CMP   AX, 0
                    JZ    grandChild22
                    CMP   childWrites, 0
                    JZ    child2End
                    MOV   dummyCell, 2          
child2End:          exit  0


grandChild11:	    CMP   grandChildWrites, 0
                    JZ    grandChild11End
                    MOV   dummyCell2, 11
grandChild11End:	exit  0          


grandChild12:	    CMP   grandChildWrites, 0
                    JZ    grandChild12End
                    MOV   dummyCell2, 12
grandChild12End:	MOV   DS, 5
                    MOV   AX, [me]  // Will get a segmentation fault          
                    exit  0


grandChild21:	    CMP   grandChildWrites, 0
                    JZ    grandChild21End
                    MOV   dummyCell2, 21
grandChild21End:	CALL  sleep
                    exit  0          


grandChild22:	    CMP   grandChildWrites, 0
                    JZ    grandChild22End
                    MOV   dummyCell2, 22
grandChild22End:	CALL  sleep
                    shutdown          
//
//                                   GET AND LOG PIDS
//
getAndLogPIDs: 		getpid
            		log   "THIS IS PROCESS"
            		MOV   me, AX
            		log   [me]
            		getppid
            		log   "MY PARENT IS"
            		MOV   parent, AX
            		log   [parent]
            		MOV   DX, me
            		AND   DX, 1
            		MOV   childWrites, DX
            		MOV   DX, [me]
            		SHR   DX, 1
            		AND   DX, 1
            		MOV   grandChildWrites, DX
                    RET

//
//                                   SLEEP
//
sleep:              MOV   CX, sleepTime
sleepLoop:			DEC   CX
					CMP   CX, 0
                    JNZ   sleepLoop
					RET