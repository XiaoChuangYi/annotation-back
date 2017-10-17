package cn.malgo.annotation.common.util.http;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

/**
 * Created by 张钟 on 2017/9/27.
 */
public class XMLClient {

    // 使用POST方法发送XML数据  
    public static String sendXMLDataByPost(String url, String xmlData) throws Exception {
        HttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(url);
        List<BasicNameValuePair> parameters = new ArrayList<>();
        parameters.add(new BasicNameValuePair("xml", xmlData));
        post.setEntity(new UrlEncodedFormEntity(parameters, "UTF-8"));
        HttpResponse response = client.execute(post);
        HttpEntity entity = response.getEntity();
        String result = EntityUtils.toString(entity, "UTF-8");
        return result;
    }

}
