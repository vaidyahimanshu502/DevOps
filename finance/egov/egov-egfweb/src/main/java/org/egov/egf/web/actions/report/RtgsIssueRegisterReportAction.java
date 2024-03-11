/*
 *    eGov  SmartCity eGovernance suite aims to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (C) 2017  eGovernments Foundation
 *
 *     The updated version of eGov suite of products as by eGovernments Foundation
 *     is available at http://www.egovernments.org
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see http://www.gnu.org/licenses/ or
 *     http://www.gnu.org/licenses/gpl.html .
 *
 *     In addition to the terms of the GPL license to be adhered to in using this
 *     program, the following additional terms are to be complied with:
 *
 *         1) All versions of this program, verbatim or modified must carry this
 *            Legal Notice.
 *            Further, all user interfaces, including but not limited to citizen facing interfaces,
 *            Urban Local Bodies interfaces, dashboards, mobile applications, of the program and any
 *            derived works should carry eGovernments Foundation logo on the top right corner.
 *
 *            For the logo, please refer http://egovernments.org/html/logo/egov_logo.png.
 *            For any further queries on attribution, including queries on brand guidelines,
 *            please contact contact@egovernments.org
 *
 *         2) Any misrepresentation of the origin of the material is prohibited. It
 *            is required that all modified versions of this material be marked in
 *            reasonable ways as different from the original version.
 *
 *         3) This license does not grant any rights to any user of the program
 *            with regards to rights under trademark law for use of the trade names
 *            or trademarks of eGovernments Foundation.
 *
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 *
 */
package org.egov.egf.web.actions.report;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.export.JRHtmlExporterParameter;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.egov.commons.Accountdetailtype;
import org.egov.commons.Bankaccount;
import org.egov.commons.CFinancialYear;
import org.egov.commons.dao.FinancialYearDAO;
import org.egov.commons.service.EntityTypeService;
import org.egov.commons.utils.EntityType;
import org.egov.egf.model.BankAdviceReportInfo;
import org.egov.infra.admin.master.service.CityService;
import org.egov.infra.config.persistence.datasource.routing.annotation.ReadOnly;
import org.egov.infra.microservice.models.Department;
import org.egov.infra.microservice.utils.MicroserviceUtils;
import org.egov.infra.reporting.util.ReportUtil;
import org.egov.infra.web.struts.annotation.ValidationErrorPage;
import org.egov.infstr.services.PersistenceService;
import org.egov.utils.Constants;
import org.egov.utils.ReportHelper;
import org.hibernate.FlushMode;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.BigDecimalType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

@Results(value = { @Result(name = "search", location = "rtgsIssueRegisterReport-search.jsp"),
		@Result(name = "PDF", type = "stream", location = "inputStream", params = { "inputName", "inputStream",
				"contentType", "application/pdf", "contentDisposition",
				"no-cache;filename=RtgsIssueRegisterReport.pdf" }),
		@Result(name = "XLS", type = "stream", location = "inputStream", params = { "inputName", "inputStream",
				"contentType", "application/xls", "contentDisposition",
				"no-cache;filename=RtgsIssueRegisterReport.xls" }),
		@Result(name = "HTML", type = "stream", location = "inputStream", params = { "inputName", "inputStream",
				"contentType", "text/html" }) })
@ParentPackage("egov")
public class RtgsIssueRegisterReportAction extends ReportAction {

	@Autowired
	@Qualifier("persistenceService")
	private PersistenceService persistenceService;

	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(RtgsIssueRegisterReportAction.class);
	private Date fromDate = new Date();
	private Date toDate = new Date();
	private Bankaccount bankaccount;
	BankAdviceReportInfo bankAdviceInfo;
	private InputStream inputStream;
	private ReportHelper reportHelper;
	String jasperpath = "/reports/templates/rtgsIssueRegisterReportAction.jasper";
	private StringBuffer header = new StringBuffer();
	List<BankAdviceReportInfo> rtgsDisplayList = new ArrayList<BankAdviceReportInfo>();
	List<Object> rtgsReportList = new ArrayList<Object>();
	Map<String, Object> paramMap = new HashMap<String, Object>();
	Boolean searchResult = Boolean.FALSE;
	@Autowired
	private FinancialYearDAO financialYearDAO;

