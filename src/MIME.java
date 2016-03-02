import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

/**
 * Created by Ha on 16. 3. 3..
 */
public class MIME {

    protected String header;
    protected String content_type;
    protected String boundary;
    protected String body;
    protected ArrayList<MIME> childMIMEs;

    public MIME() {
        childMIMEs = new ArrayList<MIME>();
    }

    // header / body 로 나뉘고 나서 header 필드들을 파싱하는 함수
    // 필드들 : header, content_type, boundary, body
    public void setHeaderFields(){
        // 한줄씩 읽기위해 BufferedReader 사용
        BufferedReader reader = new BufferedReader(new StringReader(header));

        try {
            header = reader.readLine().split(": ")[1];
            content_type = reader.readLine().split(": ")[1];

            String line = null;
            if((line = reader.readLine()) != null)
                boundary = line.split("=")[1];

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 입력 mime 문자열을 header와 body를 나누어 mime 객체를 반환하는 함수
    public static MIME divideToHeaderBody(String input){
        MIME mime = new MIME();

        // 첫 번째 "\n\n" (줄바꿈 두번) 을 기준으로 header와 body 분리
        int header_end_index = input.indexOf("\n\n");

        mime.header = input.substring(0,header_end_index);
        mime.body = input.substring(header_end_index+2, input.length() - 1);

        return mime;
    }

    public static void parseMIME(MIME mime){
        if(mime.content_type.equals("multipart")) {
            // 인자로 받은 mime의 body를 한줄씩 읽기 위해 BufferedReader 사용
            BufferedReader reader = new BufferedReader(new StringReader(mime.body));


            String line = null;
            try {
                line = reader.readLine();
                // mime의 끝을 알리는 "--" 가 나올때 까지
                while (!(line.endsWith(mime.boundary + "--"))) {
                    if (line.contains("----=" + mime.boundary)) { // startswith 로 해도되지만 일단은 contains

                        StringBuilder sb = new StringBuilder();
                        while (!(line = reader.readLine()).contains("----=" + mime.boundary)) {
                            sb.append(line + "\n");
                        }
                        // sb에 한개의 파트가 들어가 있음.

                        // 새 MIME 객체를 만들고 현재 mime 객체의 childMIMEs 리스트에 추가
                        MIME childMime = MIME.divideToHeaderBody(sb.toString());
                        childMime.setHeaderFields();
                        MIME.parseMIME(childMime);

                        mime.childMIMEs.add(childMime);

                    }
                    // 같은 레벨에 있는 것들 모두 childMIMEs 리스트에 추가
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // mime 출력함수
    public void printMIME(int level){
        println("[HEADER]", level);
        println("key: header    value: " + header, level);

        println("",level);
        println("[CONTENT-TYPE]", level);
        println(content_type, level);

        // 멀티파트인 경우 바운더리 필드값 출력
        if(content_type.equals("multipart")) {
            println("", level);
            println("[BOUNDARY]", level);
            println("---=" + boundary, level);
        }

        println("",level);
        println("[BODY]", level);

        // 멀티파트인 경우 자식 mime 객체들 모두 출력 - 재귀 호출
        if(content_type.equals("multipart")) {
            for (MIME child : childMIMEs) {
                child.printMIME(level + 1);
            }
        }
        else // text 인 경우 body 출력
            println(body + "\n", level + 1);

    }

    // level 은 0부터 시작 - 0은 루트
    // level 별로 "\t" 탭 문자를 넣어서 구분한다.
    public void println(String s, int level){
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < level; i++)
            sb.append("\t\t");

        System.out.println(sb.toString() + s);
    }


}




/*
public MIME parse(String input){
        // 한줄씩 읽기위해 BufferedReader 사용
        BufferedReader reader = new BufferedReader(new StringReader(input));
        String line = null;

        try {

            while((line = reader.readLine())!=null){
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
 */