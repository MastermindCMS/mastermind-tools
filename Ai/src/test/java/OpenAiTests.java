import com.mastermindcms.ai.gpt.config.AiConfiguration;
import com.mastermindcms.ai.gpt.services.ChatGPTService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes={
        ChatGPTService.class,
        AiConfiguration.class
})
@EnableAutoConfiguration
@TestPropertySource({"classpath:ai.properties"})
@Slf4j
public class OpenAiTests {

    @Test
    public void testParseAnswerForVisualBuilder() {
        String content = "Sure! Here's an example:\n" +
                "\n" +
                "html```\n" +
                "<div class=\"bg-gray-900 py-20\">\n" +
                "  <div class=\"max-w-7xl mx-auto px-4 sm:px-6 lg:px-8\">\n" +
                "    <div class=\"lg:text-center\">\n" +
                "      <h1 class=\"text-4xl tracking-tight font-extrabold text-gray-100 sm:text-5xl md:text-6xl\">\n" +
                "        Welcome to our Website\n" +
                "      </h1>\n" +
                "      <div class=\"mt-5 sm:mt-8 sm:flex sm:justify-center lg:justify-center\">\n" +
                "        <div class=\"rounded-md shadow\">\n" +
                "          <a href=\"#\" class=\"w-full flex items-center justify-center px-8 py-3 border border-transparent text-base font-medium rounded-md text-gray-900 bg-white hover:bg-gray-100 md:py-4 md:text-lg md:px-10\">\n" +
                "            Get started\n" +
                "          </a>\n" +
                "        </div>\n" +
                "        <div class=\"mt-3 sm:mt-0 sm:ml-3\">\n" +
                "          <a href=\"#\" class=\"w-full flex items-center justify-center px-8 py-3 border border-transparent text-base font-medium rounded-md text-gray-200 bg-gray-800 hover:bg-gray-700 md:py-4 md:text-lg md:px-10\">\n" +
                "            Learn more\n" +
                "          </a>\n" +
                "        </div>\n" +
                "      </div>\n" +
                "    </div>\n" +
                "  </div>\n" +
                "</div>\n" +
                "```\n" +
                "\n"+
                "This hero banner uses a dark background color (`bg-gray-900`) and includes a heading, subtext, and a call-to-action button. It also makes use of responsive design, with different font sizes and spacing for different screen sizes (`text-4xl` for small screens, `text-5xl` for medium screens, and `text-6xl` for large screens, for example).";
        String codeSnippet = StringUtils.substringBetween(content,"html```","```");
        log.info(codeSnippet);
        Assert.isTrue(!codeSnippet.isEmpty());
    }

}