package cn.malgo.annotation.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import lombok.extern.slf4j.Slf4j;
/**
 * TODO post
 * @author huangjq
   2018年9月5日
 */
@Slf4j
public class PostUtil {

	public static String doPost(String path,String param) {
		//String strURL = "http://127.0.0.1:8080/api/user/login";
        OutputStreamWriter out = null;
        InputStream is = null;
        try {
            URL url = new URL(path);// 创建连接
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestMethod("POST"); // 设置请求方式
            connection.setRequestProperty("Accept", "application/json"); // 设置接收数据的格式
            connection.setRequestProperty("Content-Type", "application/json"); // 设置发送数据的格式
            out = new OutputStreamWriter(connection.getOutputStream(), "UTF-8"); // utf-8编码
            //String str = "{\"nickname\":\"malgo\",\"password\":\"765f950a918d64fd2bfb53be3c59f378\"}";
            out.append(param);
            out.flush();
            out.close();
            connection.connect();
            // 读取响应
            is = connection.getInputStream();
            BufferedReader  br = new BufferedReader(new InputStreamReader(is, "utf-8"));
            String line;
            StringBuilder result = new StringBuilder();
            while ((line = br.readLine()) != null) {
            	result.append(line);
            }
            return result.toString();
        } catch (IOException e) {
        	log.error("[PostUtil]error {},{}", path,param,e);
            e.printStackTrace();
            return "";
        } finally {
            try {
                is.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
	}
}
