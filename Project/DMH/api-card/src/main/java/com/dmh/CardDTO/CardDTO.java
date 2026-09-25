package com.dmh.CardDTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CardDTO {


    public CardDTO(){}

    public CardDTO(String cardNumber, String cardCompany, String cardType, LocalDate cardDueDate, String cbu){
    this.cardNumber=cardNumber;
    this.cardCompany=cardCompany;
    this.cardType=cardType;
    this.cardDueDate=cardDueDate;
    this.cbu=cbu;
    }


    @NotBlank
    private String  cardNumber;

    @NotBlank
    private String  cardCompany;

    @NotBlank
    private String  cardType;

    @NotBlank
    private LocalDate cardDueDate;

    @NotBlank
    @JsonProperty(access =  JsonProperty.Access.WRITE_ONLY)
    private String cbu;

    public String getcardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
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
    public LocalDate setcardDueDate(LocalDate cardDueDate) {
        return this.cardDueDate=cardDueDate;
    }

    public String getcbu() {
        return cbu;
    }

    public void setcbu(String cbu) {
        this.cbu = cbu;
    }
}
