package soap;

import commands.Commands;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.HashMap;

/**
 * Created by thang on 23.11.2016.
 */
public class GameSoapBase {

    @Context
    protected HttpServletResponse response;

    @Context
    protected UriInfo uriInfo;

    protected HttpURLConnection getConnection(String requestUrl, String requestMethod) throws Exception {
        //Hystrix here.
        Commands commands = new Commands();

        return commands.connect(requestUrl, requestMethod);
    }


    protected String getJsonString(BufferedReader in) throws IOException {
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        return response.toString();
    }

    protected HashMap<String, String> getQuestionParsed(BufferedReader in) throws IOException, JSONException {
        HashMap<String, String> hashMap = new HashMap<>();

        String s = getJsonString(in);

        JSONObject object = new JSONObject(s);
        try {
            String quizId = object.getString("id");
            String subCategoryId = object.getString("subCategoryId");
            String quizName = object.getString("quizName");

            String list = object.getJSONObject("questionsAndAnswersList").toString();

            String whole = list.replace("{", "").replace("\"", "").replace(";", ":").replace("[", "").replace("]", "").replace("}", "").replace(":", ",");
            String parts[] = whole.split(",");

            String questionId = parts[0];
            String question = parts[1];
            String answerOne = parts[2];
            String answerTwo = parts[3];
            String answerThree = parts[4];
            String answerFour = parts[5];


            hashMap.put("quizId", quizId);
            hashMap.put("subCategoryId", subCategoryId);
            hashMap.put("quizName", quizName);
            hashMap.put("questionId", questionId);
            hashMap.put("question", question);
            hashMap.put("answerOne", "1: " + answerOne);
            hashMap.put("answerTwo", "2: " + answerTwo);
            hashMap.put("answerThree", "3: " + answerThree);
            hashMap.put("answerFour", "4: " + answerFour);
        }catch (Exception e){
            System.out.println("Not valid");
        }



        return hashMap;
    }

}
