package com.educational.resources.camelmicroservicea.routes.b;

import org.apache.camel.ExchangeProperties;
import org.apache.camel.Headers;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MyFileRouter extends RouteBuilder {

    @Autowired
    private DeciderBean deciderBean;

    @Override
    public void configure() throws Exception {
        from("file:files/input")
                .routeId("Files-Input-Route")
                .transform().body(String.class)
                .choice()
                    //.when(simple("${file:ext} ends with 'xml'"))
                    .when(simple("${file:ext} == 'xml'"))
                        .log("XML FILE")
                    //.when(simple("${body} contains 'USD'"))
                    .when(method(deciderBean))
                        .log("Not an XML FILE but contains USD")
                    .otherwise()
                        .log("Not a XML FILE")
                //.body()
                .end()
                //.log("${body}")
                //.log("${messageHistory} ${file:absolute.path}")
                //.to("direct://log-file-values")
                .to("file:files/output");

        from("direct:log-file-values")
                .log("${messageHistory} ${file:absolute.path}")
                .log("${file:name} ${file:name.ext}")
                .log("${file:size} ${file:modified}")
                .log("${routeId} ${camelId} ${body}");
    }
}

@Component
class DeciderBean {

    Logger logger = LoggerFactory.getLogger(DeciderBean.class);

    public boolean isThisConditionMet(String body, @Headers Map<String, String> headers, @ExchangeProperties Map<String, String> exchangeProperties) {
        logger.info("DeciderBean {} {} {}", body, headers, exchangeProperties);

        return true;
    }
}