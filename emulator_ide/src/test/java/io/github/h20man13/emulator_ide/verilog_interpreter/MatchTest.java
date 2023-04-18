package io.github.H20man13.emulator_ide.verilog_interpreter;

import static org.junit.Assert.assertTrue;
import org.junit.Test;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.IntVal;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.LongVal;
import io.github.H20man13.emulator_ide.verilog_interpreter.interpreter.value.pattern.*;

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
