package com.dmh.Entity;

import jakarta.persistence.*;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "account")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "amount", nullable = false)
    private String amount;

    @Column(name = "created_at", nullable = false)
    private ZonedDateTime created_at;

    @Column(name="account_type",nullable = false)
    private String account_type;

    @OneToOne(targetEntity = User.class)
    @JoinColumn(name = "id",referencedColumnName = "userId")
    private User user;

    public  Account(){}

    public Account(UUID id, String amount, ZonedDateTime created_at,String account_type){
        this.id=id;
        this.amount=amount;
        this.created_at=created_at;
        this.account_type=account_type;
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

}