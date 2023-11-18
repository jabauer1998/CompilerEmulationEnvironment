package io.github.H20man13.emulator_ide.verilog_interpreter.interpreter;

import static org.junit.Assert.assertTrue;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.rmi.server.UID;
import java.util.List;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.Test;
import edu.depauw.declan.common.ErrorLog.LogItem;
import edu.depauw.declan.common.ast.Program;
import io.github.H20man13.DeClan.common.IrRegisterGenerator;
import io.github.H20man13.DeClan.common.ReaderSource;
import io.github.H20man13.DeClan.common.icode.ICode;
import io.github.H20man13.DeClan.main.MyCodeGenerator;
import io.github.H20man13.DeClan.main.MyDeClanLexer;
import io.github.H20man13.DeClan.main.MyDeClanParser;
import io.github.H20man13.DeClan.main.MyICodeGenerator;
import io.github.H20man13.DeClan.main.MyOptimizer;
import io.github.H20man13.DeClan.main.MyStandardLibrary;
import io.github.H20man13.DeClan.main.assembler.ArmAssemblerLexer;
import io.github.H20man13.DeClan.main.assembler.ArmAssemblerParser;
import io.github.H20man13.DeClan.main.assembler.AssemblerVisitor;
import io.github.H20man13.DeClan.main.assembler.ArmAssemblerParser.ProgramContext;
import io.github.H20man13.emulator_ide.common.debug.ErrorLog;
import io.github.H20man13.emulator_ide.common.io.Destination;

public class ArmProcessorTest {

    public void runAssemblerAndInterpreterOnProcessor(String inputAssembly, String expectedResult){
        StringReader reader = new StringReader(inputAssembly);
        try {
            ANTLRInputStream stream = new ANTLRInputStream(reader);
            StringWriter errorOutputWriter = new StringWriter();
            Destination errorOutputDestination = new Destination(errorOutputWriter);
            ErrorLog errorLog = new ErrorLog(errorOutputDestination);
            ArmAssemblerLexer lexer = new ArmAssemblerLexer(stream);
            CommonTokenStream tokStream = new CommonTokenStream(lexer);
            ArmAssemblerParser parser = new ArmAssemblerParser(tokStream);
            
            AssemblerVisitor asmVisitor = new AssemblerVisitor();
            ProgramContext program = parser.program();

            assertTrue("Expected 0 errors but found " + parser.getNumberOfSyntaxErrors(), parser.getNumberOfSyntaxErrors() == 0);

            List<Integer> result = asmVisitor.assembleCode(program);

            File file = new File("default");
            File processor = new File("processor/ARM7TDMIS.v");

            assertTrue(processor.exists());
            FileWriter tempWriter = new FileWriter(file);
            for(Integer val : result){
                String str= Integer.toBinaryString(val);
                for(int i = str.length(); i < 32; i++){
                    tempWriter.append('0');
                }
                tempWriter.append(str);
                tempWriter.append("\r\n");
            }
            tempWriter.close();

            FileReader processorReader = new FileReader(processor);

            StringWriter stdOutputWriter = new StringWriter();
            Destination stdOutDest = new Destination(stdOutputWriter);

            VerilogInterpreter interpreter = new VerilogInterpreter(errorLog, stdOutDest);
            interpreter.interpretFile(processorReader);

            assertTrue(errorOutputWriter.toString(), errorOutputWriter.toString().length() == 0);

            assertTrue("Expected -\n\n" + expectedResult + "\n\n but found -\n\n" + stdOutputWriter.toString() + "\n\n", stdOutputWriter.toString().equals(expectedResult));
        } catch (Exception exp){
            assertTrue(exp.toString(), false);
        }
    }

    @Test
    public void performNumberTest(){
        String assembly = "MOV R0, #255\r\n"
                        + "SWI 1\r\n"
                        + "STP\r\n";
        String expectedOutput = "255\n";
        runAssemblerAndInterpreterOnProcessor(assembly, expectedOutput);
    }

