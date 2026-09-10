package com.ayushcart.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/** Stored as columns inside the orders table (no separate table). */
@Embeddable
public class ShippingAddress {

    @Column(name = "ship_full_name", nullable = false, length = 100)
    private String fullName;

    @Column(name = "ship_phone", nullable = false, length = 15)
    private String phone;

    @Column(name = "ship_address_line", nullable = false, length = 300)
    private String addressLine;

    @Column(name = "ship_city", nullable = false, length = 80)
    private String city;

    @Column(name = "ship_state", nullable = false, length = 80)
    private String state;

    @Column(name = "ship_pincode", nullable = false, length = 10)
    private String pincode;

    protected ShippingAddress() {
    }

    public ShippingAddress(String fullName, String phone, String addressLine,
                           String city, String state, String pincode) {
        this.fullName = fullName;
        this.phone = phone;
        this.addressLine = addressLine;
        this.city = city;
        this.state = state;
        this.pincode = pincode;
    }

    public String getFullName() { return fullName; }
    public String getPhone() { return phone; }
    public String getAddressLine() { return addressLine; }
    public String getCity() { return city; }
    public String getState() { return state; }
    public String getPincode() { return pincode; }
}
