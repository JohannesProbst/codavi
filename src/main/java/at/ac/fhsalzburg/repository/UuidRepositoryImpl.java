package at.ac.fhsalzburg.repository;

import com.google.common.io.CharStreams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

@Repository
public class UuidRepositoryImpl implements UuidRepository {

    //@Value("classpath:bar-chart.json")
    //private Resource resource;

    @Override
    public String getUuid(String uuid) {
        if(uuid.equals("23a45cd535f")){
            Resource resource = new ClassPathResource("charts/bar-chart.json");
            try(final Reader reader = new InputStreamReader(resource.getInputStream())) {
                return CharStreams.toString(reader);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(uuid.equals("abcd5435216")){
            Resource resource = new ClassPathResource("charts/line-chart.json");
            try(final Reader reader = new InputStreamReader(resource.getInputStream())) {
                return CharStreams.toString(reader);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(uuid.equals("db35d545ff1")){
            Resource resource = new ClassPathResource("charts/bar-chart-bitcoin.json");
            try(final Reader reader = new InputStreamReader(resource.getInputStream())) {
                return CharStreams.toString(reader);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(uuid.equals("ff433cd341a")){
            Resource resource = new ClassPathResource("charts/bar-chart-horizontal.json");
            try(final Reader reader = new InputStreamReader(resource.getInputStream())) {
                return CharStreams.toString(reader);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(uuid.equals("g1d78ac546b")){
            Resource resource = new ClassPathResource("charts/wordcloud.json");
            try(final Reader reader = new InputStreamReader(resource.getInputStream())) {
                return CharStreams.toString(reader);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(uuid.equals("123fc57ab1d")){
            //TODO: there exist some issues with proper refreshing of the area-chart at the client side
            Resource resource = new ClassPathResource("charts/area-chart.json");
            try(final Reader reader = new InputStreamReader(resource.getInputStream())) {
                return CharStreams.toString(reader);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(uuid.equals("ef90ac125db")){
            Resource resource = new ClassPathResource("charts/zenon-line.json");
            try(final Reader reader = new InputStreamReader(resource.getInputStream())) {
                return CharStreams.toString(reader);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }
}