    @Test
    public void performSubtractionTest(){
        String assembly = "MOV R0, #255\r\n"
                        + "MOV R1, #4\r\n"
                        + "SUB R0, R0, R1\r\n"
                        + "SWI 1\r\n"
                        + "STP\r\n";
        String expectedOutput = "251\n";
        runAssemblerAndInterpreterOnProcessor(assembly, expectedOutput);
    }

    @Test
    public void performAdditionTest(){
        String assembly = "MOV R1, #254\r\n"
                        + "ADD R1, R1, #254\r\n"
                        + "MOV R0, R1\r\n"
                        + "SWI 1\r\n"
                        + "STP\r\n";
        String expectedOutput = "508\n";
        runAssemblerAndInterpreterOnProcessor(assembly, expectedOutput);
    }

    @Test
    public void performLoadRegisterTestBasic(){
        String assembly = "B begin\r\n"
                        + "a: .WORD 530\r\n"
                        + "begin: LDR R0, a\r\n"
                        + "SWI 1\r\n"
                        + "STP\r\n";
        String expectedOutput = "530\n";
        runAssemblerAndInterpreterOnProcessor(assembly, expectedOutput);
    }

    @Test
    public void performLoadRegisterTestAdvanced(){
        String assembly = "B begin\r\n"
                        + "a: .WORD 530\r\n"
                        + "b: .WORD 40045\r\n"
                        + "begin: LDR R1, a\r\n"
                        + "LDR R2, b\r\n"
                        + "ADD R0, R1, R2\r\n"
                        + "SWI 1\r\n"
                        + "STP\r\n";
        String expectedOutput = "40575\n";
        runAssemblerAndInterpreterOnProcessor(assembly, expectedOutput);
    }

    @Test
    public void performStoreRegisterTestAdvanced(){
        String assembly = "B begin\r\n"
                        + "a: .WORD 530\r\n"
                        + "b: .WORD 112\r\n"
                        + "c: .WORD 430\r\n"
                        + "begin: LDR R1, a\r\n"
                        + "LDR R2, c\r\n"
                        + "STR R1, b\r\n"
                        + "STR R2, a\r\n" 
                        + "LDR R0, b\r\n"
                        + "SWI 1\r\n"
                        + "STP\r\n";
        String expectedOutput = "530\n";
        runAssemblerAndInterpreterOnProcessor(assembly, expectedOutput);
    }

    @Test
    public void performLoadRegisterTestAdvanced1(){
        String assembly = "B begin\r\n"
                        + ".WORD 2\r\n"
                        + "begin: MOV R1, #0\r\n"
                        + "LDR R0, [R1,#4]\r\n"
                        + "SWI 1\r\n"
                        + "STP\r\n";
        String expectedOutput = "2\n";
        runAssemblerAndInterpreterOnProcessor(assembly, expectedOutput);
    }

    @Test
    public void performLoadRegisterTestAdvanced2(){
        String assembly = "B begin\r\n"
                        + ".WORD 530\r\n"
                        + "offset: .WORD 12\r\n"
                        + "base: .WORD 4\r\n"
                        + ".WORD 1040\r\n"
                        + "begin: LDR R1, offset\r\n"
                        + "LDR R2, base\r\n"
                        + "LDR R0, [R2, +R1]\r\n"
                        + "SWI 1\r\n"
                        + "STP\r\n";
        String expectedOutput = "1040\n";
        runAssemblerAndInterpreterOnProcessor(assembly, expectedOutput);
    }

