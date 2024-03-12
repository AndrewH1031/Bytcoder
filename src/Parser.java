import java.util.ArrayList;

public class Parser {
    
    ArrayList<String> CST = new ArrayList<>(); //This is a list for now, will likely upgrade into a full tree eventually
    ArrayList<Integer> cstDepth = new ArrayList<>();
    ArrayList<Token> parseList; //List for storing our parse variables, we want to use these to print our CST later
    int parseCounter = 0; //for counting each token in the list, one by one
    int errors = 0; //Error count, tracks how many errors we happen to run into in our Parsing process. Program will refuse to print CST if there's any errors
    int depth = 0;
    int progCount = 0;
    String currentToken; //The current token we want to match up
    String nextToken; //The next token in line, used for finding proper tokens in sequence in stuff like IntOp
    boolean endTheDamnThing = false; //If this is true, end our program and print the CST if we have no errors. Only found if we parse through an EOP token

    //NOTE: code currently works fine, however it MAY get a little wonky when Lexing multiple programs at a time. You can usually fix this by just rerunning the program in a new command prompt window

    public void main(ArrayList<Token> list) {
        parseList = list; //set parseList equal to the list in our Lexer for comparison purposes
        parseCounter = 0;
        depth = 0;
        currentToken = parseList.get(parseCounter).tokenType;
        progCount++;

        System.out.println("PARSER: PARSER called from LEXER");
        System.out.println();
        System.out.println("Beginning Parser...");
        parse(); //Call our parser to begin parsing

    }

    public void parse() {
        System.out.println("PARSER: parseBody()");
        parseProgram();
    }

    public void parseProgram() {
        System.out.println("PARSER: parseProgram()");
        addCST("Program", depth);
        depth++;
        parseBlock();
        //Check for an End-Of-Program symbol. If there is one, end the program gracefully, and let us know how many errors we got while parsing.
        handleParseToken("EOP_BLOCK", "parseEndOfProgram()");
        depth--;
        if(endTheDamnThing == true) {
            if(errors > 0) {
                System.out.println();
                System.out.println("PARSER: Parser failed with " + errors + " errors");
                System.out.println("PARSER: CST skipped due to PARSER error(s)");
            }
            else {
                System.out.println();
                System.out.println("PARSER: Parsing completed successfully");
                System.out.println("PARSER: Printing CST for Program " + progCount + "...");
                CST();

                //Semantic analyzer call here for the future
            }
        }
    }

    public void parseBlock() {
        System.out.println("PARSER: parseBlock()");
        addCST("Block", depth);
        depth++;
        handleParseToken("OPEN_BLOCK", "parseBlock()");
        parseStatementList();
        //Use this to check for the complementary close block token
        handleParseToken("CLOSE_BLOCK", "parseCloseBlock()");
        depth--;
    }

    public void parseStatementList() {
        System.out.println("PARSER: parseStatementList()");
        addCST("Statement List", depth);
        depth++;
        //Update our current token based on the position of the pointer
        currentToken = parseList.get(parseCounter).tokenType;
        //System.out.println(currentToken);

        //If we start with any of the following tokens, then we have a valid sentence - send it to statement list for more complex parsing
        if(currentToken == "PRINTSTATEMENT" || currentToken == "ID" || currentToken == "WHILESTATEMENT" || currentToken == "IFSTATEMENT" || currentToken == "OPEN_BLOCK" || currentToken == "TYPEINT" || currentToken == "TYPESTRING" || currentToken == "TYPEBOOL") {

            parseStatement();
            parseStatementList();
        }
        depth--;
    }

    public void parseStatement() {
        System.out.println("PARSER: parseStatement()");
        addCST("Statement", depth);
        depth++;
        //add if/case statements depending on what kind of token we have
        switch(currentToken) {
            case("OPEN_BLOCK"):
                parseBlock();
                depth--;
            break;
            case("PRINTSTATEMENT"):
                parsePrint();
                depth--;
            break;
            case("IFSTATEMENT"):
                parseIf();
                depth--;
            break;
            case("WHILESTATEMENT"):
                parseWhile();
                depth--;
            break; 
            case("ID"):
            //Assigning an ID or do we just have a lone ID? Don't know, let's pass it to Assign()
                parseAssign();
                depth--;
            break;
            case("TYPEINT"):
            //Types are declared, need to pass it to VarDecl()
                parseVarDecl();
                depth--;
            break;
            case("TYPESTRING"):
                parseVarDecl();
                depth--;
            break;
            case("TYPEBOOL"):
                parseVarDecl();
                depth--;
            break;
            //If we've got nothing, then throw an error
            default:
                error("STATEMENT", currentToken);
            break;
        }
    }

    //Print statement - check for a Print (obviously), then look for both sets of parentheses and an expression
    public void parsePrint() {
        addCST("Print Statement", depth);
        depth++;
        handleParseToken("PRINTSTATEMENT", "parsePrintStatement()");
        if(currentToken == "OPEN_PAREN") {
            handleParseToken("OPEN_PAREN", "parseOpenExpression()");
            parseExpression();
            handleParseToken("CLOSE_PAREN", "parseCloseExpression()");
            depth--;
        }
        //Like usual, throw an error if we don't have the token we want
        else {
            error("OPEN_PAREN", currentToken);
        }
        
    }

    //Assignment statement - check for an existing ID to declare, then search for a BoolOp and then finally pass it to Expression() to find what we want to declare it to
    public void parseAssign() {
        System.out.println("PARSER: parseAssignStatement()");
        addCST("Assignment", depth);
        depth++;
        parseID();
        handleParseToken("ASSIGNOP", "parseAssignment()");
        parseExpression();
        depth--;
    }

    //Parses an Integer exression, which checks for an open parentheses before either initializing a number or parsing an IntOp expression
    //This is the only way we want to 
    public void parseInt() {

        if(parseList.size() - 1 > parseCounter + 1) {
            nextToken = parseList.get(parseCounter + 1).tokenType;
        }

        System.out.println("PARSER: parseIntExpression()");
        addCST("Integer Expression", depth);
        depth++;
        if(currentToken == "NUM") {
            parseDigit();
            //Use forward pointer to fix this
            if(nextToken == "INTOP") {
                parseAdd();
                parseExpression();
            }
            depth--;
        }
        else {
            error("NUM", currentToken);
        }
    }

    //Parses a string expression, which checks for an open quote token before parsing through the whole list of characters (or spaces) and then ending things off with a close string token
    public void parseString() {
        addCST("String Expression", depth);
        depth++;
        System.out.println("PARSER: parseStringExpression()");
        if(currentToken == "OPENSTRING") {
            handleParseToken("OPENSTRING", currentToken);
            parseCharList();
            handleParseToken("CLOSESTRING", currentToken);
            depth--;
        }
        else {
            error("OPENSTRING", currentToken);
        }
    }

    public void parseBoolean() {
        //Boolean initialization - we need this for our while and if statements to work
        System.out.println("PARSER: parseBoolean()");
        addCST("Boolean", depth);
        depth++;
        //If we have an open paren, then that means our statement is valid - call to handle open paren, then parse through both expressions and the boolop in between
        if(currentToken == "OPEN_PAREN") {
            //while(expr == expr/boolval) {
            handleParseToken("OPEN_PAREN", "parseOpenParen()");
            parseExpression();
            parseBoolOp();

            if(parseList.size() - 1 > parseCounter + 1) {
                nextToken = parseList.get(parseCounter + 1).tokenType;
            }

            //If we've got a BoolVal (through parsing through an existing if/while statement), then initialize it
            if(nextToken == "BOOLVAL") {
                parseBoolVal();
            }
            //Else, it's just a regular ol' expression - shove it in there
            else {
                parseExpression();
            }
            handleParseToken("CLOSE_PAREN", "parseCloseParen()");
            depth--;
        }
        //If we don't find anything relevant (i.e. open paren token), print an error
        else {
            error("OPEN_PAREN", currentToken);
        }
    }

    public void parseVarDecl() {
        System.out.println("PARSER: parseVarDeclaration()");
        addCST("Variable Declaration", depth);
        depth++;
        //Double check ourselves to make sure we've got a Type on our hands
        parseTypeCheck();
        //...then add the ID we want to declare
        parseID();
        depth--;
    }

    public void parseIf() {
        //If statement: initialize if, then pass to Boolean() to determine what to do with the following parentheses, and then Block() to make sure we have an open bracket following too
        addCST("If Statement", depth);
        depth++;
        handleParseToken("IFSTATEMENT", "parseIfStatement()");
        parseBoolean();
        parseBlock();
        depth--;
    }

    public void parseWhile() {
        //While statement: pretty much like if
        addCST("While Statement", depth);
        depth++;
        handleParseToken("WHILESTATEMENT", "parseWhileStatement()");
        parseBoolean();
        parseBlock();
        depth--;
    }

    public void parseChar() {
        //add a Character (ID part of string)
        //Need to add string functionality to lexer first
        addCST("Character", depth);
        depth++;
        handleParseToken("CHAR", "parseChar()");
        depth--;
    }

    public void parseSpace() {
        //Parse out space tokens, which can only be found in strings initialized in our lexer
        addCST("Whitespace", depth);
        depth++;
        handleParseToken("CHARSPACE", "parseSpace()");
        depth--;
    }

    public void parseCharList() {
        //Check to see if we have a valid char for our current string (i.e. if it's considered a char or charspace, NOT an id in the token list)
        //Once we have our character, loop back to charlist to parse for more chars
        System.out.println("parseCharList()");
        addCST("Character List", depth);
        depth++;
        if(currentToken == "CHAR") {
            parseChar();
            parseCharList();
            depth--;
        }
        //Finally doing stuff with our space tokens
        else if(currentToken == "CHARSPACE") {
            parseSpace();
            parseCharList();
            depth--;
        }
        else {
            //Nothing here...
        }
    }

    public void parseTypeCheck() {
        //String, Int, Boolean type checking to make extra sure we've got something to declare
        System.out.println("PARSER: parseTypeChecking()");
        addCST("Type Checking", depth);
        depth++;
        if((currentToken == "TYPEINT") || (currentToken == "TYPESTRING") || (currentToken == "TYPEBOOL")) {
            switch(currentToken) {
                //Declare an Int
                case("TYPEINT"):
                    //Can probably optimize this
                    handleParseToken("TYPEINT", "parseInt()");
                    depth--;
                break;
                //Declare a String
                case("TYPESTRING"):
                    handleParseToken("TYPESTRING", "parseString()");
                    depth--;
                break;
                //Declare a Boolean
                case("TYPEBOOL"):
                    handleParseToken("TYPEBOOL", "parseBoolean()");
                    depth--;
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
        //Expressions go here - this is reserved for stuff inside parentheses usually, which means a whole lot of boolop tokens being parsed
        System.out.println("PARSER: parseExpression()");
        addCST("Expression", depth);
        depth++;
        switch(currentToken) {
            //If it's a digit, parse it as an integer expression
            case("NUM"):
                parseInt();
                depth--;
            break;
            //If it's a string, parse it as a string expression
            case("OPENSTRING"):
                parseString();
                depth--;
            break;
            //We can parse for initialized IDs as well, or use in IntOp/if or while expressions
            case("ID"):
                parseID();
                depth--;
            break;
            //If it's an open parentheses, then it's the start of a boolean expression (since we're parsing it here) - send it over
            case("OPEN_PAREN"):
                parseBoolean();
                depth--;
            break;
            //If it's a boolean value, parse it - This is the ONLY time we should ever be parsing this!
            case("BOOLVAL"):
                parseBoolVal();
                depth--;
            break;
            default:
                error("EXPRESSION", currentToken);
            break;
        }
    }

    //Adds a Digit
    public void parseDigit() {
        addCST("Digit", depth);
        depth++;
        handleParseToken("NUM", "parseDigit()");
        depth--;
    }

    //Adds an ID
    public void parseID() {
        addCST("ID", depth);
        depth++;
        handleParseToken("ID", "parseID()");
        depth--;
    }

    //Adds a BoolOp (==, !=) here - these will be our main comparison tools inside expressions
    public void parseBoolOp() {
        addCST("Boolean Operand", depth);
        depth++;
        handleParseToken("BOOLOP", "parseBooleanOperand()");
        depth--;
    }

    //Adds a Boolean value (true, false)
    public void parseBoolVal() {
        addCST("Boolean Value", depth);
        depth++;
        handleParseToken("BOOLVAL", "parseBooleanVal()");
        depth--;
    }

    //Adds an IntOp (addition) symbol
    public void parseAdd() {
        addCST("Integer Operation", depth);
        depth++;
        handleParseToken("INTOP", "parseIntop()");
        depth--;
    }
    
    //Our Concrete Syntax Tree, which we want to print out at the end of our program. Accepts the info from addCST, and then formats and prints it out
    public void CST() {
        System.out.println();

        for(int i = 0; i < CST.size(); i++) {
            String printToken = CST.get(i);
            String depthPadded = padDepth(cstDepth.get(i));
            System.out.println(depthPadded + printToken);
        }

        CST.clear();
        cstDepth.clear();
    }

    public String padDepth(int depthToBePadded) {
        String filler = "";
        for (int i = 0; i < depthToBePadded; i++) {
            filler = filler + "-";
        }
        return filler;
    }

    //Method to add stuff to our CST - accepts current token and its place in the program, then adds it to its corresponding list
    public void addCST(String tokenInCST, int tokenDepth) {
        //temporary
        CST.add(tokenInCST);
        cstDepth.add(tokenDepth);
    }

    //Could possibly change this to accept currentToken instead of being non-static
    public void handleParseToken(String expected, String output) {
        if(currentToken == expected) {
            //f we've parsed over an EOP token, then set our boolean to true and stop the program
            if(currentToken == "EOP_BLOCK") {
                addCST("[" + currentToken + "]", depth);
                endTheDamnThing = true;
                //DON'T increment parseCounter here - it's the end of the program, there's nothing else left to read!
            }
            //Else it's just a regular token, handle it and add to the CST
            else {
                addCST("[" + currentToken + "]", depth);
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

}
