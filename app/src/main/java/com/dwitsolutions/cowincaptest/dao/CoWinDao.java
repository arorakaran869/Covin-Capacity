package com.dwitsolutions.cowincaptest.dao;

import com.dwitsolutions.cowincaptest.model.State;

import java.util.ArrayList;

public interface CoWinDao {
    public void fetchStates();

    public void fetchDistricts(String stateName);

    public void fetchDistricts(Long stateId);

    public void fetchCenters(Integer pincode,int age);

    public void fetchCenters(String districtId);

    public void fetchCenters(String stateName,String districtName);
}

