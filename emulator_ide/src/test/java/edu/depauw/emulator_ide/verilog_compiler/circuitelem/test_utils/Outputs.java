import java.util.ArrayList;

import java.lang.Boolean;

public class Outputs{
    private ArrayList<Boolean> outputs = new ArrayList();

    public Inputs(boolean... outputs){
	for(boolean output : outputs){
	    this.outputs.add(output);
	}
    }

    public ArrayList<Boolean> getList(){
	return this.inputs;
    }
}
