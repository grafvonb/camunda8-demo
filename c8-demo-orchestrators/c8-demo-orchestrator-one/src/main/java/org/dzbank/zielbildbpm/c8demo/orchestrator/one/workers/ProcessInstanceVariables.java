package org.dzbank.zielbildbpm.c8demo.orchestrator.one.workers;

import org.dzbank.zielbildbpm.c8demo.orchestrator.one.configuration.CompleteJobConfig;
import org.dzbank.zielbildbpm.c8demo.orchestrator.one.configuration.RuntimeConfig;
import org.dzbank.zielbildbpm.c8demo.orchestrator.one.configuration.SendMessageConfig;
import org.dzbank.zielbildbpm.c8demo.orchestrator.one.model.OneEntity;

import java.util.UUID;

public class ProcessInstanceVariables {

    private RuntimeConfig runtimeConfig;

    private CompleteJobConfig completeJobConfig;

    private SendMessageConfig sendMessageConfig;

    private OneEntity oneEntity;

    private UUID messageCorrelationKey;

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

    public SendMessageConfig getSendMessageConfig() {
        return sendMessageConfig;
    }

    public void setSendMessageConfig(SendMessageConfig sendMessageConfig) {
        this.sendMessageConfig = sendMessageConfig;
    }

    public UUID getMessageCorrelationKey() {
        return messageCorrelationKey;
    }

    public void setMessageCorrelationKey(UUID messageCorrelationKey) {
        this.messageCorrelationKey = messageCorrelationKey;
    }
}
