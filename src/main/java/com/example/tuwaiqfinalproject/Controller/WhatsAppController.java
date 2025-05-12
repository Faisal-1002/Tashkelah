package com.example.tuwaiqfinalproject.Controller;

import com.example.tuwaiqfinalproject.Service.WhatsAppService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WhatsAppController {
    @Autowired
    private WhatsAppService whatsAppService;
    // Endpoint لإرسال رسالة عبر WhatsApp
    @GetMapping("/send-whatsapp")
    public String sendWhatsappMessage(@RequestParam String to, @RequestParam String message) {
        return whatsAppService.sendMessage(to, message);
    }

}