    @Test
    public void performBasicProgramTest(){
        String assembly = "LDR R13, totalBytes\r\n" + //
                "B begin_0\r\n" + //
                "D: .WORD 0\r\n" + //
                "E: .WORD 0\r\n" + //
                "F: .WORD 0\r\n" + //
                "G: .WORD 10\r\n" + //
                "H: .WORD 0\r\n" + //
                "y: .WORD 4\r\n" + //
                "I: .WORD 20\r\n" + //
                "y: .WORD 4\r\n" + //
                "totalBytes: .WORD 220\r\n" + //
                "begin_0: B begin_1\r\n" + //
                "WriteLn: SWI 4\r\n" + //
                "LDR R2, [R13]\r\n" + //
                "SUB R13, R13, #2\r\n" + //
                "MOV R15, R14\r\n" + //
                "WriteInt: LDR R2, y\r\n" + //
                "LDR R2, [R13, -R2]\r\n" + //
                "STR R2, D\r\n" + //
                "LDR R0, D\r\n" + //
                "SWI 1\r\n" + //
                "LDR R3, [R13]\r\n" + //
                "SUB R13, R13, #2\r\n" + //
                "MOV R15, R14\r\n" + //
                "WriteReal: LDR R3, A\r\n" + //
                "LDR R3, [R13, -R3]\r\n" + //
                "STR R3, E\r\n" + //
                "LDR R0, E\r\n" + //
                "SWI 2\r\n" + //
                "LDR R2, [R13]\r\n" + //
                "SUB R13, R13, #2\r\n" + //
                "MOV R15, R14\r\n" + //
                "ReadInt: SWI 3\r\n" + //
                "STR R0, F\r\n" + //
                "LDR R2, [R13]\r\n" + //
                "SUB R13, R13, #2\r\n" + //
                "MOV R15, R14\r\n" + //
                "begin_1: LDR R2, G\r\n" + //
                "STR R2, H\r\n" + //
                "B begin_2\r\n" + //
                "begin_2: ADD R13, R13, #4\r\n" + //
                "STR R14, [R13, #-4]\r\n" + //
                "ADD R13, R13, #4\r\n" + //
                "LDR R3, y\r\n" + //
                "LDR R4, H\r\n" + //
                "STR R4, [R13,-R3]\r\n" + //
                "BL WriteInt\r\n" + //
                "ADD R13, R13, #4\r\n" + //
                "STR R14, [R13, #-4]\r\n" + //
                "ADD R13, R13, #4\r\n" + //
                "LDR R2, y\r\n" + //
                "LDR R3, I\r\n" + //
                "STR R3, [R13,-R2]\r\n" + //
                "BL WriteInt\r\n" + //
                "STP\r\n";
        String expectedOutput = "10\n20\n";
        runAssemblerAndInterpreterOnProcessor(assembly, expectedOutput);
    }

    @Test
    public void testForLoopBasic(){
        String assembly = "LDR R13, totalBytes\r\n" + //
                "        B begin_0\r\n" + //
                "        h: .WORD 0\r\n" + //
                "        l8: .WORD 1\r\n" + //
                "        l7: .WORD 0\r\n" + //
                "        l9: .WORD 10\r\n" + //
                "        c: .WORD 4\r\n" + //
                "        m0: .WORD 1\r\n" + //
                "        m1: .WORD 0\r\n" + //
                "        l7: .WORD 0\r\n" + //
                "        l8: .WORD 1\r\n" + //
                "        totalBytes: .WORD 3668\r\n" + //
                "        begin_0: B begin_1\r\n" + //
                "        WriteLn: SWI 4\r\n" + //
                "        MOV R15, R14\r\n" + //
                "        WriteInt: LDR R2, c\r\n" + //
                "        LDR R2, [R13, -R2]\r\n" + //
                "        STR R2, h\r\n" + //
                "        LDR R0, h\r\n" + //
                "        SWI 1\r\n" + //
                "        MOV R15, R14\r\n" + //
                "        begin_1: B begin_2\r\n" + //
                "        begin_2: B begin_3\r\n" + //
                "        begin_3: LDR R3, l8\r\n" + //
                "        STR R3, l7\r\n" + //
                "        FORBEG_0_LEVEL_0: LDR R2, l7\r\n" + //
                "        LDR R3, l9\r\n" + //
                "        TEQ R2, R3\r\n" + //
                "        BNE FORLOOP_0_LEVEL_0\r\n" + //
                "        BEQ FOREND_0_LEVEL_0\r\n" + //
                "        FORLOOP_0_LEVEL_0: ADD R13, R13, #8\r\n" + //
                "        STR R14, [R13, #-8]\r\n" + //
                "        LDR R4, c\r\n" + //
                "        LDR R2, l7\r\n" + //
                "        STR R2, [R13,-R4]\r\n" + //
                "        BL WriteInt\r\n" + //
                "        LDR R14, [R13, #-8]\r\n" + //
                "        SUB R13, R13, #8\r\n" + //
                "        LDR R2, l7\r\n" + //
                "        LDR R4, m0\r\n" + //
                "        ADD R5, R2, R4\r\n" + //
                "        STR R5, m1\r\n" + //
                "        LDR R2, m1\r\n" + //
                "        STR R2, l7\r\n" + //
                "        B FORBEG_0_LEVEL_0\r\n" + //
                "        FOREND_0_LEVEL_0: ADD R13, R13, #4\r\n" + //
                "        STR R14, [R13, #-4]\r\n" + //
                "        BL WriteLn\r\n" + //
                "        LDR R14, [R13, #-4]\r\n" + //
                "        SUB R13, R13, #4\r\n" + //
                "        STP\r\n";

                String expectedResult = "1\n2\n3\n4\n5\n6\n7\n8\n9\n\\n\n";
                runAssemblerAndInterpreterOnProcessor(assembly, expectedResult);
    }

