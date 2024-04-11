package com.boczek.c8demo.orchestrator.two.controllers.payload.messages;

public class UVZanlageMessagePayload extends MessagePayload {

    private String uvzName;

    private String kontoName;

    public String getUvzName() {
        return uvzName;
    }

    public void setUvzName(String uvzName) {
        this.uvzName = uvzName;
    }

    public String getKontoName() {
        return kontoName;
    }

    public void setKontoName(String kontoName) {
        this.kontoName = kontoName;
    }
}
