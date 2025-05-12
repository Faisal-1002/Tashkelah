package com.example.tuwaiqfinalproject.Service;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
@Service
public class WhatsAppService {
@Value("${ultra.api.url}")

private String apiUrl;

    @Value("${ultra.api.key}")
    private String apiKey;

    @Value("${ultra.whatsapp.number}")
    private String fromNumber;
// need Config
    public String sendMessage(String toNumber, String message) {
        String url = apiUrl + "/sendMessage";

        System.out.println("Sending WhatsApp to: " + toNumber);
        System.out.println("Message: " + message);
        System.out.println("API URL: " + url);
        System.out.println("Headers: Bearer " + apiKey);

        String jsonBody = String.format(
                "{\"to\": \"%s\", \"from\": \"%s\", \"body\": \"%s\"}",
                toNumber, fromNumber, message
        );

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost postRequest = new HttpPost(url);
            postRequest.setHeader("Authorization", "Bearer " + apiKey);
            postRequest.setHeader("Content-Type", "application/json");

            postRequest.setEntity(new StringEntity(jsonBody));

            try (CloseableHttpResponse response = client.execute(postRequest)) {
                String responseBody = EntityUtils.toString(response.getEntity());

                System.out.println("Ultra API response: " + responseBody);

                return responseBody;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "خطأ في الاتصال: " + e.getMessage();
        }
    }
}
