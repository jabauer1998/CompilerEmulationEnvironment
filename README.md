# Emulator Debug Enviroment
# What is it?
The EDE is software that is inspired by the PEP9 virtual computer. PEP9 is an educational tool that allows students to learn the basics of how a computer works without having to dive into any actual hardware. It is a much more cost effective solution for universities to teach undergrad/introductory level assembly/computer systems courses. The clever GUI also allows the students to easily visualize whats going on inside the CPU. It does have its limits. Since PEP 9 is not an actual computer the assembly syntax is useless with in industry. It also offeres a very impractical register file. Also architectures are constantly adding more instrctions. To account for this the EDE is a system where an instructor(or anyone else) can create an emulator with an hdl with some constraints, and a config file for the GUI then the tool will combine the inputs and produce a PEP9 like GUI Image for your Architecture. The instructor can then distribute this executable emulator gui to their class.

# Software Used in this Poject
Javafx - API to create the IDE <br>
Java - Programming langage <br>
Junit - Unit testing interface <br>
ASM - Library to Create Java Bytecode <br>
SimpleJSON - Library for Configerations <br>
bash/tmux - Work environment setup <br>
maven - buildtool <br>

# Features Completed
<h2> Verilog Compiler <h2>
  <ul>
    <li> Created Gate classes to use for interpretation (usefull for debugging) </li>
    <li> Created Source class and Destination for Dealing with IO</li>
    <li> Created Lexer, Token, and Position classes to handle Lexing a subset of the verilog language</li>
  </ul>
