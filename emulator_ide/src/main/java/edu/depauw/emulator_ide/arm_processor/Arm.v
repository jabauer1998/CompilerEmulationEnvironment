module Arm();
`define WIDTH 31
`define MEMSIZE 3000

reg [7:0] MEM [MEMSIZE]; //Simulated Ram for this processor
   

//Conditional codes
`define EQ 4'b0000
`define NE 4'b0001
`define CS 4'b0010
`define CC 4'b0011
`define MI 4'b0100
`define PL 4'b0101
`define VS 4'b0110
`define VC 4'b0111
`define HI 4'b1000
`define LS 4'b1001
`define GE 4'b1010
`define LT 4'b1011
`define GT 4'b1100
`define LE 4'b1101
`define AL 4'b1110

//OpType
`define BX 32'bzzzz000100101111111111110001zzzz
`define BBL 32'bzzzz101zzzzzzzzzzzzzzzzzzzzzzzzz
`define DATAPROC 32'bzzzz00zzzzzzzzzzzzzzzzzzzzzzzzzz
`define MRS 32'bzzzz00010z001111zzzz000000000000
`define MSR 32'bzzzz00z10z1010001111zzzzzzzzzzzz
`define MULMLA 32'bzzzz000000zzzzzzzzzzzzzz1001zzzz
`define MULLMLAL 32'bzzzz00001zzzzzzzzzzzzzzz1001zzzz
`define LDRSTR 32'bzzzz01zzzzzzzzzzzzzzzzzzzzzzzzzz
`define LDRHSTRHLDRSBLDRSH 32'bzzzz000zz0zzzzzzzzzzzzzz1zz1zzzz
`define LDMSTM 32'bzzzz100zzzzzzzzzzzzzzzzzzzzzzzzz
`define SWP 32'bzzzz00010z00zzzzzzzz00001001zzzz
`define SWI 32'bzzzz1111zzzzzzzzzzzzzzzzzzzzzzzz

//Registers
   reg [`WIDTH:0] R[16]; //General Purpouse

   /* //old register allocations
    reg [WIDTH:0] R0; //general purpouse
    reg [WIDTH:0] R1; //general purpouse
    reg [WIDTH:0] R1; //General Purpouse
    reg [WIDTH:0] R2; //General Purpouse
    reg [WIDTH:0] R3; //General Purpouse
    reg [WIDTH:0] R4; //General Purpouse
    reg [WIDTH:0] R5; //General Purpouse
    reg [WIDTH:0] R6; //General Purpouse
    reg [WIDTH:0] R7; //Holds SysCall Number
    reg [WIDTH:0] R8; //General Purpouse
    reg [WIDTH:0] R9; //General Purpouse
    reg [WIDTH:0] R10;//General Purpouse
    reg [WIDTH:0] R11;//General Purpouse
    reg [WIDTH:0] R12;//Intra Procedure Call
    reg [WIDTH:0] R13;//Stack Pointer
    reg [WIDTH:0] R14;//Link Register
    reg [WIDTH:0] R15;//Program Counter
    */
    reg [WIDTH:0] CSPR;//Status Register
   
   //hidden registers
   reg [`WIDTH:0] INSTR;
   
//CSPR register
`define N CSPR[31]
`define Z CSPR[30]
`define C CSPR[29]
`define V CSPR[28]
`define Q CSPR[27]
`define IT1 CSPR[26:25]
`define J CSPR[24]
`define GE CSPR[19:16]
`define IT2 CSPR[15:10]
`define E CSPR[9]
`define A CSPR[8]
`define I CSPR[7]
`define F CSPR[6]
`define T CSPR[5]
`define M CSPR[4:0]

   integer valid;

   initial begin
      loadProgram;
      R[15] = 0; // initialize stack pointer to 0
      while(fetch > 0 && R[15] < `MEMSIZE) begin
	 INSTR = fetch;
	 InstructionCode = decode(INSTR);
	 incriment;
	 execute(INSTR, InstructionCode);
      end
   end

   task loadProgram;
      $readmemb("file to be determined", MEM, 0, MEMSIZE);
   endtask // loadProgram

   function fetch;
	 fetch = MEM[R[15]];
   endfunction // fetch
   

   function decode;
      input reg [WIDTH:0] instruction;
      integer 	    temp;
      begin
	 casez(instruction)
	   `BX : temp = 0;
	   `BBL : temp = 1;
	   `DATAPROC : temp = instruction[24:21] + 2;
	   `MRS : temp = 16;
	   `MSR : temp = 17;
	   `MULMLA : temp = 18;
	   `MULLMLAL : temp = 19;
	   `LDRSTR : temp = 20;
	   `LDRHSTRHLDRSBLDRSH : temp = 21;
	   `LDMSTM : temp = 22;
	   `SWP : temp = 23;
	   `SWI : temp = 24;
	   default: begin
	      $display("Error: Unidentified intruction %b", instruction);
	      decode = -1;
	 endcase // casez ()
	 setCC(instruction, temp);
	 decode = temp;
      end
   endfunction // fetch

   task incriment;
      R[15] = R[15] + 4; //incriment stack pointer by 4 bytes
   endtask // incriment

   task execute;
      input [31:0] INSTR;
      input [31:0] code;
      reg [31:0] op1;
      reg [31:0] op2;
      reg [31:0] dest;
	 if(checkCC(INSTR[31:28]))
	   case(code)
	     0: R[15] = INSTR[3:0]; //BE
	     1: begin //BL or B
		if(INSTR[24]) //check if Link bit is set
		  R[14] = R[15];
		if(INSTR[23]) //check if the value is negative
		  R[15] = R[15] + {0b111111, INSTR[23:0] << 2};
		else
		  R[15] = R[15] + (INSTR[23:0] << 2);
	     end
	     2: begin //AND Instruction
		op1 = INSTR[19:16];
		if(INSTR[25])
		  op2 = INSTR[7:0] << R[INSTR[11:8]];
		else
		  op2 = INSTR[3:0] << R[INSTR[11:4]];
		if(INSTR[20])
		  setCC(code, op1, op2);
		R[INSTR[15:12]] = op1 & op2;
	     end
	     3: begin //EOR Instruction
		op1 = INSTR[19:16];
		if(INSTR[25])
		  op2 = shift(INSTR);
		else
		  op2 = shift(INSTR);
		if(INSTR[20])
		  setCC(code, op1, op2);
		R[INSTR[15:12]] = op1 ^ op2;
	     end
	     4: begin //SUB Instruction
		op1 = INSTR[19:16];
		if(INSTR[25])
		  op2 = shift(INSTR);
		else
		  op2 = shift(INSTR);
		if(INSTR[20])
		  setCC(code, op1, op2);
		R[INSTR[15:12]] = op1 - op2;
	     end
	     5: begin //RSB Instruction
		op1 = INSTR[19:16];
		if(INSTR[25])
		  op2 = shift(INSTR);
		else
		  op2 = shift(INSTR);
		if(INSTR[20])
		  setCC(code, op1, op2);
		R[INSTR[15:12]] = op2 - op1;
	     end
	     6: begin //ADD Instruction
		op1 = INSTR[19:16];
		if(INSTR[25])
		  op2 = shift(INSTR);
		else
		  op2 = shift(INSTR);
		if(INSTR[20])
		  setCC(code, op1, op2);
		R[INSTR[15:12]] = op1 + op2;
	     end
	     7: begin //ADC Instruction
		op1 = INSTR[19:16];
		if(INSTR[25])
		  op2 = shift(INSTR);
		else
		  op2 = shift(INSTR);
		if(INSTR[20])
		  setCC(code, op1, op2);
		R[INSTR[15:12]] = op1 + op2 + C;
	     end
	     8: begin //SBC Instruction
		op1 = INSTR[19:16];
		if(INSTR[25])
		  op2 = shift(INSTR);
		else
		  op2 = shift(INSTR);
		if(INSTR[20])
		  setCC(code, op1, op2);
		R[INSTR[15:12]] = op1 - op2 + C - 1;
	     end
	     9: begin //RSC Instruction
		op1 = INSTR[19:16];
		if(INSTR[25])
		  op2 = shift(INSTR);
		else
		  op2 = shift(INSTR);
		if(INSTR[20])
		  setCC(code, op1, op2);
		R[INSTR[15:12]] = op2 - op1 + C - 1;
	     end
	     10, 11, 12, 13: begin //TST, TEQ, CMP, CMN Instruction
		op1 = INSTR[19:16];
		if(INSTR[25])
		  op2 = shift(INSTR);
		else
		  op2 = shift(INSTR);
		if(INSTR[20])
		  setCC(code, op1, op2);
	     end
	     14: begin //ORR Instruction
		op1 = INSTR[19:16];
		if(INSTR[25])
		  op2 = shift(INSTR);
		else
		  op2 = shift(INSTR);
		if(INSTR[20])
		  setCC(code, op1, op2);
		R[INSTR[15:12]] = op1 | op2;
	     end
	     15: begin //MOV Instruction
		if(INSTR[25])
		  op2 = shift(INSTR);
		else
		  op2 = shift(INSTR);
		if(INSTR[20])
		  setCC(code, op1, op2);
		R[INSTR[15:12]] = op2;
	     end
	     16: begin //BIC Instruction
		op1 = INSTR[19:16];
		if(INSTR[25])
		  op2 = shift(INSTR);
		else
		  op2 = shift(INSTR);
		if(INSTR[20])
		  setCC(code, op1, op2);
		R[INSTR[15:12]] = op1 & ~op2;
	     end
	     17: begin //MVN Instruction
		if(INSTR[25])
		  op2 = shift(INSTR);
		else begin
		  op2 = shift(INSTR);
		end
		if(INSTR[20])
		  setCC(code, op1, op2);
		R[INSTR[15:12]] = ~op2;
	     end
	     18: R[INSTR[15:12]] = CSPR; //MRS Instruction
	     19: CSPR = R[INSTR[3:0]]; //MSR Instruction
	     20: begin //MULMLA Instruction
		op1 = R[INSTR[3:0]];
		op2 = R[INSTR[11:8]];
		solution = op1 * op2;
		if(INSTR[21])
		  solution = solution + R[INSTR[15:12]];
		if(INSTR[20])
		  setCC(code, op1, op2);
		R[INSTR[19:16]] = solution;
		end
	     21: begin //MULLMLAL
		op1 = R[INSTR[3:0]];
		op2 = R[INSTR[11:8]];
		solution = op1 * op2;
		if(INSTR[21])
		  solution = solution + {R[INSTR[19:16]], R[INSTR[15, 12]]} ;
		if(INSTR[20])
		  setCC(code, op1, op2);
		R[INSTR[19:16]] = solution[63:32];
		R[INSTR[15:12]] = solution[31:32];
	     end 
	     22: begin LDSTR
	       op1 = R[19:16];
	       if(INSTR[25])
		 offset = shift(INSTR);
	       else
		 offset = INSTR[11:0];
		
	       if(INSTR[20])
		 
	     end
	   endcase // case (code)
   endtask // execute

   function shift;
      input [31:0] INSTR;
      reg [4:0] AMOUNT;
      reg [1:0] TYPE;
      begin 
	 if(INSTR[4])
	   AMOUNT = R[INSTR[11:8]];
	 else
	   AMOUNT = INSTR[11:7];
	 TYPE = INSTR[6:5];
	 case(TYPE)
	   'b00: shift = INSTR[3:0] << AMOUNT;
	   'b01: shift = INSTR[3:0] >> AMOUNT;
	   'b10: if(INSTR[3])
	      shift = {1, INSTR[3:0]} >> AMOUNT;
	     else
	       INSTR[3:0] >> AMOUNT;
	   'b11: shift = {INSTR[0], INSTR[3:0]} >> AMOUNT;
	   default: begin
	      $display("Error: Unidentified Shift Type %b", TYPE);
	      shift = -1;
	   end
      end
   endfunction // shift
   
   
   function checkCC;
      input [4:0] code;
      case(code)
	`EQ : checkCC = Z;
	`NE : checkCC = ~Z;
	`CS : checkCC = C;
	`CC : checkCC = ~C;
	`MI : checkCC = N;
	`PL : checkCC = ~N;
	`VS : checkCC = V;
	`VC : checkCC = ~V;
	`HI : checkCC = C & ~Z;
	`LS : checkCC = ~C & Z;
	`GE : checkCC = N == V;
	`LT : checkCC = N != V;
	`GT : checkCC = ~Z & (N == V);
	`LE : checkCC = Z | (N != V);
	`AL : checkCC = 1; //allways ignored
	default: begin
	   $display("Error: Unidentified intruction %b", code);
	   checkCC = -1;
	end
      endcase // case (code)
   endfunction // checkCC
   
endmodule // Arm
