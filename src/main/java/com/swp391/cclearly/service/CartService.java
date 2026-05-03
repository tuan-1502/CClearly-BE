package com.swp391.cclearly.service;

import com.swp391.cclearly.dto.base.ApiResponse;
import com.swp391.cclearly.dto.cart.AddCartItemRequest;
import com.swp391.cclearly.dto.cart.CartResponse;
import com.swp391.cclearly.dto.cart.UpdateCartItemRequest;
import com.swp391.cclearly.entity.Cart;
import com.swp391.cclearly.entity.CartItem;
import com.swp391.cclearly.entity.Product;
import com.swp391.cclearly.entity.ProductImage;
import com.swp391.cclearly.entity.ProductVariant;
import com.swp391.cclearly.entity.User;
import com.swp391.cclearly.exception.BadRequestException;
import com.swp391.cclearly.exception.ResourceNotFoundException;
import com.swp391.cclearly.repository.CartItemRepository;
import com.swp391.cclearly.repository.CartRepository;
import com.swp391.cclearly.repository.ProductRepository;
import com.swp391.cclearly.repository.ProductVariantRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

  private final CartRepository cartRepository;
  private final CartItemRepository cartItemRepository;
  private final ProductVariantRepository productVariantRepository;
  private final ProductRepository productRepository;

  public ApiResponse<CartResponse> getCart(User user) {
    Cart cart = getOrCreateCart(user);
    return ApiResponse.success("Lay gio hang thanh cong", toResponse(cart));
  }

  public ApiResponse<CartResponse> addToCart(User user, AddCartItemRequest request) {
    Cart cart = getOrCreateCart(user);
    ProductVariant variant = resolveVariant(request);
    ProductVariant lensVariant = resolveLensVariant(request.getLensVariantId());

    UUID variantId = variant.getVariantId();
    UUID lensVariantId = lensVariant != null ? lensVariant.getVariantId() : null;

    Optional<CartItem> existingItem = cart.getCartItems().stream()
        .filter(ci -> isSameCartItem(ci, variantId, lensVariantId))
        .findFirst();

    if (existingItem.isPresent()) {
      CartItem item = existingItem.get();
      int currentQuantity = item.getQuantity() != null ? item.getQuantity() : 0;
      item.setQuantity(currentQuantity + request.getQuantity());
      cartItemRepository.save(item);
    } else {
      CartItem newItem = CartItem.builder()
          .cart(cart)
          .variant(variant)
          .lensVariant(lensVariant)
          .quantity(request.getQuantity())
          .build();
      cart.getCartItems().add(newItem);
      cartItemRepository.save(newItem);
    }

    cartRepository.save(cart);
    return ApiResponse.success("Them vao gio hang thanh cong", toResponse(cart));
  }

  public ApiResponse<CartResponse> updateCartItem(User user, UUID cartItemId, UpdateCartItemRequest request) {
    Cart cart = getOrCreateCart(user);

    CartItem item = cartItemRepository.findById(cartItemId)
        .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay san pham trong gio"));

    if (!item.getCart().getCartId().equals(cart.getCartId())) {
      throw new BadRequestException("Khong co quyen chinh sua san pham nay");
    }

    item.setQuantity(request.getQuantity());
    cartItemRepository.save(item);

    return ApiResponse.success("Cap nhat gio hang thanh cong", toResponse(cart));
  }

  public ApiResponse<Void> removeCartItem(User user, UUID cartItemId) {
    Cart cart = getOrCreateCart(user);

    CartItem item = cartItemRepository.findById(cartItemId)
        .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay san pham trong gio"));

    if (!item.getCart().getCartId().equals(cart.getCartId())) {
      throw new BadRequestException("Khong co quyen xoa san pham nay");
    }

    cart.getCartItems().remove(item);
    cartItemRepository.delete(item);

    return ApiResponse.success("Da xoa san pham khoi gio hang", null);
  }

  public ApiResponse<Void> clearCart(User user) {
    Cart cart = getOrCreateCart(user);
    cart.getCartItems().clear();
    cartRepository.save(cart);
    return ApiResponse.success("Da xoa toan bo gio hang", null);
  }

  private ProductVariant resolveVariant(AddCartItemRequest request) {
    if (request.getVariantId() != null) {
      return productVariantRepository.findById(request.getVariantId())
          .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay san pham"));
    }

    if (request.getProductId() == null) {
      throw new BadRequestException("Vui long chon san pham");
    }

    Product product = productRepository.findById(request.getProductId())
        .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay san pham"));
    List<ProductVariant> variants = productVariantRepository.findByProduct_ProductId(product.getProductId());

    if (variants.isEmpty()) {
      throw new BadRequestException("San pham chua co bien the de them vao gio");
    }
    if (variants.size() > 1) {
      throw new BadRequestException("San pham co nhieu bien the, vui long chon variantId");
    }

    return variants.get(0);
  }

  private ProductVariant resolveLensVariant(UUID lensVariantId) {
    if (lensVariantId == null) {
      return null;
    }

    ProductVariant lensVariant = productVariantRepository.findById(lensVariantId)
        .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay trong kinh"));

    if (lensVariant.getProduct() == null
        || !"LENS".equalsIgnoreCase(lensVariant.getProduct().getCategoryType())) {
      throw new BadRequestException("Bien the trong kinh khong hop le");
    }

    return lensVariant;
  }

  private boolean isSameCartItem(CartItem item, UUID variantId, UUID lensVariantId) {
    UUID itemLensVariantId = item.getLensVariant() != null ? item.getLensVariant().getVariantId() : null;
    return item.getVariant().getVariantId().equals(variantId)
        && Objects.equals(itemLensVariantId, lensVariantId);
  }

  private Cart getOrCreateCart(User user) {
    return cartRepository.findByUser(user).orElseGet(() -> {
      Cart newCart = Cart.builder().user(user).build();
      return cartRepository.save(newCart);
    });
  }

  private CartResponse toResponse(Cart cart) {
    if (cart.getCartItems() == null || cart.getCartItems().isEmpty()) {
      return CartResponse.builder()
          .cartId(cart.getCartId())
          .items(List.of())
          .totalItems(0)
          .totalAmount(BigDecimal.ZERO)
          .build();
    }

    List<CartResponse.CartItemResponse> items = cart.getCartItems().stream()
        .map(ci -> {
          ProductVariant v = ci.getVariant();
          ProductVariant lens = ci.getLensVariant();
          String imageUrl = v.getImages() != null
              ? v.getImages().stream()
                  .map(ProductImage::getImageUrl)
                  .findFirst()
                  .orElse(null)
              : null;

          return CartResponse.CartItemResponse.builder()
              .cartItemId(ci.getCartItemId())
              .variantId(v.getVariantId())
              .lensVariantId(lens != null ? lens.getVariantId() : null)
              .productName(v.getProduct().getName())
              .lensProductName(lens != null && lens.getProduct() != null ? lens.getProduct().getName() : null)
              .variantSku(v.getSku())
              .lensVariantSku(lens != null ? lens.getSku() : null)
              .colorName(v.getColorName())
              .productType(v.getProduct().getCategoryType())
              .refractiveIndex(v.getRefractiveIndex())
              .lensRefractiveIndex(lens != null ? lens.getRefractiveIndex() : null)
              .price(calculateUnitPrice(v, lens))
              .quantity(ci.getQuantity() != null ? ci.getQuantity() : 1)
              .imageUrl(imageUrl)
              .isPreorder(v.getIsPreorder())
              .build();
        })
        .collect(Collectors.toList());

    BigDecimal totalAmount = items.stream()
        .map(i -> i.getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    return CartResponse.builder()
        .cartId(cart.getCartId())
        .items(items)
        .totalAmount(totalAmount)
        .totalItems(items.stream().mapToInt(CartResponse.CartItemResponse::getQuantity).sum())
        .build();
  }

  private BigDecimal calculateUnitPrice(ProductVariant variant, ProductVariant lensVariant) {
    BigDecimal price = variant.getSalePrice() != null
        ? variant.getSalePrice()
        : variant.getProduct().getBasePrice();

    if (lensVariant != null) {
      BigDecimal lensPrice = lensVariant.getSalePrice() != null
          ? lensVariant.getSalePrice()
          : lensVariant.getProduct().getBasePrice();
      price = price.add(lensPrice);
    }

    return price;
  }
}
