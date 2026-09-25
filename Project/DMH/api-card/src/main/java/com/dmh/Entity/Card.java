package com.dmh.Entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name="cards")
public class Card {

    public Card(){}

    public Card(String cardNumber,String cardCompany, String cardType, LocalDate cardDueDate, String cbu, UUID accountId){
        this.cardNumber=cardNumber;
        this.cardCompany=cardCompany;
        this.cardType=cardType;
        this.cardType=cardType;
        this.cardDueDate=cardDueDate;
        this.cbu=cbu;
        this.accountId=accountId;
    }


    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false,unique = true)
    private String cardNumber;

    @Column(nullable = false)
    private String cardCompany;

    @Column(nullable = false)
    private String cardType;

    @Column(nullable = false)
    private LocalDate cardDueDate;

    @Column(nullable = false,unique = true)
    private String cbu;

    @Column(nullable = false,unique = true)
    private UUID accountId;


    public String getcardNumber() {
        return cardNumber;
    }

    public void setcardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getcardCompany() {
        return cardCompany;
    }
    public void setcardCompany(String cardCompany) {
        this.cardCompany = cardCompany;
    }
    public String getcardType() {
        return cardType;
    }
    public void setcardType(String cardType) {
        this.cardType = cardType;
    }
    public LocalDate getcardDueDate() {
        return cardDueDate;
    }
    public void setcardDueDate(LocalDate cardDueDate) {
        this.cardDueDate = cardDueDate;
    }
    public String getcbu() {
        return cbu;
    }
    public void setcbu(String cbu) {
        this.cbu=cbu;
    }

}
