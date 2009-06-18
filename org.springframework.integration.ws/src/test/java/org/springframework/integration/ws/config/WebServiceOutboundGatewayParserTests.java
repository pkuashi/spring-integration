/*
 * Copyright 2002-2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.integration.ws.config;

import static org.junit.Assert.*;

import org.junit.Test;

import org.springframework.beans.DirectFieldAccessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.integration.endpoint.AbstractEndpoint;
import org.springframework.integration.endpoint.EventDrivenConsumer;
import org.springframework.integration.endpoint.PollingConsumer;
import org.springframework.integration.scheduling.IntervalTrigger;
import org.springframework.integration.ws.MarshallingWebServiceOutboundGateway;
import org.springframework.integration.ws.SimpleWebServiceOutboundGateway;
import org.springframework.integration.ws.destination.HeaderBasedDestinationProvider;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;
import org.springframework.ws.WebServiceMessageFactory;
import org.springframework.ws.client.core.FaultMessageResolver;
import org.springframework.ws.client.core.SourceExtractor;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.transport.WebServiceMessageSender;

/**
 * @author Mark Fisher
 */
public class WebServiceOutboundGatewayParserTests {

	@Test
	public void simpleGatewayWithDefaultSourceExtractor() {
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"simpleWebServiceOutboundGatewayParserTests.xml", this.getClass());
		AbstractEndpoint endpoint = (AbstractEndpoint) context.getBean("gatewayWithDefaultSourceExtractor");
		assertEquals(EventDrivenConsumer.class, endpoint.getClass());
		Object gateway = new DirectFieldAccessor(endpoint).getPropertyValue("handler");
		assertEquals(SimpleWebServiceOutboundGateway.class, gateway.getClass());
		DirectFieldAccessor accessor = new DirectFieldAccessor(gateway);
		assertEquals("DefaultSourceExtractor", accessor.getPropertyValue("sourceExtractor").getClass().getSimpleName());
	}

	@Test
	public void simpleGatewayWithCustomSourceExtractor() {
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"simpleWebServiceOutboundGatewayParserTests.xml", this.getClass());
		AbstractEndpoint endpoint = (AbstractEndpoint) context.getBean("gatewayWithCustomSourceExtractor");
		assertEquals(EventDrivenConsumer.class, endpoint.getClass());
		Object gateway = new DirectFieldAccessor(endpoint).getPropertyValue("handler");
		assertEquals(SimpleWebServiceOutboundGateway.class, gateway.getClass());
		DirectFieldAccessor accessor = new DirectFieldAccessor(gateway);
		SourceExtractor sourceExtractor = (SourceExtractor) context.getBean("sourceExtractor");
		assertEquals(sourceExtractor, accessor.getPropertyValue("sourceExtractor"));
	}

	@Test
	public void simpleGatewayWithCustomRequestCallback() {
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"simpleWebServiceOutboundGatewayParserTests.xml", this.getClass());
		AbstractEndpoint endpoint = (AbstractEndpoint) context.getBean("gatewayWithCustomRequestCallback");
		assertEquals(EventDrivenConsumer.class, endpoint.getClass());
		Object gateway = new DirectFieldAccessor(endpoint).getPropertyValue("handler");
		assertEquals(SimpleWebServiceOutboundGateway.class, gateway.getClass());
		DirectFieldAccessor accessor = new DirectFieldAccessor(gateway);
		WebServiceMessageCallback callback = (WebServiceMessageCallback) context.getBean("requestCallback");
		assertEquals(callback, accessor.getPropertyValue("requestCallback"));
	}

	@Test
	public void simpleGatewayWithCustomMessageFactory() {
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"simpleWebServiceOutboundGatewayParserTests.xml", this.getClass());
		AbstractEndpoint endpoint = (AbstractEndpoint) context.getBean("gatewayWithCustomMessageFactory");
		assertEquals(EventDrivenConsumer.class, endpoint.getClass());
		Object gateway = new DirectFieldAccessor(endpoint).getPropertyValue("handler");
		assertEquals(SimpleWebServiceOutboundGateway.class, gateway.getClass());
		DirectFieldAccessor accessor = new DirectFieldAccessor(gateway);
		accessor = new DirectFieldAccessor(accessor.getPropertyValue("webServiceTemplate"));
		WebServiceMessageFactory factory = (WebServiceMessageFactory) context.getBean("messageFactory");
		assertEquals(factory, accessor.getPropertyValue("messageFactory"));
	}

	@Test
	public void simpleGatewayWithCustomSourceExtractorAndMessageFactory() {
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"simpleWebServiceOutboundGatewayParserTests.xml", this.getClass());
		AbstractEndpoint endpoint = (AbstractEndpoint) context.getBean("gatewayWithCustomSourceExtractorAndMessageFactory");
		SourceExtractor sourceExtractor = (SourceExtractor) context.getBean("sourceExtractor");
		assertEquals(EventDrivenConsumer.class, endpoint.getClass());
		Object gateway = new DirectFieldAccessor(endpoint).getPropertyValue("handler");
		assertEquals(SimpleWebServiceOutboundGateway.class, gateway.getClass());
		DirectFieldAccessor accessor = new DirectFieldAccessor(gateway);
		assertEquals(sourceExtractor, accessor.getPropertyValue("sourceExtractor"));
		accessor = new DirectFieldAccessor(accessor.getPropertyValue("webServiceTemplate"));
		WebServiceMessageFactory factory = (WebServiceMessageFactory) context.getBean("messageFactory");
		assertEquals(factory, accessor.getPropertyValue("messageFactory"));
	}

	@Test
	public void simpleGatewayWithCustomFaultMessageResolver() {
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"simpleWebServiceOutboundGatewayParserTests.xml", this.getClass());
		AbstractEndpoint endpoint = (AbstractEndpoint) context.getBean("gatewayWithCustomFaultMessageResolver");
		assertEquals(EventDrivenConsumer.class, endpoint.getClass());
		Object gateway = new DirectFieldAccessor(endpoint).getPropertyValue("handler");
		assertEquals(SimpleWebServiceOutboundGateway.class, gateway.getClass());
		DirectFieldAccessor accessor = new DirectFieldAccessor(gateway);
		accessor = new DirectFieldAccessor(accessor.getPropertyValue("webServiceTemplate"));
		FaultMessageResolver resolver = (FaultMessageResolver) context.getBean("faultMessageResolver");
		assertEquals(resolver, accessor.getPropertyValue("faultMessageResolver"));
	}

	
	@Test
	public void simpleGatewayWithCustomMessageSender() {
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"simpleWebServiceOutboundGatewayParserTests.xml", this.getClass());
		AbstractEndpoint endpoint = (AbstractEndpoint) context.getBean("gatewayWithCustomMessageSender");
		assertEquals(EventDrivenConsumer.class, endpoint.getClass());
		Object gateway = new DirectFieldAccessor(endpoint).getPropertyValue("handler");
		assertEquals(SimpleWebServiceOutboundGateway.class, gateway.getClass());
		DirectFieldAccessor accessor = new DirectFieldAccessor(gateway);		
		accessor = new DirectFieldAccessor(accessor.getPropertyValue("webServiceTemplate"));
		WebServiceMessageSender messageSender = (WebServiceMessageSender) context.getBean("messageSender");
		assertEquals(messageSender, ((WebServiceMessageSender[])accessor.getPropertyValue("messageSenders"))[0]);
	}
	@Test
	public void simpleGatewayWithCustomMessageSenderList() {
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"simpleWebServiceOutboundGatewayParserTests.xml", this.getClass());
		AbstractEndpoint endpoint = (AbstractEndpoint) context.getBean("gatewayWithCustomMessageSenderList");
		assertEquals(EventDrivenConsumer.class, endpoint.getClass());
		Object gateway = new DirectFieldAccessor(endpoint).getPropertyValue("handler");
		assertEquals(SimpleWebServiceOutboundGateway.class, gateway.getClass());
		DirectFieldAccessor accessor = new DirectFieldAccessor(gateway);
		accessor = new DirectFieldAccessor(accessor.getPropertyValue("webServiceTemplate"));
		WebServiceMessageSender messageSender = (WebServiceMessageSender) context.getBean("messageSender");
		assertEquals(messageSender, ((WebServiceMessageSender[])accessor.getPropertyValue("messageSenders"))[0]);
		assertEquals("Wrong number of message senders ",
				2 , ((WebServiceMessageSender[])accessor.getPropertyValue("messageSenders")).length);
	}

	@Test
	public void simpleGatewayWithPoller() {
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"simpleWebServiceOutboundGatewayParserTests.xml", this.getClass());
		AbstractEndpoint endpoint = (AbstractEndpoint) context.getBean("gatewayWithPoller");
		assertEquals(PollingConsumer.class, endpoint.getClass());
		Object obj = new DirectFieldAccessor(endpoint).getPropertyValue("trigger");
		assertEquals(IntervalTrigger.class, obj.getClass());
		IntervalTrigger trigger = (IntervalTrigger) obj;
		DirectFieldAccessor accessor = new DirectFieldAccessor(trigger);
		accessor = new DirectFieldAccessor(trigger);
		assertEquals("IntervalTrigger had wrong interval",
				5000, ((Long)accessor.getPropertyValue("interval")).longValue());
	}

	@Test
	public void marshallingGatewayWithAllInOneMarshaller() {
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"marshallingWebServiceOutboundGatewayParserTests.xml", this.getClass());
		AbstractEndpoint endpoint = (AbstractEndpoint) context.getBean("gatewayWithAllInOneMarshaller");
		assertEquals(EventDrivenConsumer.class, endpoint.getClass());
		Object gateway = new DirectFieldAccessor(endpoint).getPropertyValue("handler");
		assertEquals(MarshallingWebServiceOutboundGateway.class, gateway.getClass());
		DirectFieldAccessor gatewayAccessor = new DirectFieldAccessor(gateway);
		DirectFieldAccessor templateAccessor = new DirectFieldAccessor(
				gatewayAccessor.getPropertyValue("webServiceTemplate"));
		Marshaller marshaller = (Marshaller) context.getBean("marshallerAndUnmarshaller");
		assertEquals(marshaller, templateAccessor.getPropertyValue("marshaller"));
		assertEquals(marshaller, templateAccessor.getPropertyValue("unmarshaller"));
	}

	@Test
	public void marshallingGatewayWithSeparateMarshallerAndUnmarshaller() {
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"marshallingWebServiceOutboundGatewayParserTests.xml", this.getClass());
		AbstractEndpoint endpoint = (AbstractEndpoint) context.getBean("gatewayWithSeparateMarshallerAndUnmarshaller");
		assertEquals(EventDrivenConsumer.class, endpoint.getClass());
		Object gateway = new DirectFieldAccessor(endpoint).getPropertyValue("handler");
		assertEquals(MarshallingWebServiceOutboundGateway.class, gateway.getClass());
		DirectFieldAccessor gatewayAccessor = new DirectFieldAccessor(gateway);
		DirectFieldAccessor templateAccessor = new DirectFieldAccessor(
				gatewayAccessor.getPropertyValue("webServiceTemplate"));
		Marshaller marshaller = (Marshaller) context.getBean("marshaller");
		Unmarshaller unmarshaller = (Unmarshaller) context.getBean("unmarshaller");
		assertEquals(marshaller, templateAccessor.getPropertyValue("marshaller"));
		assertEquals(unmarshaller, templateAccessor.getPropertyValue("unmarshaller"));
	}

	@Test
	public void marshallingGatewayWithCustomRequestCallback() {
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"marshallingWebServiceOutboundGatewayParserTests.xml", this.getClass());
		AbstractEndpoint endpoint = (AbstractEndpoint) context.getBean("gatewayWithCustomRequestCallback");
		assertEquals(EventDrivenConsumer.class, endpoint.getClass());
		Object gateway = new DirectFieldAccessor(endpoint).getPropertyValue("handler");
		assertEquals(MarshallingWebServiceOutboundGateway.class, gateway.getClass());
		DirectFieldAccessor accessor = new DirectFieldAccessor(gateway);
		WebServiceMessageCallback callback = (WebServiceMessageCallback) context.getBean("requestCallback");
		assertEquals(callback, accessor.getPropertyValue("requestCallback"));
	}

	@Test
	public void marshallingGatewayWithAllInOneMarshallerAndMessageFactory() {
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"marshallingWebServiceOutboundGatewayParserTests.xml", this.getClass());
		AbstractEndpoint endpoint = (AbstractEndpoint) context.getBean("gatewayWithAllInOneMarshallerAndMessageFactory");
		assertEquals(EventDrivenConsumer.class, endpoint.getClass());
		Object gateway = new DirectFieldAccessor(endpoint).getPropertyValue("handler");
		assertEquals(MarshallingWebServiceOutboundGateway.class, gateway.getClass());
		DirectFieldAccessor gatewayAccessor = new DirectFieldAccessor(gateway);
		DirectFieldAccessor templateAccessor = new DirectFieldAccessor(
				gatewayAccessor.getPropertyValue("webServiceTemplate"));
		Marshaller marshaller = (Marshaller) context.getBean("marshallerAndUnmarshaller");
		assertEquals(marshaller, templateAccessor.getPropertyValue("marshaller"));
		assertEquals(marshaller, templateAccessor.getPropertyValue("unmarshaller"));
		WebServiceMessageFactory messageFactory = (WebServiceMessageFactory) context.getBean("messageFactory");
		assertEquals(messageFactory, templateAccessor.getPropertyValue("messageFactory"));
	}

	@Test
	public void marshallingGatewayWithSeparateMarshallerAndUnmarshallerAndMessageFactory() {
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"marshallingWebServiceOutboundGatewayParserTests.xml", this.getClass());
		AbstractEndpoint endpoint = (AbstractEndpoint) context.getBean("gatewayWithSeparateMarshallerAndUnmarshallerAndMessageFactory");
		assertEquals(EventDrivenConsumer.class, endpoint.getClass());
		Object gateway = new DirectFieldAccessor(endpoint).getPropertyValue("handler");
		assertEquals(MarshallingWebServiceOutboundGateway.class, gateway.getClass());
		DirectFieldAccessor gatewayAccessor = new DirectFieldAccessor(gateway);
		DirectFieldAccessor templateAccessor = new DirectFieldAccessor(
				gatewayAccessor.getPropertyValue("webServiceTemplate"));
		Marshaller marshaller = (Marshaller) context.getBean("marshaller");
		Unmarshaller unmarshaller = (Unmarshaller) context.getBean("unmarshaller");
		assertEquals(marshaller, templateAccessor.getPropertyValue("marshaller"));
		assertEquals(unmarshaller, templateAccessor.getPropertyValue("unmarshaller"));
		WebServiceMessageFactory messageFactory = (WebServiceMessageFactory) context.getBean("messageFactory");
		assertEquals(messageFactory, templateAccessor.getPropertyValue("messageFactory"));
	}


    @Test
	public void simpleGatewayWithUriHeader() {
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"simpleWebServiceOutboundGatewayParserTests.xml", this.getClass());
		AbstractEndpoint endpoint = (AbstractEndpoint) context.getBean("gatewayWithUriHeader");
		assertEquals(EventDrivenConsumer.class, endpoint.getClass());
		Object gateway = new DirectFieldAccessor(endpoint).getPropertyValue("handler");
		assertEquals(SimpleWebServiceOutboundGateway.class, gateway.getClass());
		DirectFieldAccessor accessor = new DirectFieldAccessor(gateway);
        Object destinationProviderObject = accessor.getPropertyValue("destinationProvider");
        assertNotNull("DestinationProvider not set", destinationProviderObject);
        assertEquals("Wrong type for destiantion provider", HeaderBasedDestinationProvider.class, destinationProviderObject.getClass());
	    accessor = new DirectFieldAccessor(destinationProviderObject);
        Object headerName = accessor.getPropertyValue("headerName");
        assertEquals("Wrong value for headerName in DestiantionProvider", "testHeaderName", headerName);
	}

      @Test
	public void simpleGatewayWithDestinationProvider() {
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"simpleWebServiceOutboundGatewayParserTests.xml", this.getClass());
		AbstractEndpoint endpoint = (AbstractEndpoint) context.getBean("gatewayWithDestinationProvider");
		assertEquals(EventDrivenConsumer.class, endpoint.getClass());
		Object gateway = new DirectFieldAccessor(endpoint).getPropertyValue("handler");
		assertEquals(SimpleWebServiceOutboundGateway.class, gateway.getClass());
		DirectFieldAccessor accessor = new DirectFieldAccessor(gateway);
        Object destinationProviderObject = accessor.getPropertyValue("destinationProvider");
        StubDestinationProvider stubProvider = (StubDestinationProvider)context.getBean("destinationProvider");
        assertEquals("Wrong DestinationProvider", stubProvider, destinationProviderObject );
    }
}
