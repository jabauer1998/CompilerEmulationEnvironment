# Emulator Debug Environment
# What is it?
The EDE is software that is inspired by the PEP9 virtual computer. PEP9 is an educational tool that allows students to learn the basics of how a computer works without having to dive into any actual hardware. It is a much more cost effective solution for universities to teach undergrad/introductory level assembly/computer systems courses. The clever GUI also allows the students to easily visualize whats going on inside the CPU. It does have its limits. Since PEP 9 is not an actual computer, the assembly syntax is useless in industry. It also offers a very impractical register file. To account for this the EDE is a system where an instructor(or anyone else) can create an emulator with a Hardware Description Language(Verilog), and a config file. Then the EDE will spit out a PEP9 like gui. This gui can be utilized to like the pep9 to teach a computer systems course, however with a customized processor. The HDL component of the project can be utilized to teach a computer architecture course without having to buy expensive FPGA's. The EDE can also be used in industry as a high level final step pre-silicon verification tool. Fabless semiconductor companies can use it to verify that there architecture works prior to sending the design off to get manufactered and if it doesn't they can use the gui to give them insight about where the error is occuring.

# Software Used in this Project
Javafx - API to create the IDE <br>
Java - Programming language <br>
Junit - Unit testing interface <br>
Verilog - Used to Design Test Processor

# Features Completed
  <ul>
    <li> Created Gate classes to use for interpretation (useful for debugging) </li>
    <li> Created Source class and Destination for Dealing with IO</li>
    <li> Created Lexer, Token, and Position classes to handle Lexing a subset of the Verilog language</li>
    <li> Specified the Grammar of a Subset of Verilog for the Compiler</li>
    <li> Created an ARMTDMI7 processor in Verilog in order to test the graphical user interface </li>
    <li> Created an Interpreter for Verilog to run the ARMTDMI7 processor code. Added functions to the Verilog language to allow for proper communication to the GUI </li>
    <li> Created a Graphical User Interface with JavaFX that works with the Verilog Processor </li>
    <li> Created an assembler for the ARMTDMI7 processor </li>
    <li> Created a new and improved Preprocessor that handles things in its own seperate pass over the tokens. Originally this was merged in with the parsing stage which isnt exactly what a preprocessor does.</li>
  </ul>
  
# Features in Development

<p>Currently I am recreating some of the Visitors to create an updated interpreter. I also plan on making my own Value classes instead of just using Onject for the Environment</p>

# Future Plans(reach out to me if you would like to work on some of these)
<ul>
  <li> FPGA synthesis tool built into the GUI to upload designs to an FPGA </li>
  <li> Built in Logic Analysis to analyze wave-forms etc... </li>
  <li> PEP9 like symbol table viewer </li>
  <li> Keyword highlighting configuration to be used by the assembler window </li>
</ul>

# Creating an Ede Gui

The *io.github.H20man13.emulator_ide.gui* package contains all of the components necessary for building a gui with JavaFx
### The Gui can be basically broken down into two components:
---
1) The Jobs component - The things that can be compiled/executed/ran
2) The Machine component - Contains a gui representation of all the components you would expect to find on a regular computer: Ram, a Register File, Io, and a status bit display

The GuiEde class is the main class that must be called to construct a gui the constructor is listed below:

Note: The items in [] are placeholders representing the literal value that should be placed inside each argument.

GuiEde edeInstance = new GuiEde(NumBytes, NumBytesInRow, AddressFormat, MemFormat, Width, Height);
Where each of the above variables contains the following meaning:
NumBytes -> The total number of bytes to be allocated for ram
NumBytesInRow -> The Memory in the Ede is formed as a giant grid.






