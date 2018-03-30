package com.mifan.user.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;

import com.mifan.user.service.impl.UsersServiceImpl;
import com.mifan.user.type.SpecType;

public class test implements Runnable{
    private UsersServiceImpl service;
    public static void main(String[] args) throws InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(80);
        UsersServiceImpl im = new UsersServiceImpl();
        for(int i = 0; i < 10000; i++){
            pool.submit(new test(im));
            System.out.print(i);
            Thread.sleep(100);
        }
    }
    
    

    public test(UsersServiceImpl service) {
        super();
        this.service = service;
    }



    @Override
    public void run() {
//        boolean flag = service.sendForMobile("15210345569","1234",SpecType.WEB_REGISTER);
//        System.out.println(flag+",");
        /*HttpPost post = new HttpPost("https://www.baidu.com");
        post.addHeader("Accept", "text/html");  
        post.addHeader("Accept-Charset", "utf-8");  
        post.addHeader("Accept-Encoding", "gzip");  
        post.addHeader("Accept-Language", "en-US,en");  
        post.addHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.22 (KHTML, like Gecko) Chrome/25.0.1364.160 Safari/537.22");  
        CloseableHttpClient httpClient = HttpClientUtil.getHttpClient();
        CloseableHttpResponse httpResponse = null;
        try {
                httpResponse = httpClient.execute(post);
                StatusLine status = httpResponse.getStatusLine();
                InputStream in=httpResponse.getEntity().getContent();
                in.close();
            System.out.println(status.getStatusCode());
        } catch (ClientProtocolException e) {
            System.out.println("111111111111111111111111");
        } catch (IOException e) {
            System.out.println("222222222222222222222222");
        }catch(Exception e){
            System.out.println("333333333333333333333333");
        }finally{
            try {
                httpResponse.close();
            } catch (IOException e) {
                System.out.println("4444444444444444");
                e.printStackTrace();
            }
        }*/
    }
    
}
