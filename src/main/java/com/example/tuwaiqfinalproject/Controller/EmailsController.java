package com.example.tuwaiqfinalproject.Controller;


import com.example.tuwaiqfinalproject.DTO.GroupEmailDTO;
import com.example.tuwaiqfinalproject.Model.Emails;
import com.example.tuwaiqfinalproject.Service.EmailsService;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/emails")
@RequiredArgsConstructor
public class EmailsController {



    private final EmailsService emailService;


    // Example:
    // /api/v1/email/send-group/user1@gmail.com,user2@gmail.com,user3@gmail.com
    @PostMapping("/send-group/{emails}")
    public ResponseEntity<String> sendGroupEmail(
            @PathVariable String emails,
            @RequestBody GroupEmailDTO groupEmailDTO) {

        String[] emailArray = emails.split(",");
        emailService.sendBulkEmail(emailArray, groupEmailDTO);

        return ResponseEntity.ok("Emails sent successfully");
    }
}
