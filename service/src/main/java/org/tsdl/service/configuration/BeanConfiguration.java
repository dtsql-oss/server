package org.tsdl.service.configuration;

import com.influxdb.query.FluxTable;
import de.siegmar.fastcsv.reader.CsvRow;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.tsdl.implementation.evaluation.TsdlQueryService;
import org.tsdl.infrastructure.api.QueryService;
import org.tsdl.service.model.TsdlStorage;
import org.tsdl.storage.csv.CsvStorageConfiguration;
import org.tsdl.storage.csv.CsvStorageProperty;
import org.tsdl.storage.csv.CsvStorageService;
import org.tsdl.storage.influxdb.InfluxDbStorageConfiguration;
import org.tsdl.storage.influxdb.InfluxDbStorageProperty;
import org.tsdl.storage.influxdb.InfluxDbStorageService;

@Configuration
public class BeanConfiguration {
  public static final String INFLUXDB_STORAGE_BEAN = "storage.influxdb";
  public static final String CSV_STORAGE_BEAN = "storage.csv";

  @Bean(CSV_STORAGE_BEAN)
  TsdlStorage<CsvRow, CsvStorageConfiguration> csvStorageService() {
    return new TsdlStorage<>(new CsvStorageService(), CsvStorageConfiguration::new, CsvStorageProperty.class);
  }

  @Bean(INFLUXDB_STORAGE_BEAN)
  TsdlStorage<FluxTable, InfluxDbStorageConfiguration> influxDbStorageService() {
    return new TsdlStorage<>(new InfluxDbStorageService(), InfluxDbStorageConfiguration::new, InfluxDbStorageProperty.class);
  }

  @Bean
  QueryService queryService() {
    return new TsdlQueryService();
  }
}