	@Autowired
        private MicroserviceUtils microserviceUtils;
	
	@Autowired
        private CityService cityService;

	@Override
	public Object getModel() {

		return null;
	}

	@Override
	public void prepare() {
		persistenceService.getSession().setDefaultReadOnly(true);
		persistenceService.getSession().setFlushMode(FlushMode.MANUAL);
		super.prepare();

		addDropdownData("bankList", persistenceService.findAllBy("from Bank where isactive=true order by upper(name)"));
		addDropdownData("bankBranchList", Collections.EMPTY_LIST);
		addDropdownData("bankAccountList", Collections.EMPTY_LIST);
		addDropdownData("accNumList", Collections.EMPTY_LIST);
		addDropdownData("chequeNumberList", Collections.EMPTY_LIST);
		finYearDate();
		mandatoryFields.clear();

	}

	@ValidationErrorPage("search")
	@Action(value = "/report/rtgsIssueRegisterReport-newForm")
	public String newForm() {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info(fromDate);
			LOGGER.info(toDate);
		}
		return "search";
	}

	private String getUlbName() {
		return ReportUtil.getCityName() +" "+(cityService.getCityGrade()==null ? "" :cityService.getCityGrade());
	}

	@SkipValidation
	@Action(value = "/report/rtgsIssueRegisterReport-exportPdf")
	public String exportPdf() throws JRException, IOException {
		search();
		if (rtgsDisplayList.size() > 0) {
			inputStream = reportHelper.exportPdf(inputStream, jasperpath, getParamMap(), rtgsReportList);
			return "PDF";
		}
		prepare();
		return newForm();
	}

	@SkipValidation
	@Action(value = "/report/rtgsIssueRegisterReport-exportHtml")
	public String exportHtml() {
		search();
		if (rtgsDisplayList.size() > 0) {
			inputStream = reportHelper.exportHtml(inputStream, jasperpath, getParamMap(), rtgsReportList,
					JRHtmlExporterParameter.SIZE_UNIT_POINT);
			return "HTML";
		}
		addActionMessage("No data found ");
		prepare();
		return "search";
	}

	@SkipValidation
	@Action(value = "/report/rtgsIssueRegisterReport-exportXls")
	public String exportXls() throws JRException, IOException {
		search();
		if (rtgsDisplayList.size() > 0) {
			inputStream = reportHelper.exportXls(inputStream, jasperpath, getParamMap(), rtgsReportList);
			return "XLS";
		}
		prepare();
		return newForm();

	}

	protected Map<String, Object> getParamMap() {

		String fundAndBankHeading = "";
		String dateRange = "";
		final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		final Date date = new Date();
		String newFromDate = "";
		String newToDate = "";
		String reportRundate = "";
		fundAndBankHeading = "RTGS Register for " + persistenceService
				.find("select name from Fund where id = ?", Integer.parseInt(parameters.get("fundId")[0])).toString();

		if (null != parameters.get("rtgsAssignedFromDate")[0]
				&& !parameters.get("rtgsAssignedFromDate")[0].isEmpty())
			dateRange = "from " + parameters.get("rtgsAssignedFromDate")[0];
		else {
			newFromDate = dateFormat.format(fromDate);
			dateRange = "from " + newFromDate;
		}
		if (null != parameters.get("rtgsAssignedToDate")[0]
				&& !parameters.get("rtgsAssignedToDate")[0].isEmpty())
			dateRange = dateRange + " to " + parameters.get("rtgsAssignedToDate")[0];
		else {
			newToDate = dateFormat.format(toDate);
			dateRange = dateRange + " to " + newToDate;
		}
		reportRundate = dateFormat.format(date);
		paramMap.put("fundAndBankHeading", fundAndBankHeading);
		paramMap.put("dateRange", dateRange);
		paramMap.put("reportRundate", reportRundate);
		paramMap.put("ulbName", getUlbName());
		paramMap.put("rtgsDetailsList", rtgsDisplayList);
		paramMap.put("rtgsReportList", rtgsReportList);
		return paramMap;
	}

	public void finYearDate() {
		if (LOGGER.isDebugEnabled())
			LOGGER.debug(" Getting Starting date of financial year ");
		final CFinancialYear date = financialYearDAO.getFinancialYearByDate(new Date());
		persistenceService.getSession().setReadOnly(date, true);
		fromDate = date.getStartingDate();
	}

	public void setFinancialYearDAO(final FinancialYearDAO financialYearDAO) {
		this.financialYearDAO = financialYearDAO;
	}

	@SuppressWarnings("unchecked")
	@ValidationErrorPage(NEW)
	@ReadOnly
	@Action(value = "/report/rtgsIssueRegisterReport-search")
	public String search() {
		if (parameters.get("fundId") == null || StringUtils.isEmpty(parameters.get("fundId")[0])
				|| parameters.get("fundId")[0].equals("-1")) {
			addActionError(getText("msg.please.select.fund"));
			return NEW;
		}
		searchResult = Boolean.TRUE;
		if (LOGGER.isDebugEnabled())
			LOGGER.debug(" Seraching RTGS result for given criteria ");
		final Entry<String, Map<String, Object>> entry = getQueryString().entrySet().iterator().next();

		final Query query = persistenceService.getSession().createSQLQuery(entry.getKey())
				.addScalar("ihId", BigDecimalType.INSTANCE).addScalar("rtgsNumber").addScalar("rtgsDate")
				.addScalar("vhId", BigDecimalType.INSTANCE).addScalar("paymentNumber").addScalar("paymentDate")
				.addScalar("paymentAmount").addScalar("department").addScalar("status").addScalar("bank")
				.addScalar("bankBranch").addScalar("dtId", BigDecimalType.INSTANCE)
				.addScalar("dkId", BigDecimalType.INSTANCE).addScalar("accountNumber");
		if (null == parameters.get("rtgsAssignedFromDate")[0]
				|| parameters.get("rtgsAssignedFromDate")[0].isEmpty())
			query.setDate("finStartDate", new java.sql.Date(fromDate.getTime()));
		if (LOGGER.isInfoEnabled())
			LOGGER.info("Search Query ------------>" + query);
		persistenceService.populateQueryWithParams(query, entry.getValue());
		query.setResultTransformer(Transformers.aliasToBean(BankAdviceReportInfo.class));
		rtgsDisplayList = query.list();
		populateSubLedgerDetails();
		this.populateDepartmentsName();
		rtgsReportList.addAll(rtgsDisplayList);
		return "search";
	}

    private void populateDepartmentsName() {
        Set<String> deptCodes = null;
        if(rtgsDisplayList != null && !rtgsDisplayList.isEmpty()){
            deptCodes = rtgsDisplayList.stream().map(bankAdvice -> {
                return bankAdvice.getDepartment();
            }).collect(Collectors.toSet());
            List<Department> departments = microserviceUtils.getDepartments(String.join(",", deptCodes));
            Map<String, Department> deptCodeMaps = new HashMap<>();
            departments.stream().forEach(dept -> {
                deptCodeMaps.put(dept.getCode(), dept);
            });
            rtgsDisplayList.stream().forEach(bankAdive -> {
                String departCode = bankAdive.getDepartment();
                Department department = deptCodeMaps.get(departCode);
                bankAdive.setDepartment(department != null ? department.getName() : departCode);
            });
        }
    }

    private Map<String, Map<String, Object>> getQueryString() {
        final StringBuffer queryString = new StringBuffer();
        final Map<String, Map<String, Object>> queryMap = new HashMap<>();
        final Map<String, Object> queryParams = new HashMap<>();
        String deptQry = "";
        final Map<String, Object> deptQueryParams = new HashMap<>();
        String fundQry = "";
        final Map<String, Object> fundQueryParams = new HashMap<>();
        String phQry = "";
        final Map<String, Object> phQueryParams = new HashMap<>();
        final StringBuffer bankQry = new StringBuffer("");
        final Map<String, Object> bankQueryParams = new HashMap<>();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        final DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        final StringBuffer instrumentHeaderQry = new StringBuffer("");
        final Map<String, Object> instrumentHeaderQueryParams = new HashMap<>();
        try {
			if (null != parameters.get("departmentid") && null != parameters.get("departmentid")[0]
					&& !parameters.get("departmentid")[0].equalsIgnoreCase("-1")) {
                deptQry = " AND vmis.departmentcode =:deptCode";
                deptQueryParams.put("deptCode", Long.valueOf(parameters.get("departmentcode")[0]));
            } 
            if (null != parameters.get("rtgsAssignedFromDate")[0]
                    && !parameters.get("rtgsAssignedFromDate")[0].equalsIgnoreCase("")) {
                instrumentHeaderQry.append(" and ih.transactiondate >=:rtgsFromDate");
                instrumentHeaderQueryParams.put("rtgsFromDate", formatter.parse(parameters.get("rtgsAssignedFromDate")[0]));
            } else {
                instrumentHeaderQry.append(" and ih.transactiondate >=:finStartDate");
                instrumentHeaderQueryParams.put("finStartDate", new java.sql.Date(fromDate.getTime()));
            }
            if (null != parameters.get("rtgsAssignedToDate")[0] && !parameters.get("rtgsAssignedToDate")[0].equalsIgnoreCase("")) {
                instrumentHeaderQry.append(" and ih.transactiondate  <=:rtgsToDate");
                instrumentHeaderQueryParams.put("rtgsToDate", formatter.parse(parameters.get("rtgsAssignedToDate")[0]));
            }
            if (null != parameters.get("bank")[0] && !parameters.get("bank")[0].equals("-1")
                    && !parameters.get("bank")[0].equalsIgnoreCase("")) {
                bankQry.append(" AND b.id = :bankId");
                bankQueryParams.put("bankId", Long.valueOf(parameters.get("bank")[0]));
            }
            if (null != parameters.get("bankbranch.id")[0] && !parameters.get("bankbranch.id")[0].equals("-1")
                    && !parameters.get("bankbranch.id")[0].equalsIgnoreCase("")) {
                bankQry.append(" AND branch.id=:bankBranchId");
                bankQueryParams.put("bankBranchId", Long.valueOf(parameters.get("bankbranch.id")[0]));
            }
            if (null != parameters.get("bankaccount.id")[0] && !parameters.get("bankaccount.id")[0].equals("-1")
                    && !parameters.get("bankaccount.id")[0].equalsIgnoreCase("")) {
                phQry = " AND ph.bankaccountnumberid=:bankAccId";
                phQueryParams.put("bankAccId", Long.valueOf(parameters.get("bankaccount.id")[0]));
                instrumentHeaderQry.append(" and ih.bankaccountid =:bankAccId");
                instrumentHeaderQueryParams.put("bankAccId", Long.valueOf(parameters.get("bankaccount.id")[0]));
            }
            if (null != parameters.get("instrumentnumber")[0] && !parameters.get("instrumentnumber")[0].equalsIgnoreCase("")) {
                instrumentHeaderQry.append(" and ih.transactionnumber = :instrumentNumber");
                instrumentHeaderQueryParams.put("instrumentNumber", parameters.get("instrumentnumber")[0]);
            }
            if (null != parameters.get("fundId")[0] && !parameters.get("fundId")[0].equalsIgnoreCase("")) {
                fundQry = " AND vh.fundId=:fundId";
                fundQueryParams.put("fundId", Long.valueOf(parameters.get("fundId")[0]));
            }

            queryString.append(" SELECT ih.id as ihId , ih.transactionnumber as rtgsNumber,  ih.transactiondate as rtgsDate, vh.id as vhId, ")
                    .append(" vh.vouchernumber as paymentNumber, to_char(vh.voucherdate,'dd/mm/yyyy') as paymentDate, gld.detailtypeid as dtId, ")
                    .append(" gld.detailkeyid as dkId,   gld.amount as paymentAmount, dept.name as department, stat.description as status,b.name as bank, ")
                    .append(" branch.branchname as bankBranch, ba.accountnumber as accountNumber")
                    .append(" FROM Paymentheader ph, voucherheader vh,vouchermis vmis, ")
                    .append(" bankaccount ba,bankbranch branch,bank b,generalledger gl,generalledgerdetail gld, egf_instrumentvoucher iv,  ")
                    .append(" egf_instrumentheader ih,  eg_department dept ,egw_status stat WHERE ph.voucherheaderid =vh.id AND vmis.voucherheaderid = vh.id ")
                    .append(bankQry.toString())
                    .append(" AND ih.bankaccountid = ba.id and branch.id = ba.branchid and branch.bankid = b.id and vh.status = 0 ")
                    .append(fundQry)
                    .append(phQry)
                    .append(" and stat.id= ih.id_status AND dept.code = vmis.departmentcode ")
                    .append(deptQry)
                    .append(" and lower(ph.type)=lower('rtgs') ")
                    .append(instrumentHeaderQry.toString())
                    .append(" AND IV.VOUCHERHEADERID  IS NOT NULL AND iv.voucherheaderid   =vh.id AND ih.instrumentnumber IS NULL ")
                    .append(" AND ih.id = iv.instrumentheaderid ")
                    .append(" AND vh.type   = 'Payment' and gl.voucherheaderid = vh.id and gld.generalledgerid = gl.id GROUP BY ih.id , ih.transactionnumber,")
                    .append(" ih.transactiondate, vh.id,  vh.vouchernumber,vh.voucherDate, vmis.departmentcode,dept.name, b.name,branch.branchname,")
                    .append(" ba.accountnumber,stat.description,gld.detailtypeid,gld.detailkeyid,gld.amount ORDER BY b.name,branch.branchname,")
                    .append(" ba.accountnumber,ih.transactiondate,ih.transactionnumber,dept.name");
            queryParams.putAll(bankQueryParams);
            queryParams.putAll(fundQueryParams);
            queryParams.putAll(phQueryParams);
            queryParams.putAll(deptQueryParams);
            queryParams.putAll(instrumentHeaderQueryParams);
            queryMap.put(queryString.toString(), queryParams);
        } catch (ParseException e) {

        }
        return queryMap;
    }
    
	private void populateSubLedgerDetails() {

		final Map<Integer, List<EntityType>> subLedgerList = new HashMap<Integer, List<EntityType>>();
		final Map<Integer, List<Long>> detailTypeMapForGetEntitys = new HashMap<Integer, List<Long>>();
		for (final BankAdviceReportInfo bankAdviceReportInfo : rtgsDisplayList)
			if (detailTypeMapForGetEntitys.get(bankAdviceReportInfo.getDtId().intValue()) == null) {
				detailTypeMapForGetEntitys.put(bankAdviceReportInfo.getDtId().intValue(), new ArrayList<Long>());
				detailTypeMapForGetEntitys.get(bankAdviceReportInfo.getDtId().intValue())
						.add(bankAdviceReportInfo.getDkId().longValue());
			} else
				detailTypeMapForGetEntitys.get(bankAdviceReportInfo.getDtId().intValue())
						.add(bankAdviceReportInfo.getDkId().longValue());

		for (final Integer keyGroup : detailTypeMapForGetEntitys.keySet())
			try {
				final List<EntityType> subDetail = new ArrayList<EntityType>();
				final Accountdetailtype detailType = (Accountdetailtype) persistenceService
						.find("from Accountdetailtype where id=? order by name", keyGroup);
				final String table = detailType.getFullQualifiedName();
				final Class<?> service = Class.forName(table);
				String simpleName = service.getSimpleName();
				simpleName = simpleName.substring(0, 1).toLowerCase() + simpleName.substring(1) + "Service";

				final WebApplicationContext wac = WebApplicationContextUtils
						.getWebApplicationContext(ServletActionContext.getServletContext());
				final EntityTypeService entityService = (EntityTypeService) wac.getBean(simpleName);
				final List<Long> entityIds = new ArrayList<Long>(detailTypeMapForGetEntitys.get(keyGroup));
				int size = entityIds.size();
				if (LOGGER.isInfoEnabled())
					LOGGER.info(entityService + " size " + size);
				if (size > 999) {
					int fromIndex = 0;
					int toIndex = 0;
					final int step = 1000;

					while (size - step >= 0) {
						toIndex += step;
						final List<EntityType> returnList = (List<EntityType>) entityService
								.getEntitiesById(entityIds.subList(fromIndex, toIndex));

						if (returnList != null)
							subDetail.addAll(returnList);

						fromIndex = toIndex;
						size -= step;

					}

					if (size > 0) {
						fromIndex = toIndex;
						toIndex = fromIndex + size;
						final List<EntityType> returnList = (List<EntityType>) entityService
								.getEntitiesById(entityIds.subList(fromIndex, toIndex));
						if (returnList != null)
							subDetail.addAll(returnList);
					}

					subLedgerList.put(keyGroup, subDetail);
				} else {
					subDetail.addAll(entityService.getEntitiesById(entityIds));
					subLedgerList.put(keyGroup, subDetail);
				}

			} catch (final ClassCastException e) {
				LOGGER.error(e);
			} catch (final ClassNotFoundException e) {
				LOGGER.error("Exception to get EntityType=" + e.getMessage());
			}
		List<EntityType> subDetail = new ArrayList<EntityType>();

		for (final Integer keyGroup : subLedgerList.keySet())
			for (final BankAdviceReportInfo bankAdviceReportInfo : rtgsDisplayList)
				if (bankAdviceReportInfo.getDtId() != null)
					if (keyGroup.equals(bankAdviceReportInfo.getDtId().intValue())) {

						subDetail = subLedgerList.get(keyGroup);

						for (final EntityType entityType : subDetail)
							if (bankAdviceReportInfo.getDtId() != null)
								if (entityType.getEntityId().equals(bankAdviceReportInfo.getDkId().intValue()))
									if (entityType != null)
										bankAdviceReportInfo.setPartyName(entityType.getName().toUpperCase());
					}
		for (final BankAdviceReportInfo bankAdviceReportInfo : rtgsDisplayList)
			if (bankAdviceReportInfo.getStatus().equalsIgnoreCase("new"))
				bankAdviceReportInfo.setStatus("Assigned");
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(final Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(final Date toDate) {
		this.toDate = toDate;
	}

	public Bankaccount getBankaccount() {
		return bankaccount;
	}

	public void setBankaccount(final Bankaccount bankaccount) {
		this.bankaccount = bankaccount;
	}

	public void setHeader(final StringBuffer header) {
		this.header = header;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public ReportHelper getReportHelper() {
		return reportHelper;
	}

	public StringBuffer getHeader() {
		return header;
	}

	public void setInputStream(final InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public void setReportHelper(final ReportHelper reportHelper) {
		this.reportHelper = reportHelper;
	}

	public List<BankAdviceReportInfo> getRtgsDisplayList() {
		return rtgsDisplayList;
	}

	public void setRtgsDisplayList(final List<BankAdviceReportInfo> rtgsDisplayList) {
		this.rtgsDisplayList = rtgsDisplayList;
	}

	public List<Object> getRtgsReportList() {
		return rtgsReportList;
	}

	public void setRtgsReportList(final List<Object> rtgsReportList) {
		this.rtgsReportList = rtgsReportList;
	}

	public String getFormattedDate(final Date date) {
		return Constants.DDMMYYYYFORMAT2.format(date);
	}

	public Boolean getSearchResult() {
		return searchResult;
	}

	public void setSearchResult(final Boolean searchResult) {
		this.searchResult = searchResult;
	}

}