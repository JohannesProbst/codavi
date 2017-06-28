package at.ac.fhsalzburg.repository;

import at.ac.fhsalzburg.controller.ChartsController;
import at.ac.fhsalzburg.entity.Chart;
import com.google.common.io.CharStreams;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

@Repository
public class PredefinedChartRepositoryImpl implements PredefinedChartRepository {

    @Override
    public String getChartByName(String chartName) {
        if(chartName.equals("bar-chart")){
            Resource resource = new ClassPathResource("charts/bar-chart.json");
            try(final Reader reader = new InputStreamReader(resource.getInputStream())) {
                return CharStreams.toString(reader);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(chartName.equals("line-chart")){
            Resource resource = new ClassPathResource("charts/line-chart.json");
            try(final Reader reader = new InputStreamReader(resource.getInputStream())) {
                return CharStreams.toString(reader);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    @Override
    public List<Chart> getAllChartNames() {
        List<Chart> charts = new ArrayList<>();
        charts.add(new Chart());
        charts.get(charts.size()-1).name = "Bar Chart";
        charts.add(new Chart());
        charts.get(charts.size()-1).name = "Line Chart";
        return charts;
    }
}
