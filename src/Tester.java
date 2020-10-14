import java.io.FileNotFoundException;
import java.io.IOException;

public class Tester {

    public static void main(String[] args) throws IOException {

        Lexer lexicalAnalyzer = new Lexer();
        String filePath = args[0];

        if (lexicalAnalyzer.initialize(filePath)) {

            Token token;
            try {
                while ((token = lexicalAnalyzer.nextToken()) != null) {
                    System.out.println(token);
                }
                lexicalAnalyzer.printStringTable();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else
            System.out.println("File not found!!");
    }

}

