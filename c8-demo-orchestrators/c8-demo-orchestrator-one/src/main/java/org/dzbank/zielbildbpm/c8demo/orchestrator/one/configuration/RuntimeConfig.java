package org.dzbank.zielbildbpm.c8demo.orchestrator.one.configuration;

public class RuntimeConfig {

    private String workflowType;

    private boolean isReactive = true;

    private boolean tryUnstable;

    private boolean withUserTasks;

    public RuntimeConfig() {
    }

    public RuntimeConfig(String workflowType, boolean isReactive, boolean tryUnstable, boolean withUserTasks) {
        this.workflowType = workflowType;
        this.isReactive = isReactive;
        this.tryUnstable = tryUnstable;
        this.withUserTasks = withUserTasks;
    }

    public String getWorkflowType() {
        return workflowType;
    }

    public void setWorkflowType(String workflowType) {
        this.workflowType = workflowType;
    }

    public boolean getIsReactive() {
        return isReactive;
    }

    public void setIsReactive(boolean reactive) {
        isReactive = reactive;
    }

    public boolean isTryUnstable() {
        return tryUnstable;
    }

    public void setTryUnstable(boolean tryUnstable) {
        this.tryUnstable = tryUnstable;
    }

    public boolean isWithUserTasks() {
        return withUserTasks;
    }

    public void setWithUserTasks(boolean withUserTasks) {
        this.withUserTasks = withUserTasks;
    }
}