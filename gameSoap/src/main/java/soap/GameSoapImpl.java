package soap;

/**
 * Created by thang on 11.12.2016.
 */

import dto.QuestionDto;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jws.WebService;
import javax.ws.rs.WebApplicationException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.HashMap;


@WebService(
        endpointInterface = "soap.GameSoapApi"
)
@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class GameSoapImpl extends GameSoapBase implements GameSoapApi {
    private String quizAddress = System.getProperty("quizWebAddress", "localhost:8099");

    private HashMap<String, String> hashMap;

    @Override
    public QuestionDto getRandomGame() throws Exception {

        String REQUEST_URL = "http://" + quizAddress + "/quiz/api/quizzes/random";

        HttpURLConnection con = getConnection(REQUEST_URL, "GET");
        QuestionDto dto = new QuestionDto();
        if (con.getResponseCode() == HttpURLConnection.HTTP_OK) { // success
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            //Create the game first.

            try{
                hashMap = getQuestionParsed(in);

                dto.id = hashMap.get("questionId");
                dto.quizId = hashMap.get("quizId");
                dto.question = hashMap.get("question");
                dto.choiceOne = hashMap.get("answerOne");
                dto.choiceTwo = hashMap.get("answerTwo");
                dto.choiceThree = hashMap.get("answerThree");
                dto.choiceFour = hashMap.get("answerFour");

            }catch (Exception e){
                throw new WebApplicationException("Not enough quizzes :: >", 405);
            }

        }else {
            throw new WebApplicationException("Unable to connect. Not found :: >", 404);
        }

        return dto;
    }

    @Override
    public String answerQuestion(String quizId, String questionId, String index) throws Exception {
        String result = "";
        String REQUEST_URL = "http://"+quizAddress+"/quiz/api/qa/"+quizId+"/"+questionId;
        HttpURLConnection con = getConnection(REQUEST_URL, "GET");
        System.out.println(con.getResponseCode() + ", " + quizId + ", " + questionId);
        if (con.getResponseCode() == HttpURLConnection.HTTP_OK ){

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String answer = getJsonString(in);

            for(String a: hashMap.keySet()){
                if(hashMap.get(a).contains(index) && hashMap.get(a).contains(answer)){
                    result = "Great! You answered: " + hashMap.get(a) + ". That is correct!";
                    break;
                }else{
                    result = "You answered wrong, try again";
                }
            }
        }else{
            throw new WebApplicationException("Invalid input: ", 405);
        }
        return result;
    }
}