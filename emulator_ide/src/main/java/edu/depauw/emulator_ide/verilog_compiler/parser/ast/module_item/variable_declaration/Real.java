package edu.depauw.emulator_ide.verilog_compiler.parser.ast.module_item.variable_declaration;

import edu.depauw.emulator_ide.verilog_compiler.passes.visitor.ModuleVisitor;
import edu.depauw.emulator_ide.common.Position;

public class Real {
    public class Ident extends IdentDeclaration{
        public Ident(Position start, String ident) {
            super(start, ident);
        }
        /**
         * The ast node visitor will allow the user to pass down data through the argument
         * vector. The accept method is needed to know which visit method to run.
         * 
         * @author Jacob Bauer
         */
        public <ModVisitType> ModVisitType accept(ModuleVisitor<ModVisitType> modVisitor, Object... argv){
            return modVisitor.visit(this, argv);
        }
    }
}
