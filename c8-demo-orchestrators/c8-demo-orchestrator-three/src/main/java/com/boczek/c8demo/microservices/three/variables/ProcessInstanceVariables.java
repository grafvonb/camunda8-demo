package com.boczek.c8demo.microservices.three.variables;

public class ProcessInstanceVariables {

    private CalculateData data;

    private Long result;

    public CalculateData getData() {
        return data;
    }

    public void setData(CalculateData data) {
        this.data = data;
    }

    public Long getResult() {
        return result;
    }

    public void setResult(Long result) {
        this.result = result;
    }
}
