package com.rafabene.mancala.domain;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;

public class WalkableBoard {

    private int[] walkableBoard;

    public WalkableBoard(){
        
    }

    private int getPitsQuantity(){
        Config config = ConfigProvider.getConfig();
        return config.getValue("pitsQuantity", Integer.class);
    }
    
}
