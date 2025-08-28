package com.juan.img.reader.repository;

import com.juan.img.reader.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface ImageRepository extends JpaRepository<Image, Integer> {
}