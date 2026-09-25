package com.dmh.TransactionMapper;

import com.dmh.Entity.Transaction;
import com.dmh.TransactionDTO.TransactionDTO;
import org.springframework.stereotype.Component;

@Component("TransactionMapper")
public class TransactionMapper {

    public TransactionDTO toTransactionDTO(Transaction transaction) {
        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setState(transaction.getState());
        transactionDTO.setAmount(transaction.getAmount());
        transactionDTO.setDescription(transaction.getDescription());
        transactionDTO.setType(transaction.getType());
        transactionDTO.setDate(transaction.getDate());
        transactionDTO.setBeneficiary(transaction.getBeneficiary());
        transactionDTO.setAccountId(transaction.getAccountId());
        transactionDTO.setCardId(transaction.getCardId());
        return transactionDTO;
    }

    public Transaction toTransaction(TransactionDTO transactionDTO) {
        Transaction transaction = new Transaction();
        transaction.setState(transactionDTO.getState());
        transaction.setAmount(transactionDTO.getAmount());
        transaction.setDescription(transactionDTO.getDescription());
        transaction.setType(transactionDTO.getType());
        transaction.setDate(transactionDTO.getDate());
        transaction.setBeneficiary(transactionDTO.getBeneficiary());
        transaction.setAccountId(transactionDTO.getAccountId());
        transaction.setCardId(transactionDTO.getCardId());
        return transaction;
    }
}
