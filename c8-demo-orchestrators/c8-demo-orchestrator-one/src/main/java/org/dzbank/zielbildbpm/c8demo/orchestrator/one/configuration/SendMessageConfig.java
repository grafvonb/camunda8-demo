package org.dzbank.zielbildbpm.c8demo.orchestrator.one.configuration;

public class SendMessageConfig {

    // https://docs.camunda.io/docs/components/best-practices/development/routing-events-to-processes/
    private String messageName;

    private String messageCorrelationKey;


    private String messageBody;

    public SendMessageConfig() {
    }

    public SendMessageConfig(String messageName, String messageCorrelationKey) {
        this.messageName = messageName;
        this.messageCorrelationKey = messageCorrelationKey;
    }

    public String getMessageName() {
        return messageName;
    }

    public void setMessageName(String messageName) {
        this.messageName = messageName;
    }

    public String getMessageCorrelationKey() {
        return messageCorrelationKey;
    }

    public void setMessageCorrelationKey(String messageCorrelationKey) {
        this.messageCorrelationKey = messageCorrelationKey;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }
}
