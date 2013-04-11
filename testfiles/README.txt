------------------------------------------------------------------------------
Project Part 1
Sean Gillespie, Kristina Flaherty, David Bernal, and Stephanie Yawn
------------------------------------------------------------------------------


-----------------------------
COMPILATION INSTRUCTIONS
-----------------------------
In order to compile this project, Apache Ant is required. To install on linux, run in bash:
   sudo apt-get install ant

Once ant is installed, typing "ant" in the root directory of this project will compile it into
two executables: "DFAGenerator.jar" and "TableWalker.jar". Both of these are put in the "lib"
subdirectory of the root folder.

-----------------------------
RUNNING INSTRUCTIONS
-----------------------------
In order to run the DFA generator, use the command
   java -jar DFAGenerator.jar input_spec_file dfa_out_file
The DFA generator program will take the input_spec_file and parse it into a DFA, which will then be written
to dfa_out_file. The DFA written to dfa_out_file is NOT human readable.

In order to run the Table Walker, use the command
   java -jar TableWalker.jar dfa_input_file parse_input_file

The TableWalker program will take the dfa_input that was the output of the DFA generator and use it to tokenize
parse_input. 

-----------------------------
TEST CASES
-----------------------------
Five test cases for this project are included in the tests/ subdirectory. Every test case has the same filenames, but
SampleSpec is slightly different in every test. The output of every test case is saved to SampleInput_output.txt, even in the
case of failures. Every test case is run in the following way:
     java -jar DFAGenerator.jar SampleSpec output.txt
If this doesn't fail (which it will for 3 of the test cases), then the next command is run
     java -jar TableWalker.jar output.txt SampleInput
In which case the output is written to standard out and SampleInput_output.txt.
