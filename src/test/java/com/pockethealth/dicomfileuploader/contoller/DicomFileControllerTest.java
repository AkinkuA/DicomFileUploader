package com.pockethealth.dicomfileuploader.contoller;

import com.pockethealth.dicomfileuploader.controller.DicomFileController;
import com.pockethealth.dicomfileuploader.model.DicomFile;
import com.pockethealth.dicomfileuploader.repo.DicomFileRepository;
import com.pockethealth.dicomfileuploader.service.DicomService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.file.Paths;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class DicomFileControllerTest {

    @Mock
    private DicomFileRepository dicomFileRepository;

    @Mock
    private DicomService dicomService;

    @InjectMocks
    private DicomFileController dicomFileController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(dicomFileController).build();
    }

    @Test
    void testUploadDicomFile() throws Exception {
        // Prepare test data
        String fileName = "test.dcm";
        MockMultipartFile file = new MockMultipartFile("file", fileName, "application/dicom", "test-data".getBytes());
        String uploadedDicomPath = "uploaded_dicom_files/test.dcm";
        String createdPngPath = "created_png_files/test.png";

        when(dicomService.storeDicomFile(any(), any())).thenReturn(Paths.get(uploadedDicomPath));
        when(dicomService.convertDicomToPng(eq(fileName), any(), any())).thenReturn(Paths.get(createdPngPath));

        // Test the POST request
        mockMvc.perform(multipart("/dicom/upload").file(file))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("File uploaded and information saved in the database."));

        // Verify the interactions with the mocks
        verify(dicomService, times(1)).storeDicomFile(any(), any());
        verify(dicomService, times(1)).convertDicomToPng(eq(fileName), any(), any());
        verify(dicomFileRepository, times(1)).save(any(DicomFile.class));
    }

    @Test
    void testGetDicomAttribute() throws Exception {
        // Prepare test data
        String fileName = "test.dcm";
        String dicomTag = "00080020";
        String attributeValue = "20211022";

        DicomFile dicomFile = new DicomFile();
        dicomFile.setFileName(fileName);
        dicomFile.setDicomPath("uploaded_dicom_files/test.dcm");

        when(dicomFileRepository.findByFileName(fileName)).thenReturn(dicomFile);
        when(dicomService.extractDicomAttribute(fileName, dicomTag, Paths.get(dicomFile.getDicomPath()).getParent())).thenReturn(attributeValue);

        // Test the GET request
        mockMvc.perform(get("/dicom/{fileName}/attribute", fileName)
                        .param("tag", dicomTag))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.fileName").value(fileName))
                .andExpect(jsonPath("$.dicomTag").value(dicomTag))
                .andExpect(jsonPath("$.attributeValue").value(attributeValue));
        // Verify the interactions with the mocks
        verify(dicomFileRepository, times(1)).findByFileName(fileName);
        verify(dicomService, times(1)).extractDicomAttribute(fileName, dicomTag, Paths.get(dicomFile.getDicomPath()).getParent());
    }

    @Test
    void testGetDicomAttribute_NotFound() throws Exception {
        // Prepare test data
        String fileName = "non_existent.dcm";
        String dicomTag = "00080020";

        when(dicomFileRepository.findByFileName(fileName)).thenReturn(null);

        // Test the GET request
        mockMvc.perform(get("/dicom/{fileName}/attribute", fileName)
                        .param("tag", dicomTag))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("DICOM file not found"))
                .andExpect(jsonPath("$.fileName").value(fileName));

        // Verify the interactions with the mocks
        verify(dicomFileRepository, times(1)).findByFileName(fileName);
    }

    @Test
    void testGetDicomAttribute_BadRequest() throws Exception {
        // Prepare test data
        String fileName = "test.dcm";
        String dicomTag = "00080020";
        String exceptionMessage = "Invalid DICOM tag or attribute not found";

        DicomFile dicomFile = new DicomFile();
        dicomFile.setFileName(fileName);
        dicomFile.setDicomPath("uploaded_dicom_files/test.dcm");

        when(dicomFileRepository.findByFileName(fileName)).thenReturn(dicomFile);
        when(dicomService.extractDicomAttribute(fileName, dicomTag, Paths.get(dicomFile.getDicomPath()).getParent())).thenThrow(new IllegalArgumentException(exceptionMessage));

        // Test the GET request
        mockMvc.perform(get("/dicom/{fileName}/attribute", fileName)
                        .param("tag", dicomTag))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Invalid DICOM tag or attribute not found"))
                .andExpect(jsonPath("$.message").value(exceptionMessage));

        // Verify the interactions with the mocks
        verify(dicomFileRepository, times(1)).findByFileName(fileName);
        verify(dicomService, times(1)).extractDicomAttribute(fileName, dicomTag, Paths.get(dicomFile.getDicomPath()).getParent());
    }
}



