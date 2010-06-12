package org.kaleidofoundry.messaging;

import org.kaleidofoundry.messaging.jms.JmsTransportMessaging;
import org.kaleidofoundry.messaging.rdv.RdvTransportMessaging;

/**
 * Enum�ration des implementations de Transport possible pour le framework
 * 
 * @author Jerome RADUGET
 */
public enum TransportMessagingEnum {

   /** JMS 1.1 */
   SunJMS("JMS-1.1", "1.1", JmsTransportMessaging.class),
   /** RVD 7.5 */
   TibcoRDV("Tibco-RDV-7.5", "7.5", RdvTransportMessaging.class);

   private String code;
   private String version;
   private Class<? extends TransportMessaging> implementation;

   TransportMessagingEnum(final String code, final String version, final Class<? extends TransportMessaging> implementation) {
	this.code = code;
	this.version = version;
	this.implementation = implementation;
   }

   /**
    * @return Code unique de l'impl�mentation
    */
   public String getCode() {
	return code;
   }

   /**
    * @return Version (API) de l'impl�mentation du transport
    */
   public String getVersion() {
	return version;
   }

   /**
    * @return Classe d'impl�mentation
    */
   public Class<? extends TransportMessaging> getImplementation() {
	return implementation;
   }

}
