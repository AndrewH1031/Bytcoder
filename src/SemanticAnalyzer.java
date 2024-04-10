import java.util.ArrayList;

public class SemanticAnalyzer {
    
    ArrayList<String> AST = new ArrayList<>(); //Assuming I'm going to need this like last time
    ArrayList<Integer> astDepth = new ArrayList<>(); //this too
    ArrayList<Symbol> symbolList = new ArrayList<>(); //make this like the token class in lexer - add stuff to it
    //add based on:
    // - name of token
    // - scope
    // - type
    // - if it's used
    // - if it's initialized
    //can just use first three for the actual definitions, used/init can be bool

    //edit these for our incoming AST
    ArrayList<Token> semanticList; //List for storing our parse variables, we want to use these to print our AST as well
    int parseCounter = 0; //for counting each token in the list, one by one
    int errors = 0; //Error count, tracks how many errors we happen to run into in our Parsing process. Program will refuse to print CST if there's any errors
    int warnings = 0; //might need this soon
    int depth = 0;
    int scope = 0;
    String currentToken; //idk what to do with this anymore
    String nextToken; //The next token in line, used for finding proper tokens in sequence in stuff like IntOp
    String sentence = "";
    boolean endTheDamnThing = false; //If this is true, end our program and print the CST if we have no errors. Only found if we parse through an EOP token
    boolean declaring = false;
    boolean assigning = false;

    //Currently just messing around with the code to accept proper AST syntax - I'll do the stupid symbol table later. Code is SORT OF WORKING, just make sure to recompile this file every time to make sure the class file is there.

    //General ideas:
    //Keep parse function redirects, but just skip over tokens themselves - we don't want to parse them a second time

    public void main(ArrayList<Token> list) {
        semanticList = list; //set parseList equal to the list in our Lexer for comparison purposes
        parseCounter = 0; //Reset parseCounter between lexer calls
        depth = 0; //Reset depth between lexer calls
        int scope = -1; //this looks awful but it's the only way to make sure it's 0 on the first block

        System.out.println();
        System.out.println("Semantic Analysis for Program " + semanticList.get(parseCounter).progNum);
        System.out.println();
        System.out.println("Beginning Analyzer...");
        currentToken = semanticList.get(parseCounter).tokenType; //Set current token to the first element in our borrowed list

        analyze(); //Call AST print from this

        AST.clear();
        astDepth.clear();
        symbolList.clear();
        
        }

    //Main parse function - I'm only keeping this here for [insert witty joke]
    public void analyze() { //use this naming scheme
        //System.out.println(semanticList);
        analyzeProgram();
        if(errors > 0) {
            System.out.println("Semantic Analysis failed with " + errors + " errors and " + warnings + "warnings");
            System.out.println();
            System.out.println("AST and Symbol Table skipped due to Semantic Analysis errors");
        }
        else {
            System.out.println("Semantic Analysis completed with " + errors + " errors and " + warnings + "warnings");
            System.out.println();
            System.out.println("Printing AST...");
            System.out.println();
            AST();
            printSymbolList();
        }
    }

    //I think this just analyzes the program thingy now and nothing else
    public void analyzeProgram() {
        analyzeBlock();
    }

    public void analyzeBlock() {
        //DON'T check for match - parser already did that

        System.out.println("analyzeBlock()");
        addAST("Open Block", depth);
        depth++;
        scope++; //increment scope to accomodate new block

        parseCounter = refresh(parseCounter);
        currentToken = semanticList.get(parseCounter).tokenType;

        //System.out.println(scope); //test

        analyzeStatementList();

        //Check for a close block at the end of our block statement - no need to match it up, parser has that covered already!
        addAST("Close Block", depth);

        parseCounter = refresh(parseCounter);
        currentToken = semanticList.get(parseCounter).tokenType;

        //Decrement depth and scope when we're done
        depth--;
        scope--;

    }

    public void analyzeStatementList() {
        System.out.println("Statement List");

        currentToken = semanticList.get(parseCounter).tokenType;
        //System.out.println(currentToken); //test

        if(currentToken == "PRINTSTATEMENT" || currentToken == "ID" || currentToken == "WHILESTATEMENT" || currentToken == "IFSTATEMENT" || currentToken == "OPEN_BLOCK" || currentToken == "TYPEINT" || currentToken == "TYPESTRING" || currentToken == "TYPEBOOL") {

            analyzeStatement();
            analyzeStatementList();
        }
    }

