package edu.depauw.emulator_ide.verilog_compiler.data_structure;

public class Pointer<DataType> {
    private DataType data;
    
    public Pointer(DataType data){
        this.data = data;
    }

    public DataType deRefrence(){
        return data;
    }

    public void assign(DataType data){
        this.data = data;
    }

    public Pointer<Pointer<DataType>> reference(){
        return new Pointer<Pointer<DataType>>(this);
    }
}
