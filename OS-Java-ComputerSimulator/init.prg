me:         equ   96
myParent:   equ   100
status:     equ   104
children:   equ   4
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
            MOV   CX, 0
forkLoop:   CMP   CX, children
            JZ    wait
			fork
            CMP   AX, 0
            JNZ   parent
            CALL  child
parent:     INC   CX
            JMP   forkLoop
wait:       LOG   "PARENT WAITS"
 			CALL  waitAndReportChildren
finalLoop:  JMP   finalLoop

//
//
//
child:   	log   "THIS IS PROCESS"
    	  	getpid
         	MOV   me, AX
	       	log   [me]
    	  	getppid
       		log   "MY PARENT IS"
         	MOV   myParent, AX
         	log   [myParent]
         	MOV   BX, CX
         	INC   CX
         	MUL   CX, 10
         	log   "I WILL EXIT AFTER THIS NUMBER OF TICKS"
         	log   CX
         	CALL  sleep
         	INC   BX
         	CMP   BX, children
         	JZ    shutdown
         	DEC   BX
         	exit  BX
         	LOG   "UNREACHABLE"
shutdown: 	shutdown         
//
//
//
sleep:    CMP  CX, 0
          JZ   sleepEnd
          DEC  CX
          JMP  sleep
sleepEnd: RET

//
//
//
waitAndReportChildren:  wait status
                        CMP  AX, -1
                        JZ   endReport
                        LOG  "CHILD TERMINATED WITH STATUS "
                        LOG  [status]
                        JMP  waitAndReportChildren
endReport:              RET