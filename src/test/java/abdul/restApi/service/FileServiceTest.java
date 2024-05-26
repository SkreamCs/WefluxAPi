package abdul.restApi.service;


import abdul.restApi.spring.webflux.model.Event;
import abdul.restApi.spring.webflux.model.File;
import abdul.restApi.spring.webflux.model.Status;
import abdul.restApi.spring.webflux.model.User;

import abdul.restApi.spring.webflux.repository.EventRepository;
import abdul.restApi.spring.webflux.repository.FileRepository;
import abdul.restApi.spring.webflux.repository.UserRepository;
import abdul.restApi.spring.webflux.service.FileService;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.VoidAnswer1;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Objects;

@ExtendWith(MockitoExtension.class)
public class FileServiceTest {

    @Mock
    private AmazonS3 s3client;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private FileRepository fileRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private FileService fileService;

    @Value("${s3.bucketName}")
    private String bucketName;

    private File getFile() {
        return new File(1, " https://<bucketName>.s3.amazonaws.com/testFile.txt", "testFile.txt", Status.ACTIVE);
    }

    @Test
    public void testCreateBucket() {
        when(s3client.doesBucketExistV2(bucketName)).thenReturn(false);

        fileService.createBucket();

        verify(s3client).createBucket(bucketName);

    }

    @Test
    public void testDownloadFile() {

        byte[] content = "Test content".getBytes();
        InputStream inputStream = new ByteArrayInputStream(content);
        S3Object s3Object = new S3Object();
        s3Object.setObjectContent(inputStream);

        when(s3client.getObject(eq(bucketName), eq("testFile.txt"))).thenReturn(s3Object);
        StepVerifier.create(fileService.download(getFile().getFileName()))
                .expectNextMatches(input -> {
                    try {
                        byte[] resultContent = input.readAllBytes();
                         assertArrayEquals(content, resultContent);
                         return true;
                    } catch (Exception e) {
                        e.getCause();
                        return false;
                    }
                })
                .verifyComplete();
    }

    @Test
    public void testUploadFile() {
        String fileName = "testFile.txt";
        User user = new User();
        user.setId(1);
        user.setUsername("Test User");
        FilePart filePart = Mockito.mock(FilePart.class);

        File file = new File();
        file.setFileName(fileName);
        file.setLocation(" https://<bucketName>.s3.amazonaws.com/testFile.txt");

        when(filePart.filename()).thenReturn(fileName);
        when(filePart.transferTo(any(java.io.File.class))).thenReturn(Mono.empty());
        when(userRepository.findById(1)).thenReturn(Mono.just(user));
        when(fileRepository.save(any(File.class))).thenReturn(Mono.just(getFile()));
        when(eventRepository.save(any(Event.class))).thenReturn(Mono.just(new Event()));
        when(s3client.putObject(eq(bucketName), eq("testFile.txt"), any(java.io.File.class))).thenReturn(new PutObjectResult());
        Mono<File> result = fileService.upload(filePart, 1);
        StepVerifier.create(result).expectNext(getFile()).verifyComplete();

    }

    @Test
    public void listFilesTest() {
        ObjectListing objectListing = new ObjectListing();
        S3ObjectSummary summary1 = new S3ObjectSummary();
        summary1.setKey("file1.txt");
        S3ObjectSummary summary2 = new S3ObjectSummary();
        summary2.setKey("file2.txt");
        objectListing.getObjectSummaries().addAll(Arrays.asList(summary1, summary2));
        String expectedList = String.valueOf(objectListing.getObjectSummaries());
        when(s3client.listObjects(eq(bucketName))).thenReturn(objectListing);
        Mono<String> result = fileService.listFiles();
        assertEquals(result.block(), expectedList);

    }

    @Test
    public void deleteFileTest() {
        String fileName = getFile().getFileName();
        when(fileRepository.deleteActiveByFileName(fileName)).thenReturn(Mono.empty());
        doNothing().when(s3client).deleteObject(bucketName, fileName);

        StepVerifier.create(fileService.deleteFile(fileName))
                .verifyComplete();
    }
}
