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
package org.kaleidofoundry.mail.sender;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.kaleidofoundry.core.naming.JndiContext;
import org.kaleidofoundry.core.naming.JndiResourceException;
import org.kaleidofoundry.core.naming.JndiResourceLocator;
import org.kaleidofoundry.mail.MailException;
import org.kaleidofoundry.mail.MailMessage;
import org.kaleidofoundry.mail.MailMessageBean;
import org.kaleidofoundry.mail.session.MailSessionContext;

/**
 * MailSenderService, impl�mentation ejb
 * 
 * @author Jerome RADUGET
 */
public class MailSenderServiceJmsImpl implements MailSenderService {

   private final QueueConnectionFactory factory;
   private final Queue queue;
   private final boolean sessionTransacted;

   /**
    * @param jndiQueueName
    * @param jndiQueueFactoryName
    * @param jndiContext
    * @param jmsSessionTransacted 
    * @throws JndiResourceException
    */
   public MailSenderServiceJmsImpl(final String jndiQueueName, final String jndiQueueFactoryName,
	   final JndiContext jndiContext, final boolean jmsSessionTransacted) throws JndiResourceException {
	// Locator pour le service (� n'instancier qu'une fois g�n�ralement.....)
	final JndiResourceLocator<QueueConnectionFactory> locatorQueueFactory = new JndiResourceLocator<QueueConnectionFactory>(
		jndiContext);

	// QueueConnectionFactory
	factory = locatorQueueFactory.lookup(jndiQueueFactoryName, QueueConnectionFactory.class);

	final JndiResourceLocator<Queue> locatorQueue = new JndiResourceLocator<Queue>(jndiContext);

	// Queue JMS
	queue = locatorQueue.lookup(jndiQueueName, Queue.class);

	// Transaction g�r�e ou non ?
	sessionTransacted = jmsSessionTransacted;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.mail.service.MailSenderService#createMessage()
    */
   public MailMessage createMessage() {
	return new MailMessageBean();
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.mail.service.MailSenderService#getSessionContext()
    */
   public MailSessionContext getSessionContext() {
	return null;
   }

   /*
    * (non-Javadoc)
    * @see org.kaleidofoundry.mail.service.MailSenderService#send(org.kaleidofoundry.mail.MailMessage)
    */
   public void send(final MailMessage message) throws MailException, AddressException, MessagingException {

	ObjectMessage jmsMessage = null;
	QueueConnection connection = null;
	QueueSession session = null;
	QueueSender sender = null;

	try {
	   // Create connection
	   connection = factory.createQueueConnection();
	   session = connection.createQueueSession(sessionTransacted, Session.AUTO_ACKNOWLEDGE);

	   // Create session (with no transaction)
	   sender = session.createSender(queue);

	   // Create sender
	   jmsMessage = session.createObjectMessage();
	   jmsMessage.setObject(message);

	   // Send
	   sender.send(jmsMessage);

	} catch (final JMSException jmse) {
	   throw new MailSenderException("jndi.error.jms", jmse, jmse.getMessage());
	} finally {
	   if (connection != null) {
		try {
		   connection.close();
		} catch (final JMSException jmse) {
		   logger.error("JMS connection close error", jmse);
		}
	   }
	}
   }

}
