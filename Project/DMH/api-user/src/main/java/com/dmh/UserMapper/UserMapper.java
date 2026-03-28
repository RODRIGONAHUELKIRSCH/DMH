package com.dmh.UserMapper;

import com.dmh.Entity.User;
import com.dmh.UserDTO.UserDTO;
import org.springframework.stereotype.Component;

@Component(value = "UserMapper")
public class UserMapper {
    public UserDTO UsertoDTO(User user){
        UserDTO dto=new UserDTO();
        dto.setNombre(user.getNombre());
        dto.setTelefono(user.getTelefono());
        dto.setDni(user.getDni());
        dto.setEmail(user.getEmail());
        dto.setCvu(user.getCvu());
        dto.setAlias(user.getAlias());
        dto.setKeycloakId(user.getKeycloackId());
        return dto;
    }

    public User DTOtoUser(UserDTO dto){

        return new User(dto.getNombre(),dto.getApellido(),dto.getEmail(),dto.getTelefono(),dto.getDni(),dto.getKeycloakId());
    }
}