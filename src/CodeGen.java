//placeholder stuff tee hee
//obviously non functional right now

import java.util.ArrayList;

public class CodeGen {
    
    ArrayList<String> genTable; //import this from Semantic
    ArrayList<Symbol> symbolOp; //project said to include this idk what to do with it yet
    ArrayList<String> opCodeList = new ArrayList<>(); //should be under 256, else throw error

    int newScope; //Copy of scope, might need this
    //Pass existing scope over? or use it from symbol class
    int opCounter;
    int declCounter; //Used for vardecl so we can store each variable in a unique address 
    int heapCount;


    public void main(ArrayList<String> list, ArrayList<Symbol> symbolList) {
        //System.out.println("IOU one Code Gen");

        genTable = list;
        symbolOp = symbolList;

        newScope = -1;
        heapCount = 255;

        System.out.println(genTable); //testing

        genCode();
        printCode();
    }

    public void genCode() {
        //System.out.println("genCode");

        //Loop through imported AST, check to see if our AST tokens match
        //Should match:
        //Assign
        //If
        //while
        //variable decl
        //strings
        //block statements
        //boolops
        //bool vals
        //something else

        //Rudimentary loop to parse through our AST we passed from semantic, will get around to including all tokens
        //System.out.println("IOU one genCode");
        for(int i = 0; i < genTable.size(); i++) {
            
            switch(genTable.get(i)) {
                case "Open Block":
                    System.out.println("oblock");

                    newScope++; //increment scope when we have a new block like usual
                    //System.out.println("newScope is " + newScope); //testing

                break;
                case "Close Block":
                    System.out.println("closeblock");

                    newScope--;
                    //System.out.println("newScope is " + newScope); //testing

                break;
                case "Assign":

                    i++;

                    System.out.println("Assign statement");

                    System.out.println("current token is " + genTable.get(i));

                    //check for int or string
                    Symbol tempSymbol;

                    for(int j = 0; j < symbolOp.size(); j++) {
                        //System.out.println(symbolOp.get(j).name);
                        //System.out.println(symbolOp.get(j).symbolType);
                        //System.out.println(newScope);
                        if (symbolOp.get(j).name == "int" && symbolOp.get(j).symbolType.equals(genTable.get(i))) {
                            //System.out.println("yayy!!!" + symbolOp.get(j).scope);
                            tempSymbol = symbolOp.get(j);
                            i++;
                            System.out.println("currenterrrr token is " + genTable.get(i));
                            break;
                        }


                        else if (symbolOp.get(j).name == "string" && symbolOp.get(j).symbolType.equals(genTable.get(i))) {
                            //System.out.println("nooooo!!!!" + symbolOp.get(j).scope);
                            //System.out.println(symbolOp.get(j).scope);
                            tempSymbol = symbolOp.get(j);
                            System.out.println("current symbol is " + tempSymbol.name);
                            i++;
                            System.out.println("currenterrrr token is " + genTable.get(i));

                            String currentString = genTable.get(i).substring(1, genTable.get(i).length()-1);

                            addOpCodes("A9");
                            addOpCodes("00");
                            addOpCodes("8D");
                            addOpCodes(Integer.toHexString(heapCount).toUpperCase());
                            heapCount--;
                            addOpCodes("00");
                            
                            for (int x = 0; x < currentString.length(); x++) {
                                addOpCodes("A9");
                                addOpCodes(Integer.toHexString((int)currentString.charAt(x)));
                                addOpCodes("8D");
                                addOpCodes(Integer.toHexString(heapCount).toUpperCase());
                                heapCount--;
                                addOpCodes("00");
                                System.out.println(currentString.charAt(x) + " is our currentString");
                            }

                            break;
                        }
                    }

                    /*if(genTable.get(i).length() > 2) {
                        System.out.println("example test " + symbolOp.get(2).symbolType);
                        System.out.println("example test " + symbolOp.get(2).name);
                        System.out.println("example test " + symbolOp.get(2).scope);
                        System.out.println("example test " + genTable.get(i));
                    }*/
                    
                    


                break;
                case "Variable Dec":
                    i = i + 2; //need to skip the next two since we can just add the value directly to memory now
                    System.out.println("Vardecl");

                    addOpCodes("A9");
                    addOpCodes("00");
                    addOpCodes("8D");
                    addOpCodes("T" + Integer.toString(declCounter)); //temp value to store in memory
                    addOpCodes("XX");
                    declCounter++;
                    
                break;
                case "Print":
                    System.out.println("Print");



                break;
                case "If":
                    System.out.println("Ifstatement");



                break;
                case "While":
                    System.out.println("Whilestatement");



                break;
                default:
                    //testing to make sure I didn't break anything :P
                    System.out.println("default response is " + genTable.get(i));
                break;

            }
        }
    }

    public void addOpCodes(String newCode) {
        opCodeList.add(opCounter, newCode);
        opCounter++;
        
        //System.out.println("CURRENT OPCODE LIST IS " + opCodeList); //testing
    }

    public void printCode() {
        System.out.println();
        System.out.println("CODEGEN: Printing Op Codes:");
        int tempcount = 1;

        //Print our opcodes as we've initialized them
        for (int i = 0; i < opCodeList.size(); i++) {
            System.out.println(tempcount + ". " + opCodeList.get(i));
            tempcount++;
        }
    }

}