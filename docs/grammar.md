# EDE Verilog Subset Grammar

<p>source_text -> module</p>
<p>module -> name_of_module list_of_ports module_item endmodule</p>

<p>list_of_ports -> port list_of_ports_expand</p>
<p>list_of_ports_expand -> null | , port list_of_ports_expand</p>

<p>module_item -> module_item_expanded</p>
<p>module_item_expanded -> null | , mod_item module_item_expanded</p>

<p>name_of_module -> IDENT </p>

<p>port -> port_expression | .name_of_port ( port_expression )</p>
<p>port_expression -> port_reference | { port_reference port_reference_expanded } </p>
<p>port_reference_expanded -> , port_reference port_reference_expanded </p>
<p>port_reference -> name_of_variable | name_of_variable [ constant_expression ] | name_of_variable [ constant_expression : constant_expression]</p>
<p> name_of_port -> IDENT </p>

<p>mod_item -> input_declaration | output_declaration | net_declaration | reg_declaration | integer_declaration | real_declaration | gate_declaration | module_instantiation | continuous_assign | initial_statement | always_statement | task | function </p>

# Declarations

<p>input_declaration -> INPUT range list_of_variables ;</p>
<p>output_declaration -> OUTPUT range list_of_variables ;</p>
<p>net_declaration -> WIRE range delay list_of_variables ;</p>
<p>reg_declaration -> REG range list_of_register_variables ;</p>
<p>real_declaration -> REAL list_of_variables ;</p>
<p>integer_declaration -> INTEGER list_of_variables ;</p>
<p>continious_assign -> ASSIGN delay list_of_assignments ;</p>

<p>list_of_variables -> name_of_variable name_of_variable_expaned | WIRE range delay list_of_assignments ;</p>
<p>name_of_variable_expanded -> null | , name_of_variable name_of_variable_expanded
<p>name_of_variable -> IDENT</p>

<p>list_of_register_variables -> register_variable register_variable_expanded</p>
<p>register_variable_expanded -> , register_variable register_variable_expanded</p>
<p>register_variable -> name_of_register | name_of_memory [ constant_expression : constant_expression ] <p>
<p>name_of_register -> IDENT</p>
<p>name_of_memory -> IDENT</p>

<p>range -> [ constant_expression : constant_expression ]</p>

<p>list_of_assignments -> assignment assignment_expanded</p>
<p>assignment_expanded -> null | , assignment assignment_expanded</p>

# Primative Instances
<p>gate_declaration -> GATETYPE delay gate_instance gate_instance_expanded ;</p>
<p>gate_instance_expanded -> null | , gate_instance gate_instance_expanded</p>
<p>gate_instance -> name_of_gate_instance ( terminal terminal_expanded )</p>
<p>name_of_gate_instance -> IDENT range</p>
<p>terminal_expanded -> null | , terminal terminal_expanded</p>
<p>terminal -> expression | IDENT</p>

# Module Instantiations
<p>module_instantiation -> name_of_module  module_instance module_instance_expanded ;</p>
<p>module_instance_expanded -> null | , module_instance module_instance_expanded</p>
<p>name_of_module -> IDENT</p>
<p>module_instance -> name_of_instance ( list_of_module_connections )</p>
<p>list_of_module_connections -> module_port_connection module_port_connections_expanded | named_port_connection named_port_connection_expanded</p>
<p>module_port_connections_expanded -> , module_port_connection module_port_connections_expanded</p>
<p>named_port_connection -> expression | NULL</p>
<p>NULL -> /* nothing ex (a, b, , d) */</p>
<p>named_port_connection -> . IDENT ( expression )</p>

# Behavorial Statements
<p>initial_statement -> initial statement</p>
<p>always_statement -> always statement</p>
<p>statement_or_null -> statement | ; </p>

<p>statement -> blocking_assignment ; | non_blocking_assignment ; | if ( expression ) statement_or_null | if ( expression ) statement_or_null else statement_or_null | case ( expression ) case_item case_item_expanded endcase | casez ( expression ) case_item case_item_expanded endcase | casex ( expression ) case_item case_item_expanded endcase | forever statement | repeat ( expression ) statement | while ( expression ) statement | for ( assignment ; expression ; assignment ) statement | wait ( expression ) statement_or_null | seq_block | task_enable | system_task_enable | ASSIGN assignment </p>

<p>assignment -> lvalue = expression</p>
<p>blocking_assignment -> lvalue = expression</p>
<p>nonblocking_assignment -> lvalue <= expression</p>
<p>case_item -> expression expression_expanded : statement_or_null | default : statement_or_null | default statement_or_null</p>

<p>seq_block -> begin statement_list end  | begin : name_of_block block_declaration_list statement_list end</p>
<p>statement_list -> null | statement statement_list</p>
<p>name_of_block -> IDENT</p>
<p>block_declaration_list -> block_declaration block_declaration_list | null</p>
<p>block_declaration -> reg_declaration | integer_declaration | real_declaration </p>

<p>task_enable -> name_of_task | name_of_task ( expression expression_expanded ) ;</p>
<p>expression_expanded -> , expression expression_expanded | null </p>
<p>system_task_enable -> name_of_system_task ; | name_of_system_task ( expression expression_expanded ) ;</p>
<p>name_of_system_task -> $ system_identifier </p>
<p>system_identifier -> IDENT</p>

# Expressions
<p>lvalue -> IDENT | IDENT [ expression ] | IDENT [ expression : expression ] | concatenation</p>
<p>constant_expression -> expression</p>
<p>expression -> primary | UNARY_OPERATOR primary | expression binary_operator expression | expression ? expression : expression | STRING</p>
<p>UNARY_OPERATOR -> PLUS | MINUS | NOT | BAND | BNAND | BOR | BXOR | BNXOR </p>
<p>BINARY_OPERATOR -> PLUS | MINUS | TIMES | DIV | MOD | EQ2 | EQ3 | NE2 | NE3 | LAND | LOR | LT | LE | GT | GE | BAND | BOR | BXOR | BXNOR | LSHIFT | RSHIFT </p>
<p>STRING -> "data in here"</p>
<p>primary -> NUM | IDENT | IDENT [ expression ] | IDENT [ constant_expression : constant_expression ] | concatenation | multiple_concatenation | function_call | ( expression )</p>

<p>concatenation -> { expression expression_expanded }</p>
<p>multiple_concatenation -> { expression { expression expression_expanded } }</p>

<p>function_call -> name_of_function ( expression expression_expanded ) | name_of_system_function ( expression expression_expanded ) | name_of_system_function</p>
<p>name_of_function -> IDENT</p>
<p>name_of_system_function -> $ IDENT</p>

# General
<p> delay -> # NUM | # IDENT</p>


