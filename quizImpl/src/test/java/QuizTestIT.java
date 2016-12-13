import Base.HttpUtil;
import Base.RestTestBase;
import dto.collection.ListDto;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import web.Formats;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import java.util.Set;

import static io.restassured.RestAssured.expect;
import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;

/**
 * Created by thang on 12.12.2016.
 */
public class QuizTestIT extends RestTestBase {
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    @Test
    public void testCleanDB() {

        get().then()
                .statusCode(200)
                .body("list.size()", is(0));
    }

    /*
        testCreateCategory: create a category with a POST, and then retrieve it with a GET
     */
    @Test
    public void testCreateCategory() {
        String rootCategory = "Thangs life";
        String rootId = createCategory(rootCategory);

        get().then().statusCode(200).body("list.size()", is(1)).body("totalSize", is(1));

        given().pathParam("id", rootId)
                .get("/{id}")
                .then()
                .statusCode(200)
                .body("id", is(rootId))
                .body("rootCategory", is(rootCategory));
    }
    /*
        testCreateCategoryRawHttp: like testCreateCategory, but in this case the POST must be
        written using directly a raw HTTP message, eg by using HttpUtil.executeHttpCommand
        from the course. The name of the created category must be "økologi".
     *///TODO: NO FINISH
    //@Test
    public void testCreateCategoryRawHttp() throws Exception {

        // "verb" (GET in this case), followed by " ", then the path to resource, " ", and finally the protocol
        String request = "POST /quiz/api/countries HTTP/1.1 \n";
        //headers are pairs <key>:<value>, where the key is case insensitive
        request += "Host:localhost:8080 \n";  //this is compulsory: a server running at an IP can serve different host names
        request += "Accept:application/json \n"; //we states that we want the resouce in Json format
        request += "\n"; //empty line indicates the end of the header section

        String result = HttpUtil.executeHttpCommand("localhost", 8080, request);
        System.out.println(result);

        String headers = HttpUtil.getHeaderBlock(result);
        assertTrue(headers.contains("200 OK"));

        String contentType = HttpUtil.getHeaderValue("Content-Type", result);
        assertTrue(contentType.contains("application/json"));

        String body = HttpUtil.getBodyBlock(result);
        assertTrue(body.contains("Norway"));
        assertTrue(body.contains("Sweden"));
        assertTrue(body.contains("Germany"));
    }
    /*
        testCreateTwoCategories: do 2 POST to create 2 categories. Do a GET on all categories, and
        verify that it returns those 2 categories.
     */
    @Test
    public void testCreateTwoCategories(){
        String rootCategory = "Thangs life";
        for(int i = 0; i < 2; i++){
            createCategory(rootCategory);
        }
        get().then().statusCode(200).body("list.size()", is(2)).body("totalSize", is(2));

    }
    /*
        testCreateSubCategory: create category with a POST, and create a subcategory of that one
        with another POST. Do a GET to retrieve that subcategory.
     */
    @Test
    public void testCreateSubCategory(){
        String rootCategory = "Thangs life";
        String subCategory = "Exam";

        String rootId = createCategory(rootCategory);
        String subId = createSubCategory(rootId, subCategory);

        changePath("subcategories");
        given().queryParam("parentId", rootId)
                .get()
                .then()
                .statusCode(200)
                //.body("subCategory", is(subCategory)) //Output gets weird, "[Exam]" with brackets..
                .body("list.size()", is(1));

        changePath("categories");
    }
    /*
    testExpand: create a category and a subcategory for it (2 POSTs). Verify the following 4
                GETs for categories:
                ◦ All categories with false expand: the subcategory should not be present
                ◦ All categories with true expand: the subcategory should be present
                ◦ Specific category with false expand: the subcategory should not be present
                ◦ Specific category with true expand: the subcategory should be present
     */
    @Test
    public void testExpand(){
        String rootCategory = "Thangs life";
        String subCategory = "Exam";

        String offset = "0";
        String limit = "10";

        String rootId = createCategory(rootCategory);
        String subId = createSubCategory(rootId, subCategory);

        //All categories with false expand: the subcategory should not be present
        given().queryParam("offset", offset)
                .queryParam("limit", limit)
                .queryParam("expand", false)
                .get()
                .then()
                .statusCode(200)
                .body("extraInfo", equalTo(null));

        //All categories with true expand: the subcategory should be present
        given().queryParam("offset", offset)
                .queryParam("limit", limit)
                .queryParam("expand", true)
                .get()
                .then()
                .statusCode(200)
                .body(containsString(subCategory));

        //Specific category with false expand: the subcategory should not be present
        given().pathParam("id", rootId)
                .queryParam("expand", false)
                .get("/{id}")
                .then()
                .statusCode(200)
                .body("extraInfo", equalTo(null));

        //Specific category with true expand: the subcategory should be present
        given().pathParam("id", rootId)
                .queryParam("expand", true)
                .get("/{id}")
                .then()
                .statusCode(200)
                .body(containsString(subCategory));

    }

