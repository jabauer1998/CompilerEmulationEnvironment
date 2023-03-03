package edu.depauw.emulator_ide.verilog_compiler;

import static org.junit.Assert.assertTrue;
import org.junit.Test;
import edu.depauw.emulator_ide.verilog_compiler.interpreter.value.IntVal;
import edu.depauw.emulator_ide.verilog_compiler.interpreter.value.LongVal;
import edu.depauw.emulator_ide.verilog_compiler.interpreter.value.pattern.*;

public class MatchTest {

    public void testHexidecimalMatch(){

        LongVal lvalue = new LongVal(0xfaaa);
        HexadecimalPattern trueLongPattern = new HexadecimalPattern("faxa");
        assertTrue("Cant match 0xfaxa with 0xfaaa", trueLongPattern.match(lvalue));

        IntVal ivalue = new IntVal(0xffff);
        HexadecimalPattern trueIntPattern = new HexadecimalPattern("0000xxff");
        assertTrue("ffff does not match hex pattern", trueIntPattern.match(ivalue));


    }
}
