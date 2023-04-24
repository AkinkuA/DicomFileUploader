package com.pockethealth.dicomfileuploader.controller;

import com.pockethealth.dicomfileuploader.model.DicomFile;
import com.pockethealth.dicomfileuploader.repo.DicomFileRepository;
import com.pockethealth.dicomfileuploader.service.DicomService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@AllArgsConstructor
public class DicomFileController {
    @Autowired
    private DicomFileRepository dicomFileRepository;

    @Autowired
    private DicomService dicomService;

    @PostMapping("/dicom/upload")
    public ResponseEntity<Map<String, Object>> uploadDicomFile(@RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();
        try {
            File dicomFolder = new File("uploaded_dicom_files");
            if (!dicomFolder.exists()) {
                dicomFolder.mkdirs(); // Create the folder if it doesn't exist
            }
            Path dicomStorageLocation = dicomFolder.toPath();
            Path uploadedDicomPath = dicomService.storeDicomFile(file, dicomStorageLocation);

            File pngFolder = new File("created_png_files");
            if (!pngFolder.exists()){
                pngFolder.mkdirs();
            }
            Path pngStorageLocation = pngFolder.toPath();
            Path createdPngPath = dicomService.convertDicomToPng(file.getOriginalFilename(), dicomStorageLocation, pngStorageLocation);

            DicomFile dicomFile = new DicomFile();
            dicomFile.setFileName(file.getOriginalFilename());
            dicomFile.setDicomPath(uploadedDicomPath.toString());
            dicomFile.setPngPath(createdPngPath.toString());
            dicomFile.setCreatedAt(LocalDateTime.now());

            dicomFileRepository.save(dicomFile);

            response.put("message", "File uploaded and information saved in the database.");

            return ResponseEntity.ok(response);
        } catch (DataIntegrityViolationException de) {
            response.put("error", "File has been uploaded previously; please try another file");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (IOException e) {
            response.put("error", "Failed to upload file");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/dicom/{fileName}/attribute")
    public ResponseEntity<Map<String, Object>> getDicomAttribute(
            @PathVariable("fileName") String fileName,
            @RequestParam("tag") String dicomTag) {
        DicomFile dicomFile = dicomFileRepository.findByFileName(fileName);
        Map<String, Object> response = new HashMap<>();
        if (dicomFile == null) {
            response = new HashMap<>();
            response.put("error", "DICOM file not found");
            response.put("fileName", fileName);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        try {
            Path storagePath = Paths.get(dicomFile.getDicomPath()).getParent();
            String attributeValue = dicomService.extractDicomAttribute(fileName, dicomTag, storagePath);

            response.put("fileName", fileName);
            response.put("dicomTag", dicomTag);
            response.put("attributeValue", attributeValue);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            response.put("error", "An error occurred while extracting the attribute");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } catch (IllegalArgumentException e) {
            response.put("error", "Invalid DICOM tag or attribute not found");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/")
    public String root() {
        return "DICOM File Uploader is running.";
    }
}
