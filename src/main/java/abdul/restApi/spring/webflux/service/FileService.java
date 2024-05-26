package abdul.restApi.spring.webflux.service;

import abdul.restApi.spring.webflux.model.Event;
import abdul.restApi.spring.webflux.model.File;
import abdul.restApi.spring.webflux.repository.EventRepository;
import abdul.restApi.spring.webflux.repository.FileRepository;
import abdul.restApi.spring.webflux.repository.UserRepository;
import com.amazonaws.services.s3.AmazonS3;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.InputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

    private final FileRepository fileRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final AmazonS3 s3client;
    @Value("${s3.bucketName}")
    private String bucketName;
    private String urlFile;

    @PostConstruct
    private void init() {
        urlFile = String.format("https://%s.s3.amazonaws.com/", bucketName);
    }

    public void createBucket() {

        if (s3client.doesBucketExistV2(bucketName)) {
            log.info("Bucket {} already exists, use a different name", bucketName);
            return;
        }

        s3client.createBucket(bucketName);
    }

    public Mono<File> upload(FilePart file, int userId) {
        File saveFile = new File();
        java.io.File fileCloud = new java.io.File(file.filename());
        saveFile.setLocation(urlFile + file.filename());
        saveFile.setFileName(file.filename());
        return userRepository.findById(userId).flatMap(user -> file.transferTo(fileCloud).then(Mono.fromRunnable(() -> s3client.putObject(bucketName, file.filename(), fileCloud))).then(Mono.fromRunnable(() -> {
                  if(!fileCloud.delete()) {
                      log.error("Failed to delete file {}", file.filename());
                  }
               }))
               .then(fileRepository.save(saveFile).flatMap(newFile -> {
                   Event event = new Event();
                   event.setFile(newFile);
                   event.setUser(user);
                   return eventRepository.save(event).thenReturn(newFile);
               })));
    }


    public Mono<InputStream> download(String fileName) {
        return Mono.fromCallable(() -> s3client.getObject(bucketName, fileName).getObjectContent());
    }

    public Mono<String> listFiles() {
        return Mono.fromCallable(() -> s3client.listObjects(bucketName).getObjectSummaries().toString());

    }

    public Mono<Void> deleteFile(String fileName) {
         return fileRepository.deleteActiveByFileName(fileName).then(Mono.fromRunnable(() -> s3client.deleteObject(bucketName, fileName)));
    }
}

