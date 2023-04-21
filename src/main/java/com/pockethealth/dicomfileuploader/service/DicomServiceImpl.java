package com.pockethealth.dicomfileuploader.service;

import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.imageio.plugins.dcm.DicomImageReadParam;
import org.dcm4che3.io.DicomInputStream;
import org.dcm4che3.util.SafeClose;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Iterator;
import java.util.Objects;

@Service
public class DicomServiceImpl implements DicomService{
    @Override
    public Path storeDicomFile(MultipartFile file, Path storageLocation) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("Failed to store empty file.");
        }
        if (Objects.equals(file.getOriginalFilename(), "")){
            throw new IOException("No filename detected");
        }

        try {
            if (!Files.exists(storageLocation)) {
                Files.createDirectories(storageLocation);
            }

            Path targetLocation = storageLocation.resolve(file.getOriginalFilename());
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return targetLocation;
        } catch (IOException e) {
            throw new IOException("Failed to store file.", e);
        }
    }

    @Override
    public String extractDicomAttribute(String fileName, String dicomTag, Path storageLocation) throws IOException {
        // Construct the file path for the DICOM file
        Path dicomPath = storageLocation.resolve(fileName);
        File dicomFile = dicomPath.toFile();

        // Check if the file exists
        if (!dicomFile.exists()) {
            throw new FileNotFoundException("File not found: " + fileName);
        }

        // Read the DICOM file and extract the attribute
        try (DicomInputStream dis = new DicomInputStream(dicomFile)) {
            Attributes attrs = dis.readDataset(-1, -1);
            int dicomTagInt = Integer.parseInt(dicomTag, 16);
            String attributeValue = attrs.getString(dicomTagInt, "");

            if (attributeValue == null || attributeValue.isEmpty()) {
                throw new IllegalArgumentException("Attribute not found for tag: " + dicomTag);
            }

            return attributeValue;
        }
    }

    @Override
    public Path convertDicomToPng(String dicomFileName, Path dicomStorageLocation, Path pngStorageLocation) throws IOException {
        Path dicomPath = dicomStorageLocation.resolve(dicomFileName);
        DicomInputStream dis = null;
        BufferedImage image;

        try {
            dis = new DicomInputStream(dicomPath.toFile());
            dis.readDataset(-1, Tag.PixelData);
            dis.close();

            Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName("DICOM");
            ImageReader reader = readers.next();
            DicomImageReadParam param = (DicomImageReadParam) reader.getDefaultReadParam();
            ImageInputStream iis = ImageIO.createImageInputStream(dicomPath.toFile());
            reader.setInput(iis, false);

            image = reader.read(0, param);
            if (image == null) {
                throw new IOException("Failed to convert DICOM to PNG: Invalid DICOM file");
            }
            iis.close();
        } finally {
            SafeClose.close(dis);
        }
        String pngFileName = "";
        if (dicomFileName.lastIndexOf('.') != -1){
            pngFileName = dicomFileName.substring(0, dicomFileName.lastIndexOf('.')) + ".png";
        } else {
            pngFileName = dicomFileName + ".png";
        }

        Path pngPath = pngStorageLocation.resolve(pngFileName);
        File pngFile = pngPath.toFile();
        ImageIO.write(image, "PNG", pngFile);

        return pngPath;
    }
}
