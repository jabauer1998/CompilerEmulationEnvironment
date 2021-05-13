package edu.depauw.emulator_ide.assembler;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import edu.depauw.emulator_ide.assembler.Address.Address;
import edu.depauw.emulator_ide.assembler.Address.AddressExpr;
import edu.depauw.emulator_ide.assembler.Address.AddressPost;
import edu.depauw.emulator_ide.assembler.Address.AddressPre;
import edu.depauw.emulator_ide.assembler.ast.AstNode;
import edu.depauw.emulator_ide.assembler.ast.instruction.BranchWExchange;
import edu.depauw.emulator_ide.assembler.ast.instruction.BranchWLink;
import edu.depauw.emulator_ide.assembler.ast.instruction.DataProcessing;
import edu.depauw.emulator_ide.assembler.ast.instruction.LDR;
import edu.depauw.emulator_ide.assembler.ast.instruction.LDRH;
import edu.depauw.emulator_ide.assembler.ast.instruction.MRS;
import edu.depauw.emulator_ide.assembler.ast.instruction.MSRImmed;
import edu.depauw.emulator_ide.assembler.ast.instruction.MSRreg;
import edu.depauw.emulator_ide.assembler.ast.instruction.MUL;
import edu.depauw.emulator_ide.assembler.ast.instruction.MULL;
import edu.depauw.emulator_ide.assembler.ast.instruction.SWI;
import edu.depauw.emulator_ide.assembler.ast.instruction.Stop;
import edu.depauw.emulator_ide.assembler.ast.op2.Op2;
import edu.depauw.emulator_ide.assembler.ast.op2.PoundExpression;
import edu.depauw.emulator_ide.assembler.ast.op2.Shift;
import edu.depauw.emulator_ide.assembler.ast.op2.ShiftExpr;
import edu.depauw.emulator_ide.assembler.ast.op2.ShiftReg;
import edu.depauw.emulator_ide.common.Position;
import edu.depauw.emulator_ide.common.debug.InfoLog;
import edu.depauw.emulator_ide.common.debug.item.ErrorItem;
import edu.depauw.emulator_ide.verilog_compiler.symbol_table.Environment;

public class Parser {
    private List<Token> tokens;
    private InfoLog errorLog;
    private Environment<String, Integer> labels;
    private int address;
	
    public Parser(List<Token> tokens, InfoLog errorLog) {
	this.tokens = tokens;
	this.errorLog = errorLog;
	this.address = 0;
	labels = new Environment<>();
    }
	
    private void errorAndExit(String message, Position pos) {
	errorLog.addItem(new ErrorItem(message, pos));
    }
	
    private void errorAndExit(String message) {
	errorAndExit(message, null);
	System.exit(1);
    }
	
    private Token skip() {
	if(tokens.isEmpty()) {
	    errorAndExit("Unexpected end of file while parsing");
	    return null;
	} else {
	    return tokens.remove(0);
	}
    }
	
	
    private boolean willMatch(Token.Type type) {
	if(tokens.isEmpty()) {
	    return false;
	} else {
	    return tokens.get(0).getType() == type;
	}
    }
	
    private Token match(Token.Type type) {
	if(tokens.isEmpty()) {
	    errorAndExit("Unexpected end of file while parsing");
	    return null;
	} else if (willMatch(type)) {
	    return skip();
	} else {
	    Token skipped = skip();
	    errorAndExit("Expected token of type " + type + " but found token of type " + skipped.getType() + " found ", skipped.getPosition());
	    return null;
	}
    }
	
    public static void printList(String listName, List<Token.Type> list) {
	System.out.println("____________" + listName + "_____________" + list.size());
	System.out.println("{");
		
	for(Token.Type type :  list) {
	    System.out.println("\t" + type);
	}
	System.out.println("}\n");
    }
	
	
    public static List<Token.Type> bxList = Token.typesContainingStart("BX");
    public static List<Token.Type> b_blList = Token.subtractLists(Token.typesContainingStart("B"), bxList); //subtract bx list from this
	
