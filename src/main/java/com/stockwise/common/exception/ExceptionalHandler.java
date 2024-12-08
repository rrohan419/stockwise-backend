package com.stockwise.common.exception;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Mono;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ExceptionalHandler implements WebExceptionHandler {

    private final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        if (ex instanceof CustomException customException) {

            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setMessage(ex.getMessage());
            errorResponse.setPath(exchange.getRequest().getURI().getPath());
            errorResponse.setRequestId(exchange.getRequest().getId());
            errorResponse.setStatus(customException.getHttpStatus().value());

            String responseBody = "";
            try {
                responseBody = objectMapper.writeValueAsString(errorResponse);
            } catch (JsonProcessingException e) {
                return Mono.error(ex);
            }

            DataBuffer dataBuffer = exchange.getResponse().bufferFactory().wrap(responseBody.getBytes());
            exchange.getResponse().setStatusCode(customException.getHttpStatus());
            exchange.getResponse().setRawStatusCode(customException.getHttpStatus().value());
            exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

            return exchange.getResponse().writeWith(Mono.just(dataBuffer));
        }
        return Mono.error(ex);
    }
}