    public void analyzeStatement() {
        System.out.println("Statement");
        //Case statement depending on what kind of token we have
        switch(currentToken) {
            //Can add nested block statements through this
            case("OPEN_BLOCK"):
                analyzeBlock();
            break;
            case("PRINTSTATEMENT"):
                analyzePrint();
            break;
            case("IFSTATEMENT"):
                analyzeIf();
            break;
            case("WHILESTATEMENT"):
                analyzeWhile();
            break; 
            case("ID"):
            //Assigning an ID or do we just have a lone ID? Don't know, let's pass it to Assign()
                analyzeAssign();
            break;
            case("TYPEINT"):
            //Types are declared, need to pass it to VarDecl()
                analyzeVarDecl();
            break;
            case("TYPESTRING"):
                analyzeVarDecl();
            break;
            case("TYPEBOOL"):
                analyzeVarDecl();
            break;
            //If we've got nothing, then throw an error
            default:
                System.out.println("ERROR: expected STMT token, got token " + currentToken +  " in scope " + scope + " on line " + semanticList.get(parseCounter).lineCount);
                errors++;
            break;
        }
    }

    public void analyzePrint() {
        addAST("Print", depth);
        System.out.println("Print");
        
        //Skip the first two tokens we encounter (string statement and open quotes) and jump straight to the expression
        parseCounter = refresh(parseCounter);
        currentToken = semanticList.get(parseCounter).tokenType;
        parseCounter = refresh(parseCounter);
        currentToken = semanticList.get(parseCounter).tokenType;
        depth++;
        analyzeExpression();
        depth--;
        //One last skip over the close quotes token
        parseCounter = refresh(parseCounter);
        currentToken = semanticList.get(parseCounter).tokenType;
        

    }

    public void analyzeAssign() {
        addAST("Assign", depth);
        System.out.println("Assign");

        depth++;
        assigning = true;  
        analyzeID();
        //Skip over this token that we previously checked for
        parseCounter = refresh(parseCounter);
        currentToken = semanticList.get(parseCounter).tokenType;
        analyzeExpression();
        depth--;
    }

    public void analyzeIf() {
        addAST("If", depth);
        System.out.println("If");
        parseCounter = refresh(parseCounter);
        currentToken = semanticList.get(parseCounter).tokenType;
        depth++;
        analyzeBoolean();
        analyzeBlock();
        depth--;
    }

    public void analyzeWhile() {
        addAST("While", depth);
        System.out.println("While");
        parseCounter = refresh(parseCounter);
        currentToken = semanticList.get(parseCounter).tokenType;
        
        depth++;
        analyzeBoolean();
        analyzeBlock();
        depth--;
    }

    public void analyzeVarDecl() {
        addAST("Variable Dec", depth);
        System.out.println("VarDecl");
        depth++;
        analyzeTypeCheck();
        declaring = true;
        analyzeID();
        depth--;
    }

    public void analyzeBoolean() {
        System.out.println("Boolean");
        if(currentToken == "OPEN_PAREN") {
            parseCounter = refresh(parseCounter);
            currentToken = semanticList.get(parseCounter).tokenType;
            analyzeExpression();
            analyzeBoolOp();

            //Set nextToken to currentToken + 1
            if(semanticList.size() - 1 > parseCounter + 1) {
                nextToken = semanticList.get(parseCounter + 1).tokenType;
            }

            if(nextToken == "BOOLVAL") {
                analyzeBoolVal();
            }
            else {
                analyzeExpression();
            }
            //increment parsecounter and skip this next close paren token - use refresh
            parseCounter = refresh(parseCounter);
            currentToken = semanticList.get(parseCounter).tokenType;
            
        }

        else {
            System.out.println("ERROR: expected " + currentToken + ", got token " + scope + " on line " + semanticList.get(parseCounter).lineCount);
            errors++;
        }
    }

    public void analyzeInt() {
        System.out.println("Int");
        //analyzeDigit - no need to check for num, it's already foretold
        
        if(semanticList.size() - 1 > parseCounter + 1) {
            nextToken = semanticList.get(parseCounter + 1).tokenType;
        }

        //Still parse digit
        analyzeDigit();

        if(nextToken == "INTOP") {
            analyzeAdd();
            analyzeExpression();
        }
    }

    public void analyzeString() {
        System.out.println("String");
        parseCounter = refresh(parseCounter);
        currentToken = semanticList.get(parseCounter).tokenType;
        analyzeCharList();
        //skip over open/close quotes, we don't need them
        parseCounter = refresh(parseCounter);
        currentToken = semanticList.get(parseCounter).tokenType;
    }

    public void analyzeChar() {
        System.out.println("Char");

        //Call our processString method to add our current token to the string
        sentence = processString(sentence);

        //Refresh our token
        parseCounter = refresh(parseCounter);
        currentToken = semanticList.get(parseCounter).tokenType;
    }