    private static List<Token.Type> movList = Token.typesContainingStart("MOV");
    private static List<Token.Type> mvnList = Token.typesContainingStart("MVN");
    private static List<Token.Type> cmpList = Token.typesContainingStart("CMP");
    private static List<Token.Type> cmnList = Token.typesContainingStart("CMN");
    private static List<Token.Type> teqList = Token.typesContainingStart("TEQ");
    private static List<Token.Type> tstList = Token.typesContainingStart("TST");
    private static List<Token.Type> andList = Token.typesContainingStart("AND");
    private static List<Token.Type> eorList = Token.typesContainingStart("EOR");
    private static List<Token.Type> subList = Token.typesContainingStart("SUB");
    private static List<Token.Type> rsbList = Token.typesContainingStart("RSB");
    private static List<Token.Type> addList = Token.typesContainingStart("ADD");
    private static List<Token.Type> adcList = Token.typesContainingStart("ADC");
    private static List<Token.Type> sbcList = Token.typesContainingStart("SBC");
    private static List<Token.Type> rscList = Token.typesContainingStart("RSC");
    private static List<Token.Type> orrList = Token.typesContainingStart("ORR");
    private static List<Token.Type> bicList = Token.typesContainingStart("BIC");
	
    private static List<Token.Type> setCCList = Token.typesContainingEnd("S");
	
    private static List<Token.Type> dataprocList = new LinkedList<>();
    static {
	dataprocList.addAll(Token.typesContainingStart("MOV"));
	dataprocList.addAll(Token.typesContainingStart("MVN"));
	dataprocList.addAll(Token.typesContainingStart("CMP"));
	dataprocList.addAll(Token.typesContainingStart("CMN"));
	dataprocList.addAll(Token.typesContainingStart("TEQ"));
	dataprocList.addAll(Token.typesContainingStart("TST"));
	dataprocList.addAll(Token.typesContainingStart("AND"));
	dataprocList.addAll(Token.typesContainingStart("EOR"));
	dataprocList.addAll(Token.typesContainingStart("SUB"));
	dataprocList.addAll(Token.typesContainingStart("RSB"));
	dataprocList.addAll(Token.typesContainingStart("ADD"));
	dataprocList.addAll(Token.typesContainingStart("ADC"));
	dataprocList.addAll(Token.typesContainingStart("SBC"));
	dataprocList.addAll(Token.typesContainingStart("RSC"));
	dataprocList.addAll(Token.typesContainingStart("ORR"));
	dataprocList.addAll(Token.typesContainingStart("BIC"));
    }
	
    private static List<Token.Type> msrList= Token.typesContainingStart("MSR");
    private static List<Token.Type> mrsList = Token.typesContainingStart("MRS");
    private static List<Token.Type> mul_mlaList = Token.typesContainingStart("MUL");
    static {
	mul_mlaList.addAll(Token.typesContainingStart("MLA"));
    }
    private static List<Token.Type> mull_mlalList = Token.typesContainingStart("UMULL");
    static {
	mull_mlalList.addAll(Token.typesContainingStart("UMLAL"));
	mull_mlalList.addAll(Token.typesContainingStart("SMULL"));
	mull_mlalList.addAll(Token.typesContainingStart("SMLAL"));
    }
	
    private static List<Token.Type> ldrh_strhList = Token.typesContainingEnd("H"); // all load and str commands with H and SB at end are different and are ldrh commands
    static {
	ldrh_strhList.addAll(Token.typesContainingEnd("SB"));
    }
	
    private static List<Token.Type> ldr_strList = Token.subtractLists(Token.typesContainingStart("LDR"), ldrh_strhList);
    static {
	ldr_strList.addAll(Token.subtractLists(Token.typesContainingStart("STR"), ldrh_strhList)); 
    }
	
    private static List<Token.Type> ldm_stmList = Token.typesContainingStart("LDM");
    static {
	ldm_stmList.addAll(Token.typesContainingStart("STM"));
    }
	
    private static List<Token.Type> swiList = Token.typesContainingStart("SWI");
	
    private static int find(Token.Type elem, List<Token.Type> list) {
	for(int i = 0; i < list.size(); i++) {
	    if(list.get(i) == elem) {
		return i;
	    }
	}
	return -1;
    }
	
    private boolean isRegister() {
	if(willMatch(Token.Type.R0) || willMatch(Token.Type.R1) || willMatch(Token.Type.R2) || willMatch(Token.Type.R3) || willMatch(Token.Type.R4) || willMatch(Token.Type.R5) || willMatch(Token.Type.R6) || willMatch(Token.Type.R7) || willMatch(Token.Type.R8) || willMatch(Token.Type.R9) || willMatch(Token.Type.R10) || willMatch(Token.Type.R11) || willMatch(Token.Type.R12) || willMatch(Token.Type.R13) || willMatch(Token.Type.R14) || willMatch(Token.Type.R15)) {
	    return true;
	} else if(willMatch(Token.Type.CPSR)) {
	    return true;
	} else {
	    return false;
	}
    }
	
    private int getRegister(Token.Type type) {
	switch(type) {
	case R0:
	    return 0;
	case R1:
	    return 1;
	case R2:
	    return 2;
	case R3:
	    return 3;
	case R4:
	    return 4;
	case R5:
	    return 5;
	case R6:
	    return 6;
	case R7:
	    return 7;
	case R8:
	    return 8;
	case R9:
	    return 9;
	case R10:
	    return 10;
	case R11:
	    return 11;
	case R12:
	    return 12;
	case R13:
	    return 13;
	case R14:
	    return 14;
	case R15:
	    return 15;
	default:
	    errorAndExit("Unexpected register found " + type);
	    return -1;
	}
    }
	
    public int getOpCode(Token.Type type) {
	if(andList.contains(type)) {
	    return 0;
	} else if(eorList.contains(type)) {
	    return 1;
	} else if(subList.contains(type)) {
	    return 2;
	} else if(rsbList.contains(type)) {
	    return 3;
	} else if(addList.contains(type)) {
	    return 4;
	} else if(adcList.contains(type)) {
	    return 5;
	} else if(sbcList.contains(type)) {
	    return 6;
	} else if(rscList.contains(type)) {
	    return 7;
	} else if(tstList.contains(type)) {
	    return 8;
	} else if(teqList.contains(type)) {
	    return 9;
	} else if(cmpList.contains(type)) {
	    return 10;
	} else if(cmnList.contains(type)) {
	    return 11;
	} else if(orrList.contains(type)) {
	    return 12;
	} else if(movList.contains(type)) {
	    return 13;
	} else if(bicList.contains(type)) {
	    return 14;
	} else if(mvnList.contains(type)) {
	    return 15;
	} else {
	    errorAndExit("unknown type found for opcode " + type);
	    return -1;
	}
    }
	
    public List<AstNode> parseAssembly() {
	labels.addScope();
	address = 0;
	List<AstNode> ret = new LinkedList<>();
	while(!tokens.isEmpty()) {
	    if(willMatch(Token.Type.LABEL)) {
		Token ident = match(Token.Type.IDENT);
		labels.addEntry(ident.getLexeme(), address);
	    } else {
		Token tok = skip();
		Token.Type type = tok.getType();
		if(Parser.bxList.contains(type)) {
		    int index = find(type, bxList);
		    int cond;
		    if(index == 0){
			cond = 14;
		    } else {
			cond = index- 1;
		    }
		    if(isRegister()) {
			int reg = getRegister(skip().getType());
			ret.add(new BranchWExchange(cond, reg, tok.getPosition()));
			address += 4;
		    } else {
			Token skip = skip();
			errorAndExit("Expected a register token but got " + skip.getLexeme(), skip.getPosition());
		    }
		} else if(Parser.b_blList.contains(type)) {
		    int index = find(type, b_blList);
		    int cond;
		    if(index == 0){
			cond = 14;
		    } else {
			cond = index- 1;
		    }
					
		    boolean link = index >= (b_blList.size() / 2);
					
		    int exp = parseRawExpression();
					
		    ret.add(new BranchWLink(cond, link, exp, tok.getPosition()));
		    address += 4;
					
		} else if(Parser.dataprocList.contains(type)) {
					
		    int index = find(type, dataprocList);
		    int cond;
		    if(index == 0){
			cond = 14;
		    } else {
			cond = index- 1;
		    }
					
		    int opCode = getOpCode(type);
		    boolean setCC = setCCList.contains(type);
					
		    int rd = getRegister(skip().getType());
		    match(Token.Type.COMMA);
					
		    Op2 op2 = parseOp2();
		    ret.add(new DataProcessing(cond, opCode, setCC, 0, rd, op2, tok.getPosition()));
		    address += 4;
		} else if(Parser.msrList.contains(type)) {
		    int index = find(type, msrList);
		    int cond;
		    if(index == 0){
			cond = 14;
		    } else {
			cond = index- 1;
		    }
					
		    Token token = match(Token.Type.CPSR);
		    match(Token.Type.COMMA);
		    if(willMatch(Token.Type.POUND)) {
			PoundExpression pound = parsePoundExpression();
			ret.add(new MSRImmed(cond, pound, tok.getPosition()));
		    } else {
			int reg = getRegister(skip().getType());
			ret.add(new MSRreg(cond, reg, tok.getPosition()));
		    }
		    address+=4;
		} else if(Parser.mrsList.contains(type)) {
		    int index = find(type, mrsList);
		    int cond;
		    if(index == 0){
			cond = 14;
		    } else {
			cond = index- 1;
		    }
					
		    int reg = getRegister(skip().getType());
		    match(Token.Type.COMMA);
					
		    ret.add(new MRS(cond, reg, tok.getPosition()));
		    address += 4;
		} else if(Parser.mul_mlaList.contains(type)) {
		    int index = find(type, mrsList);
		    int cond;
		    if(index == 0){
			cond = 14;
		    } else {
			cond = index- 1;
		    }
					
		    boolean setCC = setCCList.contains(type);
					
		    int rd = getRegister(skip().getType());
		    match(Token.Type.COMMA);
		    int rm = getRegister(skip().getType());
		    match(Token.Type.COMMA);
		    int rs = getRegister(skip().getType());
		    if(willMatch(Token.Type.COMMA)) {
			int rn = getRegister(skip().getType());
			ret.add(new MUL(cond, false, setCC, rd, rn, rs, rm, tok.getPosition()));
		    } else {
			ret.add(new MUL(cond, false, setCC, rd, 0, rs, rm, tok.getPosition()));
		    }
		    address+=4;
		} else if(Parser.mull_mlalList.contains(type)) {
		    int index = find(type, mrsList);
		    int cond;
		    if(index == 0){
			cond = 14;
		    } else {
			cond = index- 1;
		    }
					
		    boolean setCC = setCCList.contains(type);
					
		    boolean a = (index < (mull_mlalList.size() / 4)) || ((index >= (mull_mlalList.size() / 2)) && (index < mull_mlalList.size() * (3/4)));
		    boolean u = (index >= mull_mlalList.size() / 2);
					
		    int rdLow = getRegister(skip().getType());
		    match(Token.Type.COMMA);
		    int rdHigh = getRegister(skip().getType());
		    match(Token.Type.COMMA);
		    int Rm = getRegister(skip().getType());
		    int Rs = getRegister(skip().getType());
					
		    ret.add(new MULL(cond, u, a, setCC, rdHigh, rdLow, Rs, Rm, tok.getPosition()));
		    address += 4;
		} else if(Parser.ldr_strList.contains(type)) {
		    int index = find(type, ldr_strList);
		    int cond;
		    if(index == 0){
			cond = 14;
		    } else {
			cond = index- 1;
		    }
					
		    boolean b = (index < ldr_strList.size() / 6) || ((ldr_strList.size() / 3 <= index) && (index < ldr_strList.size() * (2/3))) || (index >= ldr_strList.size() * (5/6));
		    boolean t = ((index >= ldr_strList.size() / 3) && (index < ldr_strList.size()/2)) || index >= ldr_strList.size() * (5/6);
		    boolean l = index < ldr_strList.size() / 2;
					
		    int Rd = getRegister(skip().getType());
		    match(Token.Type.COMMA);
					
		    Address addr = parseAddress();
					
		    ret.add(new LDR(cond, b, t, l, addr, tok.getPosition()));
		    address+=4;
		} else if(Parser.ldrh_strhList.contains(type)) {
		    int index = find(type, ldrh_strhList);
		    int cond;
		    if(index == 0){
			cond = 14;
		    } else {
			cond = index- 1;
		    }
					
		    int sh  = 0;
		    if(index < ldrh_strhList.size() / 6 || index >= ldrh_strhList.size() / 2 && index < ldrh_strhList.size() * (2/3)) {
			sh = 1;
		    } else if((index >= ldrh_strhList.size() / 3 && index < ldrh_strhList.size() / 2) || (index >= ldrh_strhList.size() * (2/3) && index < ldrh_strhList.size() * (5/6))) {
			sh = 3;
		    } else {
			sh = 2;
		    }
					
		    int rd = getRegister(skip().getType());
					
		    boolean l = index < ldrh_strhList.size() / 2;
					
		    match(Token.Type.COMMA);
					
		    Address addr = parseAddress();
					
		    ret.add(new LDRH(cond, l, rd, sh, addr, tok.getPosition()));
					
		    address+=4;
		} else if(Parser.ldm_stmList.contains(type)) {
		    int index = find(type, ldrh_strhList);
		    int cond;
		    if(index == 0){
			cond = 14;
		    } else {
			cond = index- 1;
		    }
					
		    //will implement later***
					
		    address+=4;
		} else if(Parser.swiList.contains(type)) {
		    int index = find(type, swiList);
		    int cond;
		    if(index == 0){
			cond = 14;
		    } else {
			cond = index- 1;
		    }
		    int exp = parseRawExpression();
		    ret.add(new SWI(cond, exp, tok.getPosition()));
		} else if(type == Token.Type.STOP){
		    ret.add(new Stop(tok.getPosition()));
		    address+=4;
		} else {
		    errorAndExit("Unexpected start of instruction " + tok.getLexeme(), tok.getPosition());
		}
	    }
	}
	return ret;
    }
	
