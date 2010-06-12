package org.kaleidofoundry.messaging;

/**
 * Anc�tre pour consomateur de message asynchrone
 * 
 * @author Jerome RADUGET
 */
public interface ConsumerMessaging extends ClientMessaging {

   /**
    * @param message
    * @throws TransportMessagingException
    */
   void onMessageReceived(Message message) throws TransportMessagingException;

   /**
    * Gestion d'erreur eventuel � la reception
    * 
    * @param th
    */
   void onMessageReceivedError(Throwable th);

   /**
    * Stoppe l'�coute
    * 
    * @throws TransportMessagingException
    */
   void stop() throws TransportMessagingException;
}
