package com.dmh.UserRepository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import com.dmh.Entity.User;

public interface UserRepository extends CrudRepository<User, UUID>{

    Optional<User> findByEmail(String email);
}