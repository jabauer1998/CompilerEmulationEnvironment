package edu.depauw.emulator_ide.verilog_interpreter.parser.ast;


import edu.depauw.emulator_ide.common.Position;

public abstract class AstNode {
    public final Position position; // field to store the starting position of an ast node

    protected AstNode(Position position) { this.position = position; }
}
