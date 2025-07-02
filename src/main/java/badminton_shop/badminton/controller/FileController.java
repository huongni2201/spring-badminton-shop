package badminton_shop.badminton.controller;

import badminton_shop.badminton.domain.response.file.ResUploadFileDTO;
import badminton_shop.badminton.service.FileService;
import badminton_shop.badminton.utils.annotation.ApiMessage;
import badminton_shop.badminton.utils.error.StorageException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@RestController
public class FileController {
    private FileService fileService;

    @Value("${badminton.upload-file.base-uri}")
    private String baseURI;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/api/v1/file")
    @ApiMessage("Upload single file")
    public ResponseEntity<ResUploadFileDTO> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("folder") String folder
    ) throws URISyntaxException, IOException, StorageException {
        // skip validate
        if (file == null || file.isEmpty()) {
            throw new StorageException("File is empty. Please try again with a valid file.");
        }
        String fileName = file.getOriginalFilename();
        List<String> allowedFileTypes = Arrays.asList("png", "jpg", "jpeg", "gif");
        boolean isValidFileType = allowedFileTypes.stream().anyMatch(item -> fileName.toLowerCase().endsWith(item));

        if (!isValidFileType) {
            throw new StorageException("File type is not allowed. Please try again with a valid file type.");
        }

        // create a directory if not exist
        this.fileService.createDirectory(baseURI + folder);

        // store file
        String uploadFile = this.fileService.store(file, folder);

        ResUploadFileDTO res = ResUploadFileDTO.builder()
                .fileName(uploadFile)
                .uploadedAt(Instant.now())
                .build();


        return ResponseEntity.ok().body(res);
    }

    @PostMapping("/api/v1/files")
    @ApiMessage("Upload multiple files")
    public ResponseEntity<List<ResUploadFileDTO>> uploadMultipleFiles(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam("folder") String folder
    ) throws URISyntaxException, IOException, StorageException {

        if (files == null || files.isEmpty()) {
            throw new StorageException("File list is empty.");
        }

        List<String> allowedFileTypes = Arrays.asList("png", "jpg", "jpeg", "gif");

        // Validate all files
        for (MultipartFile file : files) {
            String fileName = file.getOriginalFilename();
            boolean isValid = allowedFileTypes.stream().anyMatch(ext ->
                    fileName != null && fileName.toLowerCase().endsWith(ext)
            );
            if (!isValid) {
                throw new StorageException("Invalid file type: " + fileName);
            }
        }

        // Create folder only once
        this.fileService.createDirectory(baseURI + folder);

        // Store all files
        List<String> savedFileNames = this.fileService.storeMultipleFile(files, folder);

        // Convert to DTO
        List<ResUploadFileDTO> res = savedFileNames.stream()
                .map(fileName -> ResUploadFileDTO.builder()
                        .fileName(fileName)
                        .uploadedAt(Instant.now())
                        .build()
                ).toList();

        return ResponseEntity.ok().body(res);
    }
}
