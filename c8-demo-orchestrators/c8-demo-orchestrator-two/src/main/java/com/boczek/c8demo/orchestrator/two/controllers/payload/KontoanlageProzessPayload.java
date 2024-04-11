package com.boczek.c8demo.orchestrator.two.controllers.payload;

public class KontoanlageProzessPayload {

    private String kundenId;

    private String kontoId;

    private String kontoName;

    public String getKundenId() {
        return kundenId;
    }

    public void setKundenId(String kundenId) {
        this.kundenId = kundenId;
    }

    public String getKontoId() {
        return kontoId;
    }

    public void setKontoId(String kontoId) {
        this.kontoId = kontoId;
    }

    public String getKontoName() {
        return kontoName;
    }

    public void setKontoName(String kontoName) {
        this.kontoName = kontoName;
    }
}
