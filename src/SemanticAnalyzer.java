import java.util.ArrayList;

public class SemanticAnalyzer {
    
    ArrayList<String> AST = new ArrayList<>(); //Assuming I'm going to need this like last time
    ArrayList<Integer> astDepth = new ArrayList<>(); //this too

    //edit these for our incoming AST
    ArrayList<Token> semanticList; //List for storing our parse variables, we want to use these to print our AST as well
    int parseCounter = 0; //for counting each token in the list, one by one
    int errors = 0; //Error count, tracks how many errors we happen to run into in our Parsing process. Program will refuse to print CST if there's any errors
    int depth = 0; //Need to reformat this
    String currentToken; //idk what to do with this anymore
    String nextToken; //The next token in line, used for finding proper tokens in sequence in stuff like IntOp
    boolean endTheDamnThing = false; //If this is true, end our program and print the CST if we have no errors. Only found if we parse through an EOP token

    int progCounter = 0;

    //Currently just messing around with the code to accept proper AST syntax - I'll do the stupid symbol table later. Code is also COMPLETELY NON-FUNCTIONAL, don't bother trying to run it yet.

    //Also basically copy-pasted my Parser over because I ASSUME the AST is going to follow at least the same parsing logic as our parser

    public void main(ArrayList<Token> list) {
        semanticList = list; //set parseList equal to the list in our Lexer for comparison purposes
        parseCounter = 0; //Reset parseCounter between lexer cals
        depth = 0; //Reset depth between lexer calls
        currentToken = semanticList.get(parseCounter).tokenType; //Set current token to the first element in our borrowed list

        System.out.println("STARTING SEMANTIC ANALYSIS ON PROGRAM " + semanticList.get(parseCounter).progNum + "."); //change
        System.out.println();
        System.out.println("Beginning Analyzer...");
        currentToken = semanticList.get(parseCounter).tokenType; //Set current token to the first element in our borrowed list
        analyze(); //Call AST print and Symbol Table methods from this

        AST.clear();
        astDepth.clear();
        
        }

    //Main parse function - I'm only keeping this here for [insert witty joke]
    public void analyze() { //use this naming scheme
        parseProgram();
    }

    //I think this just analyzes the program thingy now and nothing else
    public void parseProgram() {
        parseBlock();
    }

    public void parseBlock() {
        //Increment depth, scope comes later for symbol table
        System.out.println("parseBlock()");
        //addAST("Block", depth);
        parseStatementList();
    }

    public void parseStatementList() {
        System.out.println("god I hope this shit works");
        parseStatement();
    }

    public void parseStatement() {
        System.out.println("has it stopped working yet???");
        //parseBlock();
        parseIf();
        parseWhile();
        parsePrint();
        parseAssign();
        parseVarDecl();
        parseInt();
        parseDigit();
        parseSpace();
        parseAST();
        addAST();
    }

    public void parsePrint() {
        System.out.println("has it stopped working yet??? no");
    }

    public void parseAssign() {
        System.out.println("has it stopped working yet??? nope");
    }

    public void parseInt() {
        System.out.println("has it stopped working yet??? not yet");
    }

    public void parseIf() {
        System.out.println("has it stopped working yet??? uh uh");
    }

    public void parseWhile() {
        System.out.println("has it stopped working yet??? nothing here, no");
    }

    public void parseVarDecl() {
        System.out.println("VarDecl");
    }

    public void parseBoolean() {
        System.out.println("Boolean");
    }

    public void parseString() {
        System.out.println("String");
    }

    public void parseChar() {
        System.out.println("Char");
    }

    public void parseCharList() {
        System.out.println("CharList");
    }

    public void parseSpace() {
        System.out.println("Space");
    }

    public void parseExpression() {
        System.out.println("Expression");
    }

    public void parseID() {
        System.out.println("ID");
    }

    public void parseDigit() {
        System.out.println("Digit");
    }

    public void parseTypeCheck() {
        System.out.println("TypeCheck");
    }

    public void parseBoolOp() {
        System.out.println("BoolOp");
    }

    public void parseBoolVal() {
        System.out.println("BoolVal");
    }

    public void parseAdd() {
        System.out.println("Intop");
    }

    public void parseAST() {
        System.out.println("AST goes here");
    }

    public void addAST() {
        System.out.println("AST addition goes here");
    }

    public void error() {
        System.out.println("errors go here");
    }
}
