public class Concatenation implements Expression {

    private final List<Expression> expressionList;
    
    public Concatenation(List<Expression> expressionList){
	this.expressonList = expressionList;
    }

    public Expression getExpression(int index){
	return expressionList.get(index);
    }

    public int getSize(){
	return expressionList.size();
    }
}
