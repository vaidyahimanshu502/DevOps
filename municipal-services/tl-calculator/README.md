# Trade License Calculator (tl-calculator)

This service is used to calculate the Trade license fees based on the defined billing slabs. The service is designed in such way that it can be used to serve different type of licenses. 

### DB UML Diagram

- TBD

### Service Dependencies

- Trade License Service (tl-service)
- Billing Service (billing-service)
- MDMS Service (egov-mdms-service)

### Swagger API Contract

Link to the swagger API contract yaml and editor link like [here](https://editor.swagger.io/?url=https://raw.githubusercontent.com/upyog/UPYOG/master/municipal-services/docs/tl-calculator.yml#!/)

## Service Details

Trade License calculator application is used to calculate the Trade license Fees based on the different billing slabs in the DB. Every Billing Slab combination has its own demand configurations.
Once the demand is created for the combination, it calls billing-services API to generate the fees.

### API Details

`tl-calculator/billingslab/_create` : API used to create the billing slabs in the system

`tl-calculator/billingslab/_search` : API used to search the billing slabs.

`tl-calculator/billingslab/_update` : API used to update the existing billing slabs.

`tl-calculator/v1/_calculate` : Calculates the fees based on the demand created.


### Kafka Consumers

NA

### Kafka Producers

`save-tl-billingslab` : tl-claculator sends data to this topic to store the billing slab data.
`update-tl-billingslab` : tl-claculator sends data to this topic to update the billing slab data.
`save-tl-calculation` : tl-claculator sends data to this topic to store the calculated fee for the Tradelicense.
