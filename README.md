# DICOM File Uploader and Attribute Reader

This project allows users to upload DICOM files, convert them to PNG files, store them on an internal folder, and query DICOM 
attributes using a RESTful API. The application is built using Java with Spring Boot, and stores file metadata in a 
relational database.

## Table of Contents

- [Getting Started](#getting-started)
    - [Prerequisites](#prerequisites)
    - [Installation](#installation)
- [Running the Application](#running-the-application)
    - [Endpoints](#Endpoints)
    - [Using with Postman](#Using-with-Postman)
- [Testing](#testing)
- [Production Considerations](#production-considerations)
- [Trade-offs and Compromises](#trade-offs-and-compromises)

## Getting Started

### Prerequisites

- [Java 11](https://www.oracle.com/ca-en/java/technologies/downloads/#jdk20-linux)
- Maven

### Installation

1. Clone the repository:
    ```console
   git clone https://github.com/AkinkuA/DicomFileUploader.git
    ```
2. Navigate to the project root directory:
    ```console
   cd DicomFileUploader
    ```
3. Build the project:
    ```console
   ./mvnw clean install
    ```
## Running the Application

To run the application locally:
```console
   ./mvnw spring-boot:run
   ```
The RESTful API will be available at http://localhost:8080.

When the application is started a HyperSQL Database Manager Tool will be started as well. This can be used to interact 
with the Database. Keep in mind closing the Database Manager will stop the application as well.

### Endpoints

1. Upload DICOM File
    - **Description**: This endpoint allows users to upload a DICOM file, store it in a folder (`uploaded_dicom_files`), and save its 
   information to the database. Additionally, it converts the DICOM file to a PNG image and saves it in a separate 
   folder (`created_png_files`).
    - **Endpoint**: `/dicom/upload`
    - **Method**: POST
    - **Request**: The request should include the DICOM file as a `multipart/form-data` payload.
    - **Response**: The response will be a JSON object containing a success message if the upload was successful, and 
   an error message if not

2. Get DICOM Attribute
    - **Description**: This endpoint allows users to retrieve a specific DICOM attribute value by providing the file 
   name and the DICOM tag associated with the desired attribute.
    - **Endpoint**: `/dicom/{fileName}/attribute`
    - **Method**: GET
    - **Parameters**: `fileName` should be replaced with the name of the DICOM file, and `tag` should be provided as a query 
    - parameter representing the DICOM attribute tag in hexadecimal format.
    an example url might look like this `http://localhost:8080/dicom/IM000018/attribute?tag=00100010`. This is 
    retrieving the patient's name `(0010,0010)`  from the file `IM000018`
    - **Response**: The response will be a JSON object containing the requested attribute's value, along with the file 
    name and DICOM tag.

### Using with Postman

To access the endpoints using [Postman](https://www.postman.com/)

1. Download and install Postman on your computer.
2. Ensure the application is running on your local machine.
3. Select the desired endpoint in Postman and provide the necessary parameters, such as the file or DICOM tag.
4. Click "Send" to make the request, and observe the response.

## Testing

Unit tests were written for the main components of the application, including the repository, service, and controller 
layers. These tests use JUnit and Mockito to isolate components and validate their behavior.

To run the tests, execute:

```console
   ./mvnw test
   ```

To verify the correctness of the implementation, we tested the application by uploading various DICOM files and 
validating the extracted attributes using Postman and verified the values from the given 
[documentation](https://www.dicomlibrary.com/dicom/dicom-tags/)

## Production Considerations

If this application were destined for a production environment, the following changes and additions would be made:

1. Add an authentication and authorization system to protect the API from unauthorized access.
2. Implement a more robust and scalable file storage solution, such as cloud storage services like Amazon S3, Google 
3. Cloud Storage, or Microsoft Azure Blob Storage.
4. Improve error handling and logging throughout the application.
5. Configure SSL to enable HTTPS, ensuring secure communication between the client and the server.
6. Implement a caching mechanism for frequently accessed attributes or files.
7. Use a more production-ready database, such as PostgreSQL or MySQL, instead of the embedded H2 database.
8. In a production environment, a functional user interface (UI) would be implemented to allow users to interact with 
the application more easily. The UI would provide a convenient way to upload DICOM files and view attributes without 
using API requests.
9. Additionally, to ensure the reliability and maintainability of the application, the test coverage would be increased 
to cover more cases and scenarios. This would help identify potential issues and improve the overall quality of the 
application.

## Trade-offs and Compromises

Due to time constraints, the following compromises were made:

1. A basic file storage system is used for storing DICOM and PNG files. This may not be optimal for production, as it 
2. might not scale well with an increasing number of files.
3. Error handling is minimal, and some edge cases may not be adequately addressed.
4. The application lacks a proper user authentication and authorization system.
5. Integration tests and end-to-end tests were not implemented.

Despite these compromises, the core functionality of the application is complete, and it can be used as a starting point for further improvements and additions.