    /*
        testUpdateCategory: create a category with a given name. Do a JSON Merge Patch to
        change its name. Do a GET to verify the name has been indeed changed.
     */
    @Test
    public void testUpdateCategory(){
        String rootCategory = "Thangs life";
        String newCategoryName = "Updated";

        String rootId = createCategory(rootCategory);

        //Update patch merge
        given().contentType(Formats.V1_JSON)
                .body(newCategoryName)
                .pathParam("id", rootId)
                .patch("/{id}")
                .then()
                .statusCode(204);

        //Check for the updated name
        given().pathParam("id", rootId)
                .get("/{id}")
                .then()
                .statusCode(200)
                .body("id", is(rootId))
                .body("rootCategory", is(newCategoryName));
    }
    /*
        testGetSubCategories: create 2 categories. Create 1 subcategory for the first category, and 2
        subcategories for the second one. Verify with 3 GETs that:
        ◦ a GET on “/subcategories” with no parentId should return all the 3 categories
        ◦ a GET with parentId of first category should return one subcategory
        ◦ a GET on “/categories/{parentId}/subcategories” of second category should return 2 subcategories
    */
    @Test
    public void testGetSubCategories(){
        String rootCategoryOne = "Root one"; //Have one sub
        String rootCategoryTwo = "Root two"; //Have two sub

        String subCategoryOne = "Sub one";
        String subCategoryTwo = "Sub two";
        String subCategoryThree = "Sub three";

        String rootOne = createCategory(rootCategoryOne);
        String rootTwo = createCategory(rootCategoryTwo);

        String subOne = createSubCategory(rootOne, subCategoryOne);
        String subTwo = createSubCategory(rootTwo, subCategoryTwo);
        String subThree = createSubCategory(rootTwo, subCategoryThree);

        changePath("subcategories");

        // a GET on “/subcategories” with no parentId should return all the 3 categories
        given().queryParam("parentId", 0)
                .get()
                .then()
                .statusCode(200)
                //.body("subCategory", is(subCategory)) //Output gets weird, "[Exam]" with brackets..
                .body("list.size()", is(3));

        // a GET with parentId of first category should return one subcategory
        given().queryParam("parentId", rootOne)
                .get()
                .then()
                .statusCode(200)
                //.body("subCategory", is(subCategory)) //Output gets weird, "[Exam]" with brackets..
                .body("list.size()", is(1));

        // a GET on “/categories/{parentId}/subcategories” of second category should return 2 subcategories
        given().queryParam("parentId", rootTwo)
                .get()
                .then()
                .statusCode(200)
                //.body("subCategory", is(subCategory)) //Output gets weird, "[Exam]" with brackets..
                .body("list.size()", is(2));

        changePath("categories");
    }
    /*
        testCreateAndGetQuiz: do a POST to create a quiz, followed by a GET to retrieve it. Note:
        every time you create a quiz, you should make sure to have a valid subcategory and category
        for it (ie, you might have to create those first).
     */
    @Test
    public void testCreateAndGetQuiz(){
        String rootCategory = "Thangs life";
        String subCategory = "Exam";
        String quizName = "Dogs";

        String question = "How many dogs are black?";
        String answerOne = "Many";
        String answerTwo = "Few";
        String answerThree = "No one";
        String answerFour = "All of them";


        String rootId = createCategory(rootCategory);
        String subId = createSubCategory(rootId, subCategory);
        String quizId = createQuiz(subId, quizName);
        String questionId = createQuestion(quizId, question, answerOne, answerTwo, answerThree, answerFour);

        changePath("quizzes");

        given().queryParam("offset", 0)
                .queryParam("limit", 10)
                .queryParam("filter", 0)
                .get()
                .then()
                .statusCode(200)
                .body("list.size()", is(1));
        changePath("categories");
    }
    /*
        testGetRandom: do a POST to create a quiz. Do a GET on “/quizzes/random” to get it back
        (Note: if there is only one element, sampling at random would necessarily return that
        element)
     */
    @Test
    public void testGetRandom(){
        String rootCategory = "Thangs life";
        String subCategory = "Exam";
        String quizName = "Dogs";

        String question = "How many dogs are black?";
        String answerOne = "Many";
        String answerTwo = "Few";
        String answerThree = "No one";
        String answerFour = "All of them";


        String rootId = createCategory(rootCategory);
        String subId = createSubCategory(rootId, subCategory);
        String quizId = createQuiz(subId, quizName);
        String questionId = createQuestion(quizId, question, answerOne, answerTwo, answerThree, answerFour);

        changePath("quizzes");
        given().get("/random")
                .then()
                .statusCode(200)
                .body("quizName", is(quizName))
                .body(containsString(question));

        changePath("categories");
    }
    /*
        testPatchChangeQuestion: create a quiz. Do a JSON Merge Patch to change its question. Do
        a GET to verify the change.
     */
    @Test
    public void testPatchChangeQuestion(){
        String rootCategory = "Thangs life";
        String subCategory = "Exam";
        String quizName = "Dogs";

        String question = "How many dogs are black?";
        String answerOne = "Many";
        String answerTwo = "Few";
        String answerThree = "No one";
        String answerFour = "All of them";

        String newQuestion = "How many ppl are dumb?";

        String rootId = createCategory(rootCategory);
        String subId = createSubCategory(rootId, subCategory);
        String quizId = createQuiz(subId, quizName);
        String questionId = createQuestion(quizId, question, answerOne, answerTwo, answerThree, answerFour);

        changePath("quizzes");//Not updated yet.
        given().pathParam("id", quizId)
                .get("/{id}")
                .then()
                .statusCode(200)
                .body("quizName", is(quizName))
                .body(containsString(question));

        changePath("qa");
        given().contentType(Formats.V1_JSON)
                .pathParam("id", questionId)
                .body(newQuestion)
                .patch("/{id}")
                .then()
                .statusCode(204);

        changePath("quizzes");
        given().pathParam("id", quizId)
                .get("/{id}")
                .then()
                .statusCode(200)
                .body("quizName", is(quizName))
                .body(containsString(newQuestion));

        changePath("categories");
    }
    /*
        testPatchChangeQuizName: create a quiz. Do a JSON Merge Patch to change its quizname. Do
        a GET to verify the change.
     */
    @Test
    public void testPatchChangeQuizName(){
        String rootCategory = "Thangs life";
        String subCategory = "Exam";
        String quizName = "Dogs";

        String question = "How many dogs are black?";
        String answerOne = "Many";
        String answerTwo = "Few";
        String answerThree = "No one";
        String answerFour = "All of them";

        String newQuizName = "Yalla";

        String rootId = createCategory(rootCategory);
        String subId = createSubCategory(rootId, subCategory);
        String quizId = createQuiz(subId, quizName);
        String questionId = createQuestion(quizId, question, answerOne, answerTwo, answerThree, answerFour);

        changePath("quizzes");//Not updated yet.
        given().contentType(Formats.V1_JSON)
                .pathParam("id", quizId)
                .body(newQuizName)
                .patch("/{id}")
                .then()
                .statusCode(204);

        given().pathParam("id", quizId)
                .get("/{id}")
                .then()
                .statusCode(200)
                .body("quizName", is(newQuizName));

        changePath("categories");
    }
    /*
        testSelfLink: create several quizzes. Do a GET on all quizzes with limit smaller than the
        number of quizzes you created. Do a second GET based on the “self” link of previous GET.
        Verify the 2 GETs return the same data.
     */
    @Test
    public void testSelfLink(){
        String rootCategory = "Thangs life";
        String subCategory = "Exam";
        String quizName = "Dogs";

        int total = 30;
        int limit = 10;


        String rootId = createCategory(rootCategory);
        String subId = createSubCategory(rootId, subCategory);
        for(int i = 0; i < total; i++){
            createQuiz(subId, quizName);
        }

        changePath("quizzes");

        ListDto<?> listDto = given()
                .queryParam("limit", limit)
                .get()
                .then()
                .statusCode(200)
                .extract()
                .as(ListDto.class);

        assertEquals(total, (int) listDto.totalSize);
        assertEquals(0, (int) listDto.rangeMin);
        assertEquals(limit - 1, (int) listDto.rangeMax);

        assertNull(listDto._links.previous);
        assertNotNull(listDto._links.next);
        assertNotNull(listDto._links.self);

        //read again using self link
        ListDto<?> selfDto = given()
                .get(listDto._links.self.href)
                .then()
                .statusCode(200)
                .extract()
                .as(ListDto.class);

        Set<String> first = getTexts(listDto);
        Set<String> self = getTexts(selfDto);

        assertContainsTheSame(first, self);

        changePath("categories");
    }
    /*
        testNextLink: create several quizzes. Do a GET with limit being less than half the number of
        quizzes you created. Keep doing GETs based on the “next” links until there is no more next
        page. Verify you have read back all the quizzes you originally created.
     */
    @Test
    public void testNextLink(){
        String rootCategory = "Thangs life";
        String subCategory = "Exam";
        String quizName = "Dogs";

        int total = 30;
        int limit = 10;


        String rootId = createCategory(rootCategory);
        String subId = createSubCategory(rootId, subCategory);
        for(int i = 0; i < total; i++){
            createQuiz(subId, quizName);
        }

        changePath("quizzes");

        ListDto<?> listDto = given()
                .queryParam("limit", limit)
                .get()
                .then()
                .statusCode(200)
                .extract()
                .as(ListDto.class);

        assertEquals(total, (int) listDto.totalSize);
        assertNotNull(listDto._links.next.href);

        Set<String> values = getTexts(listDto);
        String next = listDto._links.next.href;

        int counter = 0;

        //read pages until there is still a "next" link
        while (next != null) {
            counter++;
            int beforeNextSize = values.size();

            listDto = given()
                    .get(next)
                    .then()
                    .statusCode(200)
                    .extract()
                    .as(ListDto.class);

            values.addAll(getTexts(listDto));

            assertEquals(beforeNextSize + limit, values.size());
            assertEquals(counter * limit, (int) listDto.rangeMin);
            assertEquals(listDto.rangeMin + limit - 1, (int) listDto.rangeMax);

            if (listDto._links.next != null) {
                next = listDto._links.next.href;
            } else {
                next = null;
            }
        }

        assertEquals(total, values.size());

        changePath("categories");
    }
    /*
        textPreviousLink: create several quizzes. Do a GET with limit being less than half the
        number of quizzes you created. Do a GET on the “next” link. On this new page do a GET on
        the “previous” link. Verify this new page is equal to the first one you retrieved.
     */
    @Test
    public void textPreviousLink(){
        String rootCategory = "Thangs life";
        String subCategory = "Exam";
        String quizName = "Dogs";

        int total = 30;
        int limit = 10;


        String rootId = createCategory(rootCategory);
        String subId = createSubCategory(rootId, subCategory);
        for(int i = 0; i < total; i++){
            createQuiz(subId, quizName);
        }

        changePath("quizzes");

        ListDto<?> listDto = given()
                .queryParam("limit", limit)
                .get()
                .then()
                .statusCode(200)
                .extract()
                .as(ListDto.class);

        Set<String> first = getTexts(listDto);

        //read next page
        ListDto<?> nextDto = given()
                .get(listDto._links.next.href)
                .then()
                .statusCode(200)
                .extract()
                .as(ListDto.class);

        Set<String> next = getTexts(nextDto);
        // check that an element of next page was not in the first page
        assertTrue(!first.contains(next.iterator().next()));


        ListDto<?> previousDto = given()
                .get(nextDto._links.previous.href)
                .then()
                .statusCode(200)
                .extract()
                .as(ListDto.class);

        Set<String> previous = getTexts(previousDto);
        assertContainsTheSame(first, previous);

        changePath("categories");
    }
    /*
        testFilter: create a quiz. Do a page GET and verify that it contains it. Do a page GET with
        filter equal to the subcategory of that quiz: the page should still contain it. Do a page GET
        with filter of a different (possibly non-existent) subcategory: verify the page does not
        contain the quiz
     */
    @Test
    public void testFilter(){
        String rootCategory = "Thangs life";
        String subCategory = "Exam";
        String quizName = "Dogs";


        String rootId = createCategory(rootCategory);
        String subId = createSubCategory(rootId, subCategory);
        String quizId = createQuiz(subId, quizName);

        changePath("quizzes");
        given().get()
                .then()
                .statusCode(200)
                .body(containsString(quizName));

        given().queryParam("filter", subId)
                .get()
                .then()
                .statusCode(200)
                .body(containsString(quizName));

        given().queryParam("filter", "109232")
                .get()
                .then()
                .statusCode(200)
                .body("totalSize", is(0));

        changePath("categories");
    }
    /*
        MY OWN TESTS
    */
    @Test
    public void getByIdSub(){
        String rootCategory = "Thangs life";
        String subCategory = "Exam";

        String rootId = createCategory(rootCategory);
        String subId = createSubCategory(rootId, subCategory);

        changePath("subcategories");

        given().pathParam("id", subId)
                .get("/{id}")
                .then()
                .statusCode(200)
                .body(containsString(rootCategory));
        changePath("categories");
    }
    @Test
    public void testDeprecatedSub(){
        String rootCategory = "Thangs life";
        String subCategory = "Exam";

        String rootId = createCategory(rootCategory);
        String subId = createSubCategory(rootId, subCategory);

        given().redirects().follow(false)
                .pathParam("id", rootId)
                .get("/{id}/subcategories")
                .then()
                .statusCode(301)
                .header("Location", containsString("subcategories"));
    }
    @Test
    public void testWrongOffsetAndLimitGet(){
        String rootCategory = "Thangs life";
        String subCategory = "Exam";

        String rootId = createCategory(rootCategory);
        String subId = createSubCategory(rootId, subCategory);

        given().queryParam("offset", -1)
                .queryParam("limit", 10)
                .get()
                .then()
                .statusCode(400);

        given().queryParam("offset", 0)
                .queryParam("limit", 0)
                .get()
                .then()
                .statusCode(400);

        given().queryParam("offset", 3)
                .queryParam("limit", 10)
                .get()
                .then()
                .statusCode(400);
    }
    @Test
    public void testWrongOffsetAndLimitGetForQuiz(){
        String rootCategory = "Thangs life";
        String subCategory = "Exam";
        String quizName = "The quiz";

        String rootId = createCategory(rootCategory);
        String subId = createSubCategory(rootId, subCategory);
        createQuiz(subId, quizName);

        changePath("quizzes");

        given().queryParam("offset", -1)
                .queryParam("limit", 10)
                .get()
                .then()
                .statusCode(400);

        given().queryParam("offset", 0)
                .queryParam("limit", 0)
                .get()
                .then()
                .statusCode(400);

        given().queryParam("offset", 3)
                .queryParam("limit", 10)
                .get()
                .then()
                .statusCode(400);

        changePath("categories");
    }

    @Test
    public void testQuizQuestionAnswers(){
        String rootCategory = "Thangs life";
        String subCategory = "Exam";
        String quizName = "Dogs";

        String question = "How many dogs are black?";
        String answerOne = "Many";
        String answerTwo = "Few";
        String answerThree = "No one";
        String answerFour = "All of them";

        String newQuizName = "Yalla";

        String rootId = createCategory(rootCategory);
        String subId = createSubCategory(rootId, subCategory);
        String quizId = createQuiz(subId, quizName);
        String questionId = createQuestion(quizId, question, answerOne, answerTwo, answerThree, answerFour);

        changePath("qa");//Not updated yet.

        given().pathParam("id", questionId)
                .get("/question/{id}")
                .then()
                .statusCode(200)
                .body(containsString(question));

        given().pathParam("id", "Cant find me")
                .get("/question/{id}")
                .then()
                .statusCode(404);

        changePath("categories");
    }
    @Test
    public void testDeleteQuiz(){
        String rootCategory = "Thangs life";
        String subCategory = "Exam";
        String quizName = "Dogs";

        String question = "How many dogs are black?";
        String answerOne = "Many";
        String answerTwo = "Few";
        String answerThree = "No one";
        String answerFour = "All of them";


        String rootId = createCategory(rootCategory);
        String subId = createSubCategory(rootId, subCategory);
        String quizId = createQuiz(subId, quizName);
        createQuestion(quizId, question, answerOne, answerTwo, answerThree, answerFour);

        changePath("quizzes");
        given().pathParam("id", quizId)
                .delete("/{id}")
                .then()
                .statusCode(204);

        changePath("categories");
    }
    @Test
    public void testGetAnswerByID(){
        String rootCategory = "Thangs life";
        String subCategory = "Exam";
        String quizName = "Dogs";

        String question = "How many dogs are black?";
        String answerOne = "Many";
        String answerTwo = "Few";
        String answerThree = "No one";
        String answerFour = "All of them";


        String rootId = createCategory(rootCategory);
        String subId = createSubCategory(rootId, subCategory);
        String quizId = createQuiz(subId, quizName);
        String questionId = createQuestion(quizId, question, answerOne, answerTwo, answerThree, answerFour);

        changePath("qa");
        given().pathParam("id", quizId).pathParam("answerToQuestionv2", questionId)
                .get("/{id}/{answerToQuestionv2}")
                .then()
                .statusCode(200)
                .body(containsString(answerOne));

        changePath("categories");
    }
    @Test
    public void testWrongPatch(){
        String rootCategory = "Thangs life";

        String rootId = createCategory(rootCategory);
        String newCategoryName = "Halla";

        //Test 404
        given().contentType(Formats.V1_JSON)
                .body(newCategoryName)
                .pathParam("id", "Not work")
                .patch("/{id}")
                .then()
                .statusCode(404);
        //Test 404
        given().contentType(Formats.V1_JSON)
                .body(newCategoryName)
                .pathParam("id", rootId + 1)
                .patch("/{id}")
                .then()
                .statusCode(404);
    }

    @Test
    public void testSelfLinkCategory(){
        String rootCategory = "Thangs life";

        int total = 30;
        int limit = 10;



        for(int i = 0; i < total; i++){
            createCategory(rootCategory);
        }

        ListDto<?> listDto = given()
                .queryParam("limit", limit)
                .get()
                .then()
                .statusCode(200)
                .extract()
                .as(ListDto.class);

        assertEquals(total, (int) listDto.totalSize);
        assertEquals(0, (int) listDto.rangeMin);
        assertEquals(limit - 1, (int) listDto.rangeMax);

        assertNull(listDto._links.previous);
        assertNotNull(listDto._links.next);
        assertNotNull(listDto._links.self);

        //read again using self link
        ListDto<?> selfDto = given()
                .get(listDto._links.self.href)
                .then()
                .statusCode(200)
                .extract()
                .as(ListDto.class);

        Set<String> first = getTexts(listDto);
        Set<String> self = getTexts(selfDto);

        assertContainsTheSame(first, self);

    }

    @Test
    public void testNextLinkCategory(){
        String rootCategory = "Thangs life";

        int total = 30;
        int limit = 10;


        for(int i = 0; i < total; i++){
            createCategory(rootCategory);
        }


        ListDto<?> listDto = given()
                .queryParam("limit", limit)
                .get()
                .then()
                .statusCode(200)
                .extract()
                .as(ListDto.class);

        assertEquals(total, (int) listDto.totalSize);
        assertNotNull(listDto._links.next.href);

        Set<String> values = getTexts(listDto);
        String next = listDto._links.next.href;

        int counter = 0;

        while (next != null) {
            counter++;
            int beforeNextSize = values.size();

            listDto = given()
                    .get(next)
                    .then()
                    .statusCode(200)
                    .extract()
                    .as(ListDto.class);

            values.addAll(getTexts(listDto));

            assertEquals(beforeNextSize + limit, values.size());
            assertEquals(counter * limit, (int) listDto.rangeMin);
            assertEquals(listDto.rangeMin + limit - 1, (int) listDto.rangeMax);

            if (listDto._links.next != null) {
                next = listDto._links.next.href;
            } else {
                next = null;
            }
        }

        assertEquals(total, values.size());

    }

