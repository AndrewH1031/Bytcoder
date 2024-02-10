//package src; - disabled for now

public class Compiler {
    
    public static void main(String[] args) {
        System.out.println("This one's for all da marbles!");

        Lexer lexer = new Lexer();
        lexer.main(args[0]); //Doesn't work unless calling directly to the main Lexer method's string
    }
}
