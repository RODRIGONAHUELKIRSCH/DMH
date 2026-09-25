package com.dmh.AccountController;

import com.dmh.AccountDTO.AccountDTO;
import com.dmh.AccountMapper.AccountMapper;
import com.dmh.AccountRepository.AccountRepository;
import com.dmh.AccountService.AccountService;
import com.dmh.Entity.Account;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/account")
public class AccountController {

@Autowired
    AccountService accountService;

@Autowired
    AccountMapper  accountMapper;

}