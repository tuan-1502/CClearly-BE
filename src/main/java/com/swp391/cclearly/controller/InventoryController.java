package com.swp391.cclearly.controller;

import com.swp391.cclearly.dto.base.ApiResponse;
import com.swp391.cclearly.dto.inventory.ImportStockRequest;
import com.swp391.cclearly.dto.inventory.InventoryResponse;
import com.swp391.cclearly.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Inventory", description = "APIs quản lý tồn kho")
public class InventoryController {

  private final InventoryService inventoryService;

  @Operation(summary = "Lấy danh sách tồn kho")
  @GetMapping
  public ResponseEntity<ApiResponse<List<InventoryResponse>>> getInventory(
      @RequestParam(required = false) String search,
      @RequestParam(required = false) UUID warehouseId) {
    return ResponseEntity.ok(inventoryService.getInventory(search, warehouseId));
  }

  @Operation(summary = "Nhập kho")
  @PostMapping("/import")
  public ResponseEntity<ApiResponse<InventoryResponse>> importStock(
      @Valid @RequestBody ImportStockRequest request) {
    return ResponseEntity.ok(inventoryService.importStock(request));
  }
}
