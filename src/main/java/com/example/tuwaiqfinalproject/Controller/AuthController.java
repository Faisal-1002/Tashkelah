package com.example.tuwaiqfinalproject.Controller;

import com.example.tuwaiqfinalproject.Api.ApiResponse;
import com.example.tuwaiqfinalproject.Model.User;
import com.example.tuwaiqfinalproject.Service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // ADMIN
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        List<User> users = authService.getAllUsers();
        return ResponseEntity.status(200).body(users);
    }

//    @PostMapping("/register/admin")
//    public ResponseEntity<?> registerAdmin(@RequestBody @Valid User user) {
//        authService.registerAdmin(user);
//        return ResponseEntity.status(200).body(new ApiResponse("Admin registered successfully"));
//    }
//
//    @PutMapping("/update/admin/{id}")
//    public ResponseEntity<?> updateAdmin(@PathVariable Integer id, @RequestBody @Valid User user) {
//        authService.updateAdmin(id, user);
//        return ResponseEntity.status(200).body(new ApiResponse("Admin updated successfully"));
//    }
//
//    @DeleteMapping("/delete/admin/{id}")
//    public ResponseEntity<?> deleteAdmin(@PathVariable Integer id) {
//        authService.deleteAdmin(id);
//        return ResponseEntity.status(200).body(new ApiResponse("Admin deleted successfully"));
//    }

}
