package org.devignite.springgradle.controller;

import lombok.SneakyThrows;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

@RestController
@RequestMapping("/api/file")
@Controller
public class FileController {

    @GetMapping
    public String Get() {
        return "OK";
    }

    @GetMapping(value = "/loadFromDisk", produces = {"application/octet-stream"})
    @SneakyThrows
    public ResponseEntity<FileSystemResource> loadFromDisk() {
        String fileName = createTempFile();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(ContentDisposition.attachment().filename(fileName).build());
        headers.setContentType(MediaType.parseMediaType("application/text"));
        return new ResponseEntity(new FileSystemResource(new File(fileName)), headers, HttpStatus.OK);
    }

    @GetMapping(value = "/loadFromByte", produces = {"application/octet-stream"})
    @SneakyThrows
    public ResponseEntity<byte[]> loadFromByte() {
        File tempFile = createFile();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(ContentDisposition.attachment().filename(tempFile.getName()).build());
        headers.setContentType(MediaType.parseMediaType("application/text"));
        byte[] fileBytes = new byte[(int) tempFile.length()];
        try (InputStream str = new FileInputStream(tempFile)) {
            IOUtils.readFully(str, fileBytes);
        }

        return new ResponseEntity(fileBytes, headers, HttpStatus.OK);
    }

    @GetMapping(value = "/loadFromStream", produces = {"application/octet-stream"})
    @SneakyThrows
    public void loadFromStream(HttpServletResponse response) {
        String fileName = createTempFile();
        response.addHeader("Content-Type", "application/text");
        response.addHeader("Content-Disposition", ContentDisposition.attachment().filename(fileName).build().toString());
        try (OutputStream responseStream = response.getOutputStream()) {
            try (InputStream inputStream = new FileInputStream(new File(fileName))) {
                IOUtils.copy(inputStream, responseStream);
            }
            response.flushBuffer();
        }
    }

    @SneakyThrows
    private String createTempFile() {
        String fileName = "tempFileSystemResource.txt";
        File file = new File(fileName);
        file.createNewFile();
        OutputStream out = new FileOutputStream(file);
        out.write("Something Text".getBytes());
        out.close();

        return fileName;
    }

    @SneakyThrows
    private File createFile() {
        return new File(createTempFile());
    }


}
