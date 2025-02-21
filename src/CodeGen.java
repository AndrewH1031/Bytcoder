import java.util.ArrayList;
import java.util.regex.Pattern;

public class CodeGen {
    
    ArrayList<String> genTable; //AST copy
    ArrayList<Symbol> symbolOp; //symbol list copy
    ArrayList<String> opCodeList = new ArrayList<>(); //should be under 256, else throw error
    ArrayList<String> stackList = new ArrayList<>();
    ArrayList<String> jump = new ArrayList<>(); //array to store our jump values for while loops

    int newScope; //Copy of scope, might need this
    //Pass existing scope over? or use it from symbol class
    int opCounter;
    int declCounter; //Used for vardecl so we can store each variable in a unique address 
    int heapCount; //Used to track our heap values to assign for some of our variables
    int errors;
    int branchNum; //Helps with determining jump values for while statements
    int jumpVal;
    int jumpToHere = 0; //Jump value we want to keep track of for loops
    int pastopCount;

    boolean stopAddingFirst;
    boolean stopAddingSecond;

    boolean notEquals; //For when we have a != boolop to compare to

    boolean areWeJumping = false; //jump boolean for when we want to set a jump value for our while loops
    boolean weAreIffing = false; //bool for handling if statements
    boolean assignLoop = false; //Assignment loop for intop expressions

    //NOTE: CodeGen is currently a bit wonky when dealing with intOps in assigns, currently working on a solution for this
    //Also Semantic Analyzer is still unfortunately broken...Don't assign any variables to each other yet.

    //Want to ignore those pesky Semantic bugs? Hop on over to SemanticAnalyzer.java and delete the brackets underneath the first codegen call in the analyzeProgram block!!


    public void main(ArrayList<String> list, ArrayList<Symbol> symbolList) {
        //System.out.println("IOU one Code Gen");

        //Port over our symbolList and AST from semantic
        genTable = list;
        symbolOp = symbolList;

        newScope = -1; //same as last time, dumb but it works
        heapCount = 255; //Heap pointer - starts at address FF and works its way down

        branchNum = 0; //sets our current address as a reference for our while jump

        genCode();
        addOpCodes("00");
        if(opCodeList.size() > 256) {
            System.out.println("ERROR: Cannot exceed more than 256 OpCodes in one program.");
            errors++;
        }
        if(errors > 0) {
            System.out.println("CODE GEN: OPCODE GENERATION SKIPPED DUE TO CODE GEN ERORRS");
        }
        else {
            System.out.println("CODE GEN: Code Generation completed with " + errors + " errors.");
            System.out.println();
            handleStack(); //need to process temp codes before we can print
            handleJump(); //replace jump values for if and while
            printCode(); //print this mess
        }

    }

