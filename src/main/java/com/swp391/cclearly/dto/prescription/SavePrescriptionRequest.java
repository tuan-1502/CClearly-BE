package com.swp391.cclearly.dto.prescription;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class SavePrescriptionRequest {
  private String orderItemId; // UUID of the order item

  // Right eye (OD)
  private BigDecimal sphOd;
  private BigDecimal cylOd;
  private Integer axisOd;
  private BigDecimal addOd;

  // Left eye (OS)
  private BigDecimal sphOs;
  private BigDecimal cylOs;
  private Integer axisOs;
  private BigDecimal addOs;

  // PD
  private BigDecimal pd;

  // Prescription image/notes
  private String imageUrl;
  private String salesNote;
}
