/**
 *    用于微信支付红包
 *
 */
package com.mifan.user.utils.wxpay;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * @author ZYW
 *
 */
public class HttpClientCertUtil implements EnvironmentAware{
    
    private static CloseableHttpClient httpClient;
    
    private static final String PREFIX = "moon.data.wxpay";
    
    private static String mchID = "1488379182";//商户号
    
    
    @Override
    public void setEnvironment(Environment environment) {
        mchID = environment.getProperty(PREFIX+"mchID");
    }
    
    static {
        httpClient = newInstance();
    }
    
    public static String execute(HttpUriRequest request) {
        CloseableHttpClient httpClient = getHttpClient();
        CloseableHttpResponse httpResponse = null;
        try {
            httpResponse = httpClient.execute(request);
            HttpEntity entity = httpResponse.getEntity();
            if(entity != null) {//注意：这里默认只有entity不为空的情况下才请求成功，因为我们访问的服务器都有返回信息。但是有些服务器没有entity也是执行成功，所以以后如果要用这个方法，请注意
                StatusLine status = httpResponse.getStatusLine();
                if (!(status.getStatusCode() >= 200 && status.getStatusCode() < 300)) {
                    EntityUtils.consumeQuietly(entity);
                    return null;
                }
                String json = EntityUtils.toString(entity, "UTF-8");
                return json; 
            }else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (httpResponse != null) {
                    httpResponse.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static CloseableHttpClient getHttpClient(){
        return httpClient;
    }
    private static CloseableHttpClient newInstance() {
        // 创建requestConfig
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(5000)//连接超时时间
                .setConnectionRequestTimeout(5000)//从池中获取连接超时时间
                .setSocketTimeout(10000)//读取超时时间（等待数据超时时间）
                .build();
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(custom());
        cm.setMaxTotal(100);//设置连接池最大数量 
        cm.setDefaultMaxPerRoute(100);//设置单个路由最大连接数量(如果值访问同一个连接，则可设置为与maxtotal相同)
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(cm)
                                                    .setDefaultRequestConfig(requestConfig)
                                                    .setRetryHandler(new DefaultHttpRequestRetryHandler(0, false))//设置重试次数为0
                                                    .build();
        return httpClient;
    }
    private static Registry<ConnectionSocketFactory> custom(){
        SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(
                sslContext(),
                new String[]{"TLSv1"},
                null,
                new DefaultHostnameVerifier());
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                .register("https", sslConnectionSocketFactory)
                .build();
        return socketFactoryRegistry;
    }
    
    private static SSLContext sslContext(){
        SSLContext sc = null;
//        FileInputStream instream = null;
        InputStream instream = null;
        KeyStore ks = null;
        try {
            char[] password = mchID.toCharArray();
            ks = KeyStore.getInstance("PKCS12");
            Resource resource = new ClassPathResource("apiclient_cert.p12");
            instream = resource.getInputStream();
            ks.load(instream, password);
         // 实例化密钥库 & 初始化密钥工厂
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(ks, password);
         // 创建 SSLContext
            sc = SSLContext.getInstance("TLS");
            sc.init(kmf.getKeyManagers(), null, new SecureRandom());
        }catch(Exception e) {
            e.printStackTrace();
        }finally {
            try {
                instream.close();
            } catch (IOException e) {
            }
        }
        return sc;
    }
}
