package org.dzbank.zielbildbpm.c8demo.orchestrator.one.workers;

import org.dzbank.zielbildbpm.c8demo.orchestrator.one.configuration.CompleteJobConfig;
import org.dzbank.zielbildbpm.c8demo.orchestrator.one.configuration.RuntimeConfig;
import org.dzbank.zielbildbpm.c8demo.orchestrator.one.model.OneEntity;

public class ProcessInstanceVariables {

    private RuntimeConfig runtimeConfig;

    private CompleteJobConfig completeJobConfig;

    private OneEntity oneEntity;

    private String loggerName;

    public ProcessInstanceVariables() {
    }

    public ProcessInstanceVariables(RuntimeConfig runtimeConfig) {
        this.runtimeConfig = runtimeConfig;
    }

    public RuntimeConfig getRuntimeConfig() {
        return runtimeConfig;
    }

    public void setRuntimeConfig(RuntimeConfig runtimeConfig) {
        this.runtimeConfig = runtimeConfig;
    }

    public CompleteJobConfig getCompleteJobConfig() {
        return completeJobConfig;
    }

    public void setCompleteJobConfig(CompleteJobConfig completeJobConfig) {
        this.completeJobConfig = completeJobConfig;
    }

    public OneEntity getOneEntity() {
        return oneEntity;
    }

    public void setOneEntity(OneEntity oneEntity) {
        this.oneEntity = oneEntity;
    }

    public String getLoggerName() {
        return loggerName;
    }

    public void setLoggerName(String loggerName) {
        this.loggerName = loggerName;
    }
}
