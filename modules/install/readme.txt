Disse katalogene/filene legges under C:\Kantega\SITENAVN (Windows)
eller /usr/local/kantega (Unix/Linux).

SITENAVN = navn p� nettsted/applikasjon og m� v�re angitt i fila
           kantega.properties som ligger under WEB-INF/classes

Dersom man �nsker � legge filene et annet sted m� Java parametren
kantega.dir (-d kantega.dir="...") settes i oppstart av webtjener

conf/aksess.conf:
Her legges inn parametre som f.eks databasekopling

conf/log4j.xml:
Her legges inn konfigurasjon av logging, navn p� loggfil etc

content:
Her er kataloger som filer lagres i.  Disse m� settes opp som virtuelle kataloger.
Dette gj�res slik med Tomcat:
<Context path="/aksess/media" docBase="C:\Kantega\aksess\content\media" debug="0" privileged="true"/>
<Context path="/aksess/files" docBase="C:\Kantega\aksess\content\files" debug="0" privileged="true"/>

logs:
Her opprettes loggfiler fra systemet

mail:
Her ligger det maler for epostmeldinger som sendes ut av systemet.
B�r redigeres og legges inn riktige navn p� nettsted osv