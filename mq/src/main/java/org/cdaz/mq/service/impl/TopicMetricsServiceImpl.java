package org.cdaz.mq.service.impl;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.SocketConfig;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.cdaz.mq.config.MqConfig;
import org.cdaz.mq.entity.TopicMetricsResult;
import org.cdaz.mq.service.TopicMetricsService;
import org.cdaz.mq.util.JsonUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

@Service
public class TopicMetricsServiceImpl implements TopicMetricsService {

    private HttpClient client;
    private final String topicMetricsSuffixUrl = "/api/v5/mqtt/topic_metrics";
    private String topicMetricsUrl;

    @Autowired
    private MqConfig mqConfig;

    @PostConstruct
    private void init() {
        BasicCredentialsProvider provider = new BasicCredentialsProvider();
        provider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(mqConfig.getMetricsHttpAuthUsername(), mqConfig.getMetricsHttpAuthPassword()));

        SocketConfig socketConfig = SocketConfig.custom().setSoTimeout(2000).build();
        client = HttpClientBuilder.create().setDefaultCredentialsProvider(provider).setDefaultSocketConfig(socketConfig).build();

        topicMetricsUrl = mqConfig.getMonitorHost() + topicMetricsSuffixUrl;
    }


    @Override
    public boolean createMetrics(String topic) {
        HttpPost httpPost = new HttpPost(topicMetricsUrl);

        // 初始化参数
        StringEntity entity = null;
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("topic", topic);
            entity = new StringEntity(jsonObject.toString());
            entity.setContentEncoding("UTF-8");
            entity.setContentType("application/json");
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return false;
        }

        httpPost.setEntity(entity);
        HttpResponse res = null;
        try {
            res = client.execute(httpPost);
            if (res.getStatusLine().getStatusCode() != 204) {
                res.getEntity().getContent().close();
                return false;
            }
        } catch (IOException e) {
            if (res != null) {
                try {
                    res.getEntity().getContent().close();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
            e.printStackTrace();
            return false;
        }

        return true;
    }

    @Override
    public TopicMetricsResult getMetrics(String topic) {
        HttpGet httpGet = new HttpGet(topicMetricsUrl + "/" + topic);
        TopicMetricsResult result = null;
        try {
            HttpResponse res = client.execute(httpGet);
            if (res.getStatusLine().getStatusCode() != 200) {
                res.getEntity().getContent().close();
                return new TopicMetricsResult();
            }

            result = JsonUtils.fromJson(res.getEntity().getContent(), TopicMetricsResult.class);
            res.getEntity().getContent().close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    @Override
    public TopicMetricsResult[] getAllMetrics() {
        TopicMetricsResult[] results = new TopicMetricsResult[0];
        HttpGet httpGet = new HttpGet(topicMetricsUrl);
        try {
            HttpResponse response = client.execute(httpGet);
            if (response.getStatusLine().getStatusCode() != 200) {
                response.getEntity().getContent().close();
                return results;
            }

            results = JsonUtils.fromJson(response.getEntity().getContent(), TopicMetricsResult[].class);
            response.getEntity().getContent().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return results;
    }
}
