Name : Raga Shalini Koka      PSID : 1641010

The src contains the necessary java files to run the program
The BooleanRetrieval.java has the main code to generate the output of the results
This program takes 3 command line arguments and generates output file with the name mentioned in the same folder in which project is present

Evaluating posting list
	java BooleanRetrieval PLIST "cpu" cpu_list.txt

Evaluating AND
	java BooleanRetrieval AND "mouse AND scrolling" and_result.txt

Evaluating OR
	java BooleanRetrieval OR "youtube OR reported" or_result.txt

Evaluating AND-NOT
	java BooleanRetrieval AND-NOT "Lenovo AND (NOT logitech)" and_not_result.txt