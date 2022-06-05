package org.tsdl.service.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.assertj.core.util.TriFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

/**
 * Abstract base class bundling functionality for integrationt ests.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration-test")
public abstract class BaseIntegrationTest {
  @LocalServerPort
  private int port;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private TestRestTemplate restTemplate;

  protected static <K, V> MultiValueMap<K, V> toMultiValueMap(Map<K, V> map) {
    var multiValueMap = new LinkedMultiValueMap<K, V>();
    map.forEach((key, value) -> multiValueMap.put(key, Collections.singletonList(value)));
    return multiValueMap;
  }

  abstract String getEndpointUrl();

  protected String requestUrl() {
    return "http://localhost:" + port + "/" + getEndpointUrl();
  }

  protected String requestUrl(String suffix, Object... args) {
    return requestUrl() + "/" + String.format(suffix, args);
  }

  protected <T> ResponseEntity<T> get(String url, Class<T> clazz) {
    return exchange(url, HttpMethod.GET, clazz);
  }

  protected <T> ResponseEntity<T> get(String url, MultiValueMap<String, String> queryParams, Class<T> clazz) {
    return exchange(url, HttpMethod.GET, queryParams, clazz);
  }

  protected <T> ResponseEntity<T> get(String url, MultiValueMap<String, String> queryParams, ParameterizedTypeReference<T> responseType) {
    return exchange(url, HttpMethod.GET, queryParams, null, responseType);
  }

  protected <T> ResponseEntity<T> exchange(String url, HttpMethod method, Class<T> responseType) {
    return exchange(url, method, null, null, responseType);
  }

  protected <T> ResponseEntity<T> exchange(String url, HttpMethod method, MultiValueMap<String, String> queryParams, Class<T> responseType) {
    return exchange(url, method, queryParams, null, responseType);
  }

  protected <T, U> ResponseEntity<T> exchange(String url, HttpMethod method, U requestBody, Class<T> responseType) {
    return exchange(url, method, null, requestBody, responseType);
  }

  protected <T, U> ResponseEntity<T> exchange(String url, HttpMethod method, MultiValueMap<String, String> queryParams, U requestBody,
                                              Class<T> responseType) {
    return exchange(url, method, queryParams, requestBody,
        (requestUri, httpMethod, requestEntity) -> restTemplate.exchange(requestUri, httpMethod, requestEntity, responseType));
  }

  protected <T, U> ResponseEntity<T> exchange(String url, HttpMethod method, MultiValueMap<String, String> queryParams, U requestBody,
                                              ParameterizedTypeReference<T> responseType) {
    return exchange(url, method, queryParams, requestBody,
        (requestUri, httpMethod, requestEntity) -> restTemplate.exchange(requestUri, httpMethod, requestEntity, responseType));
  }

  private <T, U> ResponseEntity<T> exchange(String url, HttpMethod method, MultiValueMap<String, String> queryParams, U requestBody,
                                            TriFunction<URI, HttpMethod, HttpEntity<U>, ResponseEntity<T>> exchangeFunction) {
    // Content Types for OpenAPI specifications (source: https://github.com/OAI/OpenAPI-Specification/issues/110#issuecomment-364498200)
    //   - "application/vnd.oai.openapi" (YAML variant), not yet registered with IANA
    //   - "application/vnd.oai.openapi+json" (JSON only variant), not yet registered with IANA
    var headers = new HttpHeaders();
    headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
    headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
    headers.setAccept(List.of(MediaType.APPLICATION_JSON,
        new MediaType("application", "vnd.oai.openapi", StandardCharsets.UTF_8),
        new MediaType("application", "vnd.oai.openapi+json", StandardCharsets.UTF_8)));

    var requestUri = UriComponentsBuilder
        .fromHttpUrl(url)
        .encode(StandardCharsets.UTF_8)
        .queryParams(encodeQueryParameters(queryParams))
        .build(true)
        .toUri();
    var requestEntity = new HttpEntity<>(requestBody, headers);

    return exchangeFunction.apply(requestUri, method, requestEntity);
  }

  private MultiValueMap<String, String> encodeQueryParameters(MultiValueMap<String, String> queryParams) {
    if (queryParams == null) {
      return null;
    }

    var encodedQueryParams = new LinkedMultiValueMap<String, String>();
    queryParams.forEach((key, value) -> encodedQueryParams.put(key, value.stream()
        .map(item -> UriUtils.encode(item, StandardCharsets.UTF_8))
        .collect(Collectors.toList())));
    return encodedQueryParams;
  }
}
