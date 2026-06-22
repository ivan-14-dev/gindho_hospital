# Service Test Results

Date: sam. 13 juin 2026 06:05:29 WAT

Testing identity-service in namespace security on port 8100

Testing identity-service in namespace security on port 8100
  Pod: ✅ RUNNING
  Pod: ✅ RUNNING
  Health: ✅ OK
  Health: ✅ OK

----------------------------------------
Testing authorization-service in namespace security on port 8107
Testing authorization-service in namespace security on port 8107

  Pod: ✅ RUNNING
  Pod: ✅ RUNNING
  Health: ✅ OK
  Health: ✅ OK

----------------------------------------
Testing audit-service in namespace security on port 8106
Testing audit-service in namespace security on port 8106

  Pod: ✅ RUNNING
  Pod: ✅ RUNNING
  Health: ✅ OK
  Health: ✅ OK

----------------------------------------
Testing patient-service in namespace patient on port 8081
Testing patient-service in namespace patient on port 8081

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
Testing admission-service in namespace patient on port 8084
Testing admission-service in namespace patient on port 8084

  Pod: ❌ NOT RUNNING
  Pod: ❌ NOT RUNNING
No resources found in patient namespace.
No resources found in patient namespace.

Testing emergency-service in namespace patient on port 8085
Testing emergency-service in namespace patient on port 8085

  Pod: ✅ RUNNING
  Pod: ✅ RUNNING
  Health: ✅ OK
  Health: ✅ OK

----------------------------------------
Testing ward-service in namespace patient on port 8086
Testing ward-service in namespace patient on port 8086

  Pod: ✅ RUNNING
  Pod: ✅ RUNNING
  Health: ✅ OK
  Health: ✅ OK

----------------------------------------
Testing bed-service in namespace patient on port 8087
Testing bed-service in namespace patient on port 8087

  Pod: ✅ RUNNING
  Pod: ✅ RUNNING
  Health: ✅ OK
  Health: ✅ OK

----------------------------------------
Testing round-service in namespace patient on port 8088
Testing round-service in namespace patient on port 8088

  Pod: ✅ RUNNING
  Pod: ✅ RUNNING
  Health: ✅ OK
  Health: ✅ OK

----------------------------------------
Testing surgery-service in namespace patient on port 8089
Testing surgery-service in namespace patient on port 8089

  Pod: ✅ RUNNING
  Pod: ✅ RUNNING
  Health: ✅ OK
  Health: ✅ OK

----------------------------------------
Testing ambulance-service in namespace patient on port 8100
Testing ambulance-service in namespace patient on port 8100

  Pod: ✅ RUNNING
  Pod: ✅ RUNNING
  Health: ✅ OK
  Health: ✅ OK

----------------------------------------
Testing appointment-service in namespace appointment on port 8082

Testing appointment-service in namespace appointment on port 8082
  Pod: ✅ RUNNING
  Pod: ✅ RUNNING
  Health: ✅ OK
  Health: ✅ OK

----------------------------------------
Testing medical-record-service in namespace emr on port 8083
Testing medical-record-service in namespace emr on port 8083

  Pod: ✅ RUNNING
  Pod: ✅ RUNNING
  Health: ✅ OK
  Health: ✅ OK

----------------------------------------
Testing imaging-service in namespace emr on port 8093
Testing imaging-service in namespace emr on port 8093

  Pod: ✅ RUNNING
  Pod: ✅ RUNNING
  Health: ✅ OK
  Health: ✅ OK

----------------------------------------
Testing billing-service in namespace billing on port 8094
Testing billing-service in namespace billing on port 8094

  Pod: ✅ RUNNING
  Pod: ✅ RUNNING
  Health: ✅ OK
  Health: ✅ OK

----------------------------------------
Testing insurance-service in namespace billing on port 8095
Testing insurance-service in namespace billing on port 8095

  Pod: ✅ RUNNING
  Pod: ✅ RUNNING
  Health: ✅ OK
  Health: ✅ OK

----------------------------------------
Testing payment-service in namespace billing on port 8096
Testing payment-service in namespace billing on port 8096

  Pod: ✅ RUNNING
  Pod: ✅ RUNNING
  Health: ✅ OK
  Health: ✅ OK

----------------------------------------
Testing pharmacy-service in namespace pharmacy on port 8091
Testing pharmacy-service in namespace pharmacy on port 8091

  Pod: ✅ RUNNING
  Pod: ✅ RUNNING
  Health: ✅ OK

----------------------------------------
  Health: ✅ OK
Testing prescription-service in namespace pharmacy on port 8090
Testing prescription-service in namespace pharmacy on port 8090

  Pod: ✅ RUNNING
  Pod: ✅ RUNNING
  Health: ✅ OK
  Health: ✅ OK

----------------------------------------
Testing laboratory-service in namespace laboratory on port 8092
Testing laboratory-service in namespace laboratory on port 8092

  Pod: ✅ RUNNING
  Pod: ✅ RUNNING
  Health: ✅ OK
  Health: ✅ OK

----------------------------------------
Testing hr-service in namespace hr on port 8101
Testing hr-service in namespace hr on port 8101

  Pod: ✅ RUNNING
  Pod: ✅ RUNNING
  Health: ✅ OK
  Health: ✅ OK

----------------------------------------
Testing scheduling-service in namespace hr on port 8102
Testing scheduling-service in namespace hr on port 8102

  Pod: ✅ RUNNING
  Pod: ✅ RUNNING
  Health: ✅ OK
  Health: ✅ OK

----------------------------------------
Testing inventory-service in namespace inventory on port 8097
Testing inventory-service in namespace inventory on port 8097

  Pod: ✅ RUNNING
  Pod: ✅ RUNNING
  Health: ✅ OK
  Health: ✅ OK

----------------------------------------
Testing procurement-service in namespace inventory on port 8098
Testing procurement-service in namespace inventory on port 8098

  Pod: ✅ RUNNING
  Pod: ✅ RUNNING
  Health: ✅ OK
  Health: ✅ OK

----------------------------------------
Testing asset-service in namespace inventory on port 8099
Testing asset-service in namespace inventory on port 8099

  Pod: ✅ RUNNING
  Pod: ✅ RUNNING
  Health: ✅ OK
  Health: ✅ OK

----------------------------------------
Testing event-service in namespace infrastructure on port 8103
Testing event-service in namespace infrastructure on port 8103

  Pod: ✅ RUNNING
  Pod: ✅ RUNNING
  Health: ✅ OK
  Health: ✅ OK

----------------------------------------
Testing notification-service in namespace infrastructure on port 8104
Testing notification-service in namespace infrastructure on port 8104

  Pod: ✅ RUNNING
  Pod: ✅ RUNNING
  Health: ✅ OK
  Health: ✅ OK

----------------------------------------
Testing reporting-service in namespace reporting on port 8105
Testing reporting-service in namespace reporting on port 8105

  Pod: ✅ RUNNING
  Pod: ✅ RUNNING
  Health: ✅ OK
  Health: ✅ OK

----------------------------------------
Test complete. Results saved to /media/ivan/Ultimate/Ivan/script/common/GinDHO/test-results/TEST-RESULTS.md
