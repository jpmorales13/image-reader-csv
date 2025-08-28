package com.juan.img.reader.controllers;


import com.juan.img.reader.model.Image;
import com.juan.img.reader.service.ImageService;
import com.juan.img.reader.service.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.nio.file.Path;
import java.util.*;

import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
public class ImageReaderController {

    @Autowired private ResourceLoader resourceLoader;
    @Autowired private JobLauncher jobLauncher;
    @Autowired private Job importJob;

    private final StorageService storageService;
    private final ImageService imageService;

    @Autowired public ImageReaderController(StorageService storageService, ImageService imageService) {
        this.storageService = storageService;
        this.imageService = imageService;
    }

    @GetMapping("/images")
    public Page<Image> returnImages(Pageable pageable) {
       // Page<Image> images =

            return this.imageService.getImages(pageable);


       // return images;
    }

    @GetMapping("/images/{imageId}")
    public Image returnImagesById(@PathVariable String imageId) {
        if (imageId != null && !imageId.isEmpty()) {
            return this.imageService.getImageWithId(Integer.parseInt(imageId));
        }
        return new Image();
    }

    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "File is empty"));
        }

        Path targetPath = storageService.store(file);

        JobParameters params = new JobParametersBuilder()
                .addString("file", targetPath.toString())
                .addLong("ts", System.currentTimeMillis())
                .toJobParameters();

        JobExecution exec = jobLauncher.run(importJob, params);

       // log.info("Job execution started: " + exec.getExitStatus());
        Map<String, Object> res = new HashMap<>();
        res.put("status", exec.getStatus().toString());
        res.put("startTime", exec.getStartTime());
        res.put("endTime", exec.getEndTime());
        res.put("jobId", exec.getJobId());
        return ResponseEntity.ok(res);
    }
}
