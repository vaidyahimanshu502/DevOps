# PDF Service (egov-pdf)

PDF Service work in between pdf-service and client requesting pdfs. Earlier client used to directly call PDF service with complete data as json, but with introduction of this new service one can provide just few parameters ex:- applicationnumber, tenantId to this new service to get a pdf. 

### DB UML Diagram

- NA

### Service Dependencies

- MDMS Service (egov-mdms-service)
- Trade License Service (tl-service)
- Property Service (property-service)
- PDF Service (pdf-service)
- User Service (user-service)
- Collection Service (collection service)
- Billing Service (willing-service)
- Workflow Service (workflow-service)


### Swagger API Contract

https://editor.swagger.io/?url=https://raw.githubusercontent.com/upyog/UPYOG/master/utilities/docs/egov-pdf_contract.yml#!/

## Service Details

PDF Service is new service being added which can work in between existing PDF service and client requesting pdfs. Earlier client used to directly call pdf-service with complete data as json, but with introduction of this new service one can provide just few parameters ex:- applicationnumber, tenantId to this new service to get a pdf. The PDF Service will take responsibility of getting application data from concerned service and also will do any enrichment if required and then with the data call pdf service to get pdf directly. The service will return pdf binary as response which can be directly downloaded by the client. With this service the existing pdf service endpoints need not be exposed to frontend.

For any new pdf requirement one new endpoint with validations and logic for getting data for pdf has to be added in the code. With separate endpoint for each pdf we can define access rules per pdf basis. Currently PDF Service has endpoint for following pdfs used in our system:-

- PT mutation certificate
- PT bill
- PT receipt
- TL receipt
- TL certifcate
- TL renewal certificate
- Consolidated receipt

#### Configurations

**Steps/guidelines for adding support for new pdf:**

- Make sure the config for pdf is added in the PDF-Service.Refer the PDF service [documentation](https://upyog-docs.gitbook.io/upyog-v-1.0/upyog-1/platform/configure-upyog/configuring-services/customizing-pdf-notices-and-certificates/pdf-generation-service)

- Follow code of [existing supported PDFs](https://github.com/upyog/UPYOG/tree/master/utilities/egov-pdf/src/routes) and create new endpoint with suitable search parameters for each PDF

- Put parameters validations, module level validations ex:- application status, application type and api error responses with proper error messages and error codes

- Make sure whatever service is used for preparing data for PDF, search call to them by citizen returns citizens own record only, if not then adjust search criteria for them by including citizen mobilenumber or uuid to restrict citizen to create pdfs for his record only. If in the requirement itself it is explained that citizen can get PDF for others records also ex:- billgenie bill PDFs then no need for this check

- Prepare data for pdf by calling required services.

- Use correct pdf key with data to call and return PDF(use “/creatnosave” endpoint of PDF service)

- Add access to endpoint in MDMS for suitable roles

### API Details
Currently below endpoints are in use for ‘CITIZEN' and 'EMPLOYEE’ roles

| Endpoint | module | query parameter | Restrict Citizen to own records |
| -------- | ------ | --------------- | ------------------------------- |
|`/egov-pdf/download/PT/ptreceipt` | property-tax | `uuid, tenantId` | yes |
|`/egov-pdf/download/PT/ptbill` | property-tax | `uuid, tenantId` | no |
|`/egov-pdf/download/PT/ptmutationcertificate` | property-tax | `uuid, tenantId` | yes |
|`/egov-pdf/download/TL/tlrenewalcertificate` | Tradelicense | `applicationNumber, tenantId` | yes |
|`/egov-pdf/download/TL/tlcertificate` | Tradelicense | `applicationNumber, tenantId` | yes |
|`/egov-pdf/download/TL/tlreceipt` | Tradelicense | `applicationNumber, tenantId` | yes |
|`/egov-pdf/download/PAYMENT/consolidatedreceipt` | Collection | `consumerCode, tenantId` | yes |

### Kafka Consumers

NA

### Kafka Producers

NA
