public class Tester {

    public static void main(String[] args) throws Exception{

        Lexer lexicalAnalyzer = new Lexer();

        if(args.length == 0){
            throw new Exception("Path del file non presente come argomento");
        }

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

