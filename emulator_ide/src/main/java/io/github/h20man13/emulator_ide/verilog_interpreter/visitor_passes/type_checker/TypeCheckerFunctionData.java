package io.github.h20man13.emulator_ide.verilog_interpreter.visitor_passes.type_checker;


import io.github.h20man13.emulator_ide.common.Position;
import java.util.ArrayList;

public class TypeCheckerFunctionData {

    private final TypeCheckerVariableData      returnType;
    private ArrayList<TypeCheckerVariableData> expressionTypes;
    private final Position                     position;

    public TypeCheckerFunctionData(TypeCheckerVariableData returnType, Position position) {
        this.position = position;
        this.returnType = returnType;
        this.expressionTypes = new ArrayList<>();
    }

    public void addParameterType(TypeCheckerVariableData parType){ expressionTypes.add(parType); }

    public int numParameterTypes(){ return expressionTypes.size(); }

    public TypeCheckerVariableData getParameterType(int index){ return expressionTypes.get(index); }

    public TypeCheckerVariableData getReturnType(){ return returnType; }

    public Position getPosition(){ return this.position; }

}
