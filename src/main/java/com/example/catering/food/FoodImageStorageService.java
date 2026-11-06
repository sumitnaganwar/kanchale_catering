package com.example.catering.food;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
public class FoodImageStorageService {
    private static final long MAX_FILE_BYTES = 8L * 1024 * 1024;
    private final Path uploadDirectory;

    public FoodImageStorageService(@Value("${app.upload.dir:uploads}") String uploadDirectory) {
        this.uploadDirectory = Path.of(uploadDirectory).toAbsolutePath().normalize();
    }

    public String store(MultipartFile file) {
        if (file == null || file.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Choose an image to upload");
        if (file.getSize() > MAX_FILE_BYTES) throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "Image must be 8 MB or smaller");
        String extension = switch (file.getContentType() == null ? "" : file.getContentType().toLowerCase(Locale.ROOT)) {
            case "image/jpeg", "image/jpg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Upload a JPG, PNG, or WebP image");
        };
        String filename = UUID.randomUUID().toString().replace("-", "") + extension;
        try {
            Files.createDirectories(uploadDirectory);
            file.transferTo(uploadDirectory.resolve(filename));
            return "/uploads/" + filename;
        } catch (IOException exception) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not store the image", exception);
        }
    }

    public void delete(String publicPath) {
        if (publicPath == null || !publicPath.matches("/uploads/[a-f0-9]{32}\\.(jpg|png|webp)")) return;
        Path image = uploadDirectory.resolve(publicPath.substring("/uploads/".length())).normalize();
        if (!image.startsWith(uploadDirectory)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid image path");
        try { Files.deleteIfExists(image); }
        catch (IOException exception) { throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not delete the image", exception); }
    }
}
