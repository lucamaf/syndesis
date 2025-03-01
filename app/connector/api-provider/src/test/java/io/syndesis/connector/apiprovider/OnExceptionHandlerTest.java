/*
 * Copyright (C) 2016 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.syndesis.connector.apiprovider;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.builder.ExchangeBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.syndesis.common.util.SyndesisConnectorException;

public class OnExceptionHandlerTest {

    private static final String HTTP_RESPONSE_CODE_PROPERTY        = "httpResponseCode";
    private static final String HTTP_ERROR_RESPONSE_CODES_PROPERTY = "errorResponseCodes";
    private static final String ERROR_RESPONSE_BODY                = "returnBody";


    static final String ERROR_RESPONSE_CODES = "{\"CONNECTOR_ERROR\":\"500\",\"SERVER_ERROR\":\"500\",\"DATA_ACCESS_ERROR\":\"400\",\"ENTITY_NOT_FOUND_ERROR\":\"404\"}";

    @Test
    public void testEmptyResponseBodyOnError() {
 
        Map<String,String> configuredProperties = new HashMap<>();
        configuredProperties.put(HTTP_RESPONSE_CODE_PROPERTY       , "200");
        configuredProperties.put(HTTP_ERROR_RESPONSE_CODES_PROPERTY, ERROR_RESPONSE_CODES);
        configuredProperties.put(ERROR_RESPONSE_BODY               , "false");

        Exception e = new SyndesisConnectorException(
                "CONNECTOR_ERROR", "error msg test");

        CamelContext context = new DefaultCamelContext();
        Exchange exchange = new ExchangeBuilder(context).build();
        exchange.setException(e);

        ApiProviderOnExceptionHandler handler = new ApiProviderOnExceptionHandler();
        handler.setProperties(configuredProperties);
        handler.process(exchange);

        Message in = exchange.getIn();
        Assertions.assertEquals(Integer.valueOf(500)             ,in.getHeader(Exchange.HTTP_RESPONSE_CODE));
        Assertions.assertEquals(""                               ,in.getBody());
    }

    @Test
    public void testErrorStatusInfoResponse() {
 
        String expectedBody = "{\"httpResponseCode\":404,\"category\":\"ENTITY_NOT_FOUND_ERROR\",\"message\":\"entity not found\",\"error\":\"syndesis_connector_error\"}";

        Map<String,String> configuredProperties = new HashMap<>();
        configuredProperties.put(HTTP_RESPONSE_CODE_PROPERTY       , "200");
        configuredProperties.put(HTTP_ERROR_RESPONSE_CODES_PROPERTY, ERROR_RESPONSE_CODES);
        configuredProperties.put(ERROR_RESPONSE_BODY               , "true");

        Exception e = new SyndesisConnectorException(
                "ENTITY_NOT_FOUND_ERROR", "entity not found");

        Exchange exchange = new ExchangeBuilder(new DefaultCamelContext()).build();
        exchange.setException(e);

        ApiProviderOnExceptionHandler handler = new ApiProviderOnExceptionHandler();
        handler.setProperties(configuredProperties);
        handler.process(exchange);

        Message in = exchange.getIn();
        Assertions.assertEquals(Integer.valueOf(404),in.getHeader(Exchange.HTTP_RESPONSE_CODE));
        Assertions.assertEquals(expectedBody        ,in.getBody());

    }
}
