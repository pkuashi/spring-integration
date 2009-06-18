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

package org.springframework.integration.ws;

import java.io.IOException;
import java.net.URI;

import org.springframework.integration.core.Message;
import org.springframework.integration.core.MessageChannel;
import org.springframework.integration.handler.AbstractReplyProducingMessageHandler;
import org.springframework.integration.handler.ReplyMessageHolder;
import org.springframework.integration.ws.destination.MessageAwareDestinationProvider;
import org.springframework.util.Assert;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.WebServiceMessageFactory;
import org.springframework.ws.client.core.FaultMessageResolver;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.ws.soap.client.core.SoapActionCallback;
import org.springframework.ws.transport.WebServiceMessageSender;

/**
 * Base class for outbound Web Service-invoking Messaging Gateways.
 * 
 * @author Mark Fisher
 */
public abstract class AbstractWebServiceOutboundGateway extends AbstractReplyProducingMessageHandler {

	private final WebServiceTemplate webServiceTemplate;

	private volatile WebServiceMessageCallback requestCallback;

    private final MessageAwareDestinationProvider destinationProvider;


	public AbstractWebServiceOutboundGateway(MessageAwareDestinationProvider destinationProvider, WebServiceMessageFactory messageFactory) {
		Assert.notNull(destinationProvider, "DestinationProvider must not be null");
		this.destinationProvider = destinationProvider;
        this.webServiceTemplate = (messageFactory != null) ?
				new WebServiceTemplate(messageFactory) : new WebServiceTemplate();
	}


	public void setReplyChannel(MessageChannel replyChannel) {
		this.setOutputChannel(replyChannel);
	}

	public void setMessageFactory(WebServiceMessageFactory messageFactory) {
		this.webServiceTemplate.setMessageFactory(messageFactory);
	}

	public void setRequestCallback(WebServiceMessageCallback requestCallback) {
		this.requestCallback = requestCallback;
	}

	public void setFaultMessageResolver(FaultMessageResolver faultMessageResolver) {
		this.webServiceTemplate.setFaultMessageResolver(faultMessageResolver);
	}

	public void setMessageSender(WebServiceMessageSender messageSender) {
		this.webServiceTemplate.setMessageSender(messageSender);
	}

	public void setMessageSenders(WebServiceMessageSender[] messageSenders) {
		this.webServiceTemplate.setMessageSenders(messageSenders);
	}

	protected WebServiceTemplate getWebServiceTemplate() {
		return this.webServiceTemplate;
	}

    protected MessageAwareDestinationProvider getDestinationProvider(){
        return destinationProvider;
    }

	@Override
	public final void handleRequestMessage(Message<?> message, ReplyMessageHolder replyHolder) {
		Object responsePayload = this.doHandle(message.getPayload(), this.getRequestCallback(message),this.getDestinationProvider().getDestination(message));
		if (responsePayload != null) {
			replyHolder.set(responsePayload);
		}
	}

	protected abstract Object doHandle(Object requestPayload, WebServiceMessageCallback requestCallback, URI uri);

	private WebServiceMessageCallback getRequestCallback(Message<?> requestMessage) {
		if (this.requestCallback != null) {
			return this.requestCallback;
		}
		String soapAction = requestMessage.getHeaders().get(WebServiceHeaders.SOAP_ACTION, String.class);
		return (soapAction != null) ? new TypeCheckingSoapActionCallback(soapAction) : null;
	}


	private static class TypeCheckingSoapActionCallback extends SoapActionCallback {

		TypeCheckingSoapActionCallback(String soapAction) {
			super(soapAction);
		}

		@Override
		public void doWithMessage(WebServiceMessage message) throws IOException {
			if (message instanceof SoapMessage) {
				super.doWithMessage(message);
			}
		}
	}

}
