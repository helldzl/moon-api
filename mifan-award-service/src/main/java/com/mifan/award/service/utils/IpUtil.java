package com.mifan.award.service.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;

public class IpUtil {
    //通过ip 得到国家，省份，区域，城市
    /*public String getAddressByIp(String ip) throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("http://ip.taobao.com/service/getIpInfo.php?ip="+ip);
        CloseableHttpResponse response = httpclient.execute(httpGet);
        try {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                return EntityUtils.toString(entity, "UTF-8");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            response.close();
            httpclient.close();
        }
        //System.out.println(response1);
        return null;
    }*/
    private static final Log logger = LogFactory.getLog(IpUtil.class);

    public static String getAddressByIp(String ip){
        String httpUrl = "http://apis.baidu.com/apistore/iplookupservice/iplookup?ip="+ip;
        BufferedReader reader = null;
        String result = null;
        StringBuilder sbf = new StringBuilder();

        try {
            URL url = new URL(httpUrl);
            // 代理配置
            Proxy proxy = new Proxy(Proxy.Type.HTTP,new InetSocketAddress("10.251.25.22", 3128));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection(proxy);

            // 本地配置
            /*
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            */

            connection.setRequestMethod("GET");
            // 填入apikey到HTTP header
            connection.setRequestProperty("apikey",  "d89edb3eb7a9c6bbde8fdbe5b1c0a943");
            connection.connect();
            InputStream is = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String strRead = null;
            while ((strRead = reader.readLine()) != null) {
                sbf.append(strRead);
                sbf.append("\r\n");
            }
            reader.close();
            result = sbf.toString();
        } catch (Exception e) {
            result = "{\"errNum\":0,\"errMsg\":\"success\",\"retData\":{\"ip\":\""
            +ip+"\",\"country\":\"\",\"province\":\"None\",\"city\":\"None\",\"district\":\"None\",\"carrier\":\"\u672a\u77e5\"}}";
            logger.warn("根据ip"+ip+"获取城市，地区，网络运营商出现异常:", e);
    }
        return result;
    }
}
