package com.healthrx.backend.controller;

import com.healthrx.backend.api.external.image.ImageRequest;
import com.healthrx.backend.api.external.image.ImageResponse;
import com.healthrx.backend.api.internal.enums.ImageType;
import com.healthrx.backend.service.impl.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/image")
@RequiredArgsConstructor
@Tag(name = "Image controller", description = "Controller for managing images")
public class ImageController {
    private final ImageService imageService;

    @PostMapping("/get")
    @Operation(summary = "Fetching images", description = "Fetching images")
    public ResponseEntity<List<ImageResponse>> getPictures(@RequestBody ImageRequest req) {
        return ResponseEntity.ok(imageService.getPictures(req, Optional.empty()));
    }

    @PostMapping()
    @Operation(summary = "Saving images", description = "Saving images")
    public ResponseEntity<List<ImageResponse>> save(@RequestParam String email, final @RequestParam("images") List<MultipartFile> files, final @RequestParam("imageTypes") List<ImageType> types) {
        return ResponseEntity.ok(imageService.save(email, types, files));
    }

    @DeleteMapping("/profile")
    @Operation(summary = "Remove profile picture", description = "Remove profile picture")
    public ResponseEntity<Void> deleteProfilePicture() {
        return ResponseEntity.ok(imageService.deleteProfilePicture());
    }
}
