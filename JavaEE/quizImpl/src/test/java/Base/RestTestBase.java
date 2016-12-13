package Base; /**
 * Created by thang on 01.11.2016.
 */

import dto.CategoryDto;
import dto.QuestionDto;
import dto.QuizDto;
import dto.SubCategoryDto;
import dto.collection.ListDto;
import io.restassured.RestAssured;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import web.Formats;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RestTestBase {
    @BeforeClass
    public static void initClass() {
        JBossUtil.waitForJBoss(10);

        // RestAssured configs shared by all the tests
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;
        RestAssured.basePath = "/quiz/api/categories/";
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }


    @Before
    @After
    public void clean() {
        int total = Integer.MAX_VALUE;

        while (total > 0) {
            ListDto<?> listDto = given()
                    .queryParam("limit", Integer.MAX_VALUE)
                    .get()
                    .then()
                    .statusCode(200)
                    .extract()
                    .as(ListDto.class);

            listDto.list.stream()
                    .map(n -> ((Map) n).get("id"))
                    .forEach(id ->
                            given().delete("/" + id)
                                    .then()
                                    .statusCode(204)
                    );

            total = listDto.totalSize - listDto.list.size();
        }

    }

    protected String createCategory(String rootCategory){
        CategoryDto dto = new CategoryDto(null, rootCategory);

        String rootId = given().contentType(Formats.V1_JSON)
                .body(dto)
                .post()
                .then()
                .statusCode(200)
                .extract().asString();

        return rootId;
    }

    protected String createSubCategory(String categoryId, String subCategory){
        SubCategoryDto dto = new SubCategoryDto(null, categoryId, subCategory);

        String rootId = given().contentType(Formats.V1_JSON)
                .body(dto)
                .post(categoryId + "/subcategories")
                .then()
                .statusCode(200)
                .extract().asString();

        return rootId;
    }

    protected String createQuiz(String subSubCategoryId, String quizName){
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;
        RestAssured.basePath = "/quiz/api/quizzes/";
        QuizDto dto = new QuizDto(null, subSubCategoryId, quizName);

        String rootId = given().contentType(Formats.V1_JSON)
                .body(dto)
                .post()
                .then()
                .statusCode(200)
                .extract().asString();

        return rootId;
    }

    protected String createQuestion(String quizId, String question, String... strings){
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;
        RestAssured.basePath = "/quiz/api/qa/";
        QuestionDto dto = new QuestionDto(null, quizId, question, strings[0], strings[1], strings[2], strings[3]);

        String rootId = given().contentType(Formats.V1_JSON)
                .body(dto)
                .post()
                .then()
                .statusCode(200)
                .extract().asString();

        return rootId;
    }


    protected Set<String> getTexts(ListDto<?> selfDto) {

        Set<String> values = new HashSet<>();
        selfDto.list.stream()
                .map(m -> (String) ((Map) m).get("id"))
                .forEach(t -> values.add(t));

        return values;
    }


    protected void assertContainsTheSame(Collection<?> a, Collection<?> b) {
        assertEquals(a.size(), b.size());
        a.stream().forEach(v -> assertTrue(b.contains(v)));
        b.stream().forEach(v -> assertTrue(a.contains(v)));
    }

    protected void changePath(String path){
        RestAssured.basePath = "/quiz/api/" + path + "/";
    }
}
