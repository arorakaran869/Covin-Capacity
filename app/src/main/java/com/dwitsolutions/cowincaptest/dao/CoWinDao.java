package com.dwitsolutions.cowincaptest.dao;

public interface CoWinDao {
    public void fetchStates();

    public void fetchDistricts(String stateName);

    public void fetchDistricts(Long stateId);

    public void fetchCenters(Integer pincode);

    public void fetchCenters(String districtId);

    public void fetchCenters(String stateName,String districtName);
}

