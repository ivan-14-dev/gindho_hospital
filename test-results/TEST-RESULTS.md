# Service Test Results

Date: sam. 13 juin 2026 06:05:29 WAT

Testing identity-service in namespace security on port 9023

Testing identity-service in namespace security on port 9023
  Pod: ✅ RUNNING
  Pod: ✅ RUNNING
  Health: ✅ OK
  Health: ✅ OK

----------------------------------------
Testing authorization-service in namespace security on port 9030
Testing authorization-service in namespace security on port 9030

  Pod: ✅ RUNNING
  Pod: ✅ RUNNING
  Health: ✅ OK
  Health: ✅ OK

----------------------------------------
Testing audit-service in namespace security on port 9029
Testing audit-service in namespace security on port 9029

  Pod: ✅ RUNNING
  Pod: ✅ RUNNING
  Health: ✅ OK
  Health: ✅ OK

----------------------------------------
Testing patient-service in namespace patient on port 9004
Testing patient-service in namespace patient on port 9004

  Pod: ✅ RUNNING
  Pod: ✅ RUNNING
  Health: ❌ FAILED
    Logs:
  Health: ❌ FAILED
      	at org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor$AutowiredFieldElement.inject(AutowiredAnnotationBeanPostProcessor.java:752) ~[spring-beans-6.1.1.jar!/:6.1.1]
      	at org.springframework.beans.factory.annotation.InjectionMetadata.inject(InjectionMetadata.java:145) ~[spring-beans-6.1.1.jar!/:6.1.1]
      	at org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor.postProcessProperties(AutowiredAnnotationBeanPostProcessor.java:493) ~[spring-beans-6.1.1.jar!/:6.1.1]
      	... 71 common frames omitted
      
      	at org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor$AutowiredFieldElement.inject(AutowiredAnnotationBeanPostProcessor.java:752) ~[spring-beans-6.1.1.jar!/:6.1.1]
      	at org.springframework.beans.factory.annotation.InjectionMetadata.inject(InjectionMetadata.java:145) ~[spring-beans-6.1.1.jar!/:6.1.1]
      	at org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor.postProcessProperties(AutowiredAnnotationBeanPostProcessor.java:493) ~[spring-beans-6.1.1.jar!/:6.1.1]
      	... 71 common frames omitted
      

----------------------------------------
Testing admission-service in namespace patient on port 9007
Testing admission-service in namespace patient on port 9007

  Pod: ❌ NOT RUNNING
  Pod: ❌ NOT RUNNING
No resources found in patient namespace.
No resources found in patient namespace.

Testing emergency-service in namespace patient on port 9008
Testing emergency-service in namespace patient on port 9008

  Pod: ✅ RUNNING
  Pod: ✅ RUNNING
  Health: ✅ OK
  Health: ✅ OK

----------------------------------------
Testing ward-service in namespace patient on port 9009
Testing ward-service in namespace patient on port 9009

  Pod: ✅ RUNNING
  Pod: ✅ RUNNING
  Health: ✅ OK
  Health: ✅ OK

----------------------------------------
Testing bed-service in namespace patient on port 9010
Testing bed-service in namespace patient on port 9010

  Pod: ✅ RUNNING
  Pod: ✅ RUNNING
  Health: ✅ OK
  Health: ✅ OK

----------------------------------------
Testing round-service in namespace patient on port 9011
Testing round-service in namespace patient on port 9011

  Pod: ✅ RUNNING
  Pod: ✅ RUNNING
  Health: ✅ OK
  Health: ✅ OK

----------------------------------------
Testing surgery-service in namespace patient on port 9012
Testing surgery-service in namespace patient on port 9012

  Pod: ✅ RUNNING
  Pod: ✅ RUNNING
  Health: ✅ OK
  Health: ✅ OK

----------------------------------------
Testing ambulance-service in namespace patient on port 9023
Testing ambulance-service in namespace patient on port 9023

  Pod: ✅ RUNNING
  Pod: ✅ RUNNING
  Health: ✅ OK
  Health: ✅ OK

----------------------------------------
Testing appointment-service in namespace appointment on port 9005

Testing appointment-service in namespace appointment on port 9005
  Pod: ✅ RUNNING
  Pod: ✅ RUNNING
  Health: ✅ OK
  Health: ✅ OK

