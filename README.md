# Emulator Debug Enviroment
# What is it?
The EDE is software that is inspired by the PEP9 virtual computer. PEP9 is an educational tool that allows students to learn the basics of how a computer works without having to dive into any actual hardware. It is a much more cost effective solution for universities to teach undergrad/introductory level assembly/computer systems courses. The clever GUI also allows the students to easily visualize whats going on inside the CPU. It does have its limits. Since PEP 9 is not an actual computer, the assembly syntax is useless in industry. It also offeres a very impractical register file. Also architectures are often adding more instrctions. To account for this the EDE is a system where an instructor(or anyone else) can create an emulator with a Hardware Description Language(Verilog), and a config file. Then the EDE will spit out a PEP9 like gui. This gui can be utilized to like the pep9 to teach a computer systems course, however with a customized processor. The HDL component of the project can be utilized to teach a computer architecture course, however without having to buy expensive FPGA's. The EDE can also be used in industry as a high level verification tool. Fabless semiconductor companies can use it to verify that there architecture works prior to sending the design off to get manufactured.

# Software Used in this Poject
Javafx - API to create the IDE <br>
Java - Programming langage <br>
Junit - Unit testing interface <br>
ASM - Library to Create Java Bytecode <br>

# Features Completed
<h2> Verilog Compiler <h2>
  <ul>
    <li> Created Gate classes to use for interpretation (usefull for debugging) </li>
    <li> Created Source class and Destination for Dealing with IO</li>
    <li> Created Lexer, Token, and Position classes to handle Lexing a subset of the verilog language</li>
    <li> Specified the Grammer of a Subset of Verilog for the Compiler</li>
  </ul>
