package abdul.restApi.spring.webflux.rest;

import abdul.restApi.spring.webflux.dto.FileDto;
import abdul.restApi.spring.webflux.mapper.FileMapper;
import abdul.restApi.spring.webflux.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.io.InputStream;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/files")
public class FileRestControllerV1 {

    private final FileService fileService;
    private final FileMapper fileMapper;

    @PostMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<FileDto> uploadFile(@RequestPart("file") Mono<FilePart> filePart, @PathVariable int id) {
        return filePart.flatMap(file -> fileService.upload(file, id).map(fileMapper::map));
    }

    @GetMapping("/download/{name}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR', 'USER')")
    public Mono<InputStream> downloadFile(@PathVariable String name) {
        return fileService.download(name);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public Mono<String> listFile() {
        return fileService.listFiles();
    }

    @DeleteMapping("/{name}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public Mono<ResponseEntity<?>> deleteFile(@PathVariable String name) {
        return fileService.deleteFile(name).thenReturn(ResponseEntity.ok("successful removal"));
    }


}
