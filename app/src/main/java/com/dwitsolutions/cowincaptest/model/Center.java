package com.dwitsolutions.cowincaptest.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.List;

public class Center {
    @JsonProperty("center_id")
    private Long centerId;
    @JsonProperty("name")
    private String name;
    @JsonProperty("address")
    private String address;

    public String getName() {
        return name;
    }

    @JsonProperty("state_name")
    private String stateName;
    @JsonProperty("district_name")
    private String districtName;
    @JsonProperty("block_name")
    private String blockName;
    @JsonProperty("pincode")
    private Integer pinCode;
    @JsonProperty("from")
    private String from;
    @JsonProperty("to")
    private String to;
    @JsonProperty("lat")
    private Double latitude;
    @JsonProperty("long")
    private Double longitude;
    @JsonProperty("fee_type")
    private String feeType;         // paid/free
    @JsonProperty("session_id")
    private String sessionId;
    @JsonProperty("date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date date;
    @JsonProperty("available_capacity")
    private Long availableCapacity;

    public Integer getMinAge() {
        return minAge;
    }

    @JsonProperty("fee")
    private Double fees;
    @JsonProperty("min_age_limit")
    private Integer minAge;
    @JsonProperty("vaccine")
    private String vaccineName;
    @JsonProperty("slots")
    private List<String> slots;

    public Long getAvailableCapacity() {
        return availableCapacity;
    }
}
