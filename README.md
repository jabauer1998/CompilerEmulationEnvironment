# Emulator Debug Enviroment
# What is it?
The EDE is software that is inspired by the PEP9 virtual computer. PEP9 is an educational tool that allows students to learn the basics of how a computer works without having to dive into any actual hardware. It is a much more cost effective solution for universities to teach undergrad/introductory level assembly/computer systems courses. The clever GUI also allows the students to easily visualize whats going on inside the CPU. It does have its limits. Since PEP 9 is not an actual computer, the assembly syntax is useless in industry. It also offeres a very impractical register file. To account for this the EDE is a system where an instructor(or anyone else) can create an emulator with a Hardware Description Language(Verilog), and a config file. Then the EDE will spit out a PEP9 like gui. This gui can be utilized to like the pep9 to teach a computer systems course, however with a customized processor. The HDL component of the project can be utilized to teach a computer architecture course without having to buy expensive FPGA's. The EDE can also be used in industry as a high level final step pre silicon verification tool. Fabless semiconductor companies can use it to verify that there architecture works prior to sending the design off to get manufactured and if it doesnt they can use the gui to give them insight about where the error is occuring.

# Software Used in this Poject
Javafx - API to create the IDE <br>
Java - Programming langage <br>
Junit - Unit testing interface <br>
Verilog - Used to Design Test Processor

# Features Completed
  <ul>
    <li> Created Gate classes to use for interpretation (usefull for debugging) </li>
    <li> Created Source class and Destination for Dealing with IO</li>
    <li> Created Lexer, Token, and Position classes to handle Lexing a subset of the verilog language</li>
    <li> Specified the Grammer of a Subset of Verilog for the Compiler</li>
    <li> Created an ARMTDMI7 processor in verilog in order to test the graphical user interface </li>
    <li> Created an Interpreter for verilog to run the ARMTDMI7 processor code. Added functions to the Verilog language to allow for proper communication to the GUI </li>
    <li> Created a Graphical User Interface with JavaFX that works with the Verilog Processor </li>
  </ul>
#Features in Development
Currently due to Java having no unsigned types I am converting this project into C++ for future use. With a 32 bit processor this works absolutely fine however if the user wanted to design a 64 bit processor this can become a bottleneck with the Verilog Compiler or interpreter.

#Future Plans(reach ot to me if you would like to work on some of these)
<ul>
  <li> FPGA sythesis tool built into the GUI to upload designs to an FPGA </li>
  <li> Built in Logic Analysis to analyse waveforms etc... </li>
  <li> Configuration file generator to generate Configuration files </li>
  <li> PEP9 like symbol table viewer </li>
  <li> Keyword highlighting configuration to be used by the assembler window </li>
</ul>
  

