package com.pockethealth.dicomfileuploader.repo;

import org.springframework.data.repository.CrudRepository;
import com.pockethealth.dicomfileuploader.model.DicomFile;
import org.springframework.stereotype.Repository;

@Repository
public interface DicomFileRepository extends CrudRepository<DicomFile, Long>  {
    DicomFile findByFileName(String fileName);
}
