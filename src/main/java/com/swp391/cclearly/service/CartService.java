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
    return ApiResponse.success("Lấy giỏ hàng thành công", toResponse(cart));
  }

  public ApiResponse<CartResponse> addToCart(User user, AddCartItemRequest request) {
    Cart cart = getOrCreateCart(user);

    // Resolve variant: use variantId if provided, otherwise look up by productId
    ProductVariant variant;
    if (request.getVariantId() != null) {
      variant = productVariantRepository.findById(request.getVariantId())
          .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm"));
    } else if (request.getProductId() != null) {
      Product product = productRepository.findById(request.getProductId())
          .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm"));
      // Find existing variant or auto-create a default one
      variant = productVariantRepository.findAll().stream()
          .filter(v -> v.getProduct().getProductId().equals(request.getProductId()))
          .findFirst()
          .orElseGet(() -> {
            ProductVariant defaultVariant = ProductVariant.builder()
                .product(product)
                .sku(product.getName() != null ? product.getName().substring(0, Math.min(product.getName().length(), 10)).toUpperCase().replaceAll("\\s+", "-") + "-DEFAULT" : "DEFAULT")
                .colorName("Mặc định")
                .salePrice(product.getBasePrice())
                .isPreorder(false)
                .build();
            return productVariantRepository.save(defaultVariant);
          });
    } else {
      throw new BadRequestException("Vui lòng chọn sản phẩm");
    }

    UUID variantId = variant.getVariantId();

    // If item with same variant already exists, increase quantity
    Optional<CartItem> existingItem = cart.getCartItems().stream()
        .filter(ci -> ci.getVariant().getVariantId().equals(variantId))
        .findFirst();

    if (existingItem.isPresent()) {
      CartItem item = existingItem.get();
      item.setQuantity(item.getQuantity() + request.getQuantity());
      cartItemRepository.save(item);
    } else {
      CartItem newItem = CartItem.builder()
          .cart(cart)
          .variant(variant)
          .quantity(request.getQuantity())
          .build();

      if (request.getLensVariantId() != null) {
        ProductVariant lensVariant = productVariantRepository.findById(request.getLensVariantId())
            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tròng kính"));
        newItem.setLensVariant(lensVariant);
      }

      cart.getCartItems().add(newItem);
      cartItemRepository.save(newItem);
    }

    cartRepository.save(cart);
    return ApiResponse.success("Thêm vào giỏ hàng thành công", toResponse(cart));
  }

  public ApiResponse<CartResponse> updateCartItem(User user, UUID cartItemId, UpdateCartItemRequest request) {
    Cart cart = getOrCreateCart(user);

    CartItem item = cartItemRepository.findById(cartItemId)
        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm trong giỏ"));

    if (!item.getCart().getCartId().equals(cart.getCartId())) {
      throw new BadRequestException("Không có quyền chỉnh sửa sản phẩm này");
    }

    item.setQuantity(request.getQuantity());
    cartItemRepository.save(item);

    return ApiResponse.success("Cập nhật giỏ hàng thành công", toResponse(cart));
  }

  public ApiResponse<Void> removeCartItem(User user, UUID cartItemId) {
    Cart cart = getOrCreateCart(user);

    CartItem item = cartItemRepository.findById(cartItemId)
        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm trong giỏ"));

    if (!item.getCart().getCartId().equals(cart.getCartId())) {
      throw new BadRequestException("Không có quyền xóa sản phẩm này");
    }

    cart.getCartItems().remove(item);
    cartItemRepository.delete(item);

    return ApiResponse.success("Đã xóa sản phẩm khỏi giỏ hàng", null);
  }

  public ApiResponse<Void> clearCart(User user) {
    Cart cart = getOrCreateCart(user);
    cart.getCartItems().clear();
    cartRepository.save(cart);
    return ApiResponse.success("Đã xóa toàn bộ giỏ hàng", null);
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
          String imageUrl = v.getImages() != null
              ? v.getImages().stream()
                  .map(ProductImage::getImageUrl)
                  .findFirst()
                  .orElse(null)
              : null;

          BigDecimal price = v.getSalePrice() != null ? v.getSalePrice() : v.getProduct().getBasePrice();

          return CartResponse.CartItemResponse.builder()
              .cartItemId(ci.getCartItemId())
              .variantId(v.getVariantId())
              .productName(v.getProduct().getName())
              .variantSku(v.getSku())
              .colorName(v.getColorName())
              .productType(v.getProduct().getCategoryType())
              .refractiveIndex(v.getRefractiveIndex())
              .price(price)
              .quantity(ci.getQuantity())
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
}
