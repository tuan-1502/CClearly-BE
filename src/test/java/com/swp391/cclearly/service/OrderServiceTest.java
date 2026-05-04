package com.swp391.cclearly.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.swp391.cclearly.dto.base.ApiResponse;
import com.swp391.cclearly.dto.order.CreateOrderRequest;
import com.swp391.cclearly.dto.order.OrderResponse;
import com.swp391.cclearly.entity.Address;
import com.swp391.cclearly.entity.Cart;
import com.swp391.cclearly.entity.CartItem;
import com.swp391.cclearly.entity.Order;
import com.swp391.cclearly.entity.Product;
import com.swp391.cclearly.entity.ProductVariant;
import com.swp391.cclearly.entity.Role;
import com.swp391.cclearly.entity.User;
import com.swp391.cclearly.repository.AddressRepository;
import com.swp391.cclearly.repository.CartRepository;
import com.swp391.cclearly.repository.OrderItemRepository;
import com.swp391.cclearly.repository.OrderRepository;
import com.swp391.cclearly.repository.RefundRepository;
import com.swp391.cclearly.repository.SystemConfigRepository;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

  @Mock private OrderRepository orderRepository;
  @Mock private CartRepository cartRepository;
  @Mock private AddressRepository addressRepository;
  @Mock private RefundRepository refundRepository;
  @Mock private OrderItemRepository orderItemRepository;
  @Mock private SystemConfigRepository systemConfigRepository;
  @Mock private InventoryService inventoryService;
  @Mock private PromotionValidationService promotionValidationService;
  @Mock private CartService cartService;

  @InjectMocks private OrderService orderService;

  @Test
  void createOrder_onlinePayment_staysPendingUntilCallback() {
    User user = User.builder()
        .userId(UUID.randomUUID())
        .role(Role.builder().roleName("CUSTOMER").build())
        .build();
    Address address = Address.builder()
        .addressId(UUID.randomUUID())
        .user(user)
        .street("Street 1")
        .city("HCM")
        .build();
    Product product = Product.builder()
        .name("Frame")
        .basePrice(new BigDecimal("100000"))
        .categoryType("FRAME")
        .build();
    ProductVariant variant = ProductVariant.builder()
        .variantId(UUID.randomUUID())
        .product(product)
        .sku("SKU-1")
        .salePrice(new BigDecimal("120000"))
        .images(new HashSet<>())
        .build();
    CartItem cartItem = CartItem.builder()
        .cartItemId(UUID.randomUUID())
        .variant(variant)
        .quantity(1)
        .build();
    Cart cart = Cart.builder()
        .cartItems(new HashSet<>())
        .build();
    cart.getCartItems().add(cartItem);

    CreateOrderRequest request = new CreateOrderRequest();
    request.setAddressId(address.getAddressId());
    request.setPaymentMethod("PAYOS");

    when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
    when(addressRepository.findById(address.getAddressId())).thenReturn(Optional.of(address));
    when(systemConfigRepository.findByConfigKey(any())).thenReturn(Optional.empty());
    when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

    ApiResponse<OrderResponse> response = orderService.createOrder(user, request);

    ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
    verify(orderRepository, times(2)).save(orderCaptor.capture());
    Order savedOrder = orderCaptor.getAllValues().get(1);

    assertThat(savedOrder.getPayments()).hasSize(1);
    assertThat(savedOrder.getPayments().iterator().next().getStatus()).isEqualTo("PENDING");
    assertThat(response.getData().getPaymentMethod()).isEqualTo("PAYOS");
    assertThat(response.getData().getPaidAmount()).isEqualByComparingTo(BigDecimal.ZERO);
  }
}
