package com.example.tuwaiqfinalproject.Service;

import com.example.tuwaiqfinalproject.Api.ApiException;
import com.example.tuwaiqfinalproject.DTO.PlayerDTO;
import com.example.tuwaiqfinalproject.Model.Player;
import com.example.tuwaiqfinalproject.Model.User;
import com.example.tuwaiqfinalproject.Repository.AuthRepository;
import com.example.tuwaiqfinalproject.Repository.FieldRepository;
import com.example.tuwaiqfinalproject.Repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final AuthRepository authRepository;
    private final FieldRepository fieldRepository;

    public List<Player> getAllPlayers() {
        return playerRepository.findAll();
    }

    public Player getPlayer(Integer userId) {
        Player player = playerRepository.findPlayerById(userId);
        if (player == null)
            throw new ApiException("Player not found");
        return player;
    }

    public Player getPlayerById(Integer player_id) {
        Player player = playerRepository.findPlayerById(player_id);
        if (player == null)
            throw new ApiException("Player not found");
        return player;
    }

    // انشاء حساب , اذا كان اللاعب مسجل رساله انه موجود
    public void registerPlayer(PlayerDTO dto) {
        List<User> userList=authRepository.getUserByUsername(dto.getUsername());
        dto.setRole("PLAYER");
        String hashPassword = new BCryptPasswordEncoder().encode(dto.getPassword());
        User user = new User(null, dto.getUsername(),hashPassword,dto.getRole(),dto.getName(),dto.getPhone(),dto.getCity(),dto.getEmail(), null, null);
        Player player = new Player(null, dto.getGender(), dto.getBirthDate(),user,null,null);
        for(User u:userList){
            if(u.getUsername()==dto.getUsername()){
                throw new ApiException("Player Exist,");
            }
        }
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
        user.setCity(dto.getCity());
        player.setGender(dto.getGender());
        player.setBirthDate(dto.getBirthDate());

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
