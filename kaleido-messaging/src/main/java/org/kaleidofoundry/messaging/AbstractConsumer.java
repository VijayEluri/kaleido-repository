/*  
 * Copyright 2008-2010 the original author or authors 
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
package org.kaleidofoundry.messaging;

import static org.kaleidofoundry.messaging.MessagingConstants.CONSUMER_DEBUG_PROPERTY;
import static org.kaleidofoundry.messaging.MessagingConstants.CONSUMER_PRINT_PROCESSED_MESSAGES_MODULO;
import static org.kaleidofoundry.messaging.MessagingConstants.CONSUMER_RESPONSE_DURATION;
import static org.kaleidofoundry.messaging.MessagingConstants.CONSUMER_THREAD_POOL_COUNT_PROPERTY;
import static org.kaleidofoundry.messaging.MessagingConstants.CONSUMER_THREAD_PREFIX_PROPERTY;
import static org.kaleidofoundry.messaging.MessagingConstants.CONSUMER_TRANSPORT_REF;
import static org.kaleidofoundry.messaging.MessagingConstants.I18N_RESOURCE;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.kaleidofoundry.core.context.EmptyContextParameterException;
import org.kaleidofoundry.core.context.RuntimeContext;
import org.kaleidofoundry.core.i18n.I18nMessages;
import org.kaleidofoundry.core.i18n.I18nMessagesFactory;
import org.kaleidofoundry.core.i18n.InternalBundleHelper;
import org.kaleidofoundry.core.lang.annotation.Task;
import org.kaleidofoundry.core.util.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jerome RADUGET
 */
@Task(comment = "I18n messages")
public abstract class AbstractConsumer implements Consumer {

   /** Default consumers logger */
   protected static final Logger LOGGER = LoggerFactory.getLogger(Consumer.class);

   /** Logger line separator */
   static final String DEBUG_SEPARATOR = StringHelper.replicate("-", 120);

   // consumer message counters
   private final AtomicInteger ProcessedMessagesOK = new AtomicInteger(0);
   private final AtomicInteger ProcessedMessagesKO = new AtomicInteger(0);
   private final AtomicInteger ProcessedMessagesSKIPPED = new AtomicInteger(0);

   /** I18n messaging bundle */
   protected final I18nMessages MESSAGING_BUNDLE = I18nMessagesFactory.provides(I18N_RESOURCE, InternalBundleHelper.CoreMessageBundle);

   /**
    * Message wrapper used by consumer processing
    */
   public class MessageWrapper {

	private Message message;
	private Object providerObject;
	private Throwable error;
	private boolean ackMessage = true;

	public Message getMessage() {
	   return message;
	}

	public void setMessage(Message message) {
	   this.message = message;
	}

	public Throwable getError() {
	   return error;
	}

	public void setError(Throwable error) {
	   this.error = error;
	}

	public boolean isAckMessage() {
	   return ackMessage;
	}

	public void setAckMessage(boolean ackMessage) {
	   this.ackMessage = ackMessage;
	}

	public Object getProviderObject() {
	   return providerObject;
	}

	public void setProviderObject(Object providerObject) {
	   this.providerObject = providerObject;
	}

	public boolean hasError() {
	   return error != null;
	}
   }

   /**
    * Consumer worker
    */
   public abstract class ConsumerWorker extends Thread {

	public ConsumerWorker(int index, String name) {
	   super(name);
	}

	/**
	 * worker initialize, execute once the first time
	 * 
	 * @throws TransportException
	 */
	public abstract void init() throws TransportException;

	/**
	 * The message receiver method, to be defined by the provider. <br/>
	 * No exception must be thrown by this method, use messageWrapper.setError
	 * 
	 * @param messageWrapper wrapper that contains message and error
	 */
	public abstract void receive(MessageWrapper messageWrapper);

	/**
	 * message acknowledgment, it will be executed after {@link #receive(MessageWrapper)}, only if all registered {@link MessageHandler}
	 * do
	 * not throws error
	 * 
	 * @param messageWrapper
	 */
	public abstract void acknowledge(MessageWrapper messageWrapper) throws MessagingException;

	/*
	 * (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {

	   initRun();

	   while (!pool.isTerminated()) {

		long beginTimeStamp = 0;
		boolean noError = true;
		MessageWrapper messageWrapper = new MessageWrapper();

		try {

		   // wait and receive the new incoming message
		   receive(messageWrapper);

		   // start processing
		   beginTimeStamp = System.currentTimeMillis();

		   // after receiving
		   noError = onceReceived(messageWrapper);

		} catch (Throwable th) {
		   // all must have been done in the receive or onceReceived methods
		   noError = false;
		} finally {
		   if (isDebug() && noError) {
			printResponseTime(beginTimeStamp);
		   }

		   // handle the minimum response time of a message (if specified)
		   final long responseTime = System.currentTimeMillis() - beginTimeStamp;
		   final long waitingDuration = context.getLong(CONSUMER_RESPONSE_DURATION, 0l) - responseTime;

		   if (waitingDuration > 0) {
			try {
			   sleep(waitingDuration);
			} catch (InterruptedException ite) {
			}
		   }
		}
	   }

	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Thread#destroy()
	 */
	@Override
	public void destroy() {
	   LOGGER.info("{} destroyed", getName());

	}

	protected boolean isDebug() {
	   return context.getBoolean(CONSUMER_DEBUG_PROPERTY, false) || LOGGER.isDebugEnabled();
	}

	protected final void initRun() {
	   try {
		init();
	   } catch (TransportException e) {
		throw new IllegalStateException("transport initialization error", e);
	   } finally {
		LOGGER.info("{} listening", getName());
	   }
	}

	/**
	 * @param messageWrapper
	 * @return <code>true</code> of no errors, <code>false</code> otherwise
	 * @throws MessagingException
	 */
	protected final boolean onceReceived(MessageWrapper messageWrapper) throws MessagingException {

	   boolean noError = true;
	   MessageHandler handlerInError = null;

	   // the message wrapper has no error and a message have been received
	   if (!messageWrapper.hasError() && messageWrapper.getMessage() != null) {

		if (isDebug()) {
		   LOGGER.info("<<< receiving message with providerId={} , correlationId={} , parameters={}", new String[] {messageWrapper.getMessage().getProviderId() ,  messageWrapper.getMessage().getCorrelationId(), String.valueOf(messageWrapper.getMessage().getParameters())});		   
		   LOGGER.info("{}", messageWrapper.getMessage().toString());
		}

		// Processing handler
		boolean skipMessage = false;
		for (MessageHandler handler : messageHandlers) {
		   try {
			if (!handler.onReceive(messageWrapper.getMessage())) {
			   skipMessage = true;
			   break;
			}
		   } catch (Throwable th) {
			handlerInError = handler;
			messageWrapper.setError(th);
		   }
		}

		if (!skipMessage) {
		   ProcessedMessagesOK.incrementAndGet();
		   acknowledge(messageWrapper);
		} else {
		   ProcessedMessagesSKIPPED.incrementAndGet();
		}

		// print processed messages each
		int printProcessedMessagesModulo = context.getInteger(CONSUMER_PRINT_PROCESSED_MESSAGES_MODULO, -1);
		if (printProcessedMessagesModulo > 0) {
		   int processedMessagesOK = ProcessedMessagesOK.get();
		   int processedMessagesKO = ProcessedMessagesKO.get();
		   int processedMessagesSkipped = ProcessedMessagesSKIPPED.get();

		   if ((processedMessagesOK + processedMessagesKO + processedMessagesSkipped) % printProcessedMessagesModulo == 0) {
			LOGGER.info("messages processed : {} OK ; {} KO ; {} SKIPPED ", new Object[] { processedMessagesOK, processedMessagesKO,
				processedMessagesSkipped });
		   }
		}

	   }

	   // check again if the message wrapper has error, if can occurred during the handler chain processing
	   if (messageWrapper.hasError()) {
		
		if (messageWrapper.getError() instanceof MessageTimeoutException) {

		   // nothing to do, but skip the loop
		   noError = false;
		   LOGGER.info(messageWrapper.getError().getMessage());

		} else {

		   // other exception to handle
		   noError = false;
		   ProcessedMessagesKO.incrementAndGet();

		   if (handlerInError != null) {
			LOGGER.error("an error occurred on the chain handler processing of this consumer",  messageWrapper.getError());
			handlerInError.onError(messageWrapper.getMessage(), messageWrapper.getError());
		   } else {
			LOGGER.error("an error occurred on this consumer", messageWrapper.getError());
		   }

		}
	   }
	   
	   return noError;
	}

	protected final void printResponseTime(long beginTimeStamp) {
	   final long responseTime = System.currentTimeMillis() - beginTimeStamp;
	   LOGGER.info("processing message in {} ms", String.valueOf(responseTime));
	   LOGGER.info(DEBUG_SEPARATOR);
	}
   }

   protected final RuntimeContext<Consumer> context;
   protected final Transport transport;
   protected ExecutorService pool;

   private final List<MessageHandler> messageHandlers;

   public AbstractConsumer(RuntimeContext<Consumer> context) {
	this.context = context;
	this.messageHandlers = Collections.synchronizedList(new LinkedList<MessageHandler>());

	// transport
	String transportRef = this.context.getString(CONSUMER_TRANSPORT_REF);
	if (!StringHelper.isEmpty(transportRef)) {
	   this.transport = TransportFactory.provides(transportRef, context);
	} else {
	   throw new EmptyContextParameterException(CONSUMER_TRANSPORT_REF, context);
	}

   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.messaging.Consumer#getName()
    */
   @Override
   public String getName() {
	return context.getName();
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.messaging.Client#getTransport()
    */
   @Override
   public Transport getTransport() throws TransportException {
	return transport;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.messaging.Consumer#start()
    */
   @Override
   public synchronized void start() throws TransportException {

	ProcessedMessagesOK.set(0);
	ProcessedMessagesKO.set(0);
	ProcessedMessagesSKIPPED.set(0);

	// shutdown the pool if needed
	stop();

	// thread executor service
	int threadCound = this.context.getInteger(CONSUMER_THREAD_POOL_COUNT_PROPERTY, 1);

	LOGGER.info("Creating consumer [{}] with a thread pool size of {}", context.getName(), threadCound);
	this.pool = Executors.newFixedThreadPool(threadCound);

	// submit thread to the pool
	String threadPrefix = this.context.getString(CONSUMER_THREAD_PREFIX_PROPERTY, context.getName());
	for (int cpt = 0; cpt < threadCound; cpt++) {
	   this.pool.submit(newWorker(threadPrefix + "[" + StringHelper.leftPad(String.valueOf(cpt + 1), 3, '0') + "]", cpt + 1));
	}

   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.messaging.Consumer#stop()
    */
   @Override
   public synchronized void stop() throws TransportException {
	if (pool != null) {
	   LOGGER.info("Shutdown consumer [{}] thread pool", context.getName());
	   pool.shutdown();
	}
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.messaging.Consumer#addMessageHandler(org.kaleidofoundry.messaging.MessageHandler)
    */
   @Override
   public Consumer addMessageHandler(MessageHandler handler) {
	if (!messageHandlers.contains(handler)) {
	   messageHandlers.add(handler);
	}
	return this;
   }

   /**
    * @param workerName
    * @param workerIndex
    * @throws TransportException
    */
   protected abstract ConsumerWorker newWorker(String workerName, int workerIndex) throws TransportException;

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.messaging.Client#getStatistics()
    */
   @Override
   public UsageStatistics getStatistics() {
	return new UsageStatistics(ProcessedMessagesOK.get(), ProcessedMessagesKO.get(), ProcessedMessagesSKIPPED.get());
   }

}