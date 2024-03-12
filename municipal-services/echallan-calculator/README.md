# eChallan Calculator Service (echallan-calculator)

This service is used to calculate the challan amount based on the details present in echallan request. This module is designed in such way that it can be used to serve echallan for different type of service. 

### DB UML Diagram

- TBD

### Service Dependencies

- eChallan  Service (echallan-service)
- Billing Service (billing-service)
- MDM Service (egov-mdms-service)

### Swagger API Contract

Link to the swagger API contract yaml and editor link like below

https://editor.swagger.io/?url=https://raw.githubusercontent.com/upyog/UPYOG/master/municipal-services/docs/e-Challan-v1.0.0.yaml#!/

## Service Details

eChallan calculator application is used to calculate the challan Fees based on the data mentioned in echallan creation. Based on tax amount mentioned in echallan, demand is created.
Once the demand is created, it calls billing service API to generate the fees.

### API Details

`echallan-calculator/v1/_calculate` : Calculates the fees based on the demand created.


### Kafka Consumers

NA

### Kafka Producers

NA
