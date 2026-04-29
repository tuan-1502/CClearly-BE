package com.swp391.cclearly.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "Products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "product_id")
  private UUID productId;

  @Column(name = "name", length = 255)
  private String name;

  @Column(name = "category_type", length = 50)
  private String categoryType;

  @Column(name = "sub_category", length = 50)
  private String subCategory;

  @Column(name = "base_price", precision = 19, scale = 2)
  private BigDecimal basePrice;

  @Column(name = "description", columnDefinition = "NVARCHAR(MAX)")
  private String description;

  @Column(name = "is_active")
  private Boolean isActive;

  @OneToOne(mappedBy = "product", cascade = CascadeType.ALL)
  private ProductFrame productFrame;

  @OneToOne(mappedBy = "product", cascade = CascadeType.ALL)
  private ProductLens productLens;

  @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
  private Set<ProductVariant> variants = new HashSet<>();

  @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
  private Set<ProductImage> images = new HashSet<>();
}
