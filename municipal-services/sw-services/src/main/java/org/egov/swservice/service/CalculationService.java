package org.egov.swservice.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.egov.swservice.config.SWConfiguration;
import org.egov.swservice.repository.ServiceRequestRepository;
import org.egov.swservice.repository.SewerageDao;
import org.egov.swservice.util.SWConstants;
import org.egov.swservice.util.SewerageServicesUtil;
import org.egov.swservice.web.models.CalculationCriteria;
import org.egov.swservice.web.models.CalculationReq;
import org.egov.swservice.web.models.CalculationRes;
import org.egov.swservice.web.models.Property;
import org.egov.swservice.web.models.RequestInfoWrapper;
import org.egov.swservice.web.models.SewerageConnectionRequest;
import org.egov.swservice.web.models.collection.Bill;
import org.egov.swservice.web.models.collection.BillResponse;
import org.egov.swservice.workflow.WorkflowIntegrator;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CalculationService {

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private ServiceRequestRepository serviceRequestRepository;

	@Autowired
	private SewerageServicesUtil sewerageServicesUtil;

	@Autowired
	private WorkflowIntegrator wfIntegrator;

	@Autowired
	private SWConfiguration config;

	@Autowired
	private SewerageDao sewerageDao;

	@Autowired
	private SewerageService sewerageService;

	@Autowired
	private EnrichmentService enrichmentService;

	/**
	 * 
	 * @param request
	 * 
	 *            If action would be APPROVE_FOR_CONNECTION then
	 * 
	 *            Estimate the fee for sewerage application and generate the
	 *            demand
	 */
	public void calculateFeeAndGenerateDemand(SewerageConnectionRequest request, Property property) {
		if (request.getSewerageConnection().getProcessInstance().getAction().equalsIgnoreCase("APPROVE_FOR_CONNECTION") && !request.isReconnectRequest()){
			StringBuilder uri = sewerageServicesUtil.getCalculatorURL();
			CalculationCriteria criteria = CalculationCriteria.builder()
					.applicationNo(request.getSewerageConnection().getApplicationNo())
					.sewerageConnection(request.getSewerageConnection())
					.tenantId(property.getTenantId()).build();
			List<CalculationCriteria> calculationCriterias = Arrays.asList(criteria);
			CalculationReq calRequest = CalculationReq.builder().calculationCriteria(calculationCriterias)
					.requestInfo(request.getRequestInfo()).isconnectionCalculation(false).isDisconnectionRequest(false).isReconnectionRequest(false).build();
			try {
				Object response = serviceRequestRepository.fetchResult(uri, calRequest);
				CalculationRes calResponse = mapper.convertValue(response, CalculationRes.class);
				log.info(mapper.writeValueAsString(calResponse));
			} catch (Exception ex) {
				log.error("Calculation response error!!", ex);
				throw new CustomException("SEWERAGE_CALCULATION_EXCEPTION", "Calculation response can not parsed!!!");
			}
		}
		else if (request.getSewerageConnection().getProcessInstance().getAction().equalsIgnoreCase("APPROVE_FOR_DISCONNECTION") && !request.isReconnectRequest()){
				StringBuilder uri = sewerageServicesUtil.getCalculatorURL();
				CalculationCriteria criteria = CalculationCriteria.builder()
						.applicationNo(request.getSewerageConnection().getApplicationNo())
						.sewerageConnection(request.getSewerageConnection())
						.connectionNo(request.getSewerageConnection().getConnectionNo())
						.tenantId(property.getTenantId()).build();
				List<CalculationCriteria> calculationCriterias = Arrays.asList(criteria);
				CalculationReq calRequest = CalculationReq.builder().calculationCriteria(calculationCriterias)
						.requestInfo(request.getRequestInfo()).isconnectionCalculation(false).isDisconnectionRequest(true).isReconnectionRequest(false).build();
				try {
					Object response = serviceRequestRepository.fetchResult(uri, calRequest);
					CalculationRes calResponse = mapper.convertValue(response, CalculationRes.class);
					log.info(mapper.writeValueAsString(calResponse));
				} catch (Exception ex) {
					log.error("Calculation response error!!", ex);
					throw new CustomException("SEWERAGE_CALCULATION_EXCEPTION", "Calculation response can not parsed!!!");
				}
			}
		else if (request.getSewerageConnection().getProcessInstance().getAction().equalsIgnoreCase("APPROVE_FOR_CONNECTION") && (request.isReconnectRequest() || request.getSewerageConnection().getApplicationType().equalsIgnoreCase(SWConstants.SEWERAGE_RECONNECTION))){
			StringBuilder uri = sewerageServicesUtil.getCalculatorURL();
			CalculationCriteria criteria = CalculationCriteria.builder()
					.applicationNo(request.getSewerageConnection().getApplicationNo())
					.sewerageConnection(request.getSewerageConnection())
					.connectionNo(request.getSewerageConnection().getConnectionNo())
					.tenantId(property.getTenantId()).build();
			List<CalculationCriteria> calculationCriterias = Arrays.asList(criteria);
			CalculationReq calRequest = CalculationReq.builder().calculationCriteria(calculationCriterias)
					.requestInfo(request.getRequestInfo()).isconnectionCalculation(false).isDisconnectionRequest(false).isReconnectionRequest(true).build();
			try {
				Object response = serviceRequestRepository.fetchResult(uri, calRequest);
				CalculationRes calResponse = mapper.convertValue(response, CalculationRes.class);
				log.info(mapper.writeValueAsString(calResponse));
			} catch (Exception ex) {
				log.error("Calculation response error!!", ex);
				throw new CustomException("SEWERAGE_CALCULATION_EXCEPTION", "Calculation response can not parsed!!!");
			}
		}
		}

	public boolean fetchBill(String tenantId, String connectionNo, RequestInfo requestInfo) {
		boolean isNoPayment = false;
		try {
			Object result = serviceRequestRepository.fetchResult(getFetchBillURL(tenantId, connectionNo)
					, RequestInfoWrapper.builder().requestInfo(requestInfo).build());
			BillResponse billResponse = mapper.convertValue(result, BillResponse.class);
			for (Bill bill : billResponse.getBill()) {
				if (bill.getTotalAmount().equals(BigDecimal.valueOf(0.0))) {
					isNoPayment = true;
				}
			}
		} catch (Exception ex) {
			throw new CustomException("SEWERAGE_FETCH_BILL_ERRORCODE", "Error while fetching the bill" + ex.getMessage());
		}
		return isNoPayment;
	}
	
	public boolean fetchBillForReconnect(String tenantId, String connectionNo, RequestInfo requestInfo) {
		boolean isNoPayment = false;
		try {
			Object result = serviceRequestRepository.fetchResult(getFetchBillURLForReconnect(tenantId, connectionNo)
					, RequestInfoWrapper.builder().requestInfo(requestInfo).build());
			BillResponse billResponse = mapper.convertValue(result, BillResponse.class);
			for (Bill bill : billResponse.getBill()) {
				if (bill.getTotalAmount().equals(BigDecimal.valueOf(0.0))) {
					isNoPayment = true;
				}
			}
		} catch (Exception ex) {
			throw new CustomException("WATER_FETCH_BILL_ERRORCODE", "Error while fetching the bill" + ex.getMessage());
		}
		return isNoPayment;
	}
	
	private StringBuilder getFetchBillURLForReconnect(String tenantId, String connectionNo) {

		return new StringBuilder().append(config.getBillingServiceHost())
				.append(config.getFetchBillEndPoint()).append(SWConstants.URL_PARAMS_SEPARATER)
				.append(SWConstants.TENANT_ID_FIELD_FOR_SEARCH_URL).append(tenantId)
				.append(SWConstants.SEPARATER).append(SWConstants.CONSUMER_CODE_SEARCH_FIELD_NAME)
				.append(connectionNo).append(SWConstants.SEPARATER)
				.append(SWConstants.BUSINESSSERVICE_FIELD_FOR_SEARCH_URL)
				.append("SWReconnection");
	}
	
	private StringBuilder getFetchBillURL(String tenantId, String connectionNo) {

		return new StringBuilder().append(config.getBillingServiceHost())
				.append(config.getFetchBillEndPoint()).append(SWConstants.URL_PARAMS_SEPARATER)
				.append(SWConstants.TENANT_ID_FIELD_FOR_SEARCH_URL).append(tenantId)
				.append(SWConstants.SEPARATER).append(SWConstants.CONSUMER_CODE_SEARCH_FIELD_NAME)
				.append(connectionNo).append(SWConstants.SEPARATER)
				.append(SWConstants.BUSINESSSERVICE_FIELD_FOR_SEARCH_URL)
				.append(SWConstants.SEWERAGE_TAX_SERVICE_CODE);
	}
}
