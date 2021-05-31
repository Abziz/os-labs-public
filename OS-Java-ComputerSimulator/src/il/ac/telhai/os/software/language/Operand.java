package il.ac.telhai.os.software.language;

import java.text.ParseException;
import java.util.Map;

import il.ac.telhai.os.hardware.Memory;
public abstract class Operand {
		
	/**
	 * 
	 * @param operandString
	 * @param symbolTable is null in firstPass
	 * @return
	 * @throws ParseException
	 */
	
    public static Operand newOperand(String operandString, Map<String, Integer> symbolTable) throws ParseException {
    	if (operandString == null) return null;
    	String s = operandString.trim();
    	if (s.length() == 0) return null;
    	
    	if (s.startsWith("\"")) {
    		if (! s.endsWith("\"")) throw new ParseException(operandString+": unmatched quotes", 0);
        	s = s.substring(1, s.length()-1);
        	return new StringOperand(s);
    	}
   		s = s.toUpperCase();
    	
    	boolean isIndirect = s.startsWith("[");
    	if (isIndirect ) { 
    		if (!s.endsWith("]")) throw new ParseException(operandString+": unmatched brackets", 0);
        	s = s.substring(1, s.length()-1).trim();
    	}
    	
    	try {
        	return new RegisterOperand (isIndirect, Register.valueOf(s));
    	} catch (IllegalArgumentException iae) {
    		try {
                return new AbsoluteOperand(isIndirect, Integer.parseInt(s)) ;
    		} catch (NumberFormatException nfe) {
    			Integer value;	
   			    if (symbolTable == null) {
   			    	value = 0; //
   			    } else {
    			    value = symbolTable.get(s);
    			    if (value == null) throw new ParseException(operandString+": undefined symbol", 0);
    			}
                return new AbsoluteOperand(isIndirect, value) ;
    		}
    	}
    }

    public abstract int getWord(Registers registers, Memory memory);
    public abstract byte getByte(Registers registers, Memory memory);
    public abstract String getString(Registers registers, Memory memory);

}
