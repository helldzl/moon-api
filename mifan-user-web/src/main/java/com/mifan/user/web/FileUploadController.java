package com.mifan.user.web;

import org.moonframework.web.jsonapi.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/3/27
 */
// @RestController
public class FileUploadController {

    @PostMapping("/upload")
    public ResponseEntity<Response> handleFormUpload(
            @RequestParam("name") String name,
            @RequestParam("file") MultipartFile file) throws IOException {
        if (!file.isEmpty()) {
            byte[] bytes = file.getBytes();
            // store the bytes somewhere
            return ResponseEntity.ok(null);
        }
        return ResponseEntity.badRequest().body(null);
    }

}
