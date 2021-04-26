data:       equ   100
stack_size: equ   100
data_segments: equ 1
main:       mov   cx, 5
            mov   sp, stack_size
            push  10
            mov   ax, data
loop:       mov   [ax],cx
            add   ax, 4
		    dec   cx
    		cmp   cx, 0
		    jnz   loop
		    push  20
            halt