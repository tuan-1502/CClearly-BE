package com.swp391.cclearly.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.swp391.cclearly.dto.base.ApiResponse;
import com.swp391.cclearly.dto.refund.RefundResponse;
import com.swp391.cclearly.entity.Order;
import com.swp391.cclearly.entity.Refund;
import com.swp391.cclearly.repository.OrderRepository;
import com.swp391.cclearly.repository.RefundRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RefundServiceTest {

  @Mock private RefundRepository refundRepository;
  @Mock private OrderRepository orderRepository;
  @Mock private InventoryService inventoryService;

  @InjectMocks private RefundService refundService;

  @Test
  void completeReturn_shouldReleaseStockAndMarkOrderReturned() {
    Order order = Order.builder()
        .status("RETURN_REQUESTED")
        .build();
    Refund refund = Refund.builder()
        .refundId(UUID.randomUUID())
        .status("APPROVED")
        .order(order)
        .build();

    when(refundRepository.findById(refund.getRefundId())).thenReturn(Optional.of(refund));
    when(refundRepository.save(refund)).thenReturn(refund);
    when(orderRepository.save(order)).thenReturn(order);

    ApiResponse<RefundResponse> response = refundService.completeReturn(refund.getRefundId());

    verify(inventoryService).releaseStockForOrderItems(order.getOrderItems(), "RETURN_COMPLETED");
    assertThat(order.getStatus()).isEqualTo("RETURNED");
    assertThat(response.getData().getStatus()).isEqualTo("COMPLETED");
  }
}
