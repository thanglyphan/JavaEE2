import config.GameApplication;
import config.GameConfiguration;
import dw.api.implementation.GameRest;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.junit.ClassRule;
import org.junit.Test;

import javax.ws.rs.core.MediaType;

import java.io.UnsupportedEncodingException;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.core.Is.is;

/**
 * Created by thang on 12.12.2016.
 */
public class GameTestIT extends GameMock{
    @ClassRule
    public static final DropwizardAppRule<GameConfiguration> RULE =
            new DropwizardAppRule<>(GameApplication.class);

    /*
        testGetRandom: GET a valid random game and verify its content
    */
    @Test
    public void testGetRandom() throws UnsupportedEncodingException {
        String quizId = "3";
        String questionId = "4";
        String question = "How are you today?";
        String a1 = "Fine";
        String a2 = "Good";
        String a3 = "Not so good";
        String a4 = "Very nice";

        String randomQuiz = getRandomQuiz(quizId, questionId, question, a1, a2, a3, a4);

        //Get the first url. The quiz ID.
        stubJsonResponseRandomQuiz(randomQuiz);

        given().accept(MediaType.APPLICATION_JSON)
                .get("/games/random")
                .then()
                .statusCode(200)
                .body(containsString(question))
                .body(containsString(a1))
                .body(containsString(a2))
                .body(containsString(a3))
                .body(containsString(a4));
    }
    /*
        testGetRandomFail: GET a random game, but should fail
    */
    @Test
    public void testGetRandomFail() throws UnsupportedEncodingException {
        stubJsonResponseRandomQuiz("Give me response HTTP 405.");


        given().accept(MediaType.APPLICATION_JSON)
                .get("/games/random")
                .then()
                .statusCode(405)
                .body(containsString("Not enough quizzes :: >")); //My fail response.
    }
    /*
        testPlayGameCorrect: do a POST to answer a game a quiz, and verify the answer was
    */
    @Test
    public void testPlayGameCorrect() throws UnsupportedEncodingException {
        String quizId = "3";
        String questionId = "4";

        String question = "How are you today?";
        String a1 = "Fine";
        String a2 = "Good";
        String a3 = "Not so good";
        String a4 = "Very nice";

        String randomQuiz = getRandomQuiz(quizId, questionId, question, a1, a2, a3, a4);

        //Get the first url. The quiz ID.
        stubJsonResponseRandomQuiz(randomQuiz);
        stubJsonResponseAnsweringQuiz(a1, quizId, questionId);
        given().accept(MediaType.APPLICATION_JSON)
                .get("/games/random")
                .then()
                .statusCode(200)
                .body(containsString(question))
                .body(containsString(a1))
                .body(containsString(a2))
                .body(containsString(a3))
                .body(containsString(a4));

        given().accept(MediaType.APPLICATION_JSON)
                .queryParam("qid", quizId)
                .queryParam("questionid", questionId)
                .queryParam("index", "1")
                .post("/games")
                .then()
                .statusCode(200)
                .body(containsString("Great! You answered: 1: Fine. That is correct!"));
    }
    /*
        testPlayGameWrong: do a POST to answer a game a quiz, and verify the answer was wrong
    */
    @Test
    public void testPlayGameWrong() throws UnsupportedEncodingException {
        String quizId = "3";
        String questionId = "4";

        String question = "How are you today?";
        String a1 = "Fine";
        String a2 = "Good";
        String a3 = "Not so good";
        String a4 = "Very nice";

        String randomQuiz = getRandomQuiz(quizId, questionId, question, a1, a2, a3, a4);

        //Get the first url. The quiz ID.
        stubJsonResponseRandomQuiz(randomQuiz);
        stubJsonResponseAnsweringQuiz(a1, quizId, questionId);
        given().accept(MediaType.APPLICATION_JSON)
                .get("/games/random")
                .then()
                .statusCode(200)
                .body(containsString(question))
                .body(containsString(a1))
                .body(containsString(a2))
                .body(containsString(a3))
                .body(containsString(a4));

        given().accept(MediaType.APPLICATION_JSON)
                .queryParam("qid", quizId)
                .queryParam("questionid", questionId)
                .queryParam("index", "2")
                .post("/games")
                .then()
                .statusCode(200)
                .body(containsString("You answered wrong, try again"));
    }
    @Test
    public void testAnswerQuestionWithWrongInput() throws UnsupportedEncodingException {
        String quizId = "3";
        String questionId = "4";

        String question = "How are you today?";
        String a1 = "Fine";
        String a2 = "Good";
        String a3 = "Not so good";
        String a4 = "Very nice";

        String randomQuiz = getRandomQuiz(quizId, questionId, question, a1, a2, a3, a4);
        stubJsonResponseRandomQuiz(randomQuiz);
        stubJsonResponseAnsweringQuiz(a1, quizId, questionId);

        given().accept(MediaType.APPLICATION_JSON)
                .get("/games/random")
                .then()
                .statusCode(200)
                .body(containsString(question))
                .body(containsString(a1))
                .body(containsString(a2))
                .body(containsString(a3))
                .body(containsString(a4));

        given().accept(MediaType.APPLICATION_JSON)
                .queryParam("qid", quizId)
                .queryParam("questionid", 2000)
                .queryParam("index", "2")
                .post("/games")
                .then()
                .statusCode(405);
    }
}
