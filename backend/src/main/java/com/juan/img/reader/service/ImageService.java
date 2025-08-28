package com.juan.img.reader.service;

import com.juan.img.reader.model.Image;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Set;

public interface ImageService {
    Page<Image> getImages(Pageable pageable);
    Image getImageWithId(Integer id);
    Image saveImage(Image image);
}
