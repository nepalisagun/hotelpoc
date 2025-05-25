package com.galaxyviewtower.hotel.crud.model.gen;

import java.util.List;

public class HotelGen {
  private Long id;
  private String name;
  private String address;
  private String city;
  private String state;
  private String zipCode;
  private String country;
  private String phoneNumber;
  private String email;
  private List<HotelGen.Room> rooms;

  // Getters and Setters
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  public String getZipCode() {
    return zipCode;
  }

  public void setZipCode(String zipCode) {
    this.zipCode = zipCode;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public List<HotelGen.Room> getRooms() {
    return rooms;
  }

  public void setRooms(List<HotelGen.Room> rooms) {
    this.rooms = rooms;
  }

  class Room {
    private Long id;
    private String roomNumber;
    private String roomType;
    private Double price;
    private Boolean isAvailable;

    // Getters and Setters
    public Long getId() {
      return id;
    }

    public void setId(Long id) {
      this.id = id;
    }

    public String getRoomNumber() {
      return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
      this.roomNumber = roomNumber;
    }

    public String getRoomType() {
      return roomType;
    }

    public void setRoomType(String roomType) {
      this.roomType = roomType;
    }

    public Double getPrice() {
      return price;
    }

    public void setPrice(Double price) {
      this.price = price;
    }

    public Boolean getIsAvailable() {
      return isAvailable;
    }

    public void setIsAvailable(Boolean isAvailable) {
      this.isAvailable = isAvailable;
    }
  }
}
