import java.util.ArrayList;

public class SemanticAnalyzer {
    
    ArrayList<String> AST = new ArrayList<>(); //Assuming I'm going to need this like last time
    ArrayList<Integer> astDepth = new ArrayList<>(); //this too

    //edit these for our incoming AST
    ArrayList<Token> parseList; //List for storing our parse variables, we want to use these to print our CST later
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
x`
        System.out.println("STARTING SEMANTIC ANALYSIS ON PROGRAM " + progCounter + "."); //change
        System.out.println();
        System.out.println("Beginning Parser...");
        currentToken = parseList.get(parseCounter).tokenType; //Set current token to the first element in our borrowed list
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
        //Add to AST here
        //Add to AST depth too, that shit's important
        //Increment depth, scope
        System.out.println("parseBlock()");
        //addAST("Block", depth);
        parseStatementList();
        }
    }

    public void parseStatementList() {
        if(currentToken == "PRINTSTATEMENT" || currentToken == "ID" || currentToken == "WHILESTATEMENT" || currentToken == "IFSTATEMENT" || currentToken == "OPEN_BLOCK" || currentToken == "TYPEINT" || currentToken == "TYPESTRING" || currentToken == "TYPEBOOL") {

            parseStatement();
            parseStatementList();
        }
    }
    
    public void parseStatement() {
        switch(currentToken) {
            case("OPEN_BLOCK"):
                parseBlock();
            break;
            case("PRINTSTATEMENT"):
                parsePrint();
            break;
            case("IFSTATEMENT"):
                parseIf();
            break;
            case("WHILESTATEMENT"):
                parseWhile();
            break; 
            case("ID"):
                parseAssign();
            break;  
            case("TYPEINT"):
                parseVarDecl();
            break;
            case("TYPESTRING"):
                parseVarDecl();
            break;
            case("TYPEBOOL"):
                parseVarDecl();
            break;
            default:
                error("STATEMENT", currentToken); //change this
            break;
        }
    }

    //Print statement - check for a Print (obviously), then look for both sets of parentheses and an expression
    public void parsePrint() {
        addAST("Print Statement", depth);  
        System.out.println("parsePrintStatement()");
        parseExpression();
        //expand
        
    }

    //Assignment statement - check for an existing ID to declare, then search for a BoolOp and then finally pass it to Expression() to find what we want to declare it to
    public void parseAssign() {
        System.out.println("PARSER: parseAssignStatement()");
        addAST("Assignment", depth);
        parseID();
        //expand
        parseExpression();
        }
    }

    //modify this
    public void parseInt() {
        parseDigit();
        if(currentToken == "INTOP") {
            parseAdd();
            parseExpression();
        }
    }

    public void parseString() {
        parseCharList();
        //expand
    }

    public void parseBoolean() {
        if(currentToken == "OPEN_PAREN") {

            parseExpression();
            parseBoolOp();


            if(parseList.size() - 1 > parseCounter + 1) {
                nextToken = parseList.get(parseCounter + 1).tokenType;
            }


            if(nextToken == "BOOLVAL") {
                parseBoolVal();
            }

            else {
                parseExpression();
            }
        }

        else {
            error("OPEN_PAREN", currentToken);
        }
        }
    }

    public void parseVarDecl() {
        System.out.println("PARSER: parseVarDeclaration()");
        addAST("Variable Declaration", depth);
        //Double check ourselves to make sure we've got a Type on our hands
        parseTypeCheck();
        //...then add the ID we want to declare
        parseID();
    }

    public void parseIf() {
        System.out.println("parseWhileStatement()");
        addAST("If Statement", depth);
        //expand
        parseBoolean();
        parseBlock();
    }

    public void parseWhile() {
        System.out.println("parseWhileStatement()");
        addAST("While Statement", depth);
        parseBoolean();
        parseBlock();
    }

    public void parseChar() {
        //add a Character (ID part of string)
        //Need to add string functionality to lexer first
        addAST("Character", depth);
        handleParseToken("CHAR", "parseChar()");
    }

    public void parseSpace() {
        //Parse out space tokens, which can only be found in strings initialized in our lexer
        addAST("Whitespace", depth);
        handleParseToken("CHARSPACE", "parseSpace()");
    }

    public void parseCharList() {
        if(currentToken == "CHAR") {
            parseChar();
            parseCharList();
        }
        else if(currentToken == "CHARSPACE") {
            parseSpace();
            parseCharList();
        }
        else {
            //add to AST and ASTdepth
        }
        }
    }

    public void parseTypeCheck() {
        if((currentToken == "TYPEINT") || (currentToken == "TYPESTRING") || (currentToken == "TYPEBOOL")) {
            switch(currentToken) {
                case("TYPEINT"):
                    //match int
                break;
                case("TYPESTRING"):
                    //match string
                break;
                case("TYPEBOOL"):
                    //match boolean
                break;
            }
        }
        //Don't need an else statement here since handleParseToken takes care of errors for us
        //nvm it's nice to have backup options
        else {
            error("TYPE", currentToken);
        }
    }

    public void parseExpression() { 
        switch(currentToken) {
            //If it's a digit, parse it as an integer expression
            case("NUM"):
                parseInt();
            break;
            //If it's a string, parse it as a string expression
            case("OPENSTRING"):
                parseString();
            break;
            //We can parse for initialized IDs as well, or use in IntOp/if or while expressions
            case("ID"):
                parseID();
            break;
            //If it's an open parentheses, then it's the start of a boolean expression (since we're parsing it here) - send it over
            case("OPEN_PAREN"):
                parseBoolean();
            break;
            //If it's a boolean value, parse it - This is the ONLY time we should ever be parsing this!
            case("BOOLVAL"):
                parseBoolVal();
            break;
            default:
                error("EXPRESSION", currentToken);
            break;
        }
    }

    public void parseDigit() {
        //match digit
    }

    public void parseID() {
        //match id
    }

    //Adds a BoolOp (==, !=) here - these will be our main comparison tools inside expressions
    public void parseBoolOp() {
        if(errors > 0) {
            //Do nothing
        }
        else {
        addCST("Boolean Operand", depth);
        handleParseToken("BOOLOP", "parseBooleanOperand()");
        }
    }

    public void parseBoolVal() {
        //match boolval
    }

    //Adds an IntOp (addition) symbol
    public void parseAdd() {
        addCST("Integer Operation", depth);
        handleParseToken("INTOP", "parseIntop()");
    }
    
    //change to AST
    public void AST() {
        System.out.println();
        for(int i = 0; i < AST.size(); i++) {
            String printToken = 
            AST.get(i);
            String depthPadded = padDepth(astDepth.get(i));
            System.out.println(depthPadded + "<" + printToken + ">");
        }

        //Clear our lists for future use
        AST.clear();
        astDepth.clear();
    }

    //Method to pad our AST depending on each entry's depth
    public String padDepth(int depthToBePadded) {
        String filler = "";
        //If depth = 7, will add 7 "-" symbols to the filler list
        for (int i = 0; i < depthToBePadded; i++) {
            filler = filler + "-";
        }
        return filler;
    }

    //Print symbol table here??
    
    //Addsymboltable stuff here

    //Method to add stuff to our CST - accepts current token and its place in the program, then adds it to its corresponding list
    public void addAST(String tokenInAST, int tokenDepth) {
        //temporary
        AST.add(tokenInAST);
        astDepth.add(tokenDepth);
    }

    //Could possibly change this to accept currentToken instead of being non-static
    public void handleParseToken(String expected, String output) {
        if(currentToken == expected) {
            //f we've parsed over an EOP token, then set our boolean to true and stop the program
            if(currentToken == "EOP_BLOCK") {
                addAST("[" + parseList.get(parseCounter).name + "]", depth);
                endTheDamnThing = true;
                //DON'T increment parseCounter here - it's the end of the program, there's nothing else left to read!
            }
            //Else it's just a regular token, handle it and add to the CST
            else {
                addAST("[" + parseList.get(parseCounter).name + "]", depth);
                //System.out.println("Correct Token! It's " + expected); //Use this to test
                parseCounter++;
            }
        }
        //If it doesn't match, then we throw an error and increment parseCounter
        else {
            error(expected, currentToken);
        }
        //Set our parser token to the next token once we're done
        currentToken = parseList.get(parseCounter).tokenType;
    }

    //Method to handle errors - takes in the token we wanted and the one we got, and prints out an error message
    public void error(String expectedToken, String errorToken) {
        System.out.println("ERROR: Expected [" + expectedToken + "], got [" + errorToken + "] on line " + parseList.get(parseCounter).lineCount);
        errors++;
        parseCounter++;
    }

    public Token nextOneOver(int parseCounter, Token currentToken) {
        //could use ths as a repacement for nextToken...maybe????
    }
}
