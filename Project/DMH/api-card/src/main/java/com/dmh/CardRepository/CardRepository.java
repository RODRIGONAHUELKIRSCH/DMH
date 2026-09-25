package com.dmh.CardRepository;

import com.dmh.CardDTO.CardDTO;
import org.springframework.data.repository.CrudRepository;
import java.util.UUID;

public interface CardRepository extends CrudRepository<CardDTO, UUID>  {

}
