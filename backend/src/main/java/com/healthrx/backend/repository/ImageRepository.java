package com.healthrx.backend.repository;

import com.healthrx.backend.api.internal.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, String> {

}
