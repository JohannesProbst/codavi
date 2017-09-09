import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;

public class App {

    static Iterator<String> dataIterator;

    static List fileContentList;

    static String path;

    public static void main(String... args ) throws Exception{
        MockWebServer server = new MockWebServer();

        Dispatcher dispatcher = new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) throws InterruptedException {

                MockResponse response = new MockResponse();
                if(!request.getRequestUrl().pathSegments().get(0).equals("favicon.ico")){

                    response.addHeader("Content-Type","application/json");
                    StringBuilder builder = new StringBuilder(1024);
                    String queryParam = request.getRequestUrl().queryParameter("n");

                    try {
                        if(App.dataIterator == null || !request.getRequestUrl().pathSegments().get(0).equals(App.path) )
                            App.dataIterator = getResourceData(request.getRequestUrl().pathSegments().get(0));
                    } catch (Exception e) {
                        e.printStackTrace();
                        return response.setBody(e.toString());
                    }
                    Boolean isRequestForMultipleValues = false;
                    Integer n = queryParam == null? 1 : Integer.parseInt(queryParam);
                    //if(n>1) {
                        isRequestForMultipleValues = true;
                        builder.append("{ \"values\": ");
                    //}
                        builder.append("[ ");
                    do{
                        if (!App.dataIterator.hasNext()){
                            App.dataIterator = App.fileContentList.iterator();
                        }
                        builder.append(App.dataIterator.next());
                        if(n>1)
                            builder.append(",");
                        n-=1;
                    }while (n>0);

                    builder.append("]");
                    //if(isRequestForMultipleValues){
                        builder.append(" }");
                    //}
                    response.setBody(builder.toString());
                }
                return response;
            }
        };
        server.setDispatcher(dispatcher);
        server.start(11369);
    }

    private static Iterator<String> getResourceData(String path) throws Exception {
        App.path = path;
        App.fileContentList = Files.readAllLines(Paths.get(ClassLoader.getSystemResource(path+".json").toURI()));
        App.dataIterator = App.fileContentList.iterator();
        return App.dataIterator;
    }
}
