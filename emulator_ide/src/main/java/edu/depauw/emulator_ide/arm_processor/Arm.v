module Arm();
`define WIDTH 31
`define MEMSIZE 1200

reg [7:0] MEM [0:`MEMSIZE]; //Simulated Ram for this processor
   

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
`define BX 32'bzzzz000100101111111111110001zzzz //24
`define MRS 32'bzzzz00010z001111zzzz000000000000 //23
`define MSR1 32'bzzzz00010z101001111100000000zzzz //23
`define SWP 32'bzzzz00010z00zzzzzzzz00001001zzzz //15
`define MSR2 32'bzzzz00z10z1010001111zzzzzzzzzzzz //14
`define MULMLA 32'bzzzz000000zzzzzzzzzzzzzz1001zzzz //10
`define MULLMLAL 32'bzzzz00001zzzzzzzzzzzzzzz1001zzzz //9
`define LDRHSTRHLDRSBLDRSH 32'bzzzz000zz0zzzzzzzzzzzzzz1zz1zzzz //6
`define BBL 32'bzzzz101zzzzzzzzzzzzzzzzzzzzzzzzz //3
`define LDMSTM 32'bzzzz100zzzzzzzzzzzzzzzzzzzzzzzzz //3
`define DATAPROC 32'bzzzz00zzzzzzzzzzzzzzzzzzzzzzzzzz //2
`define LDRSTR 32'bzzzz01zzzzzzzzzzzzzzzzzzzzzzzzzz //2

   //Registers
   reg [`WIDTH:0] R[0:16]; //General Purpouse

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
   
   reg [`WIDTH:0] CSPR;//Status Register
   
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

   integer 	  InstructionCode;

   initial begin
      loadProgram(0); //load program at memory location 2 and set the stack pointer to the top of the program after loading
      while(fetch(R[15]) != 32'b11111111111111111111111111111111 && R[15] < `MEMSIZE) begin
	 INSTR = fetch(R[15]); //Fetch next memory
	 $display("Instruction at Memory %d is equal to %b", R[15], INSTR);
	 InstructionCode = decode(INSTR);
	 $display("Decode number of the instruction is %d", InstructionCode);
	 incriment; //increment the program counter by a word or 4 bytes
	 //execute(InstructionCode);
      end
      $display("Code done!!!");
      $finish;
   end

   task loadProgram;
      input [31:0] address;
      integer status, handler;
      reg [0:31] binaryLine;
      begin
	 R[15] = address; // initialize stack pointer to address 0
	 handler = $fopen("./memfile.txt", "r");
	 while(!$feof(handler)) begin
	    status = $fscanf(handler,"%b\n",binaryLine); //scan next line as binary
	    MEM[R[15]] = binaryLine[0:7];
	    MEM[R[15] + 1] = binaryLine[8:15];
	    MEM[R[15] + 2] = binaryLine[15:23];
	    MEM[R[15] + 3] = binaryLine[24:31];
	    R[15] = R[15] + 4;
	 end
	 $fclose(handler);
	 R[15] = address;
      end
   endtask //loadProgram

   function reg [`WIDTH:0] fetch;
      input reg [`WIDTH:0] addr;
      fetch = {MEM[addr], MEM[addr + 1], MEM[addr + 2], MEM[addr + 3]};
   endfunction // fetch

   /*
   task displayMemory;
      integer   index;
      begin
         for(index = 0; index < `MEMSIZE; index = index + 1)
	   $display("memory[%d] = %b", index, MEM[index]);
      end
   endtask // readmemory
    */
   
   function integer decode;
      input reg [`WIDTH:0] instruction;
      begin
	 casez(instruction)
	   `BX : decode = 0;
	   `MRS : decode = 18;
	   `MSR2 : decode = 20;
	   `SWP : decode = 26;
	   `MSR1 : decode = 19;
	   `MULMLA : decode = 21;
	   `MULLMLAL : decode = 22;
	   `LDRHSTRHLDRSBLDRSH : decode = 24;
	   `BBL : decode = 1;
	   `LDMSTM : decode = 25;
	   `DATAPROC: decode = instruction[24:21] + 2; // 2 to 17
	   `LDRSTR : decode = 23;
	   default: begin
	      $display("Error: Unidentified intruction %b", instruction);
	      $finish;
	      decode = -1;
	   end
	 endcase // casez ()
      end
   endfunction // fetch
   

   task incriment;
      R[15] = R[15] + 4; //incriment program counter by 4 bytes
   endtask // incriment

   task execute;
      input [31:0] code;
      reg [31:0]   op1;
      reg [31:0]   op2;
      reg [31:0]   copy;
      reg [31:0]   dest;
      reg [31:0]   offset;
      reg [32:0]   solution32;
      reg [64:0]   solution64;
      reg [31:0]   address;
      reg [15:0]   regList;
      integer 	   i;
      if(checkCC(INSTR[31:28]))
	case(code)
	  0: R[15] = R[INSTR[3:0]]; //BX or BE
	  1: begin //BL | B
	     if(INSTR[24]) //check if Link bit is set
	       R[14] = R[15];

	     R[15] = INSTR[23:0] << 2;
	     if(INSTR[23]) //check if the value is negative
	       R[15] |= ((1 << (`WIDTH - 23)) - 1) << 23; //if it is sign extend it so it is still negative
	  end
	  2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17: begin //AND Instruction
	     op1 = INSTR[19:16];
	     
	     if(INSTR[25]) begin
		op2 = INSTR[7:0];
		for(i=0; i <= `WIDTH; i = i + 1)
		  copy[i] = op2[(i + INSTR[11:8] * 2) % (`WIDTH+1)];
		op2 = copy;
	     end else begin
	       op2 = R[INSTR[3:0]];
	       if(INSTR[4])
		 case(INSTR[6:5])
		   2'b00: op2 = op2 << (R[INSTR[11:8]] & 8'b11111111); //Logical left
		   2'b01: op2 = op2 >> (R[INSTR[11:8]] & 8'b11111111); //Logical right
		   2'b10: begin 
			op2 = op2 >> (R[INSTR[11:8]] & 8'b11111111);
			if(op2[`WIDTH] == 1)
			  op2 |= ((1 << (R[INSTR[11:8]] & 8'b11111111)) - 1) << (`WIDTH + 1 - (R[INSTR[11:8]] & 8'b11111111)); //Arithmetic Right						
		   end							 
		   2'b11: begin
		      for(i = 0; i <= `WIDTH; i = i + 1)
			copy[i] = op2[(i + (R[INSTR[11:8]] & 8'b11111111)) % (`WIDTH+1)]; //Rotate Right
		      op2 = copy;
		   end
		 endcase
	       else
		 case(INSTR[6:5])
		   2'b00: op2 = op2 << INSTR[11:7]; //Logical left
		   2'b01: op2 = op2 >> INSTR[11:7]; //Logical right
		   2'b10: begin //Arithmetic right
		      op2 = op2 >> INSTR[11:7];
		      if(op2[`WIDTH]) //Aritmetic right
			op2 |= ((1 << INSTR[11:7]) - 1) << (`WIDTH + 1 - INSTR[11:7]);
		   end
		   2'b11: begin
		      for(i=0; i <= `WIDTH; i = i + 1)
			copy[i] = op2[((i + INSTR[11:7]) % (`WIDTH + 1))]; //Rotate Right
		      op2 = copy;
		   end
		 endcase
	     end // else
	     
	     case(code)
	       2, 10: solution32 = op1 & op2;
	       3, 11: solution32 = op1 ^ op2;
	       4, 12: solution32 = op1 - op2;
	       5: solution32 = op2 - op1;
	       6, 13: solution32 = op1 + op2;
	       7: solution32 = op1 + op2 + `C;
	       8: solution32 = op1 - op2 + `C - 1;
	       9: solution32 = op2 - op1 + `C - 1;
	       14: solution32 = op1 | op2;
	       15: solution32 = op2;
	       16: solution32 = op1 & ~op2;
	       17: solution32 = ~op2;
	     endcase // case (code)
	     
	     if(INSTR[20]) begin
		     `C = solution32[32];
		     `Z = (solution32 == 0) ? 1 : 0;
		     `N = (solution32[31] == 1) ? 1 : 0;
                     `V = (solution32[31] & ~op1[`WIDTH] & ~op2[`WIDTH]) | (~solution32[31] & op1[`WIDTH] & op2[`WIDTH]);
	     end

	     if(code >= 2 && code <= 9 || code >= 14 && code <= 17) //If it is a destination instruction write back to the destination
		R[INSTR[15:12]] = solution32[31:0];
	     
	  end
	  18: begin //MRS Instruction
	     if(INSTR[22]) begin 
	       $display("Error: there is no SPSR on this machine");
	       $finish;	
	     end
	     R[INSTR[15:12]] = CSPR; 
	  end 
	  19:  begin //MSR1 Instruction
	     if(INSTR[22]) begin 
	       $display("Error: there is no SPSR on this machine");
	       $finish;	
	     end
	     CSPR = R[INSTR[3:0]];
	  end  
	  20: begin //MSR2 Instruction
	     if(INSTR[22]) begin
	       $display("Error: there is no SPSR on this machine");
	       $finish;	
	     end

	     if(INSTR[11:4] == 0)
	       CSPR = R[INSTR[3:0]];
	     else begin
		CSPR = INSTR[7:0]; //sign extend the value to 32 bits inside the CSPR register
		for(i = 0; i <= `WIDTH; i = i + 1)
		  copy = CSPR[(i + (INSTR[11:8] * 2)) % (`WIDTH+1)]; //rotate it
		CSPR = copy;
	     end
	     
          end
	  21: begin //MUL | MLA Instruction
	     op1 = R[INSTR[3:0]];
	     op2 = R[INSTR[11:8]];
	     solution32 = op1 * op2;
	     if(INSTR[21])
	       solution32 = solution32 + R[INSTR[15:12]];
		      
	     if(INSTR[20])
	       `C = solution32[32];
	       `Z = (solution32 == 0) ? 1 : 0;
	       `N = (solution32[`WIDTH] == 1) ? 1 : 0;
               `V = (solution32[`WIDTH] & ~op1[`WIDTH] & ~op2[`WIDTH]) | (~solution32[`WIDTH] & op1[`WIDTH] & op2[`WIDTH]);
	  end
	  22: begin //MULL | MLAL
	     op1 = R[INSTR[3:0]]; //first op
	     op2 = R[INSTR[11:8]]; //second opp
	     solution64 = op1 * op2;
	     if(INSTR[21]) //is there a add in this instruction
	       solution64 = solution64 + {R[INSTR[19:16]], R[INSTR[15:12]]};

             if(INSTR[20])
	       `C = solution64[64];
	       `Z = (solution64 == 0) ? 1 : 0;
	       `N = (solution64[63] == 1) ? 1 : 0;
               `V = (solution64[63] & ~op1[`WIDTH] & ~op2[`WIDTH]) | (~solution64[63] & op1[`WIDTH] & op2[`WIDTH]);
             
	     if(INSTR[19:16] != INSTR[15:12] && INSTR[19:16] != 15 && INSTR[15:12] != 15) begin
		R[INSTR[19:16]] = solution64[63:32];
		R[INSTR[15:12]] = solution64[31:0];
	     end
	  end 
	  23: begin //LD | STR
	     op1 = R[INSTR[19:16]];
	     if(INSTR[25])//I bit
	       offset = shift(INSTR);//register shifted
	     else
	       offset = INSTR[11:0]; //immediate
	     
	     if(INSTR[24])//U bit
	       if(INSTR[23])
		 R[INSTR[19:16]] = R[INSTR[19:16]] + offset;	        
	       else
		 R[INSTR[19:16]] = R[INSTR[19:16]] - offset;
	     
	     if(INSTR[20])// load
	       if(INSTR[22]) begin //In byte mode
		  op2 = MEM[R[INSTR[19:16]]];
		  R[INSTR[15:12]] = op2[7:0];
	       end else begin //In word mode
		  R[INSTR[15:12]] = MEM[R[INSTR[19:16]]];
	       end
	     else //store
	       if(INSTR[22])begin //In byte mode
		  op2 = R[INSTR[15:12]];
		  MEM[op1] = op2[7:0];
	       end else begin //In word mode (Big endian notation)
		  op2 = R[INSTR[15:12]];
		  op1 = R[INSTR[19:16]];
		  MEM[R[INSTR[19:16]]] = op2[31:24]; //1st byte in memory is highest order byte
		  MEM[R[INSTR[19:16]] + 1] = op2[23:16];
		  MEM[R[INSTR[19:16]] + 2] = op2[15:8];
		  MEM[R[INSTR[19:16]] + 3] = op2[7:0];
	       end

	     if(!INSTR[24])//U bit
	       if(INSTR[23])
		 R[INSTR[19:16]] = R[INSTR[19:16]] + offset;	        
	       else
		 R[INSTR[19:16]] = R[INSTR[19:16]] - offset;

	     if(INSTR[21] && INSTR[24])
	       R[INSTR[19:16]] = op1;
	     
	  end // case: 22
	  24: begin //LDRH | STRH | LDRSB | LDRSH
	     op1 = R[INSTR[19:16]];
	     
	     if(INSTR[11:8] == 0)
	       offset = R[INSTR[3:0]];
	     else
	       offset = {INSTR[11:8],INSTR[3:0]}; //immediate
	     
	     if(INSTR[24])//U bit
	       if(INSTR[23])
		 R[INSTR[19:16]] = R[INSTR[19:16]] + offset;	        
	       else
		 R[INSTR[19:16]] = R[INSTR[19:16]] - offset;
	     
	     if(INSTR[20])// load
	       case(INSTR[6:5])
		 2'b00: begin //SWP
		    op2 = MEM[R[INSTR[19:16]]];
		    MEM[R[INSTR[19:16]]] = R[INSTR[15:12]];
		    R[INSTR[15:12]] = op2;
		 end
		 2'b01: begin //Unsigned Halfwords
		    op2 = MEM[R[INSTR[19:16]]];
		    R[INSTR[15:12]] = op2[15:0];
		 end
		 2'b10: begin //Signed Bytes
		    op2 = MEM[R[INSTR[19:16]]];
		    R[INSTR[15:12]] = op2[7:0];
		 end
		 2'b11: begin //Signed Halfwords
		    op2 = MEM[R[INSTR[19:16]]];
		    R[INSTR[15:12]] = op2[15:0];
		 end
	       endcase // case (INSTR[6:5])
	     else //store
	       case(INSTR[6:5])
		 2'b00: begin //SWP
		    op2 = R[INSTR[15:12]];
		    R[INSTR[15:12]] = MEM[op1];
		    MEM[op1] = op2;
		 end
		 2'b01: begin //Unsigned Halfwords
		    op2 = R[INSTR[15:12]];
		    MEM[op1] = 0;
		    MEM[op1 + 1] = 0;
		    MEM[op1 + 2] = op2[15:0];
		 end
		 2'b10: begin //Signed Bytes
		    op2 = R[INSTR[15:12]];
		    MEM[op1] = op2[7:0];
		 end
		 2'b11: begin //Signed Halfwords
		    op2 = R[INSTR[15:12]];
		    MEM[op1] = 0;
		    MEM[op1 + 1] = 0;
		    MEM[op1 + 2] = op2[15:0];
		 end
	       endcase // case (INSTR[6:5])

	     if(!INSTR[24])//U bit
	       if(INSTR[23])
		 R[INSTR[19:16]] = R[INSTR[19:16]] + offset;	        
	       else
		 R[INSTR[19:16]] = R[INSTR[19:16]] - offset;
	     
	     if(INSTR[21] && INSTR[24])
	       R[INSTR[19:16]] = op1;
	  end // case: 24
	  25: begin
	     address = R[INSTR[19:16]];
	     regList = INSTR[15:0];
	     if(INSTR[20])begin
		if(INSTR[23]) begin
		   if(INSTR[24])
		     address = address + 4;
		   
		   for(i = 0; i < 16; i++)
		     if(regList[i] == 1) begin
			R[i] = MEM[address];
			address = address + 4;
		     end
		   
		end else begin
		   if(INSTR[24])
		     address = address - 4;
		   
		   for(i = 15; i >= 0; i--)
		     if(regList[i] == 1) begin
			R[i] = MEM[address];
			address = address - 4;
		     end
		   
		end // else: !if(INSTR[23])
	     end else begin
		
		if(INSTR[23]) begin
		   if(INSTR[24])
		     address = address + 4;
		   
		   for(i = 0; i < 16; i++)
		     if(regList[i] == 1) begin
			MEM[address] = R[i];
			address = address + 4;
		     end
		   
		end else begin
		   if(INSTR[24])
		     address = address - 4;
		   
		   for(i = 15; i >= 0; i--)
		     if(regList[i] == 1) begin
			MEM[address] = R[i];
			address = address - 4;
		     end
		   
		end // else: !if(INSTR[23])
		

		if(INSTR[21] && INSTR[24])
		  R[INSTR[19:16]] = op1;
	     end // else: !if(INSTR[20])
	     
	  end // case: 25
	  26: begin //data swap
	     if(INSTR[21]) begin
		op1 = MEM[R[INSTR[19:16]]];
		R[INSTR[15:12]] = op1[8:0];
		op1 = R[INSTR[3:0]];
		MEM[R[INSTR[19:16]]] = op1[7:0];
	     end else begin
		R[INSTR[15:12]] = MEM[R[INSTR[19:16]]];
		MEM[R[INSTR[19:16]]] = R[INSTR[3:0]];
	     end
	  end // case: 25

	  default: begin
	    $display("Unknown opcode %b", code);
	    $finish;
	  end
	  
	endcase // case (code)
   endtask // execute

   function reg[31:0] shift;
      input [31:0] INSTR;
      reg [4:0]    AMOUNT;
      reg [1:0]    TYPE;
      begin 
	 if(INSTR[4])
	   AMOUNT = R[INSTR[11:8]];
	 else
	   AMOUNT = INSTR[11:7];
	 TYPE = INSTR[6:5];
	 case(TYPE)
	   2'b00: shift = R[INSTR[3:0]] << AMOUNT;
	   2'b01: shift = R[INSTR[3:0]] >> AMOUNT;
	   2'b10: if(INSTR[3])
	     shift = {1'b1, R[INSTR[3:0]]} >> AMOUNT;
	   else
	     shift = INSTR[3:0] >> AMOUNT;
	   2'b11: shift = {R[INSTR[3:0]], R[INSTR[3:0]]} >> AMOUNT;
	   default: begin
	      $display("Error: Unidentified Shift Type %b", TYPE);
	      shift = -1;
	      $finish;
	   end
	 endcase // case (TYPE)
      end
   endfunction // shift
   
   
   function checkCC;
      input [4:0] code;
      case(code)
	`EQ : checkCC = `Z;
	`NE : checkCC = ~`Z;
	`CS : checkCC = `C;
	`CC : checkCC = ~`C;
	`MI : checkCC = `N;
	`PL : checkCC = ~`N;
	`VS : checkCC = `V;
	`VC : checkCC = ~`V;
	`HI : checkCC = `C & ~`Z;
	`LS : checkCC = ~`C & `Z;
	`GE : checkCC = `N == `V;
	`LT : checkCC = `N != `V;
	`GT : checkCC = ~`Z & (`N == `V);
	`LE : checkCC = `Z | (`N != `V);
	`AL : checkCC = 1; //allways ignored
	default: begin
	   $display("Error: Unidentified intruction %b", code);
	   $finish;
	   checkCC = -1;
	end
      endcase // case (code)
   endfunction // checkCC

   task setCC;
      input [31:0] op1;
      input [31:0] op2;
      input [31:0] result;
      begin
	 `C = result[`WIDTH];
	 `Z = result == 0;
	 `N = result[`WIDTH - 1];
	 `V = (result[`WIDTH - 1] & ~op1[`WIDTH - 1] & ~op2[`WIDTH - 1]) | (~result[`WIDTH - 1] & op1[`WIDTH - 1] & op2[`WIDTH - 1]);
      end
   endtask // setCC
   
   
   
endmodule // Arm
