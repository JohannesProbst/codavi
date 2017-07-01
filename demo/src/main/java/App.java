import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;

public class App {

    public static void main(String... args ) throws Exception{
        MockWebServer server = new MockWebServer();


        Iterator<String> sensorDataIterator = Files.readAllLines(Paths.get(ClassLoader.getSystemResource("sensordata1.json").toURI())).iterator();
        Dispatcher dispatcher = new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
                MockResponse response = new MockResponse();
                response.addHeader("Content-Type","application/json");
                StringBuilder builder = new StringBuilder(1024);
                builder.append("[");
                String queryParam = request.getRequestUrl().queryParameter("n");
                Integer n = queryParam == null? 1 : Integer.parseInt(queryParam);
                do{
                    builder.append(sensorDataIterator.next());
                    if(n>1)
                        builder.append(",");
                    n-=1;
                }while (n>0);
                builder.append("]");
                response.setBody(builder.toString());
                return response;
            }
        };
        server.setDispatcher(dispatcher);
        server.start(11369);
    }
}
