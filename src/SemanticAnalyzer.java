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

    //General ideas:
    //Keep parse function redirects, but just skip over tokens themselves - we don't want to parse them a second time

    public void main(ArrayList<Token> list) {
        semanticList = list; //set parseList equal to the list in our Lexer for comparison purposes
        parseCounter = 0; //Reset parseCounter between lexer calls
        depth = 0; //Reset depth between lexer calls

        System.out.println();
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
        //System.out.println(semanticList);
        parseProgram();
    }

    //I think this just analyzes the program thingy now and nothing else
    public void parseProgram() {
        parseBlock();
    }

    public void parseBlock() {
        //DON'T check for match - parser already did that

        //Increment depth, scope comes later for symbol table
        System.out.println("parseBlock()");
        //addAST("Block", depth);
        refresh(currentToken, parseCounter);
        parseStatementList();
        //Add end block to AST after statement list?
    }

    public void parseStatementList() {
        System.out.println("Statement List");

        currentToken = semanticList.get(parseCounter).tokenType;
        System.out.println(currentToken);

        if(currentToken == "PRINTSTATEMENT" || currentToken == "ID" || currentToken == "WHILESTATEMENT" || currentToken == "IFSTATEMENT" || currentToken == "OPEN_BLOCK" || currentToken == "TYPEINT" || currentToken == "TYPESTRING" || currentToken == "TYPEBOOL") {

            parseStatement();
            //parseStatementList(); - don't loop for now
        }
    }

    public void parseStatement() {
        System.out.println("Statement");
        //Work in progress

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
        handleAnalyzedToken();
        padDepth();
        parseExpression();
        error();
    }

    public void parsePrint() {
        //addAST("Print", depth);
        System.out.println("Print");
        //Skip these tokens
        //parseExpression();
        //Skip these tokens too
    }

    public void parseAssign() {
        //addAST("Assign", depth)
        System.out.println("Assign");
        //parseID();
    }

    public void parseIf() {
        //addAST("If", depth);
        System.out.println("If");
        //parseBoolean();
        //parseBlock();
    }

    public void parseWhile() {
        //addAST("While", depth)
        System.out.println("While");
        //parseBoolean();
        //parseBlock();
    }

    public void parseVarDecl() {
        //addAST("Variable Dec", depth);
        System.out.println("VarDecl");
        //parseTypeCheck();
        //parseID();
    }

    public void parseBoolean() {
        System.out.println("Boolean");
        //addAST if open paren detected
        //parseExpression
        //parseBoolOp
        //parseBoolVal
        //check for expression or boolval
        if(currentToken == "OPEN_PAREN") {
            handleParseToken("OPEN_PAREN", "parseOpenParen()");
            parseExpression();
            parseBoolOp();

            //Set nextToken to currentToken + 1
            if(semanticList.size() - 1 > parseCounter + 1) {
                nextToken = parseList.get(parseCounter + 1).tokenType;
            }

            if(nextToken == "BOOLVAL") {
                parseBoolVal();
            }
            else {
                parseExpression();
            }
            //increment parsecounter and skip this next close paren token - use refresh
        }

        else {
            error("OPEN_PAREN", currentToken);
        }
    }

    public void parseInt() {
        System.out.println("Int");
        //parseDigit - no need to check for num, it's already foretold
        
        if(parseList.size() - 1 > parseCounter + 1) {
            nextToken = parseList.get(parseCounter + 1).tokenType;
        }

        //Still parse digit
        parseDigit();

        if(nextToken == "INTOP") {
            //parseAdd();
            //parseExpression();
        }
    }

    public void parseString() {
        System.out.println("String");
        //parseCharList();
        //skip over open/close quotes, we don't need them
    }

    public void parseChar() {
        System.out.println("Char");
        //skip over???
        //make sure to have functionality for charlist though
    }

    public void parseCharList() {
        //Add something here to keep track of strings
        //addAST("")
        System.out.println("CharList");
        if(currentToken == "CHAR") {
            parseChar();
            parseCharList();
        }
        //Finally doing stuff with our space tokens
        else if(currentToken == "CHARSPACE") {
            parseSpace();
            parseCharList();
        }
        else {
            //Do nothing...We DON'T want to throw an error here, since that will gum things up
        }
    }

    public void parseSpace() {
        System.out.println("Space");
        //Add this??? don't really think we need to... just skip over it
    }

    public void parseExpression() {
        System.out.println("Expression");
        //expand
    }

    public void parseID() {
        System.out.println("ID");
        //add to AST?
    }

    public void parseDigit() {
        System.out.println("Digit");
        //addAST("NUM", depth);
    }

    public void parseTypeCheck() {
        System.out.println("TypeCheck");
    }

    public void parseBoolOp() {
        //addAST("BoolOp", depth)
        System.out.println("BoolOp");
    }

    public void parseBoolVal() {
        System.out.println("BoolVal");
    }

    public void parseAdd() {
        System.out.println("Intop");
        //addAST("IntOp", depth);
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

    public void padDepth() {
        System.out.println("padding depth...turn this into a string function");
    }

    //I'm not sure if I still need this but I'll keep it around
    public void handleAnalyzedToken() {
        //addAST("[" + semanticList.get(parseCounter).name + "]", depth);
        System.out.println("Analyze da tokenz");
    }

    //replaces the current token with the next token in line, effectively "refreshing" it. Basically the same as what was going on in the old handleParseToken
    //function in the Parser, only that this has a lot less steps
    public void refresh(String currentToken, int parseCounter) {
        parseCounter++;
        currentToken = semanticList.get(parseCounter).tokenType;
    }

}
