package com.dmh.AccountRepository;

import com.dmh.Entity.Account;
import org.springframework.data.repository.CrudRepository;
import java.util.UUID;

public interface AccountRepository extends CrudRepository<Account, UUID> {


}