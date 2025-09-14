import com.mastermindcms.ipfs.config.PinataConfiguration;
import com.mastermindcms.ipfs.services.PinataServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        PinataConfiguration.class,
        PinataServiceImpl.class
})
@EnableAutoConfiguration
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PinataIPFSTests {

    @Autowired
    private PinataServiceImpl pinataService;

    private static String uploadedCID;

    private static final String dummyJson = "{\"name\": \"Dummy NFT\", \"description\": \"This is a dummy NFT for testing.\"}";
    private static boolean skipRemainingTests = false;

    @Test
    @Order(1)
    public void testPropertiesAreNotEmpty() {
        String apiKey = pinataService.getApiKey();
        String secretApiKey = pinataService.getSecretApiKey();

        if (apiKey == null || apiKey.trim().isEmpty() || secretApiKey == null || secretApiKey.trim().isEmpty()) {
            skipRemainingTests = true;
            log.info("API Key or Secret API Key is empty. Skipping remaining tests.");
            return; // Skip further test logic
        }

        log.info("API Key and Secret API Key are both present.");
    }

    @Test
    @Order(2)
    public void testUploadDummyJson() throws Exception {
        if (skipRemainingTests) {
            log.info("Skipping testUploadDummyJson as API Keys are empty.");
            return;
        }

        String cid = pinataService.uploadFile(dummyJson);
        uploadedCID = cid;

        assertNotNull(cid, "CID should not be null");
        assertFalse(cid.trim().isEmpty(), "CID should not be empty");

        log.info("Uploaded CID: " + cid);
    }

    @Test
    @Order(3)
    public void testReadUploadedJson() throws Exception {
        if (skipRemainingTests) {
            log.info("Skipping testUploadDummyJson as API Keys are empty.");
            return;
        }


        assertNotNull(uploadedCID, "Uploaded CID should not be null");

        String fileContent = pinataService.readFile(uploadedCID);

        assertNotNull(fileContent, "File content should not be null");
        assertFalse(fileContent.trim().isEmpty(), "File content should not be empty");

        assertEquals(dummyJson, fileContent, "File content should match the uploaded dummy JSON");

        log.info("Retrieved file content: " + fileContent);
    }

    @Test
    @Order(4)
    public void testDeleteFileByCID() throws Exception {
        if (skipRemainingTests) {
            log.info("Skipping testUploadDummyJson as API Keys are empty.");
            return;
        }

        assertNotNull(uploadedCID, "Uploaded CID should not be null");

        boolean isDeleted = pinataService.deleteFile(uploadedCID);

        assertTrue(isDeleted, "File should be successfully deleted");

        log.info("File with CID " + uploadedCID + " has been successfully deleted.");
    }

    @Test
    @Order(5)
    public void testGetUrlByCID() throws Exception {
        if (skipRemainingTests) {
            log.info("Skipping testGetUrlByCID as API Keys are empty.");
            return;
        }
        assertNotNull(uploadedCID, "Uploaded CID should not be null");

        String fileUrl = pinataService.getUrl(uploadedCID);

        assertNotNull(fileUrl, "File URL should not be null");

        String expectedUrl = "https://gateway.pinata.cloud/ipfs/" + uploadedCID;
        assertEquals(expectedUrl, fileUrl, "File URL should match the expected Pinata gateway format");

        log.info("File URL for CID " + uploadedCID + " is " + fileUrl);
    }

}
