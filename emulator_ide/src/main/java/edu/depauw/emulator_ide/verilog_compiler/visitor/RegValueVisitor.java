package edu.depauw.emulator_ide.verilog_compiler.visitor;

import edu.depauw.emulator_ide.verilog_compiler.token.Position;
import edu.depauw.emulator_ide.verilog_compiler.ast.reg_value.*;
    
public interface RegValueVisitor<RegValVisitType>{

    /** The code for visiting Integer Array Declarations
     *  @param arr
     *  @param argv
     */

    public RegValVisitType visit(IntegerArray arr, Object... argv);

    /** The code for visiting Integer Declarations
     *  @param arr
     *  @param argv
     */

    public RegValVisitType visit(IntegerIdent arr, Object... argv);

    /**The code for visiting Integer Declarations
     * 
     */

    public RegValVisitType visit(RegScalarArray arr, Object... argv);

    /**The code for visiting Integer Declarations
     * the code for visiting register arrays
     */

     public RegValVisitType visit(RegVectorArray arr, Object... argv);

    /**The code for visiting Integer Declarations
     * the code for visiting register arrays
     */

    public RegValVisitType visit(RegScalarIdent arr, Object... argv);

    /**The code for visiting Integer Declarations
     * the code for visiting register arrays
     */

     public RegValVisitType visit(RegVectorIdent arr, Object... argv);

    
}
