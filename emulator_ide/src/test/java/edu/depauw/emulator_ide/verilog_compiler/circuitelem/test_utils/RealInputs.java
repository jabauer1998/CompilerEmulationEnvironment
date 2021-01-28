import edu.depauw.emulator_ide.verilog_compiler.circuitelem.miscelem.Register;
    
import java.util.ArrayList;
import java.lang.Boolean;

public class RealInputs{

    private ArrayList<Register> inputs = new ArrayList();

    public RealInputs(boolean... inputs){
	for(boolean input : inputs){
	    this.inputs.add(input);
	}
    }

    public ArrayList<Register> getList(){
	return this.inputs;
    }

    public int size(){
	return inputs.size();
    }
}
