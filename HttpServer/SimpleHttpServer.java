package HttpServer; /**
 * Created by zhangwj on 2017/3/3.
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;



public class SimpleHttpServer {
    public static  final Logger logger1 = Logger.getLogger(SimpleHttpServer.class.getName());
    private static ExecutorService fiexedThreadPool = Executors.newFixedThreadPool(20);

    public  static void main(String[] args) throws Exception {

        HttpServer server = HttpServer.create(new InetSocketAddress(1233), 0);
        server.createContext("/", new GetHandler());
        server.createContext("/info", new InfoHandler());
        server.createContext("/echoGet", new GetHandler());
        server.createContext("/getImage", new GetHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
        File log4jfile = new File("log4j.properties");
        PropertyConfigurator.configure(log4jfile.getAbsolutePath());

    }



    private static  class  InfoHandler implements HttpHandler {
        public void handle(HttpExchange t) throws IOException {
            String response = "Use /get to download a PDF";
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    static class  GetHandler implements HttpHandler {
        //process get Request
        public void handle(HttpExchange t) throws IOException {
            //多线程处理http请求！
            HandlerThread handlerThread = new HandlerThread(t);
            fiexedThreadPool.execute(handlerThread);
        }
    }
}