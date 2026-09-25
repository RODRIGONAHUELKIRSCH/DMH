package com.dmh.AccountService;

import com.dmh.AccountDTO.AccountDTO;
import com.dmh.AccountMapper.AccountMapper;
import com.dmh.AccountRepository.AccountRepository;
import com.dmh.Entity.Account;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AccountService {

    @Autowired
    AccountRepository AccountRepository;

    @Autowired
    AccountMapper AccountMapper;

    public AccountService(AccountRepository AccountRepository,AccountMapper accountMapper) {
        this.AccountMapper = accountMapper;
        this.AccountRepository = AccountRepository;
    }


}
