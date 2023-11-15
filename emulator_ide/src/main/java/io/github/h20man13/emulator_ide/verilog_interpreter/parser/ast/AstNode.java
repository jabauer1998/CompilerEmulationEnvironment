package io.github.H20man13.emulator_ide.verilog_interpreter.parser.ast;


import io.github.H20man13.emulator_ide.common.Position;

public abstract class AstNode {
    public final Position position; // field to store the starting position of an ast node

    protected AstNode(Position position) { this.position = position; }

    public abstract String toString();
}