----------------------------------------
Testing medical-record-service in namespace emr on port 9006
Testing medical-record-service in namespace emr on port 9006

  Pod: ✅ RUNNING
  Pod: ✅ RUNNING
  Health: ✅ OK
  Health: ✅ OK

----------------------------------------
Testing imaging-service in namespace emr on port 9016
Testing imaging-service in namespace emr on port 9016

  Pod: ✅ RUNNING
  Pod: ✅ RUNNING
  Health: ✅ OK
  Health: ✅ OK

----------------------------------------
Testing billing-service in namespace billing on port 9017
Testing billing-service in namespace billing on port 9017

  Pod: ✅ RUNNING
  Pod: ✅ RUNNING
  Health: ✅ OK
  Health: ✅ OK

----------------------------------------
Testing insurance-service in namespace billing on port 9018
Testing insurance-service in namespace billing on port 9018

  Pod: ✅ RUNNING
  Pod: ✅ RUNNING
  Health: ✅ OK
  Health: ✅ OK

----------------------------------------
Testing payment-service in namespace billing on port 9019
Testing payment-service in namespace billing on port 9019

  Pod: ✅ RUNNING
  Pod: ✅ RUNNING
  Health: ✅ OK
  Health: ✅ OK

----------------------------------------
Testing pharmacy-service in namespace pharmacy on port 9014
Testing pharmacy-service in namespace pharmacy on port 9014

  Pod: ✅ RUNNING
  Pod: ✅ RUNNING
  Health: ✅ OK

----------------------------------------
  Health: ✅ OK
Testing prescription-service in namespace pharmacy on port 9013
Testing prescription-service in namespace pharmacy on port 9013

  Pod: ✅ RUNNING
  Pod: ✅ RUNNING
  Health: ✅ OK
  Health: ✅ OK

----------------------------------------
Testing laboratory-service in namespace laboratory on port 9015
Testing laboratory-service in namespace laboratory on port 9015

  Pod: ✅ RUNNING
  Pod: ✅ RUNNING
  Health: ✅ OK
  Health: ✅ OK

----------------------------------------
Testing hr-service in namespace hr on port 9024
Testing hr-service in namespace hr on port 9024

  Pod: ✅ RUNNING
  Pod: ✅ RUNNING
  Health: ✅ OK
  Health: ✅ OK

----------------------------------------
Testing scheduling-service in namespace hr on port 9025
Testing scheduling-service in namespace hr on port 9025

  Pod: ✅ RUNNING
  Pod: ✅ RUNNING
  Health: ✅ OK
  Health: ✅ OK

----------------------------------------
Testing inventory-service in namespace inventory on port 9020
Testing inventory-service in namespace inventory on port 9020

  Pod: ✅ RUNNING
  Pod: ✅ RUNNING
  Health: ✅ OK
  Health: ✅ OK

----------------------------------------
Testing procurement-service in namespace inventory on port 9021
Testing procurement-service in namespace inventory on port 9021

  Pod: ✅ RUNNING
  Pod: ✅ RUNNING
  Health: ✅ OK
  Health: ✅ OK

----------------------------------------
Testing asset-service in namespace inventory on port 9022
Testing asset-service in namespace inventory on port 9022

  Pod: ✅ RUNNING
  Pod: ✅ RUNNING
  Health: ✅ OK
  Health: ✅ OK

----------------------------------------
Testing event-service in namespace infrastructure on port 9026
Testing event-service in namespace infrastructure on port 9026

  Pod: ✅ RUNNING
  Pod: ✅ RUNNING
  Health: ✅ OK
  Health: ✅ OK

----------------------------------------
Testing notification-service in namespace infrastructure on port 9027
Testing notification-service in namespace infrastructure on port 9027

  Pod: ✅ RUNNING
  Pod: ✅ RUNNING
  Health: ✅ OK
  Health: ✅ OK

----------------------------------------
Testing reporting-service in namespace reporting on port 9028
Testing reporting-service in namespace reporting on port 9028

  Pod: ✅ RUNNING
  Pod: ✅ RUNNING
  Health: ✅ OK
  Health: ✅ OK

----------------------------------------
Test complete. Results saved to /media/ivan/Ultimate/Ivan/script/common/GinDHO/test-results/TEST-RESULTS.md
