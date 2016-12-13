package businesslayer;

import datalayer.Category;
import datalayer.CategorySub;
import datalayer.Quiz;
import datalayer.essentials.Answer;
import datalayer.essentials.Question;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by thang on 01.11.2016.
 */
@Stateless
public class QuizEJB {

    @PersistenceContext
    EntityManager em;

    private void persistInATransaction(Object... obj) {
        for(Object o : obj) {
            em.persist(o);
        }
    }

    public Category createCategory(String categoryName){
        Category category = new Category();
        category.setCategoryName(categoryName);
        category.setCategorySubs(new ArrayList<>());


        persistInATransaction(category);
        return category;
    }

    public CategorySub createCategorySub(Category category, String categorySubName){
        CategorySub categorySub = new CategorySub();
        categorySub.setCategorySubName(categorySubName);
        categorySub.setCategory(category);
        categorySub.setQuizList(new ArrayList<>());
        persistInATransaction(categorySub);

        category.getCategorySubs().add(categorySub);
        em.merge(category);

        return categorySub;
    }
    public Quiz createQuiz(String categorySubId, String quizName){
        Quiz quiz = new Quiz();
        quiz.setQuizName(quizName);
        quiz.setCategorySubId(categorySubId);
        quiz.setQuestion(new ArrayList<>());

        CategorySub found = getSub(Long.parseLong(categorySubId));
        if(found!= null){
            persistInATransaction(quiz);
            found.getQuizList().add(quiz);
            //em.merge(found);
        }else{
            return null;
        }
        return quiz;
    }

    public Question createQuestion(Quiz quiz, String q){
        Question question = new Question();
        question.setQuestion(q);
        //question.setQuiz(quiz);
        persistInATransaction(question);

        quiz.getQuestion().add(question);
        em.merge(quiz);
        return question;
    }

    public void createAnswerToQuestion(Question question, String choiceOne, String choiseTwo, String choiceThree, String choiceFour){
        Answer answer = new Answer();
        answer.setChoiceOne(choiceOne);
        answer.setChoiceTwo(choiseTwo);
        answer.setChoiceThree(choiceThree);
        answer.setChoiceFour(choiceFour);
        //answer.setQuestion(question);
        answer.setSolutionToAnswer(choiceOne);
        persistInATransaction(answer);

        question.setAnswer(answer);
        em.merge(question);
    }


    public boolean updatePatch(@NotNull Long id, @NotNull String rootCategory){
        Category category = em.find(Category.class, id);

        if(category == null){
            return false;
        }
        category.setCategoryName(rootCategory);
        return true;
    }

    public boolean updatePatchQuiz(@NotNull Long id, @NotNull String quizName){
        Quiz quiz = getQuiz(id);

        if(quiz == null){
            return false;
        }
        quiz.setQuizName(quizName);
        return true;
    }
    public boolean updatePatchQuestion(@NotNull Long id, @NotNull String questionText){
        Question question = getQuestion(id);

        if(question == null){
            return false;
        }
        question.setQuestion(questionText);
        return true;
    }

    public List<Quiz> getQuizListWithMaxLimitAndFilterBySubID(String filter){

        List<CategorySub> categorySubList = getCategoryListSub();
        List<Quiz> quizList = new ArrayList<>();
        for(CategorySub a: categorySubList){
            if(a.getId().toString().equals(filter)){
                for(Quiz b: a.getQuizList()){
                    quizList.add(b);
                }
            }
        }
        return quizList;
    }
    public Question getQuestion(Long id) { return em.find(Question.class, id); }
    public Quiz getQuiz(Long id){
        return em.find(Quiz.class, id);
    }
    public void deleteQuiz(@NotNull Long id){
        Quiz found = em.find(Quiz.class, id);
        if(found != null){
            CategorySub categorySub = getSub(Long.parseLong(found.getCategorySubId()));
            categorySub.getQuizList().remove(found);
            em.merge(categorySub);
        }
        em.remove(found);
    }
    public List<Category> getWholePackage(boolean expand, int maxResult){
        return em.createNamedQuery(Category.FIND_ALL).setMaxResults(maxResult).getResultList();
    }
    public List<CategorySub> getCategoryListSubWithMax (int maxResult){
        return em.createNamedQuery(CategorySub.FIND_ALL).setMaxResults(maxResult).getResultList();
    }
    public List<CategorySub> getCategoryListSubWithMaxByParentID(int maxResult, String categoryID){
        List<Category> list = em.createNamedQuery(Category.FIND_ALL).setMaxResults(maxResult).getResultList();
        List<CategorySub> categorySubList = new ArrayList<>();
        for(Category a: list){
            if(a.getId().toString().equals(categoryID)){
                for(CategorySub b: a.getCategorySubs()){
                    categorySubList.add(b);
                }
            }
        }
        return categorySubList;
    }
    public List<CategorySub> getCategoryListSub(){
        return em.createNamedQuery(CategorySub.FIND_ALL).getResultList();
    }
    public List<Quiz> getQuizListWithMaxLimit(int limit){return em.createNamedQuery(Quiz.FIND_ALL).setMaxResults(limit).getResultList();}
    public Category get(@NotNull Long categoryId) {
        return em.find(Category.class, categoryId);
    }
    public CategorySub getSub(@NotNull Long id){
        return em.find(CategorySub.class, id);
    }
    public void deleteCategory(@NotNull Long categoryId) {
        em.remove(em.find(Category.class, categoryId));
    }


}


