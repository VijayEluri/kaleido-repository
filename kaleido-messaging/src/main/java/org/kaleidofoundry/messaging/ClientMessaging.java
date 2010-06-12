package org.kaleidofoundry.messaging;

/**
 * ClientMessaging anc�tre
 * 
 * @author Jerome RADUGET
 */
public interface ClientMessaging {

   /**
    * @return
    * @throws TransportMessagingException
    */
   TransportMessaging getTransport() throws TransportMessagingException;

}
