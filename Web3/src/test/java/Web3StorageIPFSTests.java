import com.mastermindcms.ipfs.config.Web3StorageConfiguration;
import com.mastermindcms.ipfs.services.Web3StorageServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
    Web3StorageConfiguration.class,
    Web3StorageServiceImpl.class
})
@EnableAutoConfiguration
@TestPropertySource({"classpath:web3storage.properties"})
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)  // Ensures tests are run in order
public class Web3StorageIPFSTests {

    @Autowired
    private Web3StorageServiceImpl ipfsService; // Inject the implementation directly

    @Test
    public void testPropertiesAreNotEmpty() {
        String apiKey = ipfsService.getApiKey();
        String proof = ipfsService.getProof();

        assertNotNull(apiKey, "API Key should not be null");
        assertFalse(apiKey.trim().isEmpty(), "API Key should not be empty");

        assertNotNull(proof, "Proof should not be null");
        assertFalse(proof.trim().isEmpty(), "Proof should not be empty");
    }
}