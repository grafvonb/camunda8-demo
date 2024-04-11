package com.boczek.c8demo.orchestrator.two.controllers.payload;

import com.boczek.c8demo.orchestrator.two.configuration.RuntimeConfig;

import java.util.ArrayList;
import java.util.Collection;

public class HauptauftragProzessPayload {

    private RuntimeConfig runtimeConfig;

    private NeukundenanlageProzessPayload kundenPayload;

    private Collection<KontoanlageProzessPayload> kontoPayloads;
    private Collection<UVZAnlageProzessPayload> uvzPayloads;

    public HauptauftragProzessPayload() {
        runtimeConfig = new RuntimeConfig();
        kundenPayload = new NeukundenanlageProzessPayload();
        kontoPayloads = new ArrayList<>();
        uvzPayloads = new ArrayList<>();
    }

    public NeukundenanlageProzessPayload getKundenPayload() {
        return kundenPayload;
    }

    public void setKundenPayload(NeukundenanlageProzessPayload kundenPayload) {
        this.kundenPayload = kundenPayload;
    }

    public Collection<KontoanlageProzessPayload> getKontoPayloads() {
        return kontoPayloads;
    }

    public void setKontoPayloads(ArrayList<KontoanlageProzessPayload> kontoPayloads) {
        this.kontoPayloads = kontoPayloads;
    }

    public Collection<UVZAnlageProzessPayload> getUvzPayloads() {
        return uvzPayloads;
    }

    public void setUvzPayloads(Collection<UVZAnlageProzessPayload> uvzPayloads) {
        this.uvzPayloads = uvzPayloads;
    }

    public RuntimeConfig getRuntimeConfig() {
        return runtimeConfig;
    }

    public void setRuntimeConfig(RuntimeConfig runtimeConfig) {
        this.runtimeConfig = runtimeConfig;
    }
}
