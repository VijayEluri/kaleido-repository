package org.kaleidofoundry.messaging;

/**
 * Anc�tre pour publier des messages asynchrone
 * 
 * @author Jerome RADUGET
 */
public interface ProducerMessaging extends ClientMessaging {

   /**
    * @param message
    * @throws TransportMessagingException
    */
   void sendMessage(Message message) throws TransportMessagingException;

}
