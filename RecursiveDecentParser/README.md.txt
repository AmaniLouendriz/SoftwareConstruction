This was done as part of the introduction to compilers chapter in the software construction course. 

Recursive Decent parser is a parser that takes input as a text file and outputs whether the input given follows the context free grammar this parser is defining.

The grammar that the parser is defining is the following:

<program> 		::= {<statement_list>}
<statement_list> 	::= <statement>;<statement_list>|<statement>;
<statement> 		::= call: <procedure_call>|compute: <expression>
<procedure_call>	::= id(<parameters>)
<parameters>		::= <factor>,<parameters>|<factor>
<expression> 		::= id=<factor>+<factor>|id=<factor>-<factor>|id=<factor>
<factor> 		::= id|num


Note that this grammar was updated by making left factoring where applicable. After the left factoring, the grammar is the following.
No left recursion was detected.

<program>     		::= {<statement_list>}
<statement_list>	::=<statement>;<statement_list’>
<statement_list’>	::=statement_list | ε
<statement>		::=call:procedure_call | compute:expression
<procedure_call>	::=id(<parameters>)
<parameters>		::=<factor><parameters’>
<parameters’>		::=,<parameters> | ε
<expression>		::=id=<factor><factor’>
<factor’>		::=+<factor>|-<factor>| ε
<factor>		::= id|num

The grammar now is an LL(1) grammar, meaning it uses the left most derivations, making the parser predictive and a bit more efficient than the Top down parsers that randomly chose a production and have to backtrack each and every time there is a problem.


To run, just compile the java file usig "javac RecursiveDescentParser.java" and run "java RecursiveDescentParser inputx.txt". You can either provide your own input.txt file or use the ones present under the tests directory.


After some tedious calculations of the First and Follow sets,the parsing table is provided below:



How to interpret the parsing table?

suppose we want to parse: { compute: a = 15;}

we will use a stack! let's start by pushing the end of string $


$

Push the first non-terminal into the stack, i.e: program


program
$

Our input is { compute: a = 15;}

for a terminal of {, we can see the following production: program=>{statement_list} pop program and push the production:

{
statement_list
}
$

the top of the stack is a terminal: {, we check if the terminal corresponds to the head of the input. Yes! then pop { from the stack: our remaining input to parse is: compute: a = 15;}

the stack now looks like:


statement_list
}
$

the head of the input is the terminal compute, going back to the parsing table, with a non-terminal of statement_list, we can get the production: statement_list=>statement;statement_list'

pop statement_list from the stack and insert the following:

statement
;
statement_list'
}
$

now we have the production statement, remember, the terminal that our input has is still compute, the parsing table says for a terminal compute, and a non terminal statement, the production is: statement=>compute:expression. Now we pop statement from the stack and we push this production:

compute
:
expression
;
statement_list'
}
$


We have a terminal in the top of the stack! Let's check if it's corresponds to the one in the beginning of our input. As a reminder, the remaining input to parse is: compute: a = 15;}

it does! pop compute from the stack, the remaining input to parse is: :a = 15;}

:
expression
;
statement_list'
}
$


There is another  terminal in the stack! the : . Does the start of the input to parse have the same terminal. Yep, now let's pop : from the stack

expression
;
statement_list'
}
$

The remaining input to parse is: a=15;}

a is an id, the parsing table says for a non terminal for expression, and a terminal of id, the production is: expression=>id=factor factor'

pop expression and push this production:

id
=
factor
factor'
;
statement_list'
}
$

we have a terminal at the top of the stack, id, and the start of our input to parse is a, so it's an id, meaning there is a match!

pop from the stack:

=
factor
factor'
;
statement_list'
}
$
 

Our remaining input to parse is =15;}

and we have = at the top of the stack, pop it!

factor
factor'
;
statement_list'
}
$

Our remaining input to parse is:

15;}

we have factor at the top of the stack, factor gives a production of factor=>num for a num. then pop factor and push this production:

factor'
;
statement_list'
}
$ 

Our remaining input to parse is:

;}

for a terminal of ; and a non terminal of factor', the production is: factor'=>ε. ε means an empty string, so we won't push anything. but we will pop factor'.

the remaining input to parse is:   ;}

The stack is: 

;
statement_list'
}
$

the terminal at the top of the stack and at the beginning of the input to parse match, so we pop from the stack:

statement_list'
}
$

the remaining input to parse is:    }

for }, statement_list' gives a production of ε again, we won't push anything but we will pop statement_list'.


the stack is now:

}
$

the top of the stack and the start of the input to parse is the same. so we pop }.

there is no more input to parse. and the stack is now just $. $ means the end of the string. That way we have concluded parsing our input.


Please note that it is quite a process to build this parse table first, and then use it to parse a small input like the one we had earlier. This program saves you time in the sense that you just have to give it an input, and it will tell you whether it conforms to the grammar. It will even tell you where there is a problem, if it encounters any. Power of computing!