    public void genCode() {

        //Initialize the first two temp values that we want to use for storing in our stack, we can use these to insert into any value to be determined
        //I realized halfway through that I was accidentally putting T0 or T1 on most of my temp opCodes and I really don't want to go back over that manually, so if
        //you see anything that draws from stack there you go it's a brand new temp code
        declCounter++;
        stackList.add("T0XX");
        stackList.add("T1XX");

        //Rudimentary loop to parse through our AST we passed from semantic
        //System.out.println("IOU one genCode");
        for(int i = 0; i < genTable.size(); i++) {
            //System.out.println("codepointer is " + opCounter);
            
            switch(genTable.get(i)) {
                case "Open Block":
                    System.out.println("oblock");

                    newScope++; //increment scope when we have a new block like usual
                    //System.out.println("newScope is " + newScope); //testing

                break;
                case "Close Block":
                    System.out.println("closeblock");

                    newScope--;

                    //If we've got an if loop, we need to jump based on when our lastpointer was set
                    if(weAreIffing == true) {
                        //System.out.println("there's supposed to be an if loop here you goober");
                        int ifJumping = opCounter - pastopCount;
                        for(int g = 0; g < jump.size(); g++) {
                            //Padding out the array to patch my spaghetti-code handleJump function
                            if(jump.get(g).equals("J" + pastopCount)) {
                                jump.set(g, "00" + Integer.toHexString(ifJumping));
                            }
                        }
                        weAreIffing = false;
                    }

                    //If we've got a while loop, Initialize a loop program and set our jump values
                    if(areWeJumping == true) {
                        addOpCodes("A9");
                        addOpCodes("01");
                        addOpCodes("8D");
                        addOpCodes("T0");
                        addOpCodes("00");
                        addOpCodes("A2");
                        addOpCodes("02");
                        addOpCodes("EC");
                        addOpCodes("T0");
                        addOpCodes("00");
                        addOpCodes("D0");

                        //Branch this amount to loop backwards to the beginning
                        //Kudos to StackOverflow for the formula for reverse branching, couldn't find the exact page but it's out there and I found it immensely helpful
                        jumpToHere = (255 - opCounter) + branchNum;
                        addOpCodes(Integer.toHexString(jumpToHere).toUpperCase());
                        areWeJumping = false;
                    }

                break;
                case "Assign":

                    i++;
                    System.out.println("Assign statement");
                    Symbol tempSymbol;

                    for(int j = 0; j < symbolOp.size(); j++) {
                        //System.out.println(symbolOp.get(j).name);
                        //System.out.println(symbolOp.get(j).symbolType);
                        //System.out.println(newScope);
                        //System.out.println("current token is " + genTable.get(i));

                        if (symbolOp.get(j).name == "int" && symbolOp.get(j).symbolType.equals(genTable.get(i))) {
                            //System.out.println("yayy!!!" + symbolOp.get(j).scope);
                            tempSymbol = symbolOp.get(j);
                            i++;
                            String tempy = "";

                            addOpCodes("A9");

                            if(genTable.get(i).substring(1, genTable.get(i).length() - 1).length() < 3) {
                                tempy = "0" + Integer.toHexString(Integer.valueOf(genTable.get(i).substring(1, genTable.get(i).length() - 1)));
                                tempy = tempy.toUpperCase();
                                addOpCodes(tempy);
                            }
                            else {
                                addOpCodes(Integer.toHexString(Integer.valueOf(genTable.get(i).substring(1, genTable.get(i).length() - 1))));
                            }

                            addOpCodes("8D");
                            addOpCodes("T0");
                            addOpCodes("00");
                            //System.out.println("next token is " + genTable.get(i + 1));

                            //Int expression handling - checks for the IntOp and + tokens to see if we need to add
                            //Works a bit weirdly sometimes, please be patient with it
                            if(genTable.get(i + 1).equals("IntOp") || genTable.get(i + 1).equals("[+]")) {
                                i = i + 3; //Skip past our intop AND + tokens
                                
                                assignLoop = true; //Setting this just in case

                                //For the length of our intop, parse through 
                                while(assignLoop == true) {

                                    //If we've got an ID coming up, then match it
                                    if(Pattern.matches("[a-z]", genTable.get(i)) && (genTable.get(i).length() < 3)) {
                                        //System.out.println("right where we need to be");
                                        addOpCodes("AD");
                                        addOpCodes(stackList.get(stackList.size()-1).substring(0, 2));
                                        addOpCodes("00");
                                        addOpCodes("6D");
                                        addOpCodes("T0");
                                        addOpCodes("00");
                                        addOpCodes("8D");
                                        addOpCodes("T0");
                                        addOpCodes("00");

                                    }

                                    //else it's a number, act accordingly
                                    else if (Pattern.matches("[0-9]", genTable.get(i).substring(1, 2))) {
                                        //System.out.println("right where we DON'T need to be");
                                        addOpCodes("A9");
                                        addOpCodes("00");
                                        addOpCodes("6D");
                                        addOpCodes("T0");
                                        addOpCodes("00");
                                        addOpCodes("8D");
                                        addOpCodes("T0");
                                        addOpCodes("00");
                                    }

                                    else {
                                        //Nothing...this will catch other intops
                                    }

                                    //If there's nothing more in the assignment (or if it's not just an Intop), terminate it and break out of the loop.
                                    if((genTable.get(i + 1).length() > 3) && (!genTable.get(i).equals("IntOp"))) {
                                        addOpCodes("8D");
                                        addOpCodes(stackList.get(stackList.size()-1).substring(0, 2));
                                        addOpCodes("00");
                                        assignLoop = false;
                                    }

                                    else {
                                        i++;
                                    }
                                }

                                break;

                            }
                        //If there's nothing after this, finalize the assignment by storing it in memory
                        //This also doubles as a backup storage command if we run into problems above
                        else if (genTable.get(i + 1).length() > 3) {
                            addOpCodes("8D");
                            addOpCodes(stackList.get(stackList.size()-1).substring(0, 2));
                            addOpCodes("00");
                            }
                        }
                    
                        //Matches a string
                        else if (symbolOp.get(j).name == "string" && symbolOp.get(j).symbolType.equals(genTable.get(i))) {
                            //System.out.println("nooooo!!!!" + symbolOp.get(j).scope);
                            //System.out.println(symbolOp.get(j).scope);
                            tempSymbol = symbolOp.get(j);
                            //System.out.println("current symbol is " + tempSymbol.name);
                            i++;
                            //System.out.println("currenterrrr token is " + genTable.get(i));

                            String currentString = genTable.get(i).substring(1, genTable.get(i).length() - 1);

                            addOpCodes("A9");
                            addOpCodes("00");
                            addOpCodes("8D");
                            addOpCodes(Integer.toHexString(heapCount).toUpperCase());
                            addOpCodes("00");
                            heapCount--;
                            
                            //This is basically just a huge charlist we parse through for every string
                            for (int x = 0; x < currentString.length(); x++) {
                                addOpCodes("A9");
                                addOpCodes(Integer.toHexString((int)currentString.charAt(x)).toUpperCase());
                                addOpCodes("8D");
                                addOpCodes(Integer.toHexString(heapCount).toUpperCase());
                                addOpCodes("00");
                                heapCount--;
                                //System.out.println(currentString.charAt(x) + " is our currentString"); //test
                            }

                            addOpCodes("A9");
                            addOpCodes(Integer.toHexString(heapCount + 1).toUpperCase());
                            addOpCodes("8D");
                            addOpCodes(stackList.get(stackList.size()-1).substring(0, 2));
                            addOpCodes("00");

                            break;
                        }

                        else if (symbolOp.get(j).name == "bool" && symbolOp.get(j).symbolType.equals(genTable.get(i))) {
                            //System.out.println("boolean is supposed to be here");
                            i++;

                            addOpCodes("A9");
                            
                            if(genTable.get(i).equals("[true]")) {
                                //System.out.println("true is supposed to go here");
                                addOpCodes("01");
                            }
                            else {
                                //System.out.println("false is supposed to go here");
                                addOpCodes("00");
                            }

                            addOpCodes("8D");
                            addOpCodes(stackList.get(stackList.size()-1).substring(0, 2));
                            addOpCodes("00");
                        }
                    }
                    

                break;
                case "Variable Dec":
                    i = i + 2; //Can skip the next two tokens since we can just add the value directly to memory now
                    System.out.println("Vardecl");

                    addOpCodes("A9");
                    //System.out.println("T" + Integer.toString(declCounter));
                    addOpCodes("00");
                    addOpCodes("8D");
                    addOpCodes("T" + Integer.toString(declCounter)); //temp value to store in memory
                    //System.out.println("whats the declcounter??? " + declCounter);
                    addOpCodes("00");
                    stackList.add("T" + Integer.toString(declCounter) + "XX");
                    declCounter++;
                    
                break;
                case "Print":
                    System.out.println("Print");
                    i++;

                    //System.out.println(genTable.get(i).substring(0, 1));

                    //Substring is a very helpful tool to grab our next token without brackets, free of charge
                    if(genTable.get(i).substring(0, 1).equals("(")) {
                        //System.out.println("this is where strings should go");

                        String currentString = genTable.get(i).substring(1, genTable.get(i).length() - 1);
                        stackList.add("T" + Integer.toString(declCounter) + "XX");

                        addOpCodes("A9");
                        addOpCodes("00");
                        addOpCodes("8D");
                        addOpCodes(Integer.toHexString(heapCount).toUpperCase());
                        addOpCodes("00");
                        heapCount--; //Shift to a new heap variable at the end

                        //Straight carbon copy of the loop from Assign, it's stupid but it works
                        for (int y = 0; y < currentString.length(); y++) {
                            addOpCodes("A9");
                            addOpCodes(Integer.toHexString((int)currentString.charAt(y)).toUpperCase());
                            addOpCodes("8D");
                            addOpCodes(Integer.toHexString(heapCount).toUpperCase());
                            addOpCodes("00");
                            heapCount--; 
                        }

                        addOpCodes("A9");
                        addOpCodes(Integer.toHexString(heapCount + 1).toUpperCase());
                        addOpCodes("8D");
                        addOpCodes("T" + Integer.toString(declCounter).toUpperCase());
                        addOpCodes("00");
                        addOpCodes("A2");
                        addOpCodes("02");
                        addOpCodes("AC");
                        addOpCodes("T" + Integer.toString(declCounter).toUpperCase());
                        addOpCodes("00");
                        addOpCodes("FF");
                        declCounter++;


                    }
                    //If our print statement is only one char long, then we've got an ID on our hands
                    //No need to double check our type/syntax since our previous parts of the compiler have that taken care of
                    else if (genTable.get(i).length() < 2) {
                        //System.out.println("This is where variables go");

                        addOpCodes("AC");
                        //System.out.println(stackList);
                        addOpCodes(stackList.get(stackList.size()-1).substring(0, 2));
                        addOpCodes("00");
                        addOpCodes("A2");

                        for(int j = 0; j < symbolOp.size(); j++) { //expand this
                            //Checking for an int with exact name and type as our current symbol
                            if (symbolOp.get(j).name.equals("int") && symbolOp.get(j).symbolType.equals(genTable.get(i))) {
                                //System.out.println("this int goes hereeeeee");
                                addOpCodes("01");

                            }

                            //Checking for a boolean type
                            else if (symbolOp.get(j).name.equals("bool") && symbolOp.get(j).symbolType.equals(genTable.get(i))) {
                                //System.out.println("this boolean goes hereeeeee");
                                addOpCodes("01");
                            }

                            //Lastly, check for a string type
                            else if (symbolOp.get(j).name.equals("string") && symbolOp.get(j).symbolType.equals(genTable.get(i))) {
                                //System.out.println("this string goes hereeeeee");
                                addOpCodes("02");
                            }
                        }
                        addOpCodes("FF");
                    }

                    //Checking if we want to print a boolean value instead
                    //I should really remove the brackets on these but I guess it's fine for now....
                    else if(genTable.get(i).equals("[true]") || genTable.get(i).equals("[false]")) {
                        //If the value is true
                        if(genTable.get(i).equals("[true]")) {
                            //System.out.println("print true");
                            addOpCodes("A2");
                            addOpCodes("01");
                            addOpCodes("A0");
                            //Set the value to 1 for true
                            addOpCodes("01");
                            addOpCodes("FF");
                        }
                        //else it's false, change the loaded variable
                        else {
                            //System.out.println("print false");
                            addOpCodes("A2");
                            addOpCodes("01");
                            addOpCodes("A0");
                            //Set the value to 0 for false
                            addOpCodes("00");
                            addOpCodes("FF");
                        }
                    }

                    else {
                        //More nothing....   
                    }

                break;

                //If statements - we're gonna take the long way around with this....
                case "If":
                    System.out.println("Ifstatement");
                    i++;
                    //System.out.println("next if is " + genTable.get(i).substring(0, 1));

                    //Sets our boolean values to false, so we don't accidentally trigger a loop
                    stopAddingFirst = false;
                    stopAddingSecond = false;
                    notEquals = false;

                    //While our first loop variable is active, search for and add tokens we find into memory
                    while(stopAddingFirst == false) {
                        addOpCodes("A9");
                        addOpCodes("00");
                        addOpCodes("8D");
                        addOpCodes("T0");
                        addOpCodes("00");

                        //Matches variables
                        if(Pattern.matches("[a-z]", genTable.get(i).substring(0, 1))) {
                            addOpCodes("AD");
                            addOpCodes(stackList.get(stackList.size()-1).substring(0, 2));
                            addOpCodes("00");
                            addOpCodes("6D");
                            addOpCodes("T0");
                            addOpCodes("00");
                            addOpCodes("8D");
                            addOpCodes("T0");
                            addOpCodes("00");
                            addOpCodes("8D");
                            addOpCodes("T0");
                            addOpCodes("00");
                        }

                        //Matches ints
                        else if (Pattern.matches("[0-9]", genTable.get(i).substring(1, 2))) {
                            addOpCodes("A9");
                            addOpCodes("0" + Integer.toHexString(Integer.valueOf(genTable.get(i).substring(1, 2))));
                            addOpCodes("6D");
                            addOpCodes("T0");
                            addOpCodes("00");
                            addOpCodes("8D");
                            addOpCodes("T0");
                            addOpCodes("00");
                        }

                        else {
                            //Do nothing, only way we should be getting here is if we have an IntOp or [+] token

                        }

                        //If we've reached the end of our loop (when the token size > 3, usually meaning there's a close block up ahead), then break out of the loop
                        if(!genTable.get(i + 1).equals("==") && !genTable.get(i + 1).equals("!=") && !genTable.get(i + 1).equals("IntOp") && genTable.get(i + 1).length() > 3) {
                            stopAddingFirst = true;
                        }

                        //If we've reached our boolean statement, move on to the second loop
                        else if(genTable.get(i + 1).equals("==") || genTable.get(i + 1).equals("!=")) {
                            if(genTable.get(i + 1).equals("!=")) {
                                notEquals = true;
                            }
                            i = i + 2; //Skip over the boolean token
                            addOpCodes("A9");
                            addOpCodes("00");
                            addOpCodes("8D");
                            addOpCodes("T1"); //Increment our temp values a bit to accommodate for our new half of the operand
                            addOpCodes("00");

                            //This loop handles the second half of the expression
                            //I probably could have combined both loops into one thing, but whatever....
                            while(stopAddingSecond == false) {

                                //System.out.println("current token is" + genTable.get(i));

                                if(genTable.get(i).equals("[true]")) {
                                    //Stores our true boolean value
                                    addOpCodes("A9");
                                    addOpCodes("01");
                                    addOpCodes("6D");
                                    addOpCodes("T1");
                                    addOpCodes("00");
                                    addOpCodes("8D");
                                    addOpCodes("T1");
                                    addOpCodes("00");
                                }
                                else if(genTable.get(i).equals("[false]")) {
                                    //Stores our false boolean value
                                    //System.out.println("this is false, you're doing it right");
                                    addOpCodes("A9");
                                    addOpCodes("00");
                                    addOpCodes("6D");
                                    addOpCodes("T1");
                                    addOpCodes("00");
                                    addOpCodes("8D");
                                    addOpCodes("T1");
                                    addOpCodes("00");
                                }
                                else if(Pattern.matches("[a-z]", genTable.get(i).substring(0, 1))) {
                                    //System.out.println("this is for letters");
                                    addOpCodes("AD");
                                    addOpCodes(stackList.get(stackList.size()-1).substring(0, 2));
                                    addOpCodes("00");
                                    addOpCodes("6D");
                                    addOpCodes("T1");
                                    addOpCodes("00");
                                    addOpCodes("8D");
                                    addOpCodes("T1");
                                    addOpCodes("00");
                                }
                                else if (Pattern.matches("[0-9]", genTable.get(i).substring(1, 2))) {
                                    //Processes numbers
                                    //System.out.println("this is for numbers");
                                    addOpCodes("A9");
                                    addOpCodes("0" + Integer.toHexString(Integer.valueOf(genTable.get(i).substring(1, 2))));
                                    addOpCodes("6D");
                                    addOpCodes("T1");
                                    addOpCodes("00");
                                    addOpCodes("8D");
                                    addOpCodes("T1");
                                    addOpCodes("00");
                                }
                                else {
                                    //Do nothing, only way we should be getting here is if we run into a close block token

                                }

                                //If we run into an ending token (NOT intop though), then we break out of the second loop
                                if((genTable.get(i + 1).length() > 3) && (!genTable.get(i + 1).equals("IntOp")))  {
                                    stopAddingSecond = true;
                                }
                                
                                //Else we're not done, keep looping
                                else {
                                    i++;
                                }
                            }
                            stopAddingFirst = true;
                        }

                        else {
                            i++;
                            //System.out.println("loop again");
                        }
                    }
                    addOpCodes("AE");
                    addOpCodes("T0");
                    addOpCodes("00");
                    addOpCodes("EC");
                    addOpCodes("T1");
                    addOpCodes("00");

                    //Thanks ChatGPT for giving me a little bit of an idea how to put this in (opcode wise)
                    if(notEquals == true) {
                        addOpCodes("A9");
                        addOpCodes("00");
                        addOpCodes("D0");
                        addOpCodes("02");
                        addOpCodes("A9");
                        addOpCodes("01");
                        addOpCodes("A2");
                        addOpCodes("00");
                        addOpCodes("8D");
                        addOpCodes("T0");
                        addOpCodes("00");
                        addOpCodes("EC");
                        addOpCodes("T0");
                        addOpCodes("00");
                    }
                    addOpCodes("D0"); //Branch instruction - here we'll initialize the jump value for this loop
                    addOpCodes("J" + Integer.toString(branchNum)); //Temporary jump value to the code list
                    jump.add("J" + opCounter);
                    
                    //Words cannot describe how terrible this looks, I really should have just made a separate class for this
                    pastopCount = opCounter;
                    weAreIffing = true;
                    branchNum++;

                break;

                //Once again, a huge thank you to the folks at StackOverflow for the help on program structure:
                //https://stackoverflow.com/questions/60924068/converting-pseudo-code-while-loop-in-to-java
                case "While":
                    System.out.println("Whilestatement");
                    i++;
                    //System.out.println("next if is " + genTable.get(i).substring(0, 1));

                    jumpVal = opCounter;

                    //Sets our boolean values to false, so we don't accidentally trigger a loop
                    stopAddingFirst = false;
                    stopAddingSecond = false;
                    notEquals = false;

                    addOpCodes("A9");
                    addOpCodes("00");
                    addOpCodes("8D");
                    addOpCodes("T0");
                    addOpCodes("00");

                    while(stopAddingFirst == false) {
                            
                        //Matches variables
                        if(Pattern.matches("[a-z]", genTable.get(i).substring(0, 1))) {
                            addOpCodes("AD");
                            addOpCodes(stackList.get(stackList.size()-1).substring(0, 2));
                            addOpCodes("00");
                            addOpCodes("6D");
                            addOpCodes("T0");
                            addOpCodes("00");
                            addOpCodes("8D");
                            addOpCodes("T0");
                            addOpCodes("00");
                            addOpCodes("8D");
                            addOpCodes("T0");
                            addOpCodes("00");
                        }

                        //Matches ints
                        else if (Pattern.matches("[0-9]", genTable.get(i).substring(1, 2))) {
                            addOpCodes("A9");
                            addOpCodes("0" + Integer.toHexString(Integer.valueOf(genTable.get(i).substring(1, 2))));
                            addOpCodes("6D");
                            addOpCodes("T0");
                            addOpCodes("00");
                            addOpCodes("8D");
                            addOpCodes("T0");
                            addOpCodes("00");
                        }
                        
                    else {
                        //Do nothing, only way we should be getting here is if we have an IntOp or [+] token

                    }

                    //If we've reached the end of our loop (when the token size > 3, usually meaning there's a close block token up ahead), then break out of the loop
                    if(!genTable.get(i + 1).equals("==") && !genTable.get(i + 1).equals("!=") && !genTable.get(i + 1).equals("IntOp") && genTable.get(i + 1).length() > 3) {
                        stopAddingFirst = true;
                    }

                    //If we've reached our boolean statement, move on to the second loop
                    else if(genTable.get(i + 1).equals("==") || genTable.get(i + 1).equals("!=")) {
                        if(genTable.get(i + 1).equals("!=")) {
                            notEquals = true;
                        }

                        while(stopAddingSecond == false) {
                            if(genTable.get(i).equals("[true]")) {
                                //Stores our true boolean value
                                addOpCodes("A9");
                                addOpCodes("01");
                                addOpCodes("6D");
                                addOpCodes("T1");
                                addOpCodes("00");
                                addOpCodes("8D");
                                addOpCodes("T1");
                                addOpCodes("00");
                            }
                            else if(genTable.get(i).equals("[false]")) {
                                //Stores our false boolean value
                                //System.out.println("this is false, you're doing it right");
                                addOpCodes("A9");
                                addOpCodes("00");
                                addOpCodes("6D");
                                addOpCodes("T1");
                                addOpCodes("00");
                                addOpCodes("8D");
                                addOpCodes("T1");
                                addOpCodes("00");
                            }
                            else if(Pattern.matches("[a-z]", genTable.get(i).substring(0, 1))) {
                                //System.out.println("this is for letters");
                                addOpCodes("AD");
                                addOpCodes(stackList.get(stackList.size()-1).substring(0, 2));
                                addOpCodes("00");
                                addOpCodes("6D");
                                addOpCodes("T1");
                                addOpCodes("00");
                                addOpCodes("8D");
                                addOpCodes("T1");
                                addOpCodes("00");
                            }
                            else if (Pattern.matches("[0-9]", genTable.get(i).substring(1, 2))) {
                                //Processes numbers
                                //System.out.println("this is for numbers");
                                addOpCodes("A9");
                                addOpCodes("0" + Integer.toHexString(Integer.valueOf(genTable.get(i).substring(1, 2))));
                                addOpCodes("6D");
                                addOpCodes("T1");
                                addOpCodes("00");
                                addOpCodes("8D");
                                addOpCodes("T1");
                                addOpCodes("00");
                            }

                            else {
                                //Do nothing, only way we should be getting here is if we run into a close block token (again)

                            }

                            //If we run into an ending token (NOT intop though), then we break out of the second loop
                            if((genTable.get(i + 1).length() > 3) && (!genTable.get(i + 1).equals("IntOp")))  {
                                stopAddingSecond = true;
                            }
                            
                            //Else we're not done, keep looping
                            else {
                                i++;
                            }
                        }
                        stopAddingFirst = true;

                    }
                    else {
                        i++;
                        //System.out.println("loop again");
                    }

                }
                addOpCodes("AE");
                addOpCodes("T0");
                addOpCodes("00");
                addOpCodes("EC");
                addOpCodes("T1");
                addOpCodes("00");

                //Same thingy here from If statement
                if(notEquals == true) {
                    addOpCodes("A9");
                    addOpCodes("00");
                    addOpCodes("D0");
                    addOpCodes("02");
                    addOpCodes("A9");
                    addOpCodes("01");
                    addOpCodes("A2");
                    addOpCodes("00");
                    addOpCodes("8D");
                    addOpCodes("T0");
                    addOpCodes("00");
                    addOpCodes("EC");
                    addOpCodes("T0");
                    addOpCodes("00");
                }

                addOpCodes("DO"); //Branch instruction - here we'll initialize the jump value for this loop
                addOpCodes("J" + Integer.toString(branchNum)); //Add a TEMPORARY jump value to the op codes for us to sort out later
                jump.add("J" + Integer.toHexString(opCounter)); //Adds the counter value we want to branch to in our jump table to keep track of
                branchNum++;
                areWeJumping = true; //Set our jumping boolean to true, since we need to jump if the while loop isn't completed
                
                break;
                default:
                    //testing to make sure I didn't break anything :P
                    System.out.println("default response is " + genTable.get(i));
                break;
            }
        }
    }

