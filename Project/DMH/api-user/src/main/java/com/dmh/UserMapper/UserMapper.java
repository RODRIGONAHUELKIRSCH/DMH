package com.dmh.UserMapper;

import org.springframework.stereotype.Component;
import com.dmh.Entity.User;
import com.dmh.UserDTO.UserDTO;

@Component(value = "UserMapper")
public class UserMapper {
    public UserDTO UsertoDTO(User user){
        UserDTO dto=new UserDTO();
        dto.setNyAp(user.getNyAp());
        dto.setTelefono(user.getTelefono());
        dto.setDNI(user.getDNI());
        dto.setEmail(user.getEmail());
        dto.setCvu(user.getCvu());
        dto.setAlias(user.getAlias());
        return dto;
    }

    public User DTOtoUser(UserDTO dto){
        User user= new User(dto.getNyAp(),dto.getEmail(),dto.getTelefono(),dto.getPwd(),dto.getDNI());

        return user;
    }
}