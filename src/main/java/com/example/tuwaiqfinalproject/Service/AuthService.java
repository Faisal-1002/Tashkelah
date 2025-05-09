package com.example.tuwaiqfinalproject.Service;

import com.example.tuwaiqfinalproject.Api.ApiException;
import com.example.tuwaiqfinalproject.Model.User;
import com.example.tuwaiqfinalproject.Repository.AuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthRepository authRepository;

    public List<User> getAllUsers(){
        return authRepository.findAll();
    }

//    public void registerAdmin(User user){
//        user.setRole("ADMIN");
//        String hashPassword = new BCryptPasswordEncoder().encode(user.getPassword());
//        user.setPassword(hashPassword);
//        authRepository.save(user);
//    }

//    public void updateAdmin(Integer user_id, User user){
//        User updatedUser = authRepository.findUserById(user_id);
//        if (updatedUser == null)
//            throw new ApiException("User not found");
//        String hashPassword = new BCryptPasswordEncoder().encode(user.getPassword());
//        updatedUser.setUsername(user.getUsername());
//        updatedUser.setPassword(hashPassword);
//        authRepository.save(updatedUser);
//    }

//    public void deleteAdmin(Integer id){
//        User user = authRepository.findUserById(id);
//        if (user == null)
//            throw new ApiException("User not found");
//        authRepository.delete(user);
//    }
}