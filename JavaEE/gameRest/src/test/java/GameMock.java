import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import io.restassured.RestAssured;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import java.io.UnsupportedEncodingException;

import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

/**
 * Created by thang on 13.12.2016.
 */
public class GameMock {
    protected static WireMockServer wiremockServer;

    @BeforeClass
    public static void initClass() {
        //JBossUtil2.waitForJBoss(10);

        System.setProperty("quizWebAddress", "localhost:8099");

        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;
        RestAssured.basePath = "/game/api";
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        wiremockServer = new WireMockServer(
                wireMockConfig().port(8099).notifier(new ConsoleNotifier(true))
        );
        wiremockServer.start();
    }

    @AfterClass
    public static void tearDown() {
        wiremockServer.stop();
    }


    protected String getRandomQuiz(String quizId, String questionId, String question, String a1, String a2, String a3, String a4){
        String json = "{\n" +
                "  \"id\": \""+quizId+"\",\n" +
                "  \"subCategoryId\": \"2\",\n" +
                "  \"quizName\": \"string\",\n" +
                "  \"questionsAndAnswersList\": {\n" +
                "    \""+questionId+";"+question+"\": [\n" +
                "      \""+a1+"\",\n" +
                "      \""+a2+"\",\n" +
                "      \""+a3+"\",\n" +
                "      \""+a4+"\"\n" +
                "    ]\n" +
                "  }\n" +
                "}";

        return json;
    }

    protected void stubJsonResponseRandomQuiz(String randomQuiz) throws UnsupportedEncodingException {
        wiremockServer.stubFor(WireMock.get(
                urlMatching("/quiz/api/quizzes/random.*"))
                .willReturn(WireMock.aResponse().withStatus(200)
                        .withHeader("Content-Type", "application/json; charset=utf-8;")
                        .withHeader("Content-Length", "" + randomQuiz.getBytes("utf-8").length)
                        .withBody(randomQuiz)));
    }

    protected void stubJsonResponseAnsweringQuiz(String json, String quizId, String questionId) throws UnsupportedEncodingException {
        wiremockServer.stubFor(WireMock.get(
                urlMatching("/quiz/api/qa/"+quizId+"/"+questionId))
                .willReturn(WireMock.aResponse().withStatus(200)
                        .withHeader("Content-Type", "application/json; charset=utf-8;")
                        .withHeader("Content-Length", "" + json.getBytes("utf-8").length)
                        .withBody(json)));
    }

}
