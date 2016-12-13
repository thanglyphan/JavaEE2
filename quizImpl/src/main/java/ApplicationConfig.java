import api.CategoryRest;
import api.QuestionAnswersRest;
import api.QuizRest;
import api.SubCategoryRest;
import datalayer.essentials.Question;
import io.swagger.jaxrs.config.BeanConfig;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by thang on 12.12.2016.
 */
@ApplicationPath("/api")
public class ApplicationConfig extends Application {


    private final Set<Class<?>> classes;

    public ApplicationConfig() {

        BeanConfig beanConfig = new BeanConfig();
        beanConfig.setVersion("0.2");
        beanConfig.setSchemes(new String[]{"http"});
        beanConfig.setHost("localhost:8080");
        beanConfig.setBasePath("/quiz/api");
        beanConfig.setResourcePackage("web");
        beanConfig.setScan(true);

    /*
      Here we define which classes provide REST APIs
     */
        HashSet<Class<?>> c = new HashSet<>();
        c.add(CategoryRest.class);
        c.add(SubCategoryRest.class);
        c.add(QuizRest.class);
        c.add(QuestionAnswersRest.class);

        //add further configuration to activate SWAGGER
        c.add(io.swagger.jaxrs.listing.ApiListingResource.class);
        c.add(io.swagger.jaxrs.listing.SwaggerSerializers.class);

        classes = Collections.unmodifiableSet(c);
    }

    @Override
    public Set<Class<?>> getClasses() {
        return classes;
    }

}