# OMA
Oracle Maintain Assistant

#### Automatically generate tablespace to create SQL statements with OMF
java -jar OMA-1.0-jar-with-dependencies.jar -url jdbc:oracle:thin:system/password@192.168.2.102:1521/orcl

CREATE TABLESPACE AQSDATA DATAFILE

SIZE 10M AUTOEXTEND ON NEXT 1000M MAXSIZE UNLIMITED;

CREATE TABLESPACE TEST1 DATAFILE

SIZE 10M AUTOEXTEND ON NEXT 1000M MAXSIZE UNLIMITED;

