package soap;

/**
 * Created by thang on 11.12.2016.
 */
import dto.QuestionDto;

import javax.jws.WebMethod;
import javax.jws.WebService;


@WebService( name = "GameSoap")
public interface GameSoapApi {
    @WebMethod
    QuestionDto getRandomGame() throws Exception;

    @WebMethod
    String answerQuestion(String quizId, String questionId, String index) throws Exception;
}