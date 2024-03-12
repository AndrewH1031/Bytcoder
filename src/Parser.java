//A quick shoutout to StackOverflow for giving me an understanding of the general structure of a recursive descent parser:
//      https://stackoverflow.com/questions/14913804/recursive-descent-parser-in-java
//And for teaching me how to format recursive descent depth properly:
//      https://stackoverflow.com/questions/28507978/recursion-depth-tabs-dents-in-java

import java.util.ArrayList;

public class Parser {
    
    ArrayList<String> CST = new ArrayList<>(); //This is a list for now, will likely upgrade into a full tree eventually
    ArrayList<Integer> cstDepth = new ArrayList<>(); //Depth of each of our CST tokens, dutifully calculated before handling each one
    ArrayList<Token> parseList; //List for storing our parse variables, we want to use these to print our CST later
    int parseCounter = 0; //for counting each token in the list, one by one
    int errors = 0; //Error count, tracks how many errors we happen to run into in our Parsing process. Program will refuse to print CST if there's any errors
    int depth = 0; //Random variable that we increase/decrease to print out in our CST. Usually is incremented before a program starts and decremented after it finishes
    String currentToken; //The current token we want to match up
    String nextToken; //The next token in line, used for finding proper tokens in sequence in stuff like IntOp
    boolean endTheDamnThing = false; //If this is true, end our program and print the CST if we have no errors. Only found if we parse through an EOP token

    //NOTE: code currently works fine, however it MAY get a little wonky when Lexing multiple long programs at a time. You can usually fix this by just rerunning the programs separately, or in a new command prompt window

    //Some rules for this parser:
    //1. Print/if/while statements MUST have at least one pair of parentheses as part of their declaration, so be sure to include a set of () when calling these
    //2. Boolean statements when comparing things naturally need a boolop token (== or !=), don't use a lone = for boolops or else you'll just get an error since that's considered an assignment token
    //3. IDs don't need to be declared by any variable, but it would be very nice of you to :)
    //4. Parser can currently only accept one number at a time (no double digits, like 12) - keep this in mind when assigning or using IntOps

    public void main(ArrayList<Token> list) {
        parseList = list; //set parseList equal to the list in our Lexer for comparison purposes
        parseCounter = 0; //Reset parseCounter between lexer cals
        depth = 0; //Reset depth between lexer calls
        currentToken = parseList.get(parseCounter).tokenType; //Set current token to the first element in our borrowed list

        System.out.println("PARSER: PARSER called from LEXER");
        System.out.println();
        System.out.println("Beginning Parser...");
        parse(); //Call our parser to begin parsing
        if(endTheDamnThing == true) {
            if(errors > 0) {
                System.out.println();
                System.out.println("PARSER: Parser failed with " + errors + " errors");
                System.out.println("PARSER: CST skipped due to PARSER error(s)");
                errors = 0;
            }
            else {
                System.out.println();
                System.out.println("PARSER: Parsing completed successfully");
                System.out.println("PARSER: Printing CST for Program " + parseList.get(parseCounter).progNum + "...");
                CST();
                errors = 0;

                //Semantic analyzer call here for the future
            }
        }
        else {
            System.out.println();
            System.out.println("PARSER: Parser failed with " + errors + " errors");
            System.out.println("PARSER: CST skipped due to PARSER error(s)");
            errors = 0;
            }
        }

    //Main parse function - to be honest, this is just here to print out the parser statement and move on
    public void parse() {
        if(errors > 0) {
            //Do nothing
        }
        else {
        System.out.println("PARSER: parseBody()");
        parseProgram();
        }
    }

    //Parses through our program by calling Block() (which is what every statement should start with), and then finally compares the output to see if we have errors
    public void parseProgram() {
        if(errors > 0) {
            //Do nothing
        }
        else {
        System.out.println("PARSER: parseProgram()");
        addCST("Program", depth);
        depth++;
        parseBlock();
        //Check for an End-Of-Program symbol. If there is one, end the program gracefully, and let us know how many errors we got while parsing.
        handleParseToken("EOP_BLOCK", "parseEndOfProgram()");
        depth--;
        }
        
    }

    //Find a block statement to parse off of, then match a corresponding close block token when we're finished
    public void parseBlock() {
        if(errors > 0) {
            //Do nothing
        }
        else {
        System.out.println("PARSER: parseBlock()");
        addCST("Block", depth);
        depth++;
        handleParseToken("OPEN_BLOCK", "parseBlock()");
        parseStatementList();
        //Use this to check for the complementary close block token
        handleParseToken("CLOSE_BLOCK", "parseCloseBlock()");
        depth--;
        }
    }

    //If we've got a proper token to start off our statement, then let it in. Otherwise, this is where our program stops
    public void parseStatementList() {
        if(errors > 0) {
            //Do nothing
        }
        else {
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
    }

    //More in-depth token searching than statementList(), this time we want to find a parsing home for all of our promising token candidates
    public void parseStatement() {
        if(errors > 0) {
            //Do nothing
        }
        else {
        System.out.println("PARSER: parseStatement()");
        addCST("Statement", depth);
        depth++;
        //Case statement depending on what kind of token we have
        switch(currentToken) {
            //Can add nested block statements through this
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
    }

    //Print statement - check for a Print (obviously), then look for both sets of parentheses and an expression
    public void parsePrint() {
        if(errors > 0) {
            //Do nothing
        }
        else {
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
        
    }

    //Assignment statement - check for an existing ID to declare, then search for a BoolOp and then finally pass it to Expression() to find what we want to declare it to
    public void parseAssign() {
        if(errors > 0) {
            //Do nothing
        }
        else {
        System.out.println("PARSER: parseAssignStatement()");
        addCST("Assignment", depth);
        depth++;
        parseID();
        handleParseToken("ASSIGNOP", "parseAssignment()");
        parseExpression();
        depth--;
        }
    }

    //Parses an Integer exression, which checks for an open parentheses before either initializing a number or parsing an IntOp expression
    //This is the only way we want to initialize digits and intop statements
    public void parseInt() {

        if(errors > 0) {
            //Do nothing
        }
        else {
        //Set nextToken to currentToken + 1
        if(parseList.size() - 1 > parseCounter + 1) {
            nextToken = parseList.get(parseCounter + 1).tokenType;
        }

        System.out.println("PARSER: parseIntExpression()");
        addCST("Integer Expression", depth);
        depth++;
        if(currentToken == "NUM") {
            parseDigit();
            //If we've got an IntOp after our num, we can assume it's addition - go ahead and parse it
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
    }

    //Parses a string expression, which checks for an open quote token before parsing through the whole list of characters (or spaces) and then ending things off with a close string token
    public void parseString() {
        if(errors > 0) {
            //Do nothing
        }
        else {
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
    }

    public void parseBoolean() {
        if(errors > 0) {
            //Do nothing
        }
        else {
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

            //Set nextToken to currentToken + 1
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
            //Check for a complementary close paren token at the end of our shenanigans
            handleParseToken("CLOSE_PAREN", "parseCloseParen()");
            depth--;
        }
        //If we don't find anything relevant (i.e. no open paren token), print an error
        else {
            error("OPEN_PAREN", currentToken);
        }
        }
    }

    public void parseVarDecl() {
        if(errors > 0) {
            //Do nothing
        }
        else {
        System.out.println("PARSER: parseVarDeclaration()");
        addCST("Variable Declaration", depth);
        depth++;
        //Double check ourselves to make sure we've got a Type on our hands
        parseTypeCheck();
        //...then add the ID we want to declare
        parseID();
        depth--;
        }
    }

    public void parseIf() {
        if(errors > 0) {
            //Do nothing
        }
        else {
        //If statement: initialize if, then pass to Boolean() to determine what to do with the following parentheses, and then Block() to make sure we have an open bracket following too
        addCST("If Statement", depth);
        depth++;
        handleParseToken("IFSTATEMENT", "parseIfStatement()");
        parseBoolean();
        parseBlock();
        depth--;
        }
    }

    public void parseWhile() {
        if(errors > 0) {
            //Do nothing
        }
        else {
        //While statement: pretty much like if
        addCST("While Statement", depth);
        depth++;
        handleParseToken("WHILESTATEMENT", "parseWhileStatement()");
        parseBoolean();
        parseBlock();
        depth--;
        }
    }

    public void parseChar() {
        if(errors > 0) {
            //Do nothing
        }
        else {
        //add a Character (ID part of string)
        //Need to add string functionality to lexer first
        addCST("Character", depth);
        depth++;
        handleParseToken("CHAR", "parseChar()");
        depth--;
        }
    }

    public void parseSpace() {
        if(errors > 0) {
            //Do nothing
        }
        else {
        //Parse out space tokens, which can only be found in strings initialized in our lexer
        addCST("Whitespace", depth);
        depth++;
        handleParseToken("CHARSPACE", "parseSpace()");
        depth--;
        }
    }

    public void parseCharList() {
        if(errors > 0) {
            //Do nothing
        }
        else {
        //Check to see if we have a valid char for our current string (i.e. if it's considered a char or charspace, NOT an id in the token list)
        //Once we have our character, loop back to charlist to parse for more chars
        System.out.println("PARSER: parseCharList()");
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
            //Do nothing...We DON'T want to throw an error here, since that will gum things up
        }
        }
    }

    public void parseTypeCheck() {
        if(errors > 0) {
            //Do nothing
        }
        else {
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
    }

    public void parseExpression() {
        if(errors > 0) {
            //Do nothing
        }
        else {
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
    }

    //Adds a Digit
    public void parseDigit() {
        if(errors > 0) {
            //Do nothing
        }
        else {
        addCST("Digit", depth);
        depth++;
        handleParseToken("NUM", "parseDigit()");
        depth--;
        }
    }

    //Adds an ID
    public void parseID() {
        if(errors > 0) {
            //Do nothing
        }
        else {
        addCST("ID", depth);
        depth++;
        handleParseToken("ID", "parseID()");
        depth--;
        }
    }

    //Adds a BoolOp (==, !=) here - these will be our main comparison tools inside expressions
    public void parseBoolOp() {
        if(errors > 0) {
            //Do nothing
        }
        else {
        addCST("Boolean Operand", depth);
        depth++;
        handleParseToken("BOOLOP", "parseBooleanOperand()");
        depth--;
        }
    }

    //Adds a Boolean value (true, false)
    public void parseBoolVal() {
        if(errors > 0) {
            //Do nothing
        }
        else {
        addCST("Boolean Value", depth);
        depth++;
        handleParseToken("BOOLVAL", "parseBooleanVal()");
        depth--;
        }
    }

    //Adds an IntOp (addition) symbol
    public void parseAdd() {
        if(errors > 0) {
            //Do nothing
        }
        else {
        addCST("Integer Operation", depth);
        depth++;
        handleParseToken("INTOP", "parseIntop()");
        depth--;
        }
    }
    
    //Our Concrete Syntax Tree, which we want to print out at the end of our program. Accepts the info from addCST, and then formats and prints it out
    public void CST() {
        System.out.println();

        //For each token stored in the CST, print it out and pad it with leading "-" symbols depending on its depth
        for(int i = 0; i < CST.size(); i++) {
            String printToken = CST.get(i);
            String depthPadded = padDepth(cstDepth.get(i));
            System.out.println(depthPadded + printToken);
        }

        //Clear our lists for future use
        CST.clear();
        cstDepth.clear();
    }

    //Method to pad our CST depending on each entry's depth
    public String padDepth(int depthToBePadded) {
        String filler = "";
        //If depth = 7, will add 7 "-" symbols to the filler list
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
        if(errors > 0) {
            //Do nothing
        }
        else {
        if(currentToken == expected) {
            //f we've parsed over an EOP token, then set our boolean to true and stop the program
            if(currentToken == "EOP_BLOCK") {
                addCST("[" + parseList.get(parseCounter).name + "]", depth);
                endTheDamnThing = true;
                //DON'T increment parseCounter here - it's the end of the program, there's nothing else left to read!
            }
            //Else it's just a regular token, handle it and add to the CST
            else {
                addCST("[" + parseList.get(parseCounter).name + "]", depth);
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
    }

    //Method to handle errors - takes in the token we wanted and the one we got, and prints out an error message
    public void error(String expectedToken, String errorToken) {
        System.out.println("ERROR: Expected [" + expectedToken + "], got [" + errorToken + "] on line " + parseList.get(parseCounter).lineCount);
        errors++;
        parseCounter++;
    }
}
