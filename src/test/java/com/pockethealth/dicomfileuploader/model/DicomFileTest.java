package com.pockethealth.dicomfileuploader.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DicomFileTest {

    private DicomFile dicomFile;

    @BeforeEach
    void setUp() {
        dicomFile = new DicomFile();
    }

    @Test
    void testId() {
        Long id = 1L;
        dicomFile.setId(id);
        assertEquals(id, dicomFile.getId());
    }

    @Test
    void testFileName() {
        String fileName = "test.dcm";
        dicomFile.setFileName(fileName);
        assertEquals(fileName, dicomFile.getFileName());
    }

    @Test
    void testDicomPath() {
        String dicomPath = "/path/to/dicom/file";
        dicomFile.setDicomPath(dicomPath);
        assertEquals(dicomPath, dicomFile.getDicomPath());
    }

    @Test
    void testPngPath() {
        String pngPath = "/path/to/png/file";
        dicomFile.setPngPath(pngPath);
        assertEquals(pngPath, dicomFile.getPngPath());
    }

    @Test
    void testCreatedAt() {
        LocalDateTime now = LocalDateTime.now();
        dicomFile.setCreatedAt(now);
        assertEquals(now, dicomFile.getCreatedAt());
    }

    @Test
    void testOnCreate() {
        assertNull(dicomFile.getCreatedAt());
        dicomFile.onCreate();
        assertNotNull(dicomFile.getCreatedAt());
    }
}
