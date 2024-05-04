//placeholder stuff tee hee
//obviously non functional right now

import java.util.ArrayList;

public class CodeGen {
    
    ArrayList<String> genTable; //import this from Semantic
    ArrayList<Symbol> symbolList; //project said to include this idk what to do with it yet
    ArrayList<String> opCodeList = new ArrayList<>(); //should be under 256, else throw error

    int newScope; //Copy of scope, might need this
    //Pass existing scope over? or use it from symbol class
    int opCounter;
    int declCounter; //Used for vardecl so we can store each variable in a unique address


    public void main(ArrayList<String> list) {
        //System.out.println("IOU one Code Gen");

        genTable = list;

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

                    newScope++;

                break;
                case "Close Block":
                    System.out.println("closeblock");

                newScope--;

                break;
                case "Assign":
                    System.out.println("Assign statement");



                break;
                case "Variable Dec":
                    i = i + 2; //need to skip the next two since we can just add the value directly to memory now
                    System.out.println("Vardecl");

                    addOpCodes("A9");
                    addOpCodes("00");
                    addOpCodes("8D");
                    addOpCodes("T" + Integer.toString(declCounter)); //temp, add actual values to this eventually
                    addOpCodes("XX");
                    declCounter++; //Increment our counter to a fresh address
                    
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
        System.out.println("CURRENT OPCODE LIST IS " + opCodeList); //testing
        opCounter++;
    }

    public void printCode() {
        System.out.println();
        System.out.println("CODEGEN: Printing Op Codes:");

        //Print our opcodes as we've initialized them
        for (int i = 0; i < opCodeList.size(); i++) {
            System.out.println(opCodeList.get(i));
        }
    }

}