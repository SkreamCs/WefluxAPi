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

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
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
        return new File(1, "http://example.com/getLocation/name", "testFile.txt", Status.ACTIVE);
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
        Mono<InputStream> result = fileService.download(getFile());
        try {
            assertArrayEquals(IOUtils.toByteArray(Objects.requireNonNull(result.block())), content);
        } catch (IOException e) {
            e.getMessage();
        }

    }

    @Test
    public void testUploadFile() {
        User user = new User();
        user.setId(1);
        user.setUsername("Test User");

        File file = new File();
        file.setFileName("testFile.txt");
        file.setLocation("/name/path");
        URL newUrl = null;
        try {
            newUrl = new URL("http://example.com/getLocation/name");

        } catch (MalformedURLException e) {
            e.getMessage();
        }

        when(s3client.getUrl(bucketName, file.getFileName())).thenReturn(newUrl);
        when(userRepository.findById(1)).thenReturn(Mono.just(user));
        when(fileRepository.save(any(File.class))).thenReturn(Mono.just(getFile()));
        when(eventRepository.save(any(Event.class))).thenReturn(Mono.just(new Event()));

        Mono<File> result = fileService.upload(file, 1);
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
    public void deleteFIleTest() {
        String fileName = getFile().getFileName();
        when(fileRepository.deleteActiveByFileName(fileName)).thenReturn(Mono.empty());
        doNothing().when(s3client).deleteObject(bucketName, fileName);
        fileService.deleteFile(fileName);
        verify(fileRepository).deleteActiveByFileName(fileName);
    }
}
