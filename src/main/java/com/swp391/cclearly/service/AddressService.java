package com.swp391.cclearly.service;

import com.swp391.cclearly.dto.address.AddressRequest;
import com.swp391.cclearly.dto.address.AddressResponse;
import com.swp391.cclearly.dto.base.ApiResponse;
import com.swp391.cclearly.entity.Address;
import com.swp391.cclearly.entity.User;
import com.swp391.cclearly.repository.AddressRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AddressService {

    private final AddressRepository addressRepository;

    @Transactional(readOnly = true)
    public ApiResponse<List<AddressResponse>> getAddresses(User user) {
        List<AddressResponse> addresses = addressRepository.findByUser(user)
                .stream()
                .map(this::toResponse)
                .toList();
        return ApiResponse.success("Lấy danh sách địa chỉ thành công", addresses);
    }

    public ApiResponse<AddressResponse> createAddress(User user, AddressRequest request) {
        // If this is set as default, unset others
        if (Boolean.TRUE.equals(request.getIsDefault())) {
            unsetDefaultAddresses(user);
        }

        // If user has no addresses, make this the default
        List<Address> existing = addressRepository.findByUser(user);
        boolean shouldBeDefault = existing.isEmpty() || Boolean.TRUE.equals(request.getIsDefault());

        Address address = Address.builder()
                .user(user)
                .name(request.getName())
                .phone(request.getPhone())
                .street(request.getAddress())
                .isDefault(shouldBeDefault)
                .build();

        addressRepository.save(address);
        return ApiResponse.success("Thêm địa chỉ thành công", toResponse(address));
    }

    public ApiResponse<AddressResponse> updateAddress(User user, UUID addressId, AddressRequest request) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy địa chỉ"));

        if (!address.getUser().getUserId().equals(user.getUserId())) {
            throw new RuntimeException("Bạn không có quyền cập nhật địa chỉ này");
        }

        if (Boolean.TRUE.equals(request.getIsDefault())) {
            unsetDefaultAddresses(user);
        }

        address.setName(request.getName());
        address.setPhone(request.getPhone());
        address.setStreet(request.getAddress());
        address.setIsDefault(request.getIsDefault() != null ? request.getIsDefault() : address.getIsDefault());

        addressRepository.save(address);
        return ApiResponse.success("Cập nhật địa chỉ thành công", toResponse(address));
    }

    public ApiResponse<Void> deleteAddress(User user, UUID addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy địa chỉ"));

        if (!address.getUser().getUserId().equals(user.getUserId())) {
            throw new RuntimeException("Bạn không có quyền xóa địa chỉ này");
        }

        boolean wasDefault = Boolean.TRUE.equals(address.getIsDefault());
        addressRepository.delete(address);

        // If deleted address was default, set another as default
        if (wasDefault) {
            List<Address> remaining = addressRepository.findByUser(user);
            if (!remaining.isEmpty()) {
                remaining.get(0).setIsDefault(true);
                addressRepository.save(remaining.get(0));
            }
        }

        return ApiResponse.success("Xóa địa chỉ thành công");
    }

    public ApiResponse<AddressResponse> setDefault(User user, UUID addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy địa chỉ"));

        if (!address.getUser().getUserId().equals(user.getUserId())) {
            throw new RuntimeException("Bạn không có quyền thay đổi địa chỉ này");
        }

        unsetDefaultAddresses(user);
        address.setIsDefault(true);
        addressRepository.save(address);

        return ApiResponse.success("Đã đặt làm địa chỉ mặc định", toResponse(address));
    }

    private void unsetDefaultAddresses(User user) {
        addressRepository.findByUser(user).forEach(a -> {
            if (Boolean.TRUE.equals(a.getIsDefault())) {
                a.setIsDefault(false);
                addressRepository.save(a);
            }
        });
    }

    private AddressResponse toResponse(Address address) {
        return AddressResponse.builder()
                .addressId(address.getAddressId())
                .name(address.getName())
                .phone(address.getPhone())
                .address(address.getStreet())
                .isDefault(address.getIsDefault())
                .build();
    }
}
