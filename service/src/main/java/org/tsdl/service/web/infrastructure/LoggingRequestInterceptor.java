package org.tsdl.service.web.infrastructure;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * An interceptor which provides extension points around a request handler, i.e. an endpoint method execution. It is used to log diagnostic
 * information such as HTTP method, request route at the beginning of the request as well as the response status code after processing of the
 * respective request.
 */
@Component
@Slf4j
public class LoggingRequestInterceptor implements HandlerInterceptor {
  @Override
  public boolean preHandle(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
    log.info("Starting request \"{} {}\"", request.getMethod(), request.getRequestURI());
    return true;
  }

  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response, @NonNull Object handler, Exception ex) throws Exception {
    var statusCode = HttpStatus.resolve(response.getStatus());
    log.info("Completed request \"{} {}\" - {}{}",
        request.getMethod(),
        request.getRequestURI(),
        response.getStatus(),
        statusCode != null ? " (" + statusCode.getReasonPhrase() + ")" : "");
  }
}

