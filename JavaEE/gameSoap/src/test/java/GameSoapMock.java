import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import game.GameSoap;
import game.GameSoapImplService;
import io.restassured.RestAssured;
import org.junit.AfterClass;
import org.junit.BeforeClass;


import javax.xml.ws.BindingProvider;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;

import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

/**
 * Created by thang on 13.12.2016.
 */
public class GameSoapMock {
    protected static WireMockServer wiremockServer;
    protected static GameSoap ws;


    @BeforeClass
    public static void initClass() {
        System.setProperty("quizWebAddress", "localhost:8099");

        wiremockServer = new WireMockServer(
                wireMockConfig().port(8099).notifier(new ConsoleNotifier(true))
        );
        wiremockServer.start();

        GameSoapImplService service = new GameSoapImplService();
        ws = service.getGameSoapImplPort();

        String url = "http://localhost:8080/gameSoap-0.0.1-SNAPSHOT/GameSoapImpl";

        ((BindingProvider)ws).getRequestContext().put(
                BindingProvider.ENDPOINT_ADDRESS_PROPERTY, url);

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
