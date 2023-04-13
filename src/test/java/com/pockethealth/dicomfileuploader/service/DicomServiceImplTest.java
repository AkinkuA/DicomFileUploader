package com.pockethealth.dicomfileuploader.service;

import org.dcm4che3.io.DicomStreamException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class DicomServiceImplTest {

    private DicomService dicomService;

    @BeforeEach
    void setUp() {
        dicomService = new DicomServiceImpl();
    }

    @Test
    void testStoreDicomFile(@TempDir Path tempDir) throws IOException {
        byte[] mockContent = "This is a mock DICOM file content.".getBytes();

        MultipartFile multipartFile = Mockito.mock(MultipartFile.class);
        Mockito.when(multipartFile.getOriginalFilename()).thenReturn("test.dcm");
        Mockito.when(multipartFile.isEmpty()).thenReturn(false);
        InputStream is = new ByteArrayInputStream(mockContent);
        Mockito.when(multipartFile.getInputStream()).thenReturn(is);

        Path storageLocation = tempDir.resolve("dicom_files");
        Path storedFile = dicomService.storeDicomFile(multipartFile, storageLocation);

        assertNotNull(storedFile);
        assertTrue(Files.exists(storedFile));
    }

    @Test
    void testExtractDicomAttribute(@TempDir Path tempDir) throws IOException {
        // Use the same mock MultipartFile as before
        byte[] mockContent = "This is a mock DICOM file content.".getBytes();

        MultipartFile multipartFile = Mockito.mock(MultipartFile.class);
        Mockito.when(multipartFile.getOriginalFilename()).thenReturn("test.dcm");
        Mockito.when(multipartFile.isEmpty()).thenReturn(false);
        InputStream is = new ByteArrayInputStream(mockContent);
        Mockito.when(multipartFile.getInputStream()).thenReturn(is);

        Path storageLocation = tempDir.resolve("dicom_files");
        Path storedFile = dicomService.storeDicomFile(multipartFile, storageLocation);

        // Use a sample DICOM file and tag for this test
        String dicomTag = "00080020"; // Study Date

        assertThrows(DicomStreamException.class, () -> {
            dicomService.extractDicomAttribute("test.dcm", dicomTag, storageLocation);
        });
    }

    @Test
    void testExtractDicomAttribute2(@TempDir Path tempDir) throws IOException {
        String fileName = "IM000018";
        Files.copy(Path.of("src/main/resources/static/IM000018"), tempDir.resolve(fileName));

        String patientName = dicomService.extractDicomAttribute(fileName, "00100010", tempDir);

        assertNotNull(patientName);
        assertFalse(patientName.isEmpty());
    }

    @Test
    void testConvertDicomToPng(@TempDir Path tempDir) throws IOException {
        String fileName = "IM000018";
        Files.copy(Path.of("src/main/resources/static/IM000018"), tempDir.resolve(fileName));

        Path pngPath = dicomService.convertDicomToPng(fileName, tempDir, tempDir);

        assertNotNull(pngPath);
        assertTrue(Files.exists(pngPath));
        assertTrue(pngPath.toString().endsWith(".png"));
    }
}
