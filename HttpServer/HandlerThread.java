package HttpServer;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.net.URI;

import static HttpServer.SimpleHttpServer.logger1;

/**
 * Created by zhangwj on 2017/3/16.
 */
public class HandlerThread extends Thread {
    private HttpExchange t;
    public  HandlerThread(HttpExchange request)
    {
        this.t = request;
    }

    public  void run ()
    {
        //获取请求的Uri ?后面的参数
        URI requestedUri = t.getRequestURI();
        String query = requestedUri.getRawQuery();
        logger1.info("get a request");

        //对文件路径进行处理 将\替换为/
        query = query.replace("\\","/" );
        //将空格字符 20%替换为space
        query = query.replace("%20"," " );

        try {
            query = java.net.URLDecoder.decode(query, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        logger1.info("After Processing query is " + query.toString());
        File file = new File (query.toString());
        Headers h = t.getResponseHeaders();
        // h.add("Content-Type", "arraybuffer");
        h.add( "Access-Control-Allow-Origin", "*");
        if(file.exists()) {
            byte[] bytearray = new byte[(int) file.length()];
            FileInputStream fis = null;
            try {

                fis = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            BufferedInputStream bis = new BufferedInputStream(fis);

            try {
                bis.read(bytearray, 0, bytearray.length);
                t.sendResponseHeaders(200, file.length());
                OutputStream os = t.getResponseBody();
                os.write(bytearray, 0, bytearray.length);
                os.close();
                bytearray = null;
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        else
        {
            logger1.error("找不到文件！");
            String response = "<h1>Server start success if you see this message</h1>" + "<h1>Port: " + "1111 " + query+ " No File</h1>";
            try {
                t.sendResponseHeaders(200, response.length());
                OutputStream os = t.getResponseBody();
                os.write(response.getBytes());
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

}
