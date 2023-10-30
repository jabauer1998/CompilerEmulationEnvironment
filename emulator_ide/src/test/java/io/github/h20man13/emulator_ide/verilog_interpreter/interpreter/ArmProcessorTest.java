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
    public void runDeclanAndInterpreterOnProcessor(String sourcefile, String expectedResult){
        edu.depauw.declan.common.ErrorLog errLog = new edu.depauw.declan.common.ErrorLog();

        try {
            FileReader reader = new FileReader(sourcefile);
            ReaderSource source = new ReaderSource(reader);
            MyDeClanLexer lexer = new MyDeClanLexer(source, errLog);
            MyDeClanParser parser = new MyDeClanParser(lexer, errLog);

            MyStandardLibrary lib = new MyStandardLibrary(errLog);
            Program prog = parser.parseProgram();

            for(LogItem item : errLog){
                assertTrue(item.toString(), false);
            }
            
            IrRegisterGenerator gen = new IrRegisterGenerator();
            MyICodeGenerator iGen = new MyICodeGenerator(errLog, gen);

            lib.ioLibrary().accept(iGen);
            lib.mathLibrary().accept(iGen);

            for(LogItem item : errLog){
                assertTrue(item.toString(), false);
            }

            prog.accept(iGen);

            for(LogItem item : errLog){
                assertTrue(item.toString(), false);
            }

            MyOptimizer optimizer = new MyOptimizer(iGen.getICode(), gen);
            optimizer.runDataFlowAnalysis();
            optimizer.performDeadCodeElimination();

            for(LogItem item : errLog){
                assertTrue(item.toString(), false);
            }

            List<ICode> resultICode = optimizer.getICode();

            MyCodeGenerator codeGen = new MyCodeGenerator(optimizer.getLiveVariableAnalysis(), resultICode, gen, errLog);

            StringWriter assemblyOutput = new StringWriter();
            codeGen.codeGen(assemblyOutput);

            for(LogItem item : errLog){
                assertTrue(item.toString(), false);
            }

            runAssemblerAndInterpreterOnProcessor(assemblyOutput.toString(), expectedResult);
        } catch (IOException e) {
            assertTrue(e.toString(), false);
        }
    }

    public void runAssemblerAndInterpreterOnProcessor(String inputAssembly, String expectedResult){
        StringReader reader = new StringReader(inputAssembly);
        try {
            ANTLRInputStream stream = new ANTLRInputStream(reader);
            ErrorLog errorLog = new ErrorLog();
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
        String expectedOutput = "255\\n\n";
        runAssemblerAndInterpreterOnProcessor(assembly, expectedOutput);
    }

    @Test
    public void performSubtractionTest(){
        String assembly = "MOV R0, #255\r\n"
                        + "MOV R1, #4\r\n"
                        + "SUB R0, R0, R1\r\n"
                        + "SWI 1\r\n"
                        + "STP\r\n";
        String expectedOutput = "251\\n\n";
        runAssemblerAndInterpreterOnProcessor(assembly, expectedOutput);
    }

    @Test
    public void performAdditionTest(){
        String assembly = "MOV R1, #254\r\n"
                        + "ADD R1, R1, #254\r\n"
                        + "MOV R0, R1\r\n"
                        + "SWI 1\r\n"
                        + "STP\r\n";
        String expectedOutput = "508\\n\n";
        runAssemblerAndInterpreterOnProcessor(assembly, expectedOutput);
    }

    @Test
    public void performLoadRegisterTestBasic(){
        String assembly = "B begin\r\n"
                        + "a: .WORD 530\r\n"
                        + "begin: LDR R0, a\r\n"
                        + "SWI 1\r\n"
                        + "STP\r\n";
        String expectedOutput = "530\\n\n";
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
        String expectedOutput = "40575\\n\n";
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
        String expectedOutput = "530\\n\n";
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
        String expectedOutput = "2\\n\n";
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
        String expectedOutput = "1040\\n\n";
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
        String expectedOutput = "10\\n\n20\\n\n";
        runAssemblerAndInterpreterOnProcessor(assembly, expectedOutput);
    }

    @Test
    public void testForLoopBasic(){
        String inputFile = "test_source/ForLoopBasic.dcl";
        String expectedResult = "10\\n\n";
        runDeclanAndInterpreterOnProcessor(inputFile, expectedResult);
    }
}
