package com.swp391.cclearly.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "Prescriptions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Prescription {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "prescription_id")
    private UUID prescriptionId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_item_id")
    private OrderItem orderItem;

    @Column(name = "image_url", length = 255)
    private String imageUrl;

    // Right eye (OD)
    @Column(name = "sph_od", precision = 5, scale = 2)
    private BigDecimal sphOd;

    @Column(name = "cyl_od", precision = 5, scale = 2)
    private BigDecimal cylOd;

    @Column(name = "axis_od")
    private Integer axisOd;

    @Column(name = "add_od", precision = 4, scale = 2)
    private BigDecimal addOd;

    // Left eye (OS)
    @Column(name = "sph_os", precision = 5, scale = 2)
    private BigDecimal sphOs;

    @Column(name = "cyl_os", precision = 5, scale = 2)
    private BigDecimal cylOs;

    @Column(name = "axis_os")
    private Integer axisOs;

    @Column(name = "add_os", precision = 4, scale = 2)
    private BigDecimal addOs;

    // Pupillary Distance
    @Column(name = "pd", precision = 4, scale = 1)
    private BigDecimal pd;

    @Column(name = "validation_status", length = 50)
    private String validationStatus;

    @Column(name = "sales_note", columnDefinition = "NVARCHAR(MAX)")
    private String salesNote;
}
