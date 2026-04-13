package com.dmh.AccountDTO;

import com.dmh.Entity.Account;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import java.time.ZonedDateTime;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class
AccountDTO {

    @NotBlank
    private UUID id;

    @NotBlank
    private String amount;

    @NotBlank
    private ZonedDateTime created_at;

    @NotBlank
    private String account_type;

    @NotBlank
    private UUID user_id;

    public AccountDTO(){}

    public AccountDTO(UUID id , String amount, ZonedDateTime created_at,UUID user_id){
        this.id=id;
        this.amount=amount;
        this.created_at=created_at;
        this.user_id=user_id;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public ZonedDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(ZonedDateTime created_at) {
        this.created_at = created_at;
    }

    public String getAccount_type() {
        return account_type;
    }

    public void setAccount_type(String account_type) {
        this.account_type = account_type;
    }

    public UUID getUser_id() {
        return user_id;
    }

    public void setUser_id(UUID user_id) {
        this.user_id = user_id;
    }

}