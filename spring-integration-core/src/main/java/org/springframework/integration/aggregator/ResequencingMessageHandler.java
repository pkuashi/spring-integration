/*
 * Copyright 2002-2016 the original author or authors.
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

package org.springframework.integration.aggregator;

import java.util.Collection;

import org.springframework.integration.IntegrationMessageHeaderAccessor;
import org.springframework.integration.store.MessageGroup;
import org.springframework.integration.store.MessageGroupStore;
import org.springframework.integration.store.SimpleMessageStore;
import org.springframework.messaging.Message;

/**
 * Resequencer specific implementation of {@link AbstractCorrelatingMessageHandler}.
 * Will remove {@link MessageGroup}s only if 'sequenceSize' is provided and reached.
 *
 * @author Oleg Zhurakousky
 * @author Gary Russell
 * @author Artem Bilan
 * @since 2.1
 */
public class ResequencingMessageHandler extends AbstractCorrelatingMessageHandler {

	public ResequencingMessageHandler(MessageGroupProcessor processor,
			MessageGroupStore store, CorrelationStrategy correlationStrategy,
			ReleaseStrategy releaseStrategy) {
		super(processor, store, correlationStrategy, releaseStrategy);
		this.setExpireGroupsUponTimeout(false);
	}


	public ResequencingMessageHandler(MessageGroupProcessor processor,
			MessageGroupStore store) {
		super(processor, store);
		this.setExpireGroupsUponTimeout(false);
	}


	public ResequencingMessageHandler(MessageGroupProcessor processor) {
		super(processor);
		this.setExpireGroupsUponTimeout(false);
	}

	/**
	 * {@inheritDoc}
	 *
	 * (overridden to false for a resequencer so late messages are immediately discarded rather
	 * than waiting for the next timeout)
	 */
	@Override
	public final void setExpireGroupsUponTimeout(boolean expireGroupsUponTimeout) {
		super.setExpireGroupsUponTimeout(expireGroupsUponTimeout);
	}


	@Override
	protected void afterRelease(MessageGroup messageGroup, Collection<Message<?>> completedMessages) {
		afterRelease(messageGroup, completedMessages, false);
	}

	@Override
	protected void afterRelease(MessageGroup messageGroup, Collection<Message<?>> completedMessages, boolean timeout) {
		int size = messageGroup.getMessages().size();
		int sequenceSize = 0;
		Message<?> message = messageGroup.getOne();
		if (message != null) {
			sequenceSize = new IntegrationMessageHeaderAccessor(message).getSequenceSize();
		}
		// If there is no sequence then it must be incomplete or unbounded
		if (sequenceSize > 0 && sequenceSize == size) {
			remove(messageGroup);
		}
		else {
			Object groupId = messageGroup.getGroupId();
			if (completedMessages != null) {
				int lastReleasedSequenceNumber = findLastReleasedSequenceNumber(groupId, completedMessages);
				this.messageStore.setLastReleasedSequenceNumberForGroup(groupId, lastReleasedSequenceNumber);
				if (this.messageStore instanceof SimpleMessageStore
						&& completedMessages.size() == messageGroup.size()) {
					((SimpleMessageStore) this.messageStore).clearMessageGroup(groupId);
				}
				else {
					this.messageStore.removeMessagesFromGroup(groupId, completedMessages);
				}
			}
			if (timeout) {
				this.messageStore.completeGroup(groupId);
			}
		}
	}

}
