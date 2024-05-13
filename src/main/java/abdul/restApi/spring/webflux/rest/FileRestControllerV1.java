package abdul.restApi.spring.webflux.rest;

import abdul.restApi.spring.webflux.dto.FileDto;
import abdul.restApi.spring.webflux.mapper.FileMapper;
import abdul.restApi.spring.webflux.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public Mono<FileDto> createFile(@RequestBody FileDto fileDto, @PathVariable int id) {
        return fileService.upload(fileMapper.map(fileDto), id).map(fileMapper::map);
    }

    @GetMapping("/download")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR', 'USER')")
    public Mono<InputStream> downloadFile(@RequestBody FileDto fileDto) {
        return fileService.download(fileMapper.map(fileDto));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public Mono<String> listFile() {
        return fileService.listFiles();
    }

    @DeleteMapping("/{name}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public Mono<ResponseEntity<?>> deleteFile(@PathVariable String name) {
        fileService.deleteFile(name);
        return Mono.just(ResponseEntity.ok("File " + name + " has been successfully deleted"));
    }


}
