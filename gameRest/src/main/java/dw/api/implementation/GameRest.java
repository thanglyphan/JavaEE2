package dw.api.implementation;


import dto.QuestionDto;
import dw.api.rest.GameRestApi;
import io.swagger.annotations.ApiParam;

import javax.ws.rs.WebApplicationException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.HashMap;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


/**
 * Created by thang on 20.11.2016.
 */
public class GameRest extends GameRestBase implements GameRestApi {
    private String quizAddress = System.getProperty("quizWebAddress", "localhost:8099");  //UNCOMMENT IF RUNNING TESTS
    //private String quizAddress = "localhost:8080";                                          //UNCOMMENT IF RUNNING WEB
    private HashMap<String,String> hashMap;

    @Override
    public QuestionDto getRandomGame() throws Exception {

        String REQUEST_URL = "http://"+quizAddress+"/quiz/api/quizzes/random";
        HttpURLConnection con = getConnection(REQUEST_URL, "GET");
        QuestionDto dto = new QuestionDto();
        if (con.getResponseCode() == HttpURLConnection.HTTP_OK ) { // success
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


        } else {
            throw new WebApplicationException("Unable to connect. Not found :: >", 404);
        }

        return dto;
    }

    @Override
    public Response answerQuestion(
            @ApiParam("Quiz ID here") String quizId,
            @ApiParam("Question ID here") String questionId,
            @ApiParam("Your answer, choose a number") String index) throws Exception {
        String result = "";
        String REQUEST_URL = "http://"+quizAddress+"/quiz/api/qa/"+quizId+"/"+questionId;
        HttpURLConnection con = getConnection(REQUEST_URL, "GET");
        System.out.println(con.getResponseCode() + ", " + quizId + ", " + questionId);
        if (con.getResponseCode() == HttpURLConnection.HTTP_OK ){

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String answer = getJsonString(in);
            System.out.println(answer);

            if(hashMap.isEmpty()){
                Response.status(500).build();
            }else{
                for(String a: hashMap.keySet()){
                    if(hashMap.get(a).contains(index) && hashMap.get(a).contains(answer)){
                        result = "Great! You answered: " + hashMap.get(a) + ". That is correct!";
                        break;
                    }else{
                        result = "You answered wrong, try again";
                    }
                }
            }
        }else{
            throw new WebApplicationException("Invalid input: ", 405);
        }

        return Response.ok(result, MediaType.TEXT_PLAIN).build();
    }


}
