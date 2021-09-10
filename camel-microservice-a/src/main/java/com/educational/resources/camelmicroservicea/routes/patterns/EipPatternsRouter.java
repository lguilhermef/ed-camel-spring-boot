package com.educational.resources.camelmicroservicea.routes.patterns;

import com.educational.resources.camelmicroservicea.CurrencyExchange;
import org.apache.camel.*;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class EipPatternsRouter extends RouteBuilder {

    @Autowired
    SplitterComponent splitter;

    @Autowired
    DynamicRouterBean dynamicRouterBean;

    @Override
    public void configure() throws Exception {

        //Pipeline
        //Content Based Routing - choice()
        //Multicast multicast()
        //Split
/*
        from("timer:multicast-timer?period=10000")
                .multicast()
                .to("log:something1", "log:something2", "log:something3");
*/
        //Message,Message2,Message3
        /*
        from("file:files/csv")
                .unmarshal().csv()
                .split(body()) //each line becomes a different entry in the queue
                .to("activemq:split-queue");

        //Message, Message2, Message3
        from("file:files/csv")
                .convertBodyTo(String.class)
                .split(body(), ",")
                .to("activemq:splitqueue");

        from("file:files/csv")
                .convertBodyTo(String.class)
                .split(method(splitter))
                .to("activemq:split-queue");

         */

        //Aggregate
        //Messages => Aggregate => Endpoint
        //to, 3
        from("file:files/aggregate-json")
                .unmarshal().json(JsonLibrary.Jackson, CurrencyExchange.class)
                .aggregate(simple("{body.to}"), new ArrayListAggregationStrategy())
                .completionSize(3)
                //.completionTimeout(HIGHEST)
                .to("log:aggregate-json");

        //routing slip
        //String routingSlip = "direct:endpoint1, direct:endpoint2, direct:endpoint3"
        String routingSlip = "direct:endpoint1,direct:endpoint2";

        /*from("timer:routingSlip?period=10000")
                .transform().constant("Hardcoded Message for Routing Slip")
                .routingSlip(simple(routingSlip));
*/
        //Dynamic Routing
        .from("timer:routingSlip?period=10000")
                .transform().constant("Hardcoded Message for Dynamic Routing")
                .dynamicRouter(method(dynamicRouterBean));


        from("direct:endpoint1")
                .to("log:directendpoint1");

        from("direct:endpoint2")
                .to("log:directendpoint2");

        from("direct:endpoint3")
                .to("log:directendpoint3");

    }
}

@Component
class SplitterComponent {

    public List<String> splitInput(String str) {
        return List.of("ABC", "DEF", "GHI");
    }
}

@Component
class ArrayListAggregationStrategy implements AggregationStrategy {

    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        Object newBody = newExchange.getIn().getBody();
        ArrayList<Object> list = null;

        if (oldExchange == null) {
            list = new ArrayList<Object>();
            list.add(newBody);
            newExchange.getIn().setBody(list);

            return newExchange;

        } else {

            list = oldExchange.getIn().getBody(ArrayList.class);
            list.add(newBody);
            return oldExchange;
        }

    }
}

@Component
class DynamicRouterBean {

    Logger logger = LoggerFactory.getLogger(DynamicRouterBean.class);

    int invocations;

    public String decideTheNextEndpoint(@ExchangeProperties Map<String, String> properties,
                                        @Headers Map<String, String> headers,
                                        @Body String body) {

        logger.info("{} {} {}", properties, headers, body);
        invocations++;

        if (invocations % 3 == 0) {
            return "direct:endpoint1";
        }

        if (invocations % 3 == 1) {
            return "direct:endpoint2,direct:endpoint3";
        }

        return null;
    }
}
