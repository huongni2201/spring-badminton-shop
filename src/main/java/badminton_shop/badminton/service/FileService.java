package badminton_shop.badminton.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.servlet.ServletContext;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileService {
    @Value("${badminton.upload-file.base-uri}")
    private String baseURI;

    public void createDirectory(String folder) throws URISyntaxException {
        URI uri = new URI(folder);
        Path path = Paths.get(uri);
        File tmpDir = new File(path.toString());
        if (!tmpDir.isDirectory()) {
            try {
                Files.createDirectory(tmpDir.toPath());
                System.out.println("Directory created: " + tmpDir.toPath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            System.out.println("Directory already exists: " + tmpDir.toPath());
        }
    }

    public String store(MultipartFile file, String folder) throws IOException, URISyntaxException {
        // create unique file name
        String finalName = System.currentTimeMillis() + "_" + UUID.randomUUID() + "_" + file.getOriginalFilename();

        URI uri = new URI(baseURI + folder + "/" + finalName);
        Path path = Paths.get(uri);
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
        }

        return finalName;
    }

    public List<String> storeMultipleFile(List<MultipartFile> files, String folder) throws IOException, URISyntaxException {
        List<String> fileNames = new ArrayList<>();

        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                String fileName = store(file, folder);
                fileNames.add(fileName);
            }
        }

        return fileNames;
    }
}
