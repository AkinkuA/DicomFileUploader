package com.pockethealth.dicomfileuploader.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "dicom_file")
public class DicomFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, name = "file_name")
    private String fileName;

    @Column(name = "dicom_path")
    private String dicomPath;

    @Column(name = "png_path")
    private String pngPath;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public DicomFile() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getDicomPath() {
        return dicomPath;
    }

    public void setDicomPath(String dicomPath) {
        this.dicomPath = dicomPath;
    }

    public String getPngPath() {
        return pngPath;
    }

    public void setPngPath(String pngPath) {
        this.pngPath = pngPath;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

}

