package com.educational.resources.camelmicroserviceb.routes;

import com.educational.resources.camelmicroserviceb.CurrencyExchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ActiveMqReceiverRouter extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        /*from("activemq:my-queue")
                .to("log:received-message-from-active-mq");
        */
        from("activemq:my-queue")
                .unmarshal().json(JsonLibrary.Jackson, CurrencyExchange.class)
                .to("log:received-message-from-active-mq");
    }
}

