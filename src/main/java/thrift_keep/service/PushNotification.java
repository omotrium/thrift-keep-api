package thrift_keep.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class PushNotification {

    public void sendPushNotification(String token, String title, String message) {

        String url = "https://exp.host/--/api/v2/push/send";

        Map<String, Object> body = new HashMap<>();
        body.put("to", token);
        body.put("title", title);
        body.put("body", message);
        body.put("sound", "default");

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.postForObject(url, body, String.class);
    }
}