    public void analyzeSpace() {
        System.out.println("Space");

        //Call our processString method to add our current token to the string
        sentence = processString(sentence);

        //Refresh our token
        parseCounter = refresh(parseCounter);
        currentToken = semanticList.get(parseCounter).tokenType;
    }

    public void analyzeCharList() {
        //Add something here to keep track of strings

        //addAST("")
        System.out.println("CharList");
        if(currentToken == "CHAR") {
            analyzeChar();
            analyzeCharList();
        }
        //Finally doing stuff with our space tokens
        else if(currentToken == "CHARSPACE") {
            analyzeSpace();
            analyzeCharList();
        }
        else {
            //Do nothing...We DON'T want to throw an error here, since that will gum things up
            addAST("[" + sentence + "]", depth);
            sentence = "";
            
            //Put something here to add strings? If not then make a new if else
        }
    }

    //Simple little function to concatenate our strings together using the power of our Token class. Thanks, Token class!
    //Basically just pulls the current token's name (not type, which is what we usually have on hand) and adds it to sentence. Since we're always going to be in a string when we reach this function,
    //we can treat every token as a char
    public String processString(String sentence) {
        sentence = sentence + semanticList.get(parseCounter).name;

        return sentence;
    }

    public void analyzeExpression() {
        System.out.println("Expression");
        //Expressions go here - this is reserved for stuff inside parentheses usually, which means a whole lot of boolop tokens being parsed
        //addAST("Expression", depth);
        switch(currentToken) {
            //If it's a digit, parse it as an integer expression
            case("NUM"):
                analyzeInt();
            break;
            //If it's a string, parse it as a string expression
            case("OPENSTRING"):
                analyzeString();
            break;
            //We can parse for initialized IDs as well, or use in IntOp/if or while expressions
            case("ID"):
                analyzeID();
            break;
            //If it's an open parentheses, then it's the start of a boolean expression (since we're parsing it here) - send it over
            case("OPEN_PAREN"):
                analyzeBoolean();
            break;
            //If it's a boolean value, parse it - This is the ONLY time we should ever be parsing this!
            case("BOOLVAL"):
                analyzeBoolVal();
            break;
            default:
                System.out.println("ERROR: expected " + currentToken + ", got token " + scope + " on line " + semanticList.get(parseCounter).lineCount);
            break;
        }
    }

    public void analyzeID() {
        System.out.println("ID");
        addAST(semanticList.get(parseCounter).name, depth);
        //System.out.println(declaring);
        scopeCheck();
        
        //refresh our token
        parseCounter = refresh(parseCounter);
        currentToken = semanticList.get(parseCounter).tokenType;
    }

    public void analyzeDigit() {
        System.out.println("Digit");
        //just handle this like normal
        handleSemanticToken("NUM", currentToken);
    }

    public void analyzeTypeCheck() {
        System.out.println("TypeCheck");
        if((currentToken == "TYPEINT") || (currentToken == "TYPESTRING") || (currentToken == "TYPEBOOL")) {
            switch(currentToken) {
                case("TYPEINT"):
                    handleSemanticToken("TYPEINT", "analyzeInt()");
                break;
                case("TYPESTRING"):
                    handleSemanticToken("TYPESTRING", "analyzeString()");
                break;
                case("TYPEBOOL"):
                    handleSemanticToken("TYPEBOOL", "analyzeBoolean()");
                break;
            }
        }
    }

    public void analyzeBoolOp() {
        System.out.println("BoolOp");
        addAST("Boolean Op", depth);
        
        depth++;
        parseCounter = refresh(parseCounter);
        currentToken = semanticList.get(parseCounter).tokenType;
        depth--;
    }

    public void analyzeBoolVal() {
        System.out.println("BoolVal");
        handleSemanticToken("BOOLVAL", currentToken);
    }

