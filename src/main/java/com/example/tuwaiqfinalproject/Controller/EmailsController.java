package com.example.tuwaiqfinalproject.Controller;
import com.example.tuwaiqfinalproject.Api.ApiResponse;
import com.example.tuwaiqfinalproject.Model.Emails;
import com.example.tuwaiqfinalproject.Model.User;
import com.example.tuwaiqfinalproject.Service.EmailsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/emails")
@RequiredArgsConstructor
public class EmailsController {

    private final EmailsService emailsService;

    @PostMapping("/add/{privateMatchId}")
    public ResponseEntity<?> addEmail(@PathVariable Integer privateMatchId, @RequestBody @Valid Emails email) {
        emailsService.addEmailToPrivateMatch(privateMatchId, email);
        return ResponseEntity.status(200).body(new ApiResponse("Email added to private match"));
    }

    @GetMapping("/match/{privateMatchId}")
    public ResponseEntity<?> getEmailsByPrivateMatch(@PathVariable Integer privateMatchId) {
        List<Emails> emails = emailsService.getEmailsForPrivateMatch(privateMatchId);
        return ResponseEntity.status(200).body(emails);
    }

    @DeleteMapping("/delete/{emailId}")
    public ResponseEntity<?> deleteEmail(@PathVariable Integer emailId) {
        emailsService.deleteEmail(emailId);
        return ResponseEntity.status(200).body(new ApiResponse("Email deleted successfully"));
    }

    @PostMapping("/private-match/send-invites/{privateMatchId}")
    public ResponseEntity<?> sendInvites(@AuthenticationPrincipal User user, @PathVariable Integer privateMatchId) {
        emailsService.sendInvites(2, privateMatchId);
        return ResponseEntity.status(200).body(new ApiResponse("Invites sent successfully."));
    }

}
