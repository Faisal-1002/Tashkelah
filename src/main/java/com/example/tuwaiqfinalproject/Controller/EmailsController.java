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

    //PLAYER
    @PostMapping("/add/{privateMatchId}")
    public ResponseEntity<?> addEmail(@AuthenticationPrincipal User user, @PathVariable Integer privateMatchId, @RequestBody @Valid Emails email) {
        emailsService.addEmailToPrivateMatch(user.getId(), privateMatchId, email);
        return ResponseEntity.status(200).body(new ApiResponse("Email added to private match"));
    }

    //PLAYER
    @GetMapping("/match/{privateMatchId}")
    public ResponseEntity<?> getEmailsByPrivateMatch(@AuthenticationPrincipal User user, @PathVariable Integer privateMatchId) {
        return ResponseEntity.status(200).body(emailsService.getEmailsForPrivateMatch(user.getId(), privateMatchId));
    }

    //PLAYER
    @DeleteMapping("/delete/{matchId}/{emailId}")
    public ResponseEntity<?> deleteEmail(@AuthenticationPrincipal User user, @PathVariable Integer matchId, @PathVariable Integer emailId) {
        emailsService.deleteEmail(user.getId(), matchId, emailId);
        return ResponseEntity.status(200).body(new ApiResponse("Email deleted successfully"));
    }

    //PLAYER
    @PostMapping("/private-match/send-invites/{privateMatchId}")
    public ResponseEntity<?> sendInvites(@AuthenticationPrincipal User user, @PathVariable Integer privateMatchId) {
        emailsService.sendInvites(user.getId(), privateMatchId);
        return ResponseEntity.status(200).body(new ApiResponse("Invites sent successfully."));
    }

}
