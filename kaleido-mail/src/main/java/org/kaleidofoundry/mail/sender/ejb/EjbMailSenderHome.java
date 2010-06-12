package org.kaleidofoundry.mail.sender.ejb;

import java.rmi.RemoteException;

import javax.ejb.CreateException;
import javax.ejb.EJBHome;

/**
 * Home interface for the bean Mailer
 */
public interface EjbMailSenderHome extends EJBHome {

   /**
    * Cr�ation en utilisant le fournisseur par d�faut
    * 
    * @throws CreateException if the bean creation failed.
    * @throws RemoteException if the call failed.
    * @return the "mailer" bean created
    *         Le section sessionMail utilis�e sera "default"
    *         (voir mailSession.properties dans META-INF/
    */
   EjbMailSender create() throws CreateException, RemoteException;

   /**
    * Cr�ation en utilisant le fournisseur dont le nom est en argument
    * 
    * @param mailsessionName Nom de la section sessionMail � utiliser
    *           (voir mailSession.properties dans META-INF/
    * @throws CreateException if the bean creation failed.
    * @throws RemoteException if the call failed.
    * @return the "mailer" bean created
    */
   EjbMailSender create(String mailsessionName) throws CreateException, RemoteException;
}
