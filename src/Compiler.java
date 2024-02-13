//Andrew Hatch - CMPT432N Design of Compilers
//Prof. Labouseur

public class Compiler {
    
    public static void main(String[] args) {
        System.out.println("Beginning Compiler");

        Lexer lexer = new Lexer();
        lexer.main(args[0]); //Doesn't work unless calling directly to the main Lexer method's string
    }
}
