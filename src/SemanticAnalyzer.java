import java.util.ArrayList;

public class SemanticAnalyzer {
    
    ArrayList<String> AST = new ArrayList<>(); //Assuming I'm going to need this like last time
    ArrayList<Integer> astDepth = new ArrayList<>(); //this too

    //edit these for our incoming AST
    ArrayList<Token> semanticList; //List for storing our parse variables, we want to use these to print our AST as well
    int parseCounter = 0; //for counting each token in the list, one by one
    int errors = 0; //Error count, tracks how many errors we happen to run into in our Parsing process. Program will refuse to print CST if there's any errors
    int warnings = 0; //might need this soon
    int depth = 0; //Need to reformat this
    String currentToken; //idk what to do with this anymore
    String nextToken; //The next token in line, used for finding proper tokens in sequence in stuff like IntOp
    boolean endTheDamnThing = false; //If this is true, end our program and print the CST if we have no errors. Only found if we parse through an EOP token

    //Currently just messing around with the code to accept proper AST syntax - I'll do the stupid symbol table later. Code is SORT OF WORKING, just make sure to recompile this file every time to make sure the class file is there.

    //General ideas:
    //Keep parse function redirects, but just skip over tokens themselves - we don't want to parse them a second time

    public void main(ArrayList<Token> list) {
        semanticList = list; //set parseList equal to the list in our Lexer for comparison purposes
        parseCounter = 0; //Reset parseCounter between lexer calls
        depth = 0; //Reset depth between lexer calls

        System.out.println();
        System.out.println("Semantic Analysis for Program " + semanticList.get(parseCounter).progNum);
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
        if(errors > 0) {
            System.out.println("Semantic Analysis failed with " + errors + " errors and " + warnings + "warnings");
            
        }
        else {
            System.out.println("Semantic Analysis completed with " + errors + " errors and " + warnings + "warnings");
            System.out.println();
            System.out.println("Printing AST...");
            System.out.println();
            AST();
            //symbol table stuff here
        }
    }

    //I think this just analyzes the program thingy now and nothing else
    public void parseProgram() {
        parseBlock();
    }

    public void parseBlock() {
        //DON'T check for match - parser already did that

        //Increment depth, scope comes later for symbol table
        System.out.println("parseBlock()");
        addAST("Open Block", depth);

        parseCounter = refresh(parseCounter);
        currentToken = semanticList.get(parseCounter).tokenType;

        parseStatementList();

        //Check for a close block at the end of our block statement - no need to match it up, parser has that covered already!
        addAST("Close Block", depth);

        parseCounter = refresh(parseCounter);
        currentToken = semanticList.get(parseCounter).tokenType;

    }

    public void parseStatementList() {
        System.out.println("Statement List");

        currentToken = semanticList.get(parseCounter).tokenType;
        System.out.println(currentToken); //test

        if(currentToken == "PRINTSTATEMENT" || currentToken == "ID" || currentToken == "WHILESTATEMENT" || currentToken == "IFSTATEMENT" || currentToken == "OPEN_BLOCK" || currentToken == "TYPEINT" || currentToken == "TYPESTRING" || currentToken == "TYPEBOOL") {

            parseStatement();
            parseStatementList();
        }
    }

    public void parseStatement() {
        System.out.println("Statement");
        //Case statement depending on what kind of token we have
        switch(currentToken) {
            //Can add nested block statements through this
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
            //Assigning an ID or do we just have a lone ID? Don't know, let's pass it to Assign()
                parseAssign();
            break;
            case("TYPEINT"):
            //Types are declared, need to pass it to VarDecl()
                parseVarDecl();
            break;
            case("TYPESTRING"):
                parseVarDecl();
            break;
            case("TYPEBOOL"):
                parseVarDecl();
            break;
            //If we've got nothing, then throw an error
            default:
                error();
            break;
        }
    }

    public void parsePrint() {
        addAST("Print", depth);
        System.out.println("Print");

        //Skip the first two tokens we encounter (string statement and open quotes) and jump straight to the expression
        parseCounter = refresh(parseCounter);
        currentToken = semanticList.get(parseCounter).tokenType;
        parseCounter = refresh(parseCounter);
        currentToken = semanticList.get(parseCounter).tokenType;

        parseExpression();

        //One last skip over the close quotes token
        parseCounter = refresh(parseCounter);
        currentToken = semanticList.get(parseCounter).tokenType;

    }

    public void parseAssign() {
        addAST("Assign", depth);
        System.out.println("Assign");
        parseID();
        //Skip over this token that we previously checked for
        parseCounter = refresh(parseCounter);
        currentToken = semanticList.get(parseCounter).tokenType;
        parseExpression();
    }

    public void parseIf() {
        addAST("If", depth);
        System.out.println("If");
        parseCounter = refresh(parseCounter);
        currentToken = semanticList.get(parseCounter).tokenType;
        parseBoolean();
        parseBlock();
    }

    public void parseWhile() {
        addAST("While", depth);
        System.out.println("While");
        parseCounter = refresh(parseCounter);
        currentToken = semanticList.get(parseCounter).tokenType;
        parseBoolean();
        parseBlock();
    }

    public void parseVarDecl() {
        addAST("Variable Dec", depth);
        System.out.println("VarDecl");
        parseTypeCheck();
        parseID();
    }

    public void parseBoolean() {
        System.out.println("Boolean");
        if(currentToken == "OPEN_PAREN") {
            parseCounter = refresh(parseCounter);
            currentToken = semanticList.get(parseCounter).tokenType;
            parseExpression();
            parseBoolOp();

            //Set nextToken to currentToken + 1
            if(semanticList.size() - 1 > parseCounter + 1) {
                nextToken = semanticList.get(parseCounter + 1).tokenType;
            }

            if(nextToken == "BOOLVAL") {
                parseBoolVal();
            }
            else {
                parseExpression();
            }
            //increment parsecounter and skip this next close paren token - use refresh
            parseCounter = refresh(parseCounter);
            currentToken = semanticList.get(parseCounter).tokenType;
            
        }

        else {
            error();
        }
    }

    public void parseInt() {
        System.out.println("Int");
        //parseDigit - no need to check for num, it's already foretold
        
        if(semanticList.size() - 1 > parseCounter + 1) {
            nextToken = semanticList.get(parseCounter + 1).tokenType;
        }

        //Still parse digit
        parseDigit();

        if(nextToken == "INTOP") {
            parseAdd();
            parseExpression();
        }
    }

    public void parseString() {
        System.out.println("String");
        parseCounter = refresh(parseCounter);
        currentToken = semanticList.get(parseCounter).tokenType;
        parseCharList();
        //skip over open/close quotes, we don't need them
        parseCounter = refresh(parseCounter);
        currentToken = semanticList.get(parseCounter).tokenType;
    }

    public void parseChar() {
        System.out.println("Char");
        parseCounter = refresh(parseCounter);
        currentToken = semanticList.get(parseCounter).tokenType;
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
        parseCounter = refresh(parseCounter);
        currentToken = semanticList.get(parseCounter).tokenType;
        //Add this??? don't really think we need to... just skip over it
    }

    public void parseExpression() {
        System.out.println("Expression");
        //Expressions go here - this is reserved for stuff inside parentheses usually, which means a whole lot of boolop tokens being parsed
        addAST("Expression", depth);
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
                error();
            break;
        }
    }

    public void parseID() {
        System.out.println("ID");
        handleSemanticToken("ID", currentToken);
        //add to AST?
    }

    public void parseDigit() {
        System.out.println("Digit");
        handleSemanticToken("NUM", currentToken);

        parseCounter = refresh(parseCounter);
        currentToken = semanticList.get(parseCounter).tokenType;
    }

    public void parseTypeCheck() {
        System.out.println("TypeCheck");
        if((currentToken == "TYPEINT") || (currentToken == "TYPESTRING") || (currentToken == "TYPEBOOL")) {
            switch(currentToken) {
                case("TYPEINT"):
                    handleSemanticToken("TYPEINT", "parseInt()");
                break;
                case("TYPESTRING"):
                    handleSemanticToken("TYPESTRING", "parseString()");
                break;
                case("TYPEBOOL"):
                    handleSemanticToken("TYPEBOOL", "parseBoolean()");
                break;
            }
        }
        //Don't need an else statement here since handleParseToken takes care of errors for us
        //nvm it's nice to have backup options
        else {
            error();
        }
    }

    public void parseBoolOp() {
        System.out.println("BoolOp");
        addAST("Boolean Op", depth);
        
        parseCounter = refresh(parseCounter);
        currentToken = semanticList.get(parseCounter).tokenType;
    }

    public void parseBoolVal() {
        System.out.println("BoolVal");
        handleSemanticToken("BOOLVAL", currentToken);
    }

    public void parseAdd() {
        System.out.println("Intop");
        addAST("Integer Op", depth);

        parseCounter = refresh(parseCounter);
        currentToken = semanticList.get(parseCounter).tokenType;
    }

    public void error() {
        System.out.println("errors go here");
    }

    public void AST() {
        System.out.println();

        for(int i = 0; i < AST.size(); i++) {
            String printToken = AST.get(i);
            String depthPadded = padDepth(astDepth.get(i));
            System.out.println(depthPadded + "<" + printToken + ">");
        }

        //Clear our lists for future use
        AST.clear();
        astDepth.clear();
    }

    public void addAST(String tokenInAST, int tokenDepth) {
        System.out.println("AST addition goes here - current token is " + tokenInAST); //test
        AST.add(tokenInAST);
        astDepth.add(tokenDepth);
    }

    public String padDepth(int depthToBePadded) {
        String filler = "";
        //If depth = 7, will add 7 "-" symbols to the filler list
        for (int i = 0; i < depthToBePadded; i++) {
            filler = filler + "-";
        }
        return filler;
    }

    //I'm not sure if I still need this but I'll keep it around
    public void handleSemanticToken(String expected, String output) {
        if(currentToken == expected) {
            //f we've parsed over an EOP token, then set our boolean to true and stop the program
            if(currentToken == "EOP_BLOCK") {
                addAST("[" + semanticList.get(parseCounter).name + "]", depth);
                endTheDamnThing = true;
                //DON'T increment parseCounter here - it's the end of the program, there's nothing else left to read!
            }
            //Else it's just a regular token, handle it and add to the CST
            else {
                addAST("[" + semanticList.get(parseCounter).name + "]", depth);
                //System.out.println("Correct Token! It's " + expected); //Use this to test
                parseCounter++;
            }
        }
        //If it doesn't match, then we throw an error and increment parseCounter
        else {
            error();
        }
        //Set our parser token to the next token once we're done
        currentToken = semanticList.get(parseCounter).tokenType;
    }

    //replaces the current token with the next token in line, effectively "refreshing" it. Basically the same as what was going on in the old handleParseToken
    //function in the Parser, only that this has a lot less steps
    public int refresh(int parseCounter) {
        parseCounter++;
        currentToken = semanticList.get(parseCounter).tokenType;

        return parseCounter;
    }

}
