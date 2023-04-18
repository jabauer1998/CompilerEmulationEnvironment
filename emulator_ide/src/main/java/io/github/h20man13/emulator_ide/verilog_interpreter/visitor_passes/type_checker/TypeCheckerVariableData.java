package io.github.h20man13.emulator_ide.verilog_interpreter.visitor_passes.type_checker;


import io.github.h20man13.emulator_ide.common.Position;

public class TypeCheckerVariableData {

	public enum Type{
		// Literal Types
		INTEGER, REAL, BOOLEAN, STRING,

		// CONSTANT TYPES
		CONSTANT_INTEGER, CONSTANT_REAL,
		// NET types
		REGISTER, WIRE, OUTPUT, INPUT, OUTPUT_WIRE, OUTPUT_REGISTER, INPUT_WIRE, REGISTER_VECTOR, WIRE_VECTOR, OUTPUT_VECTOR,
		INPUT_VECTOR, OUTPUT_REGISTER_VECTOR, OUTPUT_WIRE_VECTOR, INPUT_WIRE_VECTOR, INPUT_REGISTER_VECTOR, INPUT_REGISTER,

		// Array Types
		REGISTER_ARRAY, REGISTER_VECTOR_ARRAY, OUTPUT_REGISTER_VECTOR_ARRAY, OUTPUT_REGISTER_ARRAY, INTEGER_ARRAY,

		// Other Types
		UNDEFINED, MIXED_VECTOR
	}

	public Type            type;
	private final Position position;
	private final int      size;

	public TypeCheckerVariableData(Type type, Position position) {
		this.position = position;
		this.type = type;
		size = 1;
	}

	public TypeCheckerVariableData(Type type, int size, Position position) {
		this.position = position;
		this.type = type;
		this.size = size;
	}

	public Position getPosition(){ return this.position; }

	public int getSize(){ return this.size; }

}
