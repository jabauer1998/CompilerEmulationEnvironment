import java.util.ArrayList;
import java.lang.Boolean;

public class Inputs{

    private ArrayList<Boolean> inputs = new ArrayList();

    public Inputs(boolean... inputs){
	for(boolean input : inputs){
	    this.inputs.add(input);
	}
    }

    public ArrayList<Boolean> getList(){
	return this.inputs;
    }

    public int size(){
	return inputs.size();
    }
}
