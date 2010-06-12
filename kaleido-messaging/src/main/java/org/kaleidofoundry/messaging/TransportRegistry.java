package org.kaleidofoundry.messaging;

import java.util.HashMap;
import java.util.Map;

/**
 * Registry des Transports instanci�s
 * 
 * @author Jerome RADUGET
 */
public class TransportRegistry {

   private static final Map<String, TransportMessaging> registry = new HashMap<String, TransportMessaging>();

   /**
    * R�cup�re un TransportMessaging pr�c�demment enregistr�
    * 
    * @param name
    * @return
    * @throws TransportRegistryException Si non enregistr�
    */
   public static TransportMessaging getTransportMessaging(final String name) throws TransportRegistryException {
	TransportMessaging ds = null;
	ds = registry.get(name);
	if (ds == null) throw new TransportRegistryException("messaging.error.register.none", new String[] { name });
	return ds;
   }

   /**
    * Enregistre un nouveau TransportMessaging
    * 
    * @param name
    * @param messaging
    * @throws TransportRegistryException Si d�j� enregistr�
    */
   public static void register(final String name, final TransportMessaging messaging) throws TransportRegistryException {
	synchronized (registry) {
	   if (registry.get(name) == null) {
		registry.put(name, messaging);
	   } else
		throw new TransportRegistryException("messaging.error.register.already", new String[] { name });
	}
   }

   /**
    * D�senregistre un TransportMessaging existant
    * 
    * @param name
    */
   public static void unregister(final String name) {
	synchronized (registry) {
	   registry.remove(name);
	}
   }

   /**
    * @param name
    * @return
    */
   public static boolean isRegistered(final String name) {
	return registry.get(name) != null;
   }
}
