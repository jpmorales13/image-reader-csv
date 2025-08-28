package com.juan.img.reader.service;

import com.juan.img.reader.model.Image;
import com.juan.img.reader.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DatabaseImageService implements ImageService {
    @Autowired
    private ImageRepository imageRepository;

    @Override
    public Page<Image> getImages(Pageable page) {
        return this.imageRepository.findAll(page);
    }

    @Override
    public Image getImageWithId(Integer id) {
        Optional<Image> img = this.imageRepository.findById(id);
        return img.orElse(null);
    }

    @Override
    public Image saveImage(Image image) {
        return this.imageRepository.save(image);
    }
}
