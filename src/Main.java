import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class Main {

    public static void main(String[] args) {
        StringBuilder sb = new StringBuilder();

        try {
            File inputfile = new File(".//src//input.txt");
            FileReader fileReader = new FileReader(inputfile);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String line = null;
            while((line = bufferedReader.readLine()) != null)
                sb.append(line + "\n");

            bufferedReader.close();
        }
        catch(Exception e){
            System.out.println(e);
        }

        // input에 input.txt 내용이 모두 들어있음
        String input = sb.toString();

        MIME mime = MIME.divideToHeaderBody(input);
        mime.setHeaderFields();
        MIME.parseMIME(mime);

        mime.printMIME(0);
    }
}
