package com.swp391.cclearly.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.swp391.cclearly.exception.BadRequestException;
import java.io.IOException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudinaryService {

  private final Cloudinary cloudinary;

  /**
   * Upload image to Cloudinary
   *
   * @param file MultipartFile to upload
   * @param folder Folder name in Cloudinary (e.g., "products", "users")
   * @return URL of the uploaded image
   */
  public String uploadImage(MultipartFile file, String folder) {
    if (file == null || file.isEmpty()) {
      throw new BadRequestException("File không được để trống");
    }

    // Validate file type
    String contentType = file.getContentType();
    if (contentType == null || !contentType.startsWith("image/")) {
      throw new BadRequestException("File phải là hình ảnh");
    }

    try {
      Map<String, Object> uploadResult =
          cloudinary.uploader().upload(
              file.getBytes(),
              ObjectUtils.asMap(
                  "folder", "cclearly/" + folder,
                  "resource_type", "image"));

      String url = (String) uploadResult.get("secure_url");
      log.info("Image uploaded successfully: {}", url);
      return url;
    } catch (IOException e) {
      log.error("Failed to upload image: {}", e.getMessage());
      throw new BadRequestException("Upload ảnh thất bại: " + e.getMessage());
    }
  }

  /**
   * Delete image from Cloudinary by public ID
   *
   * @param publicId Public ID of the image
   */
  public void deleteImage(String publicId) {
    if (publicId == null || publicId.isEmpty()) {
      return;
    }

    try {
      cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
      log.info("Image deleted successfully: {}", publicId);
    } catch (IOException e) {
      log.error("Failed to delete image: {}", e.getMessage());
    }
  }

  /**
   * Extract public ID from Cloudinary URL
   *
   * @param url Cloudinary URL
   * @return Public ID
   */
  public String extractPublicId(String url) {
    if (url == null || url.isEmpty()) {
      return null;
    }

    // URL format: https://res.cloudinary.com/{cloud_name}/image/upload/{version}/{public_id}.{format}
    try {
      String[] parts = url.split("/upload/");
      if (parts.length > 1) {
        String path = parts[1];
        // Remove version if exists (v1234567890/)
        if (path.startsWith("v")) {
          int slashIndex = path.indexOf('/');
          if (slashIndex > 0) {
            path = path.substring(slashIndex + 1);
          }
        }
        // Remove file extension
        int dotIndex = path.lastIndexOf('.');
        if (dotIndex > 0) {
          path = path.substring(0, dotIndex);
        }
        return path;
      }
    } catch (Exception e) {
      log.warn("Failed to extract public ID from URL: {}", url);
    }
    return null;
  }
}
