package edu.depauw.emulator_ide.verilog_compiler.visitor;

import edu.depauw.emulator_ide.verilog_compiler.token.Position;

public class TypeCheckerVariableData{

    public enum Type{
	//Literal Types
	INTEGER,
	REAL,
	BOOLEAN,
	STRING,

	//CONSTANT TYPES
	CONSTANT_INTEGER,
	CONSTANT_REAL,
	//NET types
	REGISTER,
	WIRE,
	OUTPUT,
	INPUT,
	OUTPUT_WIRE,
	OUTPUT_REG,
	INPUT_WIRE,
	REGISTER_VECTOR,
	OUTPUT_VECTOR,
	INPUT_VECTOR,
	OUTPUT_REGISTER_VECTOR,
	OUTPUT_WIRE_VECTOR,
	INPUT_WIRE_VECTOR,

	//Array Types
	REGISTER_ARRAY,
	REGISTER_VECTOR_ARRAY,
	OUTPUT_REGISTER_VECTOR_ARRAY,
	INTEGER_ARRAY
    }

    public Type type;
    private final Position position;
    private final int size;

    public TypeCheckerVariableData(Type type, Position position){
	this.position = position;
	this.type = type;
	size = 1;
    }

    public TypeCheckerVariableData(Type type, int size, Position position){
	this.position = position;
	this.type = type;
	this.size = size;
    }

    public Position getPosition(){
	return this.position;
    }
    
    
}
