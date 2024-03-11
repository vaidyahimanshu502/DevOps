package org.egov.swcalculation.repository;

import java.util.List;

import org.egov.swcalculation.web.models.SewerageConnection;

public interface SewerageCalculatorDao {

	List<String> getTenantId();
	
	List<SewerageConnection> getConnectionsNoList(String tenantId, String connectionType, Integer batchOffset, Integer batchsize, Long fromDate, Long toDate);

	long getConnectionCount(String tenantid, Long fromDate, Long toDate);
	
	List<SewerageConnection> getConnectionsNoListForDemand(String tenantId, String connectionType, Long fromDate, Long toDate);

	List<SewerageConnection> getConnection(String tenantId, String consumerCode,String connectionType,Long fromDate, Long toDate);

	
}
