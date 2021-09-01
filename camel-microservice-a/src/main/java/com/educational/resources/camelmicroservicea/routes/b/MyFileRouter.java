package com.educational.resources.camelmicroservicea.routes.b;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class MyFileRouter extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("file:files/input")
                .routeId("Files-Input-Route")
                .transform().body(String.class)
                .choice()
                    //.when(simple("${file:ext} ends with 'xml'"))
                    .when(simple("${file:ext} == 'xml'"))
                        .log("XML FILE")
                    .when(simple("${body} contains 'USD'"))
                        .log("Not an XML FILE but contains USD")
                    .otherwise()
                        .log("Not a XML FILE")
                //.body()
                .end()
                //.log("${body}")
                //.log("${messageHistory} ${file:absolute.path}")
                .to("direct://log-file-values")
                .to("file:files/output");

        from("direct:log-file-values")
                .log("${messageHistory} ${file:absolute.path}")
                .log("${file:name} ${file:name.txt}")
                .log("${file:size} ${file:modified}")
                .log("${routeId} ${camelId} ${body}");
    }
}