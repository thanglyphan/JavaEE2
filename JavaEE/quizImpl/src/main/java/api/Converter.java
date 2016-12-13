package api;


import datalayer.Category;
import datalayer.CategorySub;
import datalayer.Quiz;
import datalayer.essentials.Question;
import dto.CategoryDto;
import dto.QuestionDto;
import dto.QuizDto;
import dto.SubCategoryDto;
import dto.collection.ListDto;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by thang on 31.10.2016.
 */
public class Converter {
    public Converter() {
    }

    public static CategoryDto transform(Category entity, boolean expand) {
        Objects.requireNonNull(entity);

        CategoryDto dto = new CategoryDto();
        dto.id = String.valueOf(entity.getId());
        dto.rootCategory = entity.getCategoryName();
        Map<String, String> list = new HashMap<>();

        if(expand){
            for(CategorySub a: entity.getCategorySubs()){
                if(a.getCategory().getId().equals(entity.getId())){
                    list.put(a.getId().toString(), a.getCategorySubName());
                }
            }
            dto.extraInfo = list;
        }

        return dto;
    }

    public static SubCategoryDto transform(CategorySub entity) {
        Objects.requireNonNull(entity);

        SubCategoryDto dto = new SubCategoryDto();
        dto.id = String.valueOf(entity.getId());
        dto.rootId = String.valueOf(entity.getCategory().getId());
        dto.subCategory = entity.getCategorySubName();
        dto.rootCategory = entity.getCategory().getCategoryName();
        return dto;
    }
    public static QuestionDto transform(Question entity){
        Objects.requireNonNull(entity);

        QuestionDto dto = new QuestionDto();
        dto.id = String.valueOf(entity.getQuestionsId());
        dto.question = entity.getQuestion();
        dto.choiceOne = entity.getAnswer().getChoiceOne();
        dto.choiceTwo = entity.getAnswer().getChoiceThree();
        dto.choiceThree = entity.getAnswer().getChoiceThree();
        dto.choiceFour = entity.getAnswer().getChoiceFour();

        return dto;
    }
    public static QuizDto transform(Quiz entity) {
        Objects.requireNonNull(entity);

        QuizDto dto = new QuizDto();
        dto.id = String.valueOf(entity.getId());
        dto.quizName = entity.getQuizName();
        dto.subCategoryId = entity.getCategorySubId();


        Map<String, List<String>> listHashMap = new HashMap<>();
        List<String> holderAnswer;

        for(Question a: entity.getQuestion()){
            holderAnswer = new ArrayList<>();
            holderAnswer.add(a.getAnswer().getChoiceOne());
            holderAnswer.add(a.getAnswer().getChoiceTwo());
            holderAnswer.add(a.getAnswer().getChoiceThree());
            holderAnswer.add(a.getAnswer().getChoiceFour());
            listHashMap.put(a.getQuestionsId() + "; " + a.getQuestion(), holderAnswer);
        }
        dto.questionsAndAnswersList = listHashMap;
        return dto;
    }

    public static ListDto<CategoryDto> transformCollection(List<Category> categoryList, int offset, int limit, boolean expand){
        List<CategoryDto> dtoList = null;
        if(categoryList != null){
            dtoList = categoryList.stream()
                    .skip(offset) // this is a good example of how streams simplify coding
                    .limit(limit)
                    .map(n -> transform(n, expand))
                    .collect(Collectors.toList());
        }


        ListDto<CategoryDto> dto = new ListDto<>();
        dto.list = dtoList;
        dto._links = new ListDto.ListLinks();
        dto.rangeMin = offset;
        dto.rangeMax = dto.rangeMin + dtoList.size() - 1;
        dto.totalSize = categoryList.size();
        return dto;
    }

    public static List<SubCategoryDto> transformSub(List<CategorySub> entities) {
        Objects.requireNonNull(entities);
        return entities.stream().map(Converter::transform).collect(Collectors.toList());
    }
    public static ListDto<QuizDto> transformCollectionQuiz(List<Quiz> quizList, int offset, int limit) {
        List<QuizDto> dtoList = null;
        if (quizList != null) {
            dtoList = quizList.stream()
                    .skip(offset) // this is a good example of how streams simplify coding
                    .limit(limit)
                    .map(n -> transform(n))
                    .collect(Collectors.toList());
        }


        ListDto<QuizDto> dto = new ListDto<>();
        dto.list = dtoList;
        dto._links = new ListDto.ListLinks();
        dto.rangeMin = offset;
        dto.rangeMax = dto.rangeMin + dtoList.size() - 1;
        dto.totalSize = quizList.size();
        return dto;
    }
}