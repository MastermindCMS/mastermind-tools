import com.mastermindcms.config.Web3Configuration;
import com.mastermindcms.services.Web3ServiceImpl;
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
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
    Web3Configuration.class,
    Web3ServiceImpl.class
})
@EnableAutoConfiguration
@TestPropertySource({"classpath:web3.properties"})
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)  // Ensures tests are run in order
public class Web3Tests {

    private static boolean skipApiTests = false;
    private static final String chainName = "sepoliaArbitrum";

    @Autowired
    private Web3ServiceImpl web3Service;

    @Test
    @Order(1)
    public void testPropertiesAreNotEmpty() {
        String blockchainNetwork = web3Service.getBlockchainNetwork();

        assertNotNull(blockchainNetwork, "blockchainNetwork should not be null");
        assertFalse(blockchainNetwork.trim().isEmpty(), "blockchainNetwork should not be empty");
    }

    @Test
    @Order(2)
    public void testApiKeyNotEmpty() {
        String apiKey = web3Service.getApiKey(chainName);
        if (apiKey == null || apiKey.trim().isEmpty()) {
            skipApiTests = true;
            log.info("API Key is empty. Skipping API dependent tests.");
            return;
        }

        log.info("API Key is present.");
    }
}