package com.rafabene.mancala.domain;

public class IllegalGameMoveException extends Exception{

    public IllegalGameMoveException(String message) {
        super(message);
    }

    
}
