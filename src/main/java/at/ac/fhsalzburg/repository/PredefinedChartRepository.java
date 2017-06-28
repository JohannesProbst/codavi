package at.ac.fhsalzburg.repository;

import java.util.List;

public interface PredefinedChartRepository {

    String getChartByName(String chartName);

    List getAllChartNames();
}
