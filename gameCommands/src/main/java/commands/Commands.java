package commands;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by thang on 12.12.2016.
 */
public class Commands {

    public HttpURLConnection connect(String requestUrl, String requestMethod){
        return new CallQuiz(requestUrl, requestMethod).execute();
    }
    private class CallQuiz extends HystrixCommand<HttpURLConnection> {

        private final String requestUrl;
        private final String requestMethod;

        protected CallQuiz(String requestUrl, String requestMethod) {
            super(HystrixCommandGroupKey.Factory.asKey("Interactions with Quiz"));
            this.requestUrl = requestUrl;
            this.requestMethod = requestMethod;
        }

        @Override
        protected HttpURLConnection run() throws Exception {
            URL obj = new URL(requestUrl);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod(requestMethod);
            return con;
        }

        @Override
        protected HttpURLConnection getFallback() {
            //this is what is returned in case of exceptions or timeouts
            return null;
        }
    }
}
