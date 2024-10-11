package com.healthrx.backend.api.external.image;

import com.healthrx.backend.api.internal.enums.ImageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageResponse {
    private ImageType imageType;
    private byte[] image;
}
