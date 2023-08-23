package io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.module_item.variable_declaration;

import io.github.H20man13.emulator_ide.common.Position;
import io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast.expression.Expression;
import io.github.H20man13.emulator_ide.verilog_interpreter.visitor_passes.visitor.ModuleVisitor;


   
public class Reg{
    public class Vector extends VectorDeclaration{
        public Vector(Expression exp1, Expression exp2){
            super(exp1, exp2);
        }
        public class Array extends ArrayDeclaration implements VectorDeclarationInterface{
            public final String annotationLexeme;

            public Array(Position start, String annotationLexeme, String lexeme, Expression index1, Expression index2){
                super(start, lexeme, index1, index2);
                this.annotationLexeme = annotationLexeme;
            }

            public Array(Position start, String lexeme, Expression index1, Expression index2){
                super(start, lexeme, index1, index2);
                this.annotationLexeme = null;
            }
    
            public <ModItemVisitType> ModItemVisitType accept(ModuleVisitor<ModItemVisitType> regValueVisitor, Object... argv){
                return regValueVisitor.visit(this, argv);
            }

            @Override
            public Expression GetIndex1(){ // TODO Auto-generated method stub
                return vectorIndex1; 
            }

            @Override
            public Expression GetIndex2(){ // TODO Auto-generated method stub
                return vectorIndex2; 
            }
        }
        public class Ident extends IdentDeclaration implements VectorDeclarationInterface{
            public final String annotationLexeme;

            public Ident(Position start, String lexeme){
                super(start, lexeme);
                this.annotationLexeme = null;
            }

            public Ident(Position start, String annotationLexeme, String lexeme){
                super(start, lexeme);
                this.annotationLexeme = annotationLexeme;
            }
    
            public <ModItemVisitType> ModItemVisitType accept(ModuleVisitor<ModItemVisitType> regValueVisitor, Object... argv){
                return regValueVisitor.visit(this, argv);
            }

            @Override
            public Expression GetIndex1(){ // TODO Auto-generated method stub
                return vectorIndex1; }

            @Override
            public Expression GetIndex2(){ // TODO Auto-generated method stub
                return vectorIndex2; 
            }
        }
    }
    public class Scalar{
        public class Array extends ArrayDeclaration{
            public final String annotationLexeme;
            
            public Array(Position start, String lexeme, Expression index1, Expression index2){
                super(start, lexeme, index1, index2);
                this.annotationLexeme = null;
            }

            public Array(Position start, String annotationLexeme, String lexeme, Expression index1, Expression index2){
                super(start, lexeme, index1, index2);
                this.annotationLexeme = annotationLexeme;
            }
    
            public <ModItemVisitType> ModItemVisitType accept(ModuleVisitor<ModItemVisitType> regValueVisitor, Object... argv){
                return regValueVisitor.visit(this, argv);
            }
        }
        public class Ident extends IdentDeclaration{
            public final String annotationLexeme;

            public Ident(Position start, String lexeme){
                super(start, lexeme);
                this.annotationLexeme = null;
            }

            public Ident(Position start, String annotationLexeme, String lexeme){
                super(start, lexeme);
                this.annotationLexeme = annotationLexeme;
            }
    
            public <ModItemVisitType> ModItemVisitType accept(ModuleVisitor<ModItemVisitType> regValueVisitor, Object... argv){
                return regValueVisitor.visit(this, argv);
            }
        }
    }
}