    @Test
    public void textPreviousLinkCategory(){
        String rootCategory = "Thangs life";

        int total = 30;
        int limit = 10;


        for(int i = 0; i < total; i++){
            createCategory(rootCategory);
        }

        ListDto<?> listDto = given()
                .queryParam("limit", limit)
                .get()
                .then()
                .statusCode(200)
                .extract()
                .as(ListDto.class);

        Set<String> first = getTexts(listDto);

        //read next page
        ListDto<?> nextDto = given()
                .get(listDto._links.next.href)
                .then()
                .statusCode(200)
                .extract()
                .as(ListDto.class);

        Set<String> next = getTexts(nextDto);
        // check that an element of next page was not in the first page
        assertTrue(!first.contains(next.iterator().next()));


        ListDto<?> previousDto = given()
                .get(nextDto._links.previous.href)
                .then()
                .statusCode(200)
                .extract()
                .as(ListDto.class);

        Set<String> previous = getTexts(previousDto);
        assertContainsTheSame(first, previous);

    }
    @Test
    public void testExpandCategory(){
        String rootCategory = "Thangs life";
        String subCategory = "Exam";

        String rootId = createCategory(rootCategory);
        createSubCategory(rootId, subCategory);

        //"expand" is default "false". I Expect "null" extraInfo.
        given().get()
                .then()
                .statusCode(200)
                .body(containsString("null"));

        given().queryParam("expand", true)
                .get()
                .then()
                .statusCode(200)
                .body(containsString(subCategory));

        given().queryParam("expand", false)
                .get()
                .then()
                .statusCode(200)
                .body(containsString("null"));
    }

}
