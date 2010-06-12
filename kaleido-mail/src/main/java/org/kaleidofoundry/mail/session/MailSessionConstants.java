package org.kaleidofoundry.mail.session;

/**
 * MailSession Constantes
 * 
 * @author Jerome RADUGET
 */
public interface MailSessionConstants {

   /** Security Provider par d�faut en cas d'utilisation du SSL */
   public final static String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
   /** Port d'envoi SMTP utilis� par d�faut */
   public final static int DEFAULT_SMTP_PORT = 25;
   /** Nom de la racine des propri�t�s du contexte �num�rant les diff�rents MailSessions */
   public static final String LocalRootProperty = "mailsession";

   /** Nom de la propri�t� du contexte contenant le type d'implementation */
   public static final String TypeProperty = "type";

   /** Type d'implementation Locale */
   public static final String LocalMailSessionType = "local";
   /** Nom de la propri�t� du contexte, sp�cifiant le nom du serveur d'envoi */
   public static final String LocalMailSessionHost = "mail.smtp.host";
   /** Nom de la propri�t� du contexte, sp�cifiant le port du serveur pour envoi */
   public static final String LocalMailSessionPort = "mail.smtp.port";
   /** Nom de la propri�t� du contexte, sp�cifiant si l'authentification est activ�e ou non true / false */
   public static final String LocalMailSessionAuthen = "mail.smtp.auth";
   /** Nom de la propri�t� du contexte, sp�cifiant le nom d'utilisateur si l'authentification est activ�e */
   public static final String LocalMailSessionAuthenUser = "mail.smtp.auth.user";
   /** Nom de la propri�t� du contexte, sp�cifiant le mot de passe si l'authentification est activ�e */
   public static final String LocalMailSessionAuthenPwd = "mail.smtp.auth.pwd";

   // SMTP SSL
   /** Nom de la propri�t� du contexte, sp�cifiant la class socketFactory � utiliser pour le SSL */
   public static final String LocalMailSessionSocketFactoryClass = "mail.smtp.socketFactory.class";
   /** Nom de la propri�t� du contexte, sp�cifiant le fallback � utiliser avec le socketFactory SSL */
   public static final String LocalMailSessionSocketFactoryFallBack = "mail.smtp.socketFactory.fallback";
   /** Nom de la propri�t� du contexte, sp�cifiant le num�ro de port a utiliser pour le SSL */
   public static final String LocalMailSessionSocketFactoryPort = "mail.smtp.socketFactory.port";

   /** Type d'implementation Jndi */
   public static final String JndiMailSessionType = "jndi";
   /** Nom de la propri�t� du contexte, donnant la r�f�rence du nom de contexte jndi � utiliser */
   public static final String JndiContextNameProperty = "jndi.context.local-ref";
   /** Nom de la propri�t� du contexte, donnant le nom jndi de la sessionFactory Jndi */
   public static final String JndiSessionNameProperty = "jndi.name";

}
