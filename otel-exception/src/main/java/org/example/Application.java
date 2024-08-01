package org.example;

import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.common.StorageSharedKeyCredential;
import com.microsoft.applicationinsights.attach.ApplicationInsights;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
public class Application {

    private final BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
            .endpoint("http://localhost:10000/account1")
            .credential(new StorageSharedKeyCredential("account1", "key1"))
            .buildClient();

    private final BlobContainerClient blobContainerClient = blobServiceClient.getBlobContainerClient("blob-container");

    @GetMapping("/1")
    public void handle1() {
        // should only throw exception internally
        // produces 'exception' attribute
        blobContainerClient.exists();
    }

    @GetMapping("/2")
    public void handle2() {
        // should propagate exception to main thread
        // produces 'exception.type', 'exception.message', 'exception.stacktrace' attributes
        blobContainerClient.delete();
    }

    public static void main(String[] args) {
        ApplicationInsights.attach();
        SpringApplication.run(Application.class, args);
    }

}
