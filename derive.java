import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
* Derive Hint Program
* This program reads and parses a grammar file. The name of the grammar file is
* given as a command line argument.
*
* The program is compiled using:
*
*   > javac Derive.java
*
* which create a Derive.class file that can be executed bu the JVM.
*
*   > java Derive <grammar filename>
*
* For example:
*   > javac Derive.java
*   > java Derive example_3.1.txt
*   CSCI 3415 Fall 2019 Program 1 Hint
*   Reading grammar from example_3.1.txt
*   <program> -> begin <stmt_list> end
*   <stmt_list> -> <stmt>
*   <stmt_list> -> <stmt> ; <stmt_list>
*   <stmt> -> <var> = <expression>
*   <var> -> A
*   <var> -> B
*   <var> -> C
*   <expression> -> <var> + <var>
*   expression> -> <var> - <var>
*   <expression> -> <var>
*   >
* 
*/
class Derive {
	
	Grammar grammar = new Grammar();
	
	/**
	* Runs the Derive program. This hint program just reads the grammar
	* file and prints the grammar. You will have to add the code to accept
	* sentences and print their leftmost derivations. The Rule and Grammar
	* classes in this hint are suitable for your program and may be used as
	* desired.
	*/
	void run(String filename) 	{
		grammar.readGrammar(filename);
		grammar.printGrammar();
		// A call to your derivation code will go here.
		String statement = GatherSentenceFromUser();
		ExecuteLeftMostDerivation(grammar, statement);
	}
	
	/**
	* This is the main procedure for the Derive program. The main procedure
	* must be a public static void procedure. Since it is static, it can only
	* access static objects, which isn't particularly useful for real programs.
	* So, it prints a welcome message, gets the filename from the command line,
	* and creates an instance of the Derive class to run the program.
	*/
	public static void main(String[] args) {
		
		System.out.println("CSCI 3415 Fall 2019 Program 1 Hint");
		
		String filename = args[0];
		Derive derive = new Derive();
		derive.run(filename);
	}
	
	/**
	* Returns true if str contains a nonterminal symbol.
	*/
	boolean isNonterminal(String str) {
		return str.matches("^<.*>$");
	}
	
	/**
	* Rule Class
	* A rule has a left hand side (lhs), which is a Sting, and a right hand
	* side, which is an ArrayList<String>. The printRule method prints a rule
	* in a human readable format.
	*/
	class Rule {
		
		String lhs;
		ArrayList<String> rhs;
		
		Rule(String lhs, ArrayList<String> rhs) {
			this.lhs = lhs;
			this.rhs = rhs;
		}
		
		void printRule() {
			System.out.println(lhs + " -> " + String.join(" ", rhs));
		}
	}
	
	/**
	* Grammar Class
	* A grammar is a list of rules. The method readGrammar reads a grammar from
	* a specified file. The method printGrammar prints the grammar in a human
	* readable form.
	*/
	class Grammar {
		
		ArrayList<Rule> rules = new ArrayList<Rule>();
		
		String currentLHS = null;
		
		ArrayList<Rule> parseRHS(String[] lexemes) {
			ArrayList<Rule> rules = new ArrayList<Rule>();
			ArrayList<String> rhs = new ArrayList<String>();
			for (String lexeme : lexemes) {
				if (lexeme.equals("|")) {
					rules.add(new Rule(currentLHS, rhs));
					rhs = new ArrayList<String>(); // Don't use clear
				} else {
					rhs.add(lexeme);
				}
			}
			rules.add(new Rule(currentLHS, rhs));
			return rules;
		}
		
		void readGrammar(String filename) {
			System.out.println("Reading grammar from " + filename);
			BufferedReader reader;
			try {
				reader = new BufferedReader(new FileReader(filename));
				String line = reader.readLine();
				while (line != null) {
					String[] lexemes = line.trim().split("\\s+", 0);
					if (lexemes.length == 0) {
						;
					} else if (lexemes.length == 1) {
						throw new RuntimeException("Illegal rule " + line);
					} else if (isNonterminal(lexemes[0]) && lexemes[1].equals("->")) {
						currentLHS = lexemes[0];
						rules.addAll(parseRHS(Arrays.copyOfRange(lexemes, 2, lexemes.length)));
					} else if (lexemes[0].equals("|")) {
						rules.addAll(parseRHS(Arrays.copyOfRange(lexemes, 1, lexemes.length)));
					} else {
						throw new RuntimeException("Illegal rule " + line);
					}
					line = reader.readLine();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		void printGrammar() {
			grammar.rules.forEach(rule->rule.printRule());
		}
	}
	
	//------
	class ParseTreeNode
	{
		String node;
		ArrayList<ParseTreeNode> children;
	}
	
	boolean TestAgainstRule(String rhsRuleStr, String sentenceStr)
	{
		if(rhsRuleStr.contains(sentenceStr))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	String GatherSentenceFromUser()
	{
		System.out.println("\nPlease input the sentence you wish to parse the leftmost derivation of:");
		String sentence = System.console().readLine();
		return sentence;
	}
	
	
	void ExecuteLeftMostDerivation(Grammar grammar, String sentence)
	{
		System.out.println("Sentence:\n" + sentence + "\nDerivation:");
		ArrayList<String> sentenceList = new ArrayList<String>();
		ParseTreeNode parseTree = new ParseTreeNode();
		int step = 1;
		String derivation = new String();
		
        for (String a: sentence.split(" "))
		{
			sentenceList.add(a);
            //System.out.println(a);
		}
		
		for(String str : sentenceList)
		{
			for(Rule rule : grammar.rules)
			{
				for(String rhsRuleStr : rule.rhs)
				{
					if(TestAgainstRule(rhsRuleStr, str))
					{
						System.out.println(step+": "+ rhsRuleStr + str);
						rule.printRule();
						++step;
					}
				}
				
			}
			
		}
		
	}
	//-----
}
