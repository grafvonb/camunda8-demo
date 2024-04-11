package com.boczek.c8demo.orchestrator.two.controllers.payload.messages;

public class KontoanlageMessagePayload extends MessagePayload {

    private String kontoName;

    public String getKontoName() {
        return kontoName;
    }

    public void setKontoName(String kontoName) {
        this.kontoName = kontoName;
    }
}
