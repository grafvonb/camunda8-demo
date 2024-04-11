package com.boczek.c8demo.orchestrator.two.controllers.payload.messages;

public class NeukundenanlageMessagePayload extends MessagePayload {

    private String kundenName;

    public String getKundenName() {
        return kundenName;
    }

    public void setKundenName(String kundenName) {
        this.kundenName = kundenName;
    }
}
