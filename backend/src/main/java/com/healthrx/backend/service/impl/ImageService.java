package com.healthrx.backend.service.impl;

import com.healthrx.backend.api.external.image.ImageResponse;
import com.healthrx.backend.api.internal.DoctorDetails;
import com.healthrx.backend.api.internal.Image;
import com.healthrx.backend.api.internal.User;
import com.healthrx.backend.api.internal.enums.ImageType;
import com.healthrx.backend.repository.DoctorDetailsRepository;
import com.healthrx.backend.repository.ImageRepository;
import com.healthrx.backend.repository.UserRepository;
import com.healthrx.backend.security.aes.AesHandler;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import static com.healthrx.backend.handler.BusinessErrorCodes.DOCTOR_DETAILS_NOT_FOUND;
import static com.healthrx.backend.handler.BusinessErrorCodes.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageService {
    private final UserRepository userRepository;
    private final DoctorDetailsRepository doctorDetailsRepository;
    private final ImageRepository imageRepository;
    private final Supplier<User> principalSupplier;

    @SneakyThrows
    @Transactional
    public List<ImageResponse> save(String email, List<ImageType> types, List<MultipartFile> files) {
        User user = userRepository.findUserByEmail(email).orElseThrow(USER_NOT_FOUND::getError);
        List<ImageResponse> res = new ArrayList<>();

        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);
            ImageType type = types.get(i);
            byte[] content = file.getBytes();
            handleSaveImage(user, type, content);
            res.add(ImageResponse.builder()
                    .imageType(type)
                    .image(content)
                    .build());
        }

        return res;
    }

    @Transactional
    public ImageResponse getProfilePicture() {
        User user = principalSupplier.get();

        Image image = null;
        if (user.getProfilePicture() != null){
            image = imageRepository.findById(user.getProfilePicture().getId()).orElse(null);
        }

        return ImageResponse.builder()
                .imageType(ImageType.PROFILE)
                .image(image != null ? AesHandler.decrypt(image.getContent()): null)
                .build();
    }

    private void handleSaveImage(User user, ImageType type, byte[] content) {
        byte[] encryptedContent = AesHandler.encrypt(content);

        switch (type) {
            case PROFILE:
                Image profilePicture = user.getProfilePicture();

                if (profilePicture != null) {
                    profilePicture.setContent(encryptedContent);
                    imageRepository.save(profilePicture);
                } else {
                    profilePicture = Image.builder()
                            .content(encryptedContent)
                            .build();
                    imageRepository.save(profilePicture);
                    user.setProfilePicture(profilePicture);
                }
                userRepository.save(user);
                break;

            case FRONT_PWZ_PHOTO:
            case BACK_PWZ_PHOTO:
                DoctorDetails doctorDetails = doctorDetailsRepository.findByUserId(user.getId()).orElseThrow(DOCTOR_DETAILS_NOT_FOUND::getError);

                if (type == ImageType.FRONT_PWZ_PHOTO) {
                    Image frontPWZ = doctorDetails.getFrontPwzCardImage();

                    if (frontPWZ != null) {
                        frontPWZ.setContent(encryptedContent);
                        imageRepository.save(frontPWZ);
                    } else {
                        frontPWZ = Image.builder()
                                .content(encryptedContent)
                                .build();
                        imageRepository.save(frontPWZ);
                        doctorDetails.setFrontPwzCardImage(frontPWZ);
                    }
                } else {
                    Image backPWZ = doctorDetails.getBackPwzCardImage();

                    if (backPWZ != null) {
                        backPWZ.setContent(encryptedContent);
                        imageRepository.save(backPWZ);
                    } else {
                        backPWZ = Image.builder()
                                .content(encryptedContent)
                                .build();
                        imageRepository.save(backPWZ);
                        doctorDetails.setBackPwzCardImage(backPWZ);
                    }
                }

                doctorDetailsRepository.save(doctorDetails);
                break;
        }
    }
}
