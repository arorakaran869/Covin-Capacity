package com.dwitsolutions.cowincaptest.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class State {
    @JsonProperty("state_name")
    String stateName;
    @JsonProperty("state_id")
    Integer stateId;

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public Integer getStateId() {
        return stateId;
    }

    public void setStateId(Integer stateId) {
        this.stateId = stateId;
    }
}
