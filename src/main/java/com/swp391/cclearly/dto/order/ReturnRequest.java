package com.swp391.cclearly.dto.order;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ReturnRequest {

  @NotBlank(message = "Lý do trả hàng không được để trống")
  private String reason;

  private String type; // RETURN, REFUND
}
