package com.swp391.cclearly.entity;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "Content_Banners")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContentBanner {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "banner_id")
    private UUID bannerId;

    @Column(name = "title", columnDefinition = "NVARCHAR(255)")
    private String title;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "position", length = 50)
    private String position;

    @Builder.Default
    @Column(name = "display_order")
    private Integer displayOrder = 1;

    @Builder.Default
    @Column(name = "is_active")
    private Boolean isActive = true;
}
