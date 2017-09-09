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
        return "";
    }
}
