package com.dmh.AccountMapper;

import com.dmh.AccountDTO.AccountDTO;
import com.dmh.Entity.Account;
import org.springframework.stereotype.Component;

@Component(value = "AccountMapper")
public class AccountMapper {

        public AccountDTO AccounttoDTO(Account account){
            AccountDTO accountDTO= new AccountDTO();
            accountDTO.setAmount(account.getAmount());
            accountDTO.setCreated_at(account.getCreated_at());
            accountDTO.setAccount_type(account.getAccount_type());

            return accountDTO;
        }

        public Account DTOtoAccount(AccountDTO accountDTO){
            Account account = new Account();
            account.setAmount(accountDTO.getAmount());
            account.setCreated_at(accountDTO.getCreated_at());
            account.setAccount_type(accountDTO.getAccount_type());

            return account;
        }

}
