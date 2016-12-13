import game.Exception_Exception;
import org.junit.*;

import javax.ws.rs.WebApplicationException;
import java.io.UnsupportedEncodingException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by thang on 13.12.2016.
 */
public class GameSoapTestIT extends GameSoapMock{

    /*
        testGetRandom: GET a valid random game and verify its content
    */
    @Test
    public void testGetRandom() throws Exception {
        String quizId = "3";
        String questionId = "4";

        String question = "How are you today?";
        String a1 = "Fine";
        String a2 = "Good";
        String a3 = "Not so good";
        String a4 = "Very nice";


        String a = getRandomQuiz(quizId, questionId, question, a1, a2, a3, a4);
        stubJsonResponseRandomQuiz(a);
        game.QuestionDto dto = ws.getRandomGame();

        //Check if i got the things i needed.
        assertEquals(question, dto.getQuestion());
        assertTrue(dto.getChoiceOne().contains(a1));
        assertTrue(dto.getChoiceTwo().contains(a2));
        assertTrue(dto.getChoiceThree().contains(a3));
        assertTrue(dto.getChoiceFour().contains(a4));

    }
    /*
        testGetRandomFail: GET a random game, but should fail
    */
    @Test
    public void testGetRandomFail() throws Exception {
        stubJsonResponseRandomQuiz("{ lol: a }");

        ws.getRandomGame();
        game.QuestionDto dto = ws.getRandomGame();

        //Check if i got the things i needed. Its null.
        assertNull(dto.getChoiceFour());
    }
    /*
        testPlayGameCorrect: do a POST to answer a game a quiz, and verify the answer was
    */
    @Test
    public void testPlayGameCorrect() throws UnsupportedEncodingException, Exception_Exception {
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

        game.QuestionDto dto = ws.getRandomGame();

        //Check if i got the things i needed.
        assertEquals(question, dto.getQuestion());
        assertTrue(dto.getChoiceOne().contains(a1));
        assertTrue(dto.getChoiceTwo().contains(a2));
        assertTrue(dto.getChoiceThree().contains(a3));
        assertTrue(dto.getChoiceFour().contains(a4));

        //Then, lets play. 1 is right solution.
        String result = ws.answerQuestion(quizId, questionId, "1");

        assertEquals("Great! You answered: 1: Fine. That is correct!", result);
    }
    /*
        testPlayGameWrong: do a POST to answer a game a quiz, and verify the answer was wrong
    */
    @Test
    public void testPlayGameWrong() throws UnsupportedEncodingException, Exception_Exception {
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

        game.QuestionDto dto = ws.getRandomGame();

        //Check if i got the things i needed.
        assertEquals(question, dto.getQuestion());
        assertTrue(dto.getChoiceOne().contains(a1));
        assertTrue(dto.getChoiceTwo().contains(a2));
        assertTrue(dto.getChoiceThree().contains(a3));
        assertTrue(dto.getChoiceFour().contains(a4));

        //Then, lets play. 2 is wrong solution.
        String result = ws.answerQuestion(quizId, questionId, "2");

        assertEquals("You answered wrong, try again", result);
    }
    @Test
    public void testAnswerQuestionWithWrongInput() throws UnsupportedEncodingException, Exception_Exception, WebApplicationException {
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

        game.QuestionDto dto = ws.getRandomGame();

        //Check if i got the things i needed.
        assertEquals(question, dto.getQuestion());
        assertTrue(dto.getChoiceOne().contains(a1));
        assertTrue(dto.getChoiceTwo().contains(a2));
        assertTrue(dto.getChoiceThree().contains(a3));
        assertTrue(dto.getChoiceFour().contains(a4));

        String result = "failed";
        //Then, lets play. 2 is wrong solution.
        try{
            result = ws.answerQuestion(quizId, "2000", "2");
        } catch (Exception e){
            //Expected
        }
        assertEquals("failed", result);
    }
}
