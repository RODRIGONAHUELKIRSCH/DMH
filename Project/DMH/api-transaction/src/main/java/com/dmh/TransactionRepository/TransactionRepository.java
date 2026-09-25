package com.dmh.TransactionRepository;


import com.dmh.TransactionDTO.TransactionDTO;
import org.springframework.data.repository.CrudRepository;
import java.util.UUID;

public interface TransactionRepository extends CrudRepository<TransactionDTO, UUID> {

}
