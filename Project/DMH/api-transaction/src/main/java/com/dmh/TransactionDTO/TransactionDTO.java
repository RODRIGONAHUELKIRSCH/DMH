package com.dmh.TransactionDTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionDTO {

    @NotBlank
    private String state;

    @NotBlank
    private String amount;

    @NotBlank
    private String description;

    @NotBlank
    private String type;

    @NotBlank
    private LocalDate date;

    @NotBlank
    private String beneficiary;

    @NotBlank
    @JsonProperty(access =  JsonProperty.Access.WRITE_ONLY)
    private UUID accountId;

    @NotBlank
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private UUID cardId;

    public TransactionDTO() {}

    public TransactionDTO(String state, String amount, String description,String type ,LocalDate date) {
        this.state = state;
        this.amount = amount;
        this.description = description;
        this.type = type;
        this.date = date;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getBeneficiary() {
        return beneficiary;
    }

    public void setBeneficiary(String beneficiary) {
        this.beneficiary = beneficiary;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public void setAccountId(UUID accountId) {
        this.accountId = accountId;
    }

    public UUID getCardId() {
        return cardId;
    }

    public void setCardId(UUID cardId) {
        this.cardId = cardId;
    }

}