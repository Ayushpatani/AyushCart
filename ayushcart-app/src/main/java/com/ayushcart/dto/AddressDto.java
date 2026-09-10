package com.ayushcart.dto;

import com.ayushcart.entity.ShippingAddress;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Used both as the checkout request body and inside order responses. */
public record AddressDto(
        @NotBlank(message = "Full name is required")
        @Size(max = 100)
        String fullName,

        @NotBlank(message = "Phone is required")
        @Pattern(regexp = "^[6-9]\\d{9}$", message = "Enter a 10-digit mobile number")
        String phone,

        @NotBlank(message = "Address is required")
        @Size(max = 300)
        String addressLine,

        @NotBlank(message = "City is required")
        @Size(max = 80)
        String city,

        @NotBlank(message = "State is required")
        @Size(max = 80)
        String state,

        @NotBlank(message = "PIN code is required")
        @Pattern(regexp = "^\\d{6}$", message = "Enter a 6-digit PIN code")
        String pincode
) {
    public ShippingAddress toEntity() {
        return new ShippingAddress(fullName.trim(), phone, addressLine.trim(), city.trim(), state.trim(), pincode);
    }

    public static AddressDto from(ShippingAddress a) {
        return new AddressDto(a.getFullName(), a.getPhone(), a.getAddressLine(),
                a.getCity(), a.getState(), a.getPincode());
    }
}
