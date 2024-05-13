package abdul.restApi.spring.webflux.service;

import abdul.restApi.spring.webflux.model.Event;
import abdul.restApi.spring.webflux.model.File;
import abdul.restApi.spring.webflux.repository.EventRepository;
import abdul.restApi.spring.webflux.repository.FileRepository;
import abdul.restApi.spring.webflux.repository.UserRepository;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

    public void createBucket() {

        if (s3client.doesBucketExistV2(bucketName)) {
            log.info("Bucket {} already exists, use a different name", bucketName);
            return;
        }

        s3client.createBucket(bucketName);
    }

    public Mono<File> upload(File file, int userId) {
        return userRepository.findById(userId).flatMap(user -> {
                    java.io.File fileCloud = new java.io.File(file.getLocation());
                    s3client.putObject(bucketName, file.getFileName(), fileCloud);
                    file.setLocation(s3client.getUrl(bucketName, file.getFileName()).toString());
                    Event event = new Event();
                    event.setFile(file);
                    event.setUser(user);
                    eventRepository.save(event);
                    return fileRepository.save(file);
                }
        );
    }

    public Mono<InputStream> download(File file) {
        S3Object s3Object = s3client.getObject(bucketName, file.getFileName());
        return Mono.just(s3Object.getObjectContent());
    }

    public Mono<String> listFiles() {
        ObjectListing objectListing = s3client.listObjects(bucketName);
        return Mono.just(String.valueOf(objectListing.getObjectSummaries()));

    }

    public Mono<Void> deleteFile(String fileName) {
        fileRepository.deleteActiveByFileName(fileName);
        s3client.deleteObject(bucketName, fileName);
        return Mono.empty();
    }

}

