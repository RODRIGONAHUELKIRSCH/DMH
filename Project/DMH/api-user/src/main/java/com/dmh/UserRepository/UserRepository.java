package com.dmh.UserRepository;

import com.dmh.Entity.User;
import org.springframework.data.repository.CrudRepository;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends CrudRepository<User, UUID>{

    Optional<User> findByEmail(String email);
}