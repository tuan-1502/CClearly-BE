package com.swp391.cclearly.dto.address;

import java.util.UUID;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressResponse {

    private UUID addressId;
    private String name;
    private String phone;
    private String address;
    private Boolean isDefault;
}
