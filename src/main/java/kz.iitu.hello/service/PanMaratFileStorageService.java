package kz.iitu.hello.service;

import kz.iitu.hello.domain.entity.User;
import kz.iitu.hello.domain.entity.UserFile;
import kz.iitu.hello.domain.repository.UserFileRepository;
import kz.iitu.hello.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class PanMaratFileStorageService {
    private static final String AVATAR_TYPE = "AVATAR";
    private static final String DOCUMENT_TYPE = "DOCUMENT";

    private static final Set<String> ALLOWED_AVATAR_CONTENT_TYPES = Set.of("image/jpeg", "image/png");
    private static final String ALLOWED_DOCUMENT_CONTENT_TYPE = "application/pdf";
    private static final long MAX_FILE_SIZE_BYTES = 5 * 1024 * 1024;

    private final PanMaratUserService userService;
    private final UserFileRepository userFileRepository;

    public UserFile saveAvatar(Long userId, MultipartFile file) {
        validateNotEmpty(file, "Avatar file is required");
        validateFileSize(file);
        validateAvatarContentType(file.getContentType());

        User user = userService.findById(userId);
        List<UserFile> oldAvatars = userFileRepository.findByUserIdAndFileType(userId, AVATAR_TYPE);

        UserFile newAvatar = saveFile(user, file, AVATAR_TYPE);
        oldAvatars.forEach(this::deleteFileInternal);

        return newAvatar;
    }

    public List<UserFile> saveDocuments(Long userId, List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            throw new IllegalArgumentException("At least one document file is required");
        }

        User user = userService.findById(userId);
        return files.stream()
                .map(file -> {
                    validateNotEmpty(file, "Document file is required");
                    validateFileSize(file);
                    validateDocumentContentType(file.getContentType());
                    return saveFile(user, file, DOCUMENT_TYPE);
                })
                .toList();
    }

    public void deleteFile(Long fileId) {
        UserFile userFile = getFile(fileId);
        deleteFileInternal(userFile);
    }

    @Transactional(readOnly = true)
    public UserFile getFile(Long fileId) {
        return userFileRepository.findById(fileId)
                .orElseThrow(() -> new EntityNotFoundException("File not found with id: " + fileId));
    }

    @Transactional(readOnly = true)
    public Resource loadFileAsResource(Long fileId) {
        UserFile userFile = getFile(fileId);

        try {
            Path filePath = Paths.get(userFile.getPath()).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            }
            throw new EntityNotFoundException("Stored file not found for id: " + fileId);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Failed to load file for id: " + fileId, e);
        }
    }

    @Transactional(readOnly = true)
    public List<UserFile> findByUserAndFileType(Long userId, String fileType) {
        userService.findById(userId);
        return userFileRepository.findByUserIdAndFileType(userId, fileType);
    }

    private UserFile saveFile(User user, MultipartFile file, String fileType) {
        String originalName = StringUtils.cleanPath(file.getOriginalFilename() == null ? "file" : file.getOriginalFilename());
        String extension = extractExtension(originalName);
        String uniqueName = UUID.randomUUID() + extension;

        Path userDir = buildUserDirectory(user.getId());
        Path destination = userDir.resolve(uniqueName);

        try {
            Files.createDirectories(userDir);
            Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file: " + originalName, e);
        }

        UserFile userFile = new UserFile();
        userFile.setUser(user);
        userFile.setFileName(originalName);
        userFile.setFileType(fileType);
        userFile.setContentType(file.getContentType());
        userFile.setPath(destination.toAbsolutePath().toString());
        userFile.setUploadedAt(LocalDateTime.now());

        return userFileRepository.save(userFile);
    }

    private void deleteFileInternal(UserFile userFile) {
        Path filePath = Paths.get(userFile.getPath());
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete file with id: " + userFile.getId(), e);
        }
        userFileRepository.delete(userFile);
    }

    private void validateNotEmpty(MultipartFile file, String message) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
    }

    private void validateFileSize(MultipartFile file) {
        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw new IllegalArgumentException("File size exceeds 5MB limit");
        }
    }

    private void validateAvatarContentType(String contentType) {
        if (!ALLOWED_AVATAR_CONTENT_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("Avatar must be a JPEG or PNG image");
        }
    }

    private void validateDocumentContentType(String contentType) {
        if (!ALLOWED_DOCUMENT_CONTENT_TYPE.equals(contentType)) {
            throw new IllegalArgumentException("Documents must be PDF files");
        }
    }

    private Path buildUserDirectory(Long userId) {
        return Paths.get("uploads", String.valueOf(userId)).toAbsolutePath().normalize();
    }

    private String extractExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex == -1) {
            return "";
        }
        return fileName.substring(dotIndex);
    }
}