    @Test
    public void testForLoopBasic2(){
        String assembly = "LDR R13, totalBytes\r\n" + //
                          "B begin_0\r\n" + //
                          "h: .WORD 0\r\n" + //
                          "l8: .WORD 1\r\n" + //
                          "l7: .WORD 0\r\n" + //
                          "l9: .WORD 10\r\n" + //
                          "c: .WORD 4\r\n" + //
                          "m0: .WORD 1\r\n" + //
                          "m1: .WORD 0\r\n" + //
                          "totalBytes: .WORD 3668\r\n" + //
                          "begin_0: B begin_1\r\n" + //
                          "WriteLn: SWI 4\r\n" + //
                          "MOV R15, R14\r\n" + //
                          "WriteInt: LDR R2, c\r\n" + //
                          "LDR R2, [R13, -R2]\r\n" + //
                          "STR R2, h\r\n" + //
                          "LDR R0, h\r\n" + //
                          "SWI 1\r\n" + //
                          "MOV R15, R14\r\n" + //
                          "begin_1: B begin_2\r\n" + //
                          "begin_2: B begin_3\r\n" + //
                          "begin_3: LDR R3, l8\r\n" + //
                          "STR R3, l7\r\n" + //
                          "FORBEG_0_LEVEL_0: LDR R2, l7\r\n" + //
                          "LDR R3, l9\r\n" + //
                          "CMP R2, R3\r\n" + //
                          "BLT FORLOOP_0_LEVEL_0\r\n" + //
                          "BGE FOREND_0_LEVEL_0\r\n" + //
                          "FORLOOP_0_LEVEL_0: ADD R13, R13, #8\r\n" + //
                          "STR R14, [R13, #-8]\r\n" + //
                          "LDR R4, c\r\n" + //
                          "LDR R2, l7\r\n" + //
                          "STR R2, [R13,-R4]\r\n" + //
                          "BL WriteInt\r\n" + //
                          "LDR R14, [R13, #-8]\r\n" + //
                          "SUB R13, R13, #8\r\n" + //
                          "LDR R2, l7\r\n" + //
                          "LDR R4, m0\r\n" + //
                          "ADD R5, R2, R4\r\n" + //
                          "STR R5, m1\r\n" + //
                          "LDR R2, m1\r\n" + //
                          "STR R2, l7\r\n" + //
                          "B FORBEG_0_LEVEL_0\r\n" + //
                          "FOREND_0_LEVEL_0: ADD R13, R13, #4\r\n" + //
                          "STR R14, [R13, #-4]\r\n" + //
                          "BL WriteLn\r\n" + //
                          "LDR R14, [R13, #-4]\r\n" + //
                          "SUB R13, R13, #4\r\n" + //
                          "STP\r\n";
        String expectedResult = "1\n2\n3\n4\n5\n6\n7\n8\n9\n\\n\n";
        runAssemblerAndInterpreterOnProcessor(assembly, expectedResult);
    }

