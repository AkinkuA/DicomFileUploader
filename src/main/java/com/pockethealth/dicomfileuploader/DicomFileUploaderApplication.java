package com.pockethealth.dicomfileuploader;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.hsqldb.util.DatabaseManagerSwing;

@SpringBootApplication
public class DicomFileUploaderApplication {

    public static void main(String[] args) {
        SpringApplication.run(DicomFileUploaderApplication.class, args);
        startHsqldbManager();
    }

    private static void startHsqldbManager() {
        System.setProperty("java.awt.headless", "false");
        DatabaseManagerSwing.main(new String[]{
                "--url", "jdbc:hsqldb:mem:pockethealthdb",
        });
    }

}
