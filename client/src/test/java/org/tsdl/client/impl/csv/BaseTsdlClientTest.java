package org.tsdl.client.impl.csv;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.tsdl.client.api.QueryClientResult;
import org.tsdl.client.api.QueryClientSpecification;
import org.tsdl.infrastructure.dto.QueryResultDto;
import org.tsdl.infrastructure.model.QueryResult;

class BaseTsdlClientTest {
  private static final BaseTsdlClient<QueryClientSpecification> CLIENT = new TestBaseClientImpl();
  private static MockWebServer mockWebServer;
  private static String baseUrl;

  @BeforeAll
  static void setUp() throws IOException {
    mockWebServer = new MockWebServer();
    mockWebServer.start();
    baseUrl = String.format("http://localhost:%s", mockWebServer.getPort());
  }

  @AfterAll
  static void tearDown() throws IOException {
    mockWebServer.shutdown();
  }

  @ParameterizedTest
  @MethodSource("org.tsdl.client.stub.BaseTsdlClientTestDataFactory#query_serviceReturnsDataPoints_deserializesCorrectly")
  void query_serviceReturnsDataPoints_deserializesCorrectly(String serviceResponse, QueryResultDto expectedResult) throws IOException {
    enqueueMockResponse(serviceResponse);

    var result = CLIENT.query(baseUrl, null);
    assertThat(result)
        .usingRecursiveComparison()
        .isEqualTo(expectedResult);
  }

  @ParameterizedTest
  @MethodSource("org.tsdl.client.stub.BaseTsdlClientTestDataFactory#query_serviceReturnsPeriodSet_deserializesCorrectly")
  void query_serviceReturnsPeriodsSet_deserializesCorrectly(String serviceResponse, QueryResultDto expectedResult) throws IOException {
    enqueueMockResponse(serviceResponse);

    var result = CLIENT.query(baseUrl, null);
    assertThat(result)
        .usingRecursiveComparison()
        .isEqualTo(expectedResult);
  }

  @ParameterizedTest
  @MethodSource("org.tsdl.client.stub.BaseTsdlClientTestDataFactory#query_serviceReturnsPeriod_deserializesCorrectly")
  void query_serviceReturnsPeriod_deserializesCorrectly(String serviceResponse, QueryResultDto expectedResult) throws IOException {
    enqueueMockResponse(serviceResponse);

    var result = CLIENT.query(baseUrl, null);
    assertThat(result)
        .usingRecursiveComparison()
        .isEqualTo(expectedResult);
  }

  @ParameterizedTest
  @MethodSource("org.tsdl.client.stub.BaseTsdlClientTestDataFactory#query_serviceReturnsScalar_deserializesCorrectly")
  void query_serviceReturnsScalar_deserializesCorrectly(String serviceResponse, QueryResultDto expectedResult) throws IOException {
    enqueueMockResponse(serviceResponse);

    var result = CLIENT.query(baseUrl, null);
    assertThat(result)
        .usingRecursiveComparison()
        .isEqualTo(expectedResult);
  }

  @ParameterizedTest
  @MethodSource("org.tsdl.client.stub.BaseTsdlClientTestDataFactory#query_serviceReturnsScalarList_deserializesCorrectly")
  void query_serviceReturnsScalarList_deserializesCorrectly(String serviceResponse, QueryResultDto expectedResult) throws IOException {
    enqueueMockResponse(serviceResponse);

    var result = CLIENT.query(baseUrl, null);
    assertThat(result)
        .usingRecursiveComparison()
        .isEqualTo(expectedResult);
  }

  @ParameterizedTest
  @ValueSource(strings = {"", "null", "{{}}", "{;]", "{}", "{ \"result\": {}, \"type\": \"SCALAR\" } ",
      "{ \"result\": null, \"type\": \"SCALAR\" } ", "{ \"type\": \"SCALAR\" } "})
  void query_serviceReturnsInvalidResponse_throws(String response) {
    enqueueMockResponse(response);

    assertThatThrownBy(() -> CLIENT.query(baseUrl, null));
  }

  @Test
  void query_serviceReturnsError_throws() {
    mockWebServer.enqueue(new MockResponse()
        .setStatus("HTTP/1.1 500 Internal Server Error")
        .setBody("problems")
        .addHeader("Content-Type", "application/json")
    );

    assertThatThrownBy(() -> CLIENT.query(baseUrl, null)).isInstanceOf(IOException.class);
  }

  @Test
  void query_serviceReturns2xxButNot200_throws() {
    mockWebServer.enqueue(new MockResponse()
        .setStatus("HTTP/1.1 201 Created")
        .setBody("problems")
        .addHeader("Content-Type", "application/json")
    );

    assertThatThrownBy(() -> CLIENT.query(baseUrl, null)).isInstanceOf(IOException.class);
  }

  private static void enqueueMockResponse(String responseBody) {
    mockWebServer.enqueue(new MockResponse()
        .setBody(responseBody)
        .addHeader("Content-Type", "application/json")
    );
  }

  static class TestBaseClientImpl extends BaseTsdlClient<QueryClientSpecification> {
    @Override
    protected QueryClientResult query(QueryClientSpecification querySpecification, QueryResultDto serverResponse) {
      throw new UnsupportedOperationException("This class subtype solely serves as means to instantiate the otherwise abstract BaseTsdlClient.");
    }

    @Override
    public QueryResult query(String filePath, Object arg1, Object arg2) {
      throw new UnsupportedOperationException("This class subtype solely serves as means to instantiate the otherwise abstract BaseTsdlClient.");
    }

    @Override
    Class<QueryClientSpecification> configClass() {
      return QueryClientSpecification.class;
    }
  }
}
