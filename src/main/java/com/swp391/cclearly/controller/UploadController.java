package com.swp391.cclearly.controller;

import com.swp391.cclearly.dto.base.ApiResponse;
import com.swp391.cclearly.service.CloudinaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
@Tag(name = "Upload", description = "APIs upload ảnh lên Cloudinary")
public class UploadController {

  private final CloudinaryService cloudinaryService;

  @Operation(summary = "Upload một ảnh")
  @SecurityRequirement(name = "bearerAuth")
  @PostMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<ApiResponse<String>> uploadImage(
      @RequestParam("file") MultipartFile file,
      @RequestParam(value = "folder", defaultValue = "products") String folder) {
    String url = cloudinaryService.uploadImage(file, folder);
    return ResponseEntity.ok(ApiResponse.success("Upload ảnh thành công", url));
  }

  @Operation(summary = "Upload nhiều ảnh")
  @SecurityRequirement(name = "bearerAuth")
  @PostMapping(value = "/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<ApiResponse<List<String>>> uploadImages(
      @RequestParam("files") MultipartFile[] files,
      @RequestParam(value = "folder", defaultValue = "products") String folder) {
    List<String> urls = new ArrayList<>();
    for (MultipartFile file : files) {
      urls.add(cloudinaryService.uploadImage(file, folder));
    }
    return ResponseEntity.ok(ApiResponse.success("Upload ảnh thành công", urls));
  }

  @Operation(summary = "Xóa ảnh")
  @SecurityRequirement(name = "bearerAuth")
  @DeleteMapping("/image")
  public ResponseEntity<ApiResponse<Void>> deleteImage(@RequestParam("url") String url) {
    String publicId = cloudinaryService.extractPublicId(url);
    cloudinaryService.deleteImage(publicId);
    return ResponseEntity.ok(ApiResponse.success("Xóa ảnh thành công", null));
  }
}