    public void analyzeAdd() {
        System.out.println("Intop");
        addAST("Integer Operation", depth);
        depth++;
        handleSemanticToken("INTOP", currentToken);
        depth--;
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
        //System.out.println("AST addition goes here - current token is " + tokenInAST);
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
            //if we've parsed over an EOP token, then set our boolean to true and stop the program
            if(currentToken == "EOP_BLOCK") {
                //System.out.println("yay!");
                endTheDamnThing = true;
                //DON'T increment parseCounter here - it's the end of the program, there's nothing else left to read!
            }
            //Else it's just a regular token, handle it and add to the CST
                addAST("[" + semanticList.get(parseCounter).name + "]", depth);
                //System.out.println("Correct Token! It's " + expected); //Use this to test
                parseCounter++;
        }
        //If it doesn't match, then we throw an error and increment parseCounter
        else {
            System.out.println("ERROR: expected " + expected + ", got token " + currentToken +  " in scope " + scope + " on line " + semanticList.get(parseCounter).lineCount);
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

    //addSymbolTable

    public void scopeCheck() {

        //if we're declaring the ID:
            //add to symbol table
            //scope check it
                //can't declare two variables with the same name in the same scope
                //I'm guessing below and above are fine??? test it out
        //else then we're not declaring - just check scope to make sure it's being called properly
            //can call variables from scopes lower than the dec but not higher - 1 can call 0 but not 2

            if(declaring == true){
                //System.out.println("yay!!!");
                int backup = symbolMatch(symbolList, scope);
                
                if(backup != 300) {
                
                
                
                //System.out.println("backup is " + backup);
                //System.out.println("scope is " + scope);

                if(backup > scope || backup < scope) {
                    //System.out.println("backup completed!");
                    symbolList.add(new Symbol(semanticList.get(parseCounter).name, semanticList.get(parseCounter-1).name, scope));
                    symbolList.get(symbolList.size()-1).isItDeclared = true;
                }
                else if(backup == scope) {
                    declError();
                    errors++;
                }
            }
            else {
                symbolList.add(new Symbol(semanticList.get(parseCounter).name, semanticList.get(parseCounter-1).name, scope)); //move this
                symbolList.get(symbolList.size()-1).isItDeclared = true;
            }
            }
            declaring = false;

        if(assigning == true) {
            //System.out.println("yay!");
            //System.out.println("yay!!!");
            int backup = symbolMatch(symbolList, scope);
            if(backup != 300) {
            
            
            
            //System.out.println("backup is " + backup);
            //System.out.println("scope is " + scope);

            if(backup < scope || backup == scope) {
                //System.out.println("secondary backup completed!");
                //This is currently wonky, working on fixing it
                for (Symbol symbol : symbolList) {
                    if (symbol.scope == scope) {
                        symbol.isItUsed = true;
                    }
                }
            }
            else if(backup > scope) {
                assignError();
                errors++;
            }
            else {
                for (Symbol symbol : symbolList) {
                    if (symbol.scope == scope) {
                        symbol.isItUsed = true;
                    }
                }
            }
            assigning = false;
        }
        else {
            assignError();
            errors++;
        }
        }

            /*if symbol scope is less than or greater than current scope {
                add to symbol table and set isItDeclared to true
            }
            if symbol scope is equal to current scope {
                throw error
            }*/

        //symbolList.get(symbolList.size()-1).isItUsed = true;


        
        
        //symbolList.get(symbolList.size()-1).isItUsed = true; //testing

    }

    public void printSymbolList() {
        //print out table - put this in its own method
        System.out.println();
        System.out.println("NAME | TYPE | SCOPE | IS DECLARED? | IS USED?");
        System.out.println("-----------------------------------------------");
        int specialWarning = 0; //I am very creative
        for(int i = 0; i < symbolList.size(); i++) {
            System.out.println(symbolList.get(i).symbolType + "     " +  symbolList.get(i).name + "     " + symbolList.get(i).scope + "     " + symbolList.get(i).isItDeclared + "      " + symbolList.get(i).isItUsed); //thing that we want to compare
            if(symbolList.get(i).isItDeclared == true && symbolList.get(i).isItUsed == false) {
                specialWarning++;
            }
        }
        if(specialWarning > 0) {
            System.out.println("WARNING: variables declared but never used");
        }
    }

    //Used ChatGPT a little bit for this - asked to optimize the name matching between the two compared lists
    public int symbolMatch(ArrayList<Symbol> symbolList, int scope) {
        //Declaring currentToken again in case
        String currentToken = semanticList.get(parseCounter).name;
        for (Symbol symbol : symbolList) {
            if (symbol.symbolType.equals(currentToken)) {
                //If our names and scopes match, great! hand it back to our main loop and set it to declared
                if (symbol.scope == scope) {
                    return symbol.scope;
                }
                //If our names match but our scopes don't, return scope anyways
                else {
                    return symbol.scope;
                }
            }
        }
        return 300; //Returns a dummy number if nothing matches
    }

    public void assignError() {
        System.out.println("ERROR: the token " + currentToken + " was used before being declared on line " + semanticList.get(parseCounter).lineCount);
    }

    public void declError() {
        System.out.println("ERROR: the token " + currentToken + " has already been declared in scope " + scope + " on line " + semanticList.get(parseCounter).lineCount);
    }

}
