package edu.depauw.emulator_ide.verilog_compiler.passes.interpreter.value;

public class RealVal implements Value{
    
    private double value;

    public RealVal(double value){
        this.value = value;
    }

    public double getValue(){
        return value;
    }

    public String toString(){
        return Double.toString(value);
    }
    
}