    public PoundExpression parsePoundExpression() {
	match(Token.Type.POUND);
	Token numTok = match(Token.Type.NUM);
	int num = Integer.getInteger(numTok.getLexeme());
	return new PoundExpression(num, numTok.getPosition());
    }
	
    public Op2 parseOp2() {
	if(willMatch(Token.Type.POUND)) {
	    return parsePoundExpression();
	} else {
	    return parseShift();
	}
    }
	
    public int parseRawExpression() {
	if(willMatch(Token.Type.NUM)) {
	    Token numToken = skip();
	    int num = Integer.getInteger(numToken.getLexeme());
	    return num;
	} else if (willMatch(Token.Type.IDENT)) {
	    Token ident = skip();
	    return labels.getEntry(ident.getLexeme());
	} else {
	    return -1;
	}
    }
	
    public Shift parseShift() {
	Token s = skip();
	int register = getRegister(s.getType());
	if(willMatch(Token.Type.COMMA)){
	    skip();
	    int shiftType = parseShiftType();
	    if(willMatch(Token.Type.POUND)) {
		PoundExpression exp = parsePoundExpression();
		return new ShiftExpr(register, shiftType, exp, s.getPosition());
	    } else {
		int reg2 = getRegister(skip().getType());
		return new ShiftReg(register, shiftType, reg2, s.getPosition());
	    }
	} else {
	    return new Shift(register, s.getPosition());
	}
    }
	
    public int parseShiftType() {
	Token tok = skip();
	switch(tok.getType()) {
	case ASL:
	    return 0; //fill this number in later
	case LSL:
	    return 0;
	case LSR:
	    return 1;
	case ASR:
	    return 2;
	case ROR:
	    return 3;
	default:
	    errorAndExit("Exprected shift type but got " + tok.getType(), tok.getPosition());
	    return -1;
	}
    }
	
    private Address parseAddress() {
	if(willMatch(Token.Type.LBRACK)) {
	    skip();
	    Token regToken = skip();
	    int Rn = getRegister(regToken.getType());
	    if(willMatch(Token.Type.RBRACK)) {
			skip();
			if(willMatch(Token.Type.COMMA)) {
			    skip();
			    boolean pm = true;
			    if(willMatch(Token.Type.MINUS)) {
				pm = false;
				skip();
			    } else if(willMatch(Token.Type.PLUS)) {
				skip();
			    }
			    Op2 op2 = parseOp2();
			    return new AddressPost(Rn, pm, op2, regToken.getPosition());
			} else {
			    return new AddressPre(Rn, regToken.getPosition());
			}
	    } else if(willMatch(Token.Type.COMMA)) {
			skip();
			boolean pm = true;
			if(willMatch(Token.Type.MINUS)) {
			    pm = false;
			    skip();
			} else if(willMatch(Token.Type.PLUS)) {
			    skip();
			}
			Op2 op2 = parseOp2();
			match(Token.Type.RBRACK);
			boolean writeBack;
			if(willMatch(Token.Type.EXPL)) {
			    writeBack = true;
			    skip();
			} else {
			    writeBack = false;
			}
			return new AddressPre(Rn, pm, op2, writeBack, regToken.getPosition());
	    } else {
	    	return null;
	    }
			
	} else {
		int expressionRes = parseRawExpression();
    	return new AddressExpr(expressionRes, null);
	}
    }
	
}
