package com.boczek.c8demo.orchestrator.two.controllers.payload;

import java.io.Serializable;

public class NeukundenanlageProzessPayload {

    private String kundenId;

    private String kundenName;

    public String getKundenId() {
        return kundenId;
    }

    public void setKundenId(String kundenId) {
        this.kundenId = kundenId;
    }

    public String getKundenName() {
        return kundenName;
    }

    public void setKundenName(String kundenName) {
        this.kundenName = kundenName;
    }
}
