package com.example.tuwaiqfinalproject.Service;

import com.example.tuwaiqfinalproject.Api.ApiException;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WhatsAppService {

    @Value("${ultra.instance.id}")
    private String instanceId;
    @Value("${ultra.api.token}")
    private String apiToken;
    @Value("${ultra.api.url}")
    private String baseUrl;

    public String sendMessage(String to, String message) {
        String apiUrl = baseUrl + instanceId + "/messages/chat";

        try {
            HttpResponse<String> response = Unirest.post(apiUrl)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .field("token", apiToken)
                    .field("to", to)
                    .field("body", message)
                    .asString();

            String responseBody = response.getBody();
            System.out.println("WhatsApp API Response: " + responseBody);

            if (response.getStatus() != 200) {
                throw new ApiException("Failed to send WhatsApp message: " + responseBody);
            }

            return responseBody;

        } catch (Exception e) {
            throw new ApiException("WhatsApp Error: " + e.getMessage());
        }
    }
}