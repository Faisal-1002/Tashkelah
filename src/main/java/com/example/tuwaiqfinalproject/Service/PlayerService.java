
package com.example.tuwaiqfinalproject.Service;

import com.example.tuwaiqfinalproject.Api.ApiException;
import com.example.tuwaiqfinalproject.DTO.PlayerDTO;
import com.example.tuwaiqfinalproject.Model.Player;
import com.example.tuwaiqfinalproject.Model.User;
import com.example.tuwaiqfinalproject.Repository.AuthRepository;
import com.example.tuwaiqfinalproject.Repository.FieldRepository;
import com.example.tuwaiqfinalproject.Repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final AuthRepository authRepository;

    public List<Player> getAllPlayers() {
        return playerRepository.findAll();
    }

    // 22. Faisal - Get my info as a player - Tested
    public Player getPlayer(Integer userId) {
        Player player = playerRepository.findPlayerById(userId);
        if (player == null)
            throw new ApiException("Player not found");
        return player;
    }

    // 23. Faisal - Get player info by id - Tested
    public Player getPlayerById(Integer player_id) {
        Player player = playerRepository.findPlayerById(player_id);
        if (player == null)
            throw new ApiException("Player not found");
        return player;
    }

    // 24. Eatzaz + Faisal - Register Player - Tested
    public void registerPlayer(PlayerDTO dto) {
        int age = Period.between(dto.getBirth_date(), LocalDate.now()).getYears();
        if (age < 15) {
            throw new ApiException("You must be at least 15 years old to register.");
        }
        String hashPassword = new BCryptPasswordEncoder().encode(dto.getPassword());
        User user = new User(null, dto.getUsername(),hashPassword,"PLAYER",dto.getName(),dto.getPhone(),dto.getAddress(),dto.getEmail(), null, null);
        Player player = new Player(null, dto.getGender(), dto.getBirth_date(),user,null,null,null);
        authRepository.save(user);
        playerRepository.save(player);
    }

    public void updatePlayer(Integer player_id, PlayerDTO dto) {
        Player player = playerRepository.findPlayerById(player_id);
        if (player == null)
            throw new ApiException("Player not found");

        User user = player.getUser();
        user.setUsername(dto.getUsername());
        String hashedPassword = new BCryptPasswordEncoder().encode(dto.getPassword());
        user.setPassword(hashedPassword);
        user.setEmail(dto.getEmail());
        user.setName(dto.getName());
        user.setPhone(dto.getPhone());
        user.setAddress(dto.getAddress());
        player.setGender(dto.getGender());
        player.setBirth_date(dto.getBirth_date());

        authRepository.save(user);
        playerRepository.save(player);
    }

    public void deletePlayer(Integer player_id) {
        Player player = playerRepository.findPlayerById(player_id);
        if (player == null)
            throw new ApiException("Player not found");
        authRepository.delete(player.getUser());
        playerRepository.delete(player);
    }

}
