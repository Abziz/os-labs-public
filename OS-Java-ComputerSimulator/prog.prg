stack_size: 		equ   200
data_segments: 		equ   1
me:     			equ   96
parent:     		equ   100
sleepTime:          equ   20
SIGHUP:             equ   1
SIGTERM:    		equ   15
SIGKILL:    		equ   9
SIG_IGN:    		equ   -1
SIG_DFL:    		equ   0


main:               CALL  getAndLogPIDs
                    signal SIGKILL, SIG_IGN
                    log   "SIGNAL SIGKILL RETURNS"
                    log   AX
                    signal SIGTERM, SIG_IGN
                    log   "SIGNAL SIGTERM RETURNS"
                    log   AX
                    signal SIGHUP, handle
                    log   "SIGNAL SIGHUP RETURNS"
                    log   AX
                    MOV   CX, sleepTime
sleepLoop:          CMP   CX, 0
                    JZ    exitLoop
                    DEC   CX
                    JMP   sleepLoop
exitLoop:           CMP   [me], 7
                    JNZ   finalLoop
                    shutdown               
finalLoop:          JMP   finalLoop                                      
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
                    RET
//
//
//
handle:             LOG   "HANDLING SIGNAL"
                    LOG   [SP]
                    POP   AX
                    RET