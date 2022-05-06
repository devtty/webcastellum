Dieses Quickstart-Readme enthaelt die minimal notwendigen Schritte zur Einbindung der WebCastellum Web Application Firewall 
in das WAR-Archiv einer Web-Anwendung. Weitergehende Informationen zur Installation sowie detaillierte Anleitungen 
zur Aktivierung und zum Customizing der zahlreichen angebotenen Schutzfunktionen finden Sie im WebCastellum 
Reference Guide. Naehere Informationen hierzu unter http://www.WebCastellum.org sowie im WebCastellum Forum
unter http://forum.WebCastellum.org



Grundlegende Installation und Konfiguration von WebCastellum:
===================================================

1. Entpacken des WAR-Archivs der Web-Applikation


2. Kopieren der webcastellum.jar in das WEB-INF/lib Verzeichnis


3. Anpassung der web.xml:

   a) Definition eines neuen Servlet-Filters, Wichtig: Filter ist vor eventuell weiteren Filtern zu definieren
       <filter>
           <filter-name>WebCastellum</filter-name>
           <filter-class>org.webcastellum.WebCastellumFilter</filter-class>
           <init-param>
               <param-name>ApplicationName</param-name>
               <!-- Name der Anwendung (fuer das Logging relevant) -->
               <param-value>MyApplication</param-value>
           </init-param>
           <init-param>
               <param-name>RedirectWelcomePage</param-name>
               <!-- Redirect-Target (absolut definiert) zum Login-Screen der zu schuetzenden Anwendung -->
               <param-value>/MyApplication/</param-value>
           </init-param>
           <init-param>
               <param-name>CharacterEncoding</param-name>
               <!-- Die haeufigsten Encodings sind ISO-8859-1 oder UTF-8 je nach zu schuetzender Web-Anwendung -->
               <param-value>ISO-8859-1</param-value>
           </init-param>
           <init-param>
               <param-name>AttackLogDirectory</param-name>
               <!-- Logging-Ordner (entweder relativ zum JVM root oder absolut definierbar) -->
               <param-value>logs</param-value>
           </init-param>
           <init-param>
               <param-name>DefaultProductionModeCheckerValue</param-name>
               <!-- Bitte auf "true" setzen, wenn produktiver Einsatz stattfinden soll, damit keine detaillierten Hinweistexte bei Angriffen
                      gezeigt werden, wie es im Entwicklungs-Modus (development mode) der Fall ist. -->
               <param-value>false</param-value>
           </init-param>
           <init-param>
               <param-name>TransparentForwarding</param-name>
               <!-- Je nach verwendetem Web-Framework auf "true" bzw. "false" zu setzen (siehe unten). -->
               <param-value>false</param-value>
           </init-param>
           <init-param>
               <param-name>TransparentQueryString</param-name>
               <param-value>false</param-value>
           </init-param>
             <!-- 
                    ...
                    ...
                    ...
                    Die weiteren zahlreichen Schutzfunktionen (z.B. URL-Encryption) und deren Konfigurationsparameter 
                    fuer die web.xml finden Sie im WebCastellum Reference Guide.
             -->
        </filter>

    b) Definition des Filter-Mappings fuer die Dispatcher Modi requst, include und forward
       <filter-mapping>
           <filter-name>WebCastellum</filter-name>
           <url-pattern>/*</url-pattern>
           <dispatcher>REQUEST</dispatcher>
           <dispatcher>INCLUDE</dispatcher>
           <dispatcher>FORWARD</dispatcher>
       </filter-mapping>

 
       WICHTIG: Sofern in der web.xml der Anwendung weitere Servlet-Filter 
       konfiguriert sind, so ist WebCastellum im Regelfall als "vorderster" Filter, das 
       heisst als der Filter, welchen der Request zuerst durchlaeuft, zu konfigurieren. 
       Alle weiteren Filter muessen immer im Nachgang durchlaufen werden. Eine 
       Ausnahme bilden jedoch spezielle Kompressionsfilter. Diese sind (sofern vorhanden)
       ausnahmsweise vor WebCastellum in der web.xml zu konfigurieren, damit eine 
       Kompression der Responses erfolgt, nachdem diese an WebCastellum 
       uebergeben werden.  

       Ausserdem ist zu beachten, dass fuer alle weiteren Filter der Anwendung (sofern sie welche hat)
       die Dispatcher-Einstellung im Filter-Mapping auch auf "FORWARD" zusaetzlich zu "REQUEST"
       ausgedehnt wird, damit die WebCastellum Funktionen entsprechend greifen. Insbesondere bei
       Verwendung von URL-Encryption ist es wichtig, dass die Filter der Anwendung auch auf
       "FORWARD" eingestellt sind.



Beispielhafte Konfigurationsmoeglichkeiten:
===================================================

Im Zuge der weiteren Absicherung koennen nach und nach die einzelnen
Sicherheitsfeatures in der web.xml aktiviert werden. Insbesondere eignen sich die
folgenden aufeinander aufbauenden Sicherheitsfeatures zur Demonstration des 
ansteigenden Schutzgrades. Hierzu existieren unter anderem die folgenden
Konfigurationseinstellungen, welche z.B. in der web.xml auf "true" gesetzt
werden koennen:

"SecretTokenLinkInjection"
Bei Aktivierung dieses Sicherheitsfeatures werden in Links und Formulare
entsprechend zufaellig erzeugte Tokens injiziert und gegengeprueft (u.a. zur weiteren
Haertung gegenueber CSRF-Angriffen).

"QueryStringEncryption"
Bei Aktivierung dieses Sicherheitsfeatures werden zusaetzlich die Query-Strings der
Links verschluesselt und damit gegen Manipulation geschuetzt (u.a. zur weiteren
Haertung gegenueber CSRF-Angriffen bzw. Manipulationen im Allgemeinen).

"ExtraEncryptedMediumPathRemoval"
Bei Aktivierung dieses Sicherheitsfeatures werden zusaetzlich die Filenamen der
Links durch zufaellige Werte ersetzt (u.a. zur Irrefuehrung von boesartigen Scannern
und Crawlern)

"ParameterAndFormProtection"
Bei Aktivierung dieses Sicherheitsfeatures werden zusaetzlich die URL- und vor
allem die Formularparameter gegen Manipulation (u.a. Entfernen oder Hinzufuegen
von Parametern) geschuetzt.

"ExtraDisabledFormFieldProtection"
Bei Aktivierung dieses Sicherheitsfeatures werden zusaetzlich als disabled
vorgesehene Formularfelder gegen Ueberschreiben geschuetzt (zur weiteren Haertung
gegenueber Manipulationen von deaktivierten Formularfeldern).

"ExtraHiddenFormFieldProtection"
Bei Aktivierung dieses Sicherheitsfeatures werden zusaetzlich versteckte
Formularfelder entfernt und im Folgerequest wieder eingesetzt (zur weiteren
Haertung gegenueber Manipulationen von versteckten Formularfeldern).

"ExtraSelectboxProtection"
Bei Aktivierung dieses Sicherheitsfeatures werden zusaetzlich die Selectbox-Werte
beim Folgerequest gegen die zulaessigen Werte gegengeprueft (u.a. zur weiteren
Haertung gegenueber Privilege Escalation).

"ExtraCheckboxProtection"
Bei Aktivierung dieses Sicherheitsfeatures werden zusaetzlich die Checkbox-Werte
beim Folgerequest gegen die zulaessigen Werte gegengeprueft (u.a. zur weiteren
Haertung gegenueber Privilege Escalation).

"ExtraRadiobuttonProtection"
Bei Aktivierung dieses Sicherheitsfeatures werden zusaetzlich die Radiobutton-Werte
beim Folgerequest gegen die zulaessigen Werte gegengeprueft (u.a. zur weiteren
Haertung gegenueber Privilege Escalation).

"ExtraSelectboxValueMasking"
Bei Aktivierung dieses Sicherheitsfeatures werden zusaetzlich die Selectbox-Werte
durch eine Kombination aus Zufallstoken und numerischem Wert ersetzt und im
Folgerequest wieder gegen den jeweiligen Originalwert getauscht (u.a. zur
Vermeidung von Information Disclosure, was z.B. durch Preisgeben von
Primarykeys der Fall waere; sowie zur weiteren Haertung gegenueber CSRF-
Angriffen).

"ExtraCheckboxValueMasking"
Bei Aktivierung dieses Sicherheitsfeatures werden zusaetzlich die Checkbox-Werte
durch eine Kombination aus Zufallstoken und numerischem Wert ersetzt und im
Folgerequest wieder gegen den jeweiligen Originalwert getauscht (u.a. zur
Vermeidung von Information Disclosure, was z.B. durch Preisgeben von
Primarykeys der Fall waere; sowie zur weiteren Haertung gegenueber CSRF-
Angriffen).

"ExtraRadiobuttonValueMasking"
Bei Aktivierung dieses Sicherheitsfeatures werden zusaetzlich die Radiobutton-Werte
durch eine Kombination aus Zufallstoken und numerischem Wert ersetzt und im
Folgerequest wieder gegen den jeweiligen Originalwert getauscht (u.a. zur
Readme WebCastellum Demo-Anwendung Seite 3 von 4
Vermeidung von Information Disclosure, was z.B. durch Preisgeben von
Primarykeys der Fall waere; sowie zur weiteren Haertung gegenueber CSRF-
Angriffen).

Weitere Moeglichkeiten der Konfiguration finden Sie im WebCastellum Reference Guide.



Hilfreiche Hinweise:
===================================================

Die mitgelieferten Regeldateien befinden sich standardmaessig im Java-Archiv webcastellum.jar in der
Datei rules.zip. Die einzelnen Schutzfunktionen muessen selektiv in der web.xml aktiviert werden.

Die Basis-Konfiguration "development mode" sieht eine Meldung vor, welche dem
Anwender bei einem Angriffsversuch praesentiert wird, was zu Entwicklungszwecken
hilfreich ist. In einem realen Einsatz wuerde man durch die Konfiguration anstelle der
Hinweismeldung einen Status-Code (z.B. HTTP 403 forbidden oder HTTP 200 OK zur
Verwirrung der Vulnerability-Scanner mit false positives) in der web.xml als Antwort auf
einen Angriffsversuch einstellen.

Die Angriffs-Logs werden in dem Ordner abgelegt, welcher zu dem Konfigurationswert
"AttackLogDirectory" eingestellt ist. Ist hier eine relative Pfadangabe statt einer absoluten
vorgenommen, wird vom Startpfad des Java-Prozesses des Applikations-Servers
ausgegangen. Ein nicht bestehender Logging-Ordner wird nicht automatisch angelegt,
sondern es wird in einem solchen Fall eine entsprechende Hinweismeldung auf der
Konsole (stdout bzw. stderr) ausgegeben. Bei Konfigurationsfehlern wird die Web-Anwendung 
nicht gestartet. Stattdessen wird eine entsprechende, den Konfigurationsfehler aufzeigende, 
Exception ausgegeben.

Fuer den Einsatz von WebCastellum ausserhalb eines JavaEE-Containers (z.B. in einem reinen 
Web-Container) ist sicherzustellen, dass zusaetzlich zur Aufnahme der webcastellum.jar in den 
Klassenpfad der Anwendung das javax.mail Package verfuegbar ist. Dies kann durch Aufnahme der 
JAR-Files (z.B. mail-api.jar und activation-api.jar) aus dem JavaEE-Stack in den Klassenpfad 
des Web-Containers oder der Web-Anwendung erfolgen. Bei Verwendung in einem JavaEE-Container 
ist keine weitere Aufnahme von JAR-Files notwendig, da der JavaEE-Stack bereits alle notwendigen 
Bibliotheken beinhaltet. Ein Download der JAR-Files zum javax.mail und javax.activation Package
ist unter anderem hier moeglich:
  - http://java.sun.com/products/javamail/downloads/index.html
  - http://java.sun.com/javase/technologies/desktop/javabeans/jaf/index.jsp

Fuer Anwendungen mit Multipart-Formularen (Fileuploads) ist die Regeldatei 
"multipart-size-limits/01_Oversized-Uploads.wcr" anzupassen, damit Multipart-Formulare 
zugelassen werden. Zum Thema Anpassung von Regeldateien siehe beigliegenden ReferenceGuide.

Der Konfigurationsparameter "TransparentForwarding" ist eventuell auf "true" bzw. "false" (je nach
verwendetem Web-Framework) zu setzen, wenn es bei Verwendung der URL-Encryption zu Problemen
mit Links bzw. Formular-Actions kommt. Das Web-Framework "Apache Wicket" benoetigt hier
z.B. "true" und Spring Web-Flow basierte Anwendungen ein "false".

Der optionale Konfigurationsparameter "ConfigurationLoader" definiert den Klassennamen einer
Implementation des Interfaces "org.webcastellum.ConfigurationLoader", welches zum Laden von
Konfigurationswerten verwendet werden kann. Die Default-Implementation liest die Konfigurations-
daten aus der web.xml Datei. Alternativ existieren auch Implementationen zum Auslesen aus
Properties-Files.



Erweitere Tipps zum Auslagern der Regeln:
===================================================

Zum Laden der Regeln existiert ein Interface, deren Implementationsklassenname in 
der web.xml (init-param "RuleFileLoader") konfiguriert werden kann (der default ist hier
"org.webcastellum.ClasspathZipRuleFileLoader"). Von Haus aus bringt WebCastellum
die folgenden Implementierungen als "RuleFileLoader" mit:

   org.webcastellum.DatabaseRuleFileLoader
   org.webcastellum.DatasourceRuleFileLoader
   org.webcastellum.FilesystemRuleFileLoader
   org.webcastellum.ClasspathZipRuleFileLoader

Die ersten beiden dienen dem Auslesen der Regeln aus einer DB-Tabelle. Der Filesystem-basierte
Loader laedt von der Platte und der letzte Loader sucht ueber einen Resource-Lookup im Classpath.

Diese Implementierungen haben ihres Zeichens eigene (spezifische) Konfigurationen, welche
ebenfalls als init-params in der web.xml gepflegt werden koennen:

   org.webcastellum.DatabaseRuleFileLoader
     init-param: RuleFilesJdbcDriver
     init-param: RuleFilesJdbcUrl
     init-param: RuleFilesJdbcUser
     init-param: RuleFilesJdbcPassword
     init-param: RuleFilesJdbcTable
     init-param: RuleFilesJdbcColumnPath
     init-param: RuleFilesJdbcColumnFilename
     init-param: RuleFilesJdbcColumnPropertyKey
     init-param: RuleFilesJdbcColumnPropertyValue

   org.webcastellum.DatasourceRuleFileLoader
     init-param: RuleFilesJdbcDatasource
     init-param: RuleFilesJdbcTable
     init-param: RuleFilesJdbcColumnPath
     init-param: RuleFilesJdbcColumnFilename
     init-param: RuleFilesJdbcColumnPropertyKey
     init-param: RuleFilesJdbcColumnPropertyValue

   org.webcastellum.FilesystemRuleFileLoader
     init-param: RuleFilesBasePath
     init-param: RuleFilesSuffix

Der voreingestellte RuleFileLoader ClasspathZipRuleFileLoader laedt das rules.zip aus dem
Klassenpfad (per default unter org.webcastellum. im JAR) und der FilesystemRuleFileLoader
geht auf ein exploded Verzeichnis. Hierzu ist einfach das rules.zip auf die Platte entpacken
und der Namen des entpackten Wurzelverzeichnisses (relativ vom Java-Prozess-Root oder
absolut mit voller Pfadangabe) in dem spezifischen Parameter dieses Rule-File Loaders in
der web.xml definieren: init-param "RuleFilesBasePath".

Das dynamische Neuladen von Regeln zur Laufzeit ist ueber den init-param "RuleFileReloadingInterval"
steuerbar (Wert in Minuten; default ist 0 = kein Neuladen zur Laufzeit).

Es gibt in den Quellen eine Klasse (DatabaseRuleFileInserter), welche in der Lage ist, Regeldateien
in eine DB-Struktur zu ueberfuehren. Den Tabellen- und die Spaltennamen sind ueber die Kommandozeile
zu uebergeben. Die Tabelle muss vorher entsprechend angelegt sein (Spaltentyp zum Aufnehmen von
Strings). Hiermit kann man durch Aufruf je Regeltyp letztendlich alle Regeln aller Regeltypen in eine
einzige Tabelle ueberfuehren.



Maven Repository:
===================================================

Falls Sie anstelle des Downloads von SourceForge.net lieber ein Maven-kompatibles Repository nutzen
moechten, verwenden Sie folgende Konfigurationsdaten in Ihrer pom.xml Datei:

    <project>
      . . .
      <dependencies>
        <dependency>
          <groupId>org.webcastellum</groupId>
          <artifactId>webcastellum</artifactId>
          <version>[1.8.4,)</version>
          <scope>runtime</scope>
        </dependency>
      </dependencies>
      . . .
      <repositories>
        <repository>
          <id>webcastellum-repository</id>
          <url>http://www.WebCastellum.org/maven/repository</url>
        </repository>
      </repositories>
      . . .
    </project>

Die Einrichtung einer automatischen Synchronisation des Repositories auf WebCastellum.org mit
dem oeffentlichen Maven Central Repository befindet sich in Planung.



Weitere Tipps und Feedback:
===================================================

Fuer weitere Hinweise lesen Sie bitte das mitgelieferte Reference-Guide sowie die Datei CHANGELOG.txt
des WebCastellum-Releases bzw. nutzen Sie das WebCastellum Forum unter http://forum.WebCastellum.org

Helfen Sie mit, den Funktionsumfang von WebCastellum weiter auszubauen und diskutieren
Sie im Forum mit anderen Anwendern und den Entwicklern. Verbesserungsvorschlaege sind
ausdruecklich willkommen!

Ueber ein Feedback zu WebCastellum unter feedback@WebCastellum.org oder auch im Forum
wuerden wir uns freuen - sowie ueber ein Vote zum Projekt auf SourceForge unter
http://sourceforge.net/projects/webcastellum/

Aktuelle Informationen zur Weiterentwicklung von WebCastellum erhalten Sie ueber den kostenlosen
(in etwa monatlich erscheinenden) ausfuehrlichen Email-Newsletter, welcher durch Ausfuellen des Email-Formulars
abonniert werden kann. Nutzen Sie ebenfalls unseren RSS/Atom-Feed, um zeitnah ueber jede Neuigkeit zu WebCastellum
informiert zu bleiben. Beide Nachrichtenquellen erreichen Sie ueber folgende Seite: http://www.webcastellum.org/news.html

Viel Erfolg beim nachhaltigen Absichern Ihrer Web-Anwendungen.

Ihr WebCastellum Team
www.WebCastellum.org



Schulungsmoeglichkeiten:
===================================================

"Professional Support & Training" Pakete:
  * WebCastellum Einfuehrung (1 Tag)
  * WebCastellum Developer Schulung (2 Tage)
  * WebCastellum Expert Customizing Schulung (3 Tage)
  * Security Code Review inkl. Auswertung (individuell nach Umfang der Anwendung)
  * Penetrationtesting inkl. Auswertung (individuell nach Umfang der Anwendung)
  * Absicherung Ihrer Anwendung durch Installation und Customizing von
    WebCastellum (individuell nach Umfang der Anwendung)
  * WebCastellum Online und Vor-Ort Support (individuell nach Absprache)

Fuer weitere Informationen oder Preisanfragen nehmen Sie bitte Kontakt mit uns
unter mail@itanius.com auf.

itanius informatik GmbH
Im Mediapark 8
50670 Koeln

mail@itanius.com
Telefon (0221) 55 405 - 532
Telefax (0221) 55 405 - 45
www.itanius.com



Disclaimer:
===================================================

Technische Aenderungen vorbehalten. Die Firma itanius informatik GmbH uebernimmt
keine Haftung fuer Richtigkeit und Vollstaendigkeit der Angaben.

Bitte beachten Sie die entsprechenden Lizenzvereinbarungen der Open Source Lizenz
"Eclipse Public License (EPL)".

Copyright (c) 2006-2009 itanius informatik GmbH
Im Mediapark 8
50670 Koeln
Germany

www.itanius.com