    //Adds our opcodes to the list
    public void addOpCodes(String newCode) {
        opCodeList.add(opCounter, newCode);
        opCounter++; //increment counter to let us know where we are after the addition
        
        //System.out.println("CURRENT OPCODE LIST IS " + opCodeList); //testing
    }

    public void printCode() {
        System.out.println();
        System.out.println("CODEGEN: Printing Op Codes:");

        //Print our opcodes as we've initialized them
        for (int i = 0; i < opCodeList.size(); i++) {
            System.out.print(" " + opCodeList.get(i));
        }
    }

    //ChatGPT'd this - was easy printing stack but I asked how to modify the list for each pointer value
    //Modifies our temp variables so we store them in different values in our list depending on their assignment
    public void handleStack() {
        for(int y = 0; y < stackList.size(); y++) {
            for(int z = 0; z < opCodeList.size(); z++) {//If it appears at all
                if(opCodeList.get(z).equals("T" + Integer.toString(y))) {
                    opCodeList.set(z, Integer.toHexString(opCounter).toUpperCase());
                }
            }
            opCounter++;
        }
    }

    public void handleJump() {
        //Initializes jump values for our while loops, a lot like handleStack
        for(int k = 0; k < jump.size(); k++) {
            for(int l = 0; l < opCodeList.size(); l++) {
                if(opCodeList.get(l).equals("J" + Integer.toString(k))) {//J is used as a defining tag for our jumptable parsing
                    //System.out.println(jump.get(k).substring(1, 3));
                    opCodeList.set(l, Integer.toHexString(Integer.valueOf(jump.get(k).substring(1, 3)))); //Set our new jump value
                    if(opCodeList.get(l).length() < 2) {
                        //I'm sure there's a less egregious way to do this
                        opCodeList.set(l, "0" + Integer.toHexString(Integer.valueOf(jump.get(k).substring(1, 3))));
                    }
                    //System.out.println(opCodeList.get(l));
                }
            }
        }
    }
}