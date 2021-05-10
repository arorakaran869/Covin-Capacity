package com.dwitsolutions.cowincaptest.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class District {
    @JsonProperty("district_name")
    String districtName;
    @JsonProperty("district_id")
    Integer districtId;

    public String getDistrictName() {
        return districtName;
    }

    public Integer getDistrictId() {
        return districtId;
    }
}