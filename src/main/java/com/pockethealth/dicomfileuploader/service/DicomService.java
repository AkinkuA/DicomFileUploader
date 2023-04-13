package com.pockethealth.dicomfileuploader.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;

public interface DicomService {
    Path storeDicomFile(MultipartFile file, Path storageLocation) throws IOException;
    String extractDicomAttribute(String fileName, String dicomTag, Path storageLocation) throws IOException;
    Path convertDicomToPng(String dicomFileName, Path dicomStorageLocation, Path pngStorageLocation) throws IOException;

}
