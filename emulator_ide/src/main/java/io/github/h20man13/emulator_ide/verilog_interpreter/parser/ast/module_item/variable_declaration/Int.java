package io.github.h20man13.emulator_ide.verilog_interpreter.parser.ast.module_item.variable_declaration;

import io.github.h20man13.emulator_ide.common.Position;
import io.github.h20man13.emulator_ide.verilog_interpreter.parser.ast.expression.Expression;
import io.github.h20man13.emulator_ide.verilog_interpreter.visitor_passes.visitor.ModuleVisitor;

public class Int {
    public class Array extends ArrayDeclaration{
        public Array(Position start, String lexeme, Expression index1, Expression index2){
            super(start, lexeme, index1, index2);
        }

        public <ModVisitType> ModVisitType accept(ModuleVisitor<ModVisitType> modVisitor, Object... argv){
            return modVisitor.visit(this, argv);
        }
    }
    public class Ident extends IdentDeclaration{
        public Ident(Position start, String lexeme){
            super(start, lexeme);
        }

        public <ModVisitType> ModVisitType accept(ModuleVisitor<ModVisitType> modVisitor, Object... argv){
            return modVisitor.visit(this, argv);
        }
    }
}