    @Test
    public void testForLoopBasic3(){
        String assembly = "LDR R13, totalBytes\r\n" + //
                "B begin_0\r\n" + //
                "h: .WORD 0\r\n" + //
                "m8: .WORD 0\r\n" + //
                "m9: .WORD 0\r\n" + //
                "n0: .WORD 1\r\n" + //
                "n1: .WORD 0\r\n" + //
                "m7: .WORD 0\r\n" + //
                "n3: .WORD 10\r\n" + //
                "n2: .WORD 0\r\n" + //
                "n4: .WORD 1\r\n" + //
                "c: .WORD 4\r\n" + //
                "n5: .WORD 1\r\n" + //
                "n6: .WORD 0\r\n" + //
                "n7: .WORD 0\r\n" + //
                "n2: .WORD 0\r\n" + //
                "N: .WORD 4\r\n" +
                "O: .WORD 8\r\n" +
                "totalBytes: .WORD 3860\r\n" + //
                "begin_0: B begin_1\r\n" + //
                "WriteInt: LDR R2, c\r\n" + //
                "LDR R2, [R13, -R2]\r\n" + //
                "STR R2, h\r\n" + //
                "LDR R0, h\r\n" + //
                "SWI 1\r\n" + //
                "MOV R15, R14\r\n" + //
                "begin_1: B begin_2\r\n" +//
                "INeg: LDR R3, N\r\n" + //
                "LDR R3, [R13, -R3]\r\n" + //
                "STR R3, m8\r\n" + //
                "LDR R2, m8\r\n" + //
                "MVN R2, R2\r\n" + //
                "STR R2, m9\r\n" + //
                "LDR R3, m9\r\n" + //
                "LDR R3, n0\r\n" + //
                "ADD R3, R3, R3\r\n" + //
                "STR R3, n1\r\n" + //
                "LDR R3, n1\r\n" + //
                "STR R3, m7\r\n" + //
                "LDR R2, m7\r\n" + //
                "LDR R3, O\r\n" + //
                "STR R2, [R13, -R3]\r\n" + //
                "MOV R15, R14\r\n" + //
                "begin_2: B begin_3\r\n" + //
                "begin_3: LDR R3, n3\r\n" + //
                "STR R3, n2\r\n" + //
                "FORBEG_0_LEVEL_0: LDR R2, n2\r\n" + //
                "LDR R3, n4\r\n" + //
                "CMP R2, R3\r\n" + //
                "BGT FORLOOP_0_LEVEL_0\r\n" + //
                "BLE FOREND_0_LEVEL_0\r\n" + //
                "FORLOOP_0_LEVEL_0: ADD R13, R13, #8\r\n" + //
                "STR R14, [R13, #-8]\r\n" + //
                "LDR R4, c\r\n" + //
                "LDR R2, n2\r\n" + //
                "STR R2, [R13,-R4]\r\n" + //
                "BL WriteInt\r\n" + //
                "LDR R14, [R13, #-8]\r\n" + //
                "SUB R13, R13, #8\r\n" + //
                "LDR R4, n5\r\n" + //
                "ADD R13, R13, #12\r\n" + //
                "STR R4, [R13, #-4]\r\n" + //
                "STR R14, [R13, #-12]\r\n" + //
                "BL INeg\r\n" + //
                "LDR R4, [R13, #-8]\r\n" + //
                "LDR R14, [R13, #-12]\r\n" + //
                "SUB R13, R13, #12\r\n" + //
                "STR R4, n6\r\n" + //
                "LDR R2, n2\r\n" + //
                "LDR R2, n6\r\n" + //
                "ADD R2, R2, R2\r\n" + //
                "STR R2, n7\r\n" + //
                "LDR R2, n7\r\n" + //
                "STR R2, n2\r\n" + //
                "B FORBEG_0_LEVEL_0\r\n" + //
                "FOREND_0_LEVEL_0: STP\r\n";
        String expectedResult = "";
        runAssemblerAndInterpreterOnProcessor(assembly, expectedResult);
    }
}
