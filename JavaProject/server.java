import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.net.InetSocketAddress;

public class server {
    public static void main(String[] args) throws IOException {
        // 웹 서버 생성 (포트 8000으로 설정)
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);

        // 요청 핸들러 등록
        server.createContext("/", new MyHandler());

        // 서버 시작
        server.start();
        System.out.println("웹 서버가 시작되었습니다. http://localhost:8000 에 접속하세요.");
    }

    // 요청 핸들러 클래스
    static class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            String requestedUrl = t.getRequestURI().toString();
            String filename = requestedUrl.equals("/") ? "login.html" : requestedUrl.substring(1);

            // 파일을 읽어서 전송
            Path path = Paths.get(filename);
            if(Files.exists(path) && Files.isRegularFile(path)){
                byte[] data = Files.readAllBytes(path);
                t.sendResponseHeaders(200, data.length);
                OutputStream os = t.getResponseBody();
                os.write(data);
                os.close();
            } else {
                // 파일이 존재하지 않거나 디렉토리일 경우, 404 Not Found 응답 보냄
                String response = "404 Not Found";
                t.sendResponseHeaders(404, response.length());
                OutputStream os = t.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }
    }
}
