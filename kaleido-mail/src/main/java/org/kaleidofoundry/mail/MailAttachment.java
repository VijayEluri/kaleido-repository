package org.kaleidofoundry.mail;

import java.io.Serializable;
import java.net.URL;

/**
 * Pi�ce jointe d'un mail.
 * On peut sp�cifier un chemin syst�me
 * ou
 * une URL java pour localiser la pi�ce jointe
 * si il est sp�cifi�, le chemin syst�me est utilis� par d�faut
 * 
 * @author Jerome RADUGET
 */
public interface MailAttachment extends Serializable {

   /** @return Nom de la pi�ce jointe */
   String getName();

   /** @return Type mime de la pi�ce jointe */
   String getContentType();

   /** @return Chemin complet vers la pi�ce jointe */
   String getContentPath();

   /** @return URL vers la pi�ce jointe */
   URL getContentURL();

   void setName(String name);

   void setContentType(String contentType);

   void setContentPath(String contentPath);

   void setContentURL(URL contentURL);
}
