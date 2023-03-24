package edu.depauw.emulator_ide.verilog_interpreter.parser.ast.module_item.variable_declaration;

import edu.depauw.emulator_ide.common.Position;
import edu.depauw.emulator_ide.verilog_interpreter.parser.ast.expression.Expression;
import edu.depauw.emulator_ide.verilog_interpreter.visitor_passes.visitor.ModuleVisitor;

public class Input {
    public final class Wire{
        public class Vector extends VectorDeclaration{
            public Vector(Expression exp1, Expression exp2){
                super(exp1, exp2);
            }
            public class Ident extends IdentDeclaration implements VectorDeclarationInterface{
                public Ident(Position start, String label){
                    super(start, label);
                }

                public <ModVisitType> ModVisitType accept(ModuleVisitor<ModVisitType> modVisitor, Object... argv){
                    return modVisitor.visit(this, argv);
                }

                @Override
                public Expression GetIndex1(){ 
                    return vectorIndex1;// TODO Auto-generated method stub
                }

                @Override
                public Expression GetIndex2(){ // TODO Auto-generated method stub
                    return vectorIndex2; 
                }
            }
        }
        public final class Scalar{
            public class Ident extends IdentDeclaration{
                public Ident(Position start, String label){
                    super(start, label);
                }

                public <ModVisitType> ModVisitType accept(ModuleVisitor<ModVisitType> modVisitor, Object... argv){
                    return modVisitor.visit(this, argv);
                }
            }
        }
    }
    public final class Reg {
        public final class Vector extends VectorDeclaration{
            public Vector(Expression exp1, Expression exp2){
                super(exp1, exp2);
            }
            public class Ident extends IdentDeclaration implements VectorDeclarationInterface{
                public Ident(Position start, String label){
                    super(start, label);
                }

                public <ModVisitType> ModVisitType accept(ModuleVisitor<ModVisitType> modVisitor, Object... argv){
                    return modVisitor.visit(this, argv);
                }

                @Override
                public Expression GetIndex1(){ 
                    // TODO Auto-generated method stub
                    return vectorIndex1; 
                }

                @Override
                public Expression GetIndex2(){ // TODO Auto-generated method stub
                    return vectorIndex2; 
                }
            }
        }
        public final class Scalar {
            public final class Ident extends IdentDeclaration{
                public Ident(Position start, String lexeme){
                    super(start, lexeme);
                }
        
                public <ModVisitType> ModVisitType accept(ModuleVisitor<ModVisitType> modVisitor, Object... argv){
                    return modVisitor.visit(this, argv);
                }
            }
        }
    }
}