package com.dmh.CardMapper;

import com.dmh.CardDTO.CardDTO;
import com.dmh.Entity.Card;
import org.springframework.stereotype.Component;

@Component(value = "CardMapper")
public class CardMapper {

    public CardDTO CardtoCDTO(Card card) {
        CardDTO cardDTO = new CardDTO();
        cardDTO.setCardNumber(card.getcardNumber());
        cardDTO.setcardCompany(card.getcardCompany());
        cardDTO.setcardType(card.getcardType());
        cardDTO.setcardDueDate(card.getcardDueDate());
        cardDTO.setcbu(card.getcbu());
        return  cardDTO;
    }

    public  Card DTOtoCard(CardDTO cardDTO) {
        Card card = new Card();
        card.setcardNumber(cardDTO.getcardNumber());
        card.setcardCompany(cardDTO.getcardCompany());
        card.setcardType(cardDTO.getcardType());
        card.setcardDueDate(cardDTO.getcardDueDate());
        card.setcbu(cardDTO.getcbu());
        return card;
    }

}