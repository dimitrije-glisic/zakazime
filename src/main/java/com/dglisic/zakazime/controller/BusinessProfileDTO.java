package com.dglisic.zakazime.controller;

public class BusinessProfileDTO {

  private final String businessName;
  private final String phoneNumber;
  private final String city;
  private final String postalCode;
  private final String address;
  private final String status;

  BusinessProfileDTO(String businessName, String phoneNumber, String city, String postalCode, String address,
                     String status) {
    this.businessName = businessName;
    this.phoneNumber = phoneNumber;
    this.city = city;
    this.postalCode = postalCode;
    this.address = address;
    this.status = status;
  }

  public String getBusinessName() {
    return businessName;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public String getCity() {
    return city;
  }

  public String getPostalCode() {
    return postalCode;
  }

  public String getAddress() {
    return address;
  }

  public String getStatus() {
    return status;
  }

  public static BusinessProfileDTOBuilder builder() {
    return new BusinessProfileDTOBuilder();
  }

  static class BusinessProfileDTOBuilder {

    private String businessName;
    private String phoneNumber;
    private String city;
    private String postalCode;
    private String address;
    private String status;

    public BusinessProfileDTOBuilder businessName(String businessName) {
      this.businessName = businessName;
      return this;
    }

    public BusinessProfileDTOBuilder phoneNumber(String phoneNumber) {
      this.phoneNumber = phoneNumber;
      return this;
    }

    public BusinessProfileDTOBuilder city(String city) {
      this.city = city;
      return this;
    }

    public BusinessProfileDTOBuilder postalCode(String postalCode) {
      this.postalCode = postalCode;
      return this;
    }

    public BusinessProfileDTOBuilder address(String address) {
      this.address = address;
      return this;
    }

    public BusinessProfileDTOBuilder status(String status) {
      this.status = status;
      return this;
    }

    public BusinessProfileDTO build() {
      return new BusinessProfileDTO(businessName, phoneNumber, city, postalCode, address, status);
    }
  }
}