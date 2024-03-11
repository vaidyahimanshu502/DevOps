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
import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.egov.commons.CFinancialYear;
import org.egov.commons.CFunction;
import org.egov.commons.Fund;
import org.egov.commons.dao.FinancialYearDAO;
import org.egov.dao.budget.BudgetDetailsDAO;
import org.egov.egf.model.BudgetAppDisplay;
import org.egov.infra.admin.master.entity.AppConfigValues;
import org.egov.infra.admin.master.service.AppConfigValueService;
import org.egov.infra.config.persistence.datasource.routing.annotation.ReadOnly;
import org.egov.infra.microservice.models.Department;
import org.egov.infra.validation.exception.ValidationException;
import org.egov.infra.web.struts.actions.BaseFormAction;
import org.egov.infstr.services.PersistenceService;
import org.egov.infstr.utils.EgovMasterDataCaching;
import org.egov.model.budget.BudgetDetail;
import org.egov.model.budget.BudgetGroup;
import org.egov.services.budget.BudgetDetailService;
import org.egov.services.budget.BudgetService;
import org.egov.utils.BudgetDetailConfig;
import org.egov.utils.BudgetingType;
import org.egov.utils.Constants;
import org.egov.utils.ReportHelper;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.BigDecimalType;
import org.hibernate.type.DateType;
import org.hibernate.type.LongType;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.StringType;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Results(value = {
        @Result(name = "result", location = "budgetAppropriationRegisterReport-form.jsp"),
        @Result(name = "PDF", type = "stream", location = Constants.INPUT_STREAM, params = { Constants.INPUT_NAME,
                Constants.INPUT_STREAM, Constants.CONTENT_TYPE, "application/pdf", Constants.CONTENT_DISPOSITION,
                "no-cache;filename=BudgetAppropriationRegisterRepor.pdf" }),
        @Result(name = "XLS", type = "stream", location = Constants.INPUT_STREAM, params = { Constants.INPUT_NAME,
                Constants.INPUT_STREAM, Constants.CONTENT_TYPE, "application/xls", Constants.CONTENT_DISPOSITION,
                "no-cache;filename=BudgetAppropriationRegisterRepor.xls" })
})
@ParentPackage("egov")
public class BudgetAppropriationRegisterReportAction extends BaseFormAction {
    private static final long serialVersionUID = 1658431423915247237L;
    private static final Logger LOGGER = Logger.getLogger(BudgetAppropriationRegisterReportAction.class);
    String jasperpath = "/reports/templates/BudgetAppReport.jasper";
    private Department department = new Department();
    private CFunction function = new CFunction();
    private Fund fund = new Fund();
    private BudgetGroup budgetGroup = new BudgetGroup();
    private List<BudgetAppDisplay> budgetAppropriationRegisterList = new ArrayList<BudgetAppDisplay>();
    private List<BudgetAppDisplay> updatedBdgtAppropriationRegisterList = new ArrayList<BudgetAppDisplay>();
    private String budgetHead;
    private BigDecimal totalGrant;
    BudgetDetailsDAO budgetDetailsDAO;
    FinancialYearDAO financialYearDAO;
    ReportHelper reportHelper;
    private InputStream inputStream;
    private String strAsOnDate;
    String financialYearId = "";
    Date dtAsOnDate = null;
    private BigDecimal addtionalAppropriationForBe = BigDecimal.ZERO;
    private BigDecimal addtionalAppropriationForRe = BigDecimal.ZERO;
    private BigDecimal beAmount = BigDecimal.ZERO;
    private BigDecimal reAmount = BigDecimal.ZERO;
    private String finYearRange;
    protected List<String> mandatoryFields = new ArrayList<String>();
    private BudgetService budgetService;
    private boolean isBeDefined = true;
    private boolean isReDefined = true;
    @Autowired
    private AppConfigValueService appConfigValuesService;
    private Boolean shouldShowREAppropriations = false;
    @Autowired
    private EgovMasterDataCaching masterDataCache;
    @Autowired
    private BudgetDetailConfig budgetDetailConfig;
    @Autowired
    private BudgetDetailService budgetDetailService;

    public BudgetAppropriationRegisterReportAction() {
        addRelatedEntity(Constants.FUNCTION, CFunction.class);
        addRelatedEntity(Constants.EXECUTING_DEPARTMENT, Department.class);
        addRelatedEntity(Constants.FUND, Fund.class);
        addRelatedEntity(Constants.FUND, Fund.class);
    }
    private void populateSelectedData() {
        // TODO Auto-generated method stub
        if (fund.getId() != null && fund.getId() != -1){
            fund = (Fund) persistenceService.find("from Fund where id=?", fund.getId());
            if (department.getCode() != null && department.getCode() != 0+""){
                department = microserviceUtils.getDepartmentByCode(department.getCode());
                ArrayList<Department> listOfDepartments = new ArrayList<Department>();
                List<String> deptCodeList = budgetDetailService.getDepartmentFromBudgetDetailByFundId(fund.getId());
                if(deptCodeList != null && !deptCodeList.isEmpty()){
                    deptCodeList.stream().forEach(bd -> {
                    listOfDepartments.add(microserviceUtils.getDepartmentByCode(bd));
                    });
                }
                dropdownData.put("executingDepartmentList",listOfDepartments);
                if (function.getId() != null && function.getId() != -1){
                    function = (CFunction) persistenceService.find("from CFunction where id=?", function.getId());
                    List<BudgetDetail> functionList = budgetDetailService.getFunctionFromBudgetDetailByDepartmentId(department.getCode());
                    dropdownData.put("functionList", functionList);
                    if (budgetGroup.getId() != null && budgetGroup.getId() != -1){
                        budgetGroup = (BudgetGroup) persistenceService.find("from BudgetGroup where id=?", budgetGroup.getId());
                        List<BudgetDetail> budgetDetailList = budgetDetailService.getBudgetDetailByFunctionId(function.getId());
                        dropdownData.put("budgetGroupList", budgetDetailList);
                    }
                }
            }
        }
        
    }

    @Override
    public void prepare() {
        super.prepare();
        mandatoryFields = budgetDetailConfig.getMandatoryFields();
        dropdownData.put("functionList",Collections.EMPTY_LIST);
        dropdownData.put("executingDepartmentList",Collections.EMPTY_LIST);
        dropdownData.put("budgetGroupList", Collections.EMPTY_LIST);
        dropdownData.put("fundList", masterDataCache.get("egi-fund"));
        populateSelectedData();
    }

    @ReadOnly
    @SkipValidation
    @Action(value = "/report/budgetAppropriationRegisterReport-search")
    public String search() {
        CFinancialYear financialYear = new CFinancialYear();
        if (parameters.get("asOnDate")[0] != null) {
            strAsOnDate = parameters.get("asOnDate")[0];
            try {
                dtAsOnDate = Constants.DDMMYYYYFORMAT2.parse(strAsOnDate);
                financialYear = financialYearDAO.getFinancialYearByDate(dtAsOnDate);
            } catch (final ParseException e) {
                if (LOGGER.isInfoEnabled())
                    LOGGER.info("ParseException the date :" + e.getMessage());
            }
        }
        // Get this to show at header level
        if (budgetService.hasApprovedBeForYear(financialYear.getId()))
            beAmount = getBudgetBEorREAmt("BE");
        else {
            isBeDefined = false;
            isReDefined = false;
        }
        // -- Consider RE if RE is present & approved for the current yr.
        if (budgetService.hasApprovedReForYear(financialYear.getId())) {
            reAmount = getBudgetBEorREAmt("RE");
            if (getConsiderReAppropriationAsSeperate())
                totalGrant = reAmount.add(addtionalAppropriationForRe);
            else
                totalGrant = reAmount;
        } else if (budgetService.hasApprovedBeForYear(financialYear.getId())) {
            isReDefined = false;
            totalGrant = beAmount.add(addtionalAppropriationForBe);
        }
        generateReport();
        return "result";
    }


    private void generateReport() {
        CFinancialYear financialYr = new CFinancialYear();
        financialYr = financialYearDAO.getFinancialYearByDate(dtAsOnDate);
        CFinancialYear financialYear = null;
        financialYear = financialYearDAO.getFinancialYearById(Long.valueOf(financialYr.getId()));
        finYearRange = financialYear.getFinYearRange();
        final Date dStartDate = financialYear.getStartingDate();
        Query query = null;

        if (budgetGroup != null) {
            budgetHead = budgetGroup.getName();
            StringBuilder strQuery = new StringBuilder();
            
            strQuery.append("select vmis.budgetary_appnumber as bdgApprNumber, vh.vouchernumber as VoucherNumber, vh.voucherdate as voucherDate,")
            .append(" vh.description as description,vh.createddate as createdDate, null as billNumber, null as billDate,null as billCreatedDate, gl.debitamount as debitAmount,")
            .append(" gl.creditamount as creditAmount from generalledger gl, vouchermis vmis, voucherheader vh")
            .append("  where vh.id = gl.voucherheaderid and vh.id = vmis.voucherheaderid and  gl.glcodeid =:glCodeId")
            .append(" and (vmis.budgetary_appnumber  != 'null' and vmis.budgetary_appnumber is not null) and vh.status != 4 and vh.voucherdate  >=:strStDate")
            .append(" and vh.voucherdate <=:strAODate")
            .append(getFunctionQuery("gl.functionid"))
            .append(getDepartmentQuery("vmis.departmentcode"))
            .append(getFundQuery("vh.fundid"))
            .append(" ")
            .append(" union select distinct bmis.budgetary_appnumber as bdgApprNumber, vh1.vouchernumber as VoucherNumber, vh1.voucherdate as  voucherDate,")
            .append(" br.narration as description,vh1.createddate as createdDate, br.billnumber as billNumber, br.billdate as billDate,br.createddate as billCreatedDate,")
            .append("  bd.debitamount as debitAmount, bd.creditamount as creditAmount  ")
            .append(" from eg_billdetails bd, eg_billregistermis bmis, eg_billregister br, voucherHeader vh1 where br.id = bd.billid and br.id = bmis.billid")
            .append(" and  bd.glcodeid =:glCodeId ")
            .append(" and (bmis.budgetary_appnumber != 'null' and bmis.budgetary_appnumber is not null) and br.statusid not in (select id from egw_status")
            .append(" where description='Cancelled' and moduletype in ('EXPENSEBILL', 'SALBILL', 'WORKSBILL', 'PURCHBILL', 'CBILL', 'SBILL', 'CONTRACTORBILL'))")
            .append(" and (vh1.id = bmis.voucherheaderid )  and br.billdate  >=:strStDate and br.billdate  <=:strAODate")
            .append(getFunctionQuery("bd.functionid"))
            .append(getDepartmentQuery("bmis.departmentcode"))
            .append(getFundQuery("bmis.fundid"))
            .append("  ")
            .append(" union select distinct bmis1.budgetary_appnumber as bdgApprNumber, null as VoucherNumber,cast( null as date) voucherDate , ")
            .append(" br.narration as description,cast( null as date) createdDate, br.billnumber as billNumber, br.billdate as billDate,")
            .append("br.createddate as billCreatedDate ,   bd1.debitamount as debitAmount, bd1.creditamount as creditAmount")
            .append(" from eg_billdetails bd1, eg_billregistermis bmis1, eg_billregister br  ")
            .append(" where br.id = bd1.billid and br.id = bmis1.billid and  bd1.glcodeid =:glCodeId ")
            .append(" and (bmis1.budgetary_appnumber != 'null' and bmis1.budgetary_appnumber is not null) ")
            .append(" and br.statusid not in (select id from egw_status where description='Cancelled'")
            .append(" and moduletype in ('EXPENSEBILL', 'SALBILL', 'WORKSBILL', 'PURCHBILL', 'CBILL', 'SBILL', 'CONTRACTORBILL'))")
            .append(" and bmis1.voucherheaderid is null and br.billdate   >=:strStDate")
            .append(" and br.billdate <=:strAODate")
            .append(getFunctionQuery("bd1.functionid"))
            .append(getDepartmentQuery("bmis1.departmentcode"))
            .append(getFundQuery("bmis1.fundid"))
            .append("  order by bdgApprNumber ");
            
            if (LOGGER.isDebugEnabled())
                LOGGER.debug("BudgetAppropriationRegisterReportAction -- strQuery...." + strQuery);

            query = persistenceService.getSession().createSQLQuery(strQuery.toString())
                    .addScalar("bdgApprNumber")
                    .addScalar("voucherDate", StandardBasicTypes.DATE)
                    .addScalar("billDate", StandardBasicTypes.DATE)
                    .addScalar("createdDate",StandardBasicTypes.DATE)
                    .addScalar("billCreatedDate", StandardBasicTypes.DATE)
                    .addScalar("description")
                    .addScalar("VoucherNumber")
                    .addScalar("billNumber")
                    .addScalar("debitAmount", BigDecimalType.INSTANCE)
                    .addScalar("creditAmount", BigDecimalType.INSTANCE)
                    .setResultTransformer(Transformers.aliasToBean(BudgetAppDisplay.class));
            query=setParameterForBudgetAppDisplay(query,dtAsOnDate,dStartDate);
        }
        budgetAppropriationRegisterList = query.list();

        List<BudgetAppDisplay> budgetApprRegNewList = new ArrayList<BudgetAppDisplay>();
        final List<BudgetAppDisplay> budgetApprRegUpdatedList1 = new ArrayList<BudgetAppDisplay>();
        final HashMap<String, BudgetAppDisplay> regMap = new HashMap<String, BudgetAppDisplay>();
        if (budgetAppropriationRegisterList.size() > 0) {
            StringBuilder strsubQuery = new StringBuilder();
            
            strsubQuery.append("select vmis.budgetary_appnumber as bdgApprNumber, vh.vouchernumber as VoucherNumber, vh.voucherdate as voucherDate, vh.description as description,")
            .append("vh.createddate as createdDate, br.billnumber as billNumber, br.billdate as billDate,br.createddate as billCreatedDate, gl.debitamount as debitAmount,")
            .append(" gl.creditamount as creditAmount from generalledger gl, vouchermis vmis, voucherheader vh,  eg_billregistermis bmis, eg_billregister br")
            .append(" where vh.id = gl.voucherheaderid and vh.id = vmis.voucherheaderid and vh.id = bmis.voucherheaderid and bmis.billid = br.id ")
            .append(" and  gl.glcodeid =:glCodeId ")
            .append(" and  ")
            .append(" (vmis.budgetary_appnumber  != 'null' and vmis.budgetary_appnumber is not null) and vh.status != 4 and vh.voucherdate  >=:strStDate")
            .append(" and vh.voucherdate <=:strAODate")
            .append(getFunctionQuery("gl.functionid"))
            .append(getDepartmentQuery("vmis.departmentid"))
            .append(getFundQuery("vh.fundid"))
            .append("  order by bdgApprNumber ");
            
            if (LOGGER.isDebugEnabled())
                LOGGER.debug("BudgetAppropriationRegisterReportAction -- strsubQuery...." + strsubQuery);

            query = persistenceService.getSession().createSQLQuery(strsubQuery.toString())
                    .addScalar("bdgApprNumber")
                    .addScalar("voucherDate", StandardBasicTypes.DATE)
                    .addScalar("billDate", StandardBasicTypes.DATE)
                    .addScalar("createdDate", StandardBasicTypes.DATE)
                    .addScalar("billCreatedDate", StandardBasicTypes.DATE)
                    .addScalar("description")
                    .addScalar("VoucherNumber")
                    .addScalar("billNumber")
                    .addScalar("debitAmount", BigDecimalType.INSTANCE)
                    .addScalar("creditAmount", BigDecimalType.INSTANCE)
                    .setResultTransformer(Transformers.aliasToBean(BudgetAppDisplay.class));
            query=setParameterForBudgetAppDisplay(query,dtAsOnDate,dStartDate); 
            budgetApprRegNewList = query.list();
            if (budgetApprRegNewList.size() > 0) {
                for (final BudgetAppDisplay budgetAppRtDisp : budgetApprRegNewList)
                    regMap.put(budgetAppRtDisp.getBdgApprNumber(), budgetAppRtDisp);

                for (final BudgetAppDisplay budgetAppropriationRegisterDisp : budgetAppropriationRegisterList)
                    if (regMap.containsKey(budgetAppropriationRegisterDisp.getBdgApprNumber()))
                        budgetApprRegUpdatedList1.add(regMap.get(budgetAppropriationRegisterDisp.getBdgApprNumber()));
                    else
                        budgetApprRegUpdatedList1.add(budgetAppropriationRegisterDisp);
            }
        }
        if (budgetApprRegUpdatedList1.size() > 0) {
            budgetAppropriationRegisterList.clear();
            budgetAppropriationRegisterList.addAll(budgetApprRegUpdatedList1);
        }
        updateBdgtAppropriationList();
    }

    private String getFundQuery(final String string) {
        final String query = "";
        if (fund.getId() != null && fund.getId() != -1)
            return " and " + string + " =:fundId ";
        return query;
    }

    private String getFunctionQuery(final String string) {
        final String query = "";
        if (function.getId() != null && function.getId() != -1)
            return " and " + string + " =:functionId ";
        return query;
    }

    private String getDepartmentQuery(final String string) {
        final String query = "";
        if (department.getCode() != null && "-1".equals(department.getCode()))
            return " and " + string + " =:departmentcode ";
        return query;
    }

    public boolean isFieldMandatory(final String field) {
        return mandatoryFields.contains(field);
    }

    private void updateBdgtAppropriationList() {
        BigDecimal cumulativeAmt = null;
        BigDecimal balanceAvailableAmt = new BigDecimal(0.0);
        BigDecimal totalDebit = new BigDecimal(0.0);
        BigDecimal totalCredit = new BigDecimal(0.0);
        if (totalGrant == null)
            totalGrant = new BigDecimal(0.0);

        if (LOGGER.isInfoEnabled())
            LOGGER.info("budgetAppropriationRegisterList.size() :" + budgetAppropriationRegisterList.size());
        if (budgetAppropriationRegisterList.size() > 0) {
            int iSerialNumber = 1;
            for (final BudgetAppDisplay budgetAppropriationRegisterDisp : budgetAppropriationRegisterList) {
                if (BudgetingType.DEBIT.equals(budgetGroup.getBudgetingType()))
                    if (budgetAppropriationRegisterDisp.getDebitAmount() != null
                            && budgetAppropriationRegisterDisp.getDebitAmount().compareTo(BigDecimal.ZERO) == 1) {

                        budgetAppropriationRegisterDisp.setBillAmount(budgetAppropriationRegisterDisp.getDebitAmount());
                        totalDebit = totalDebit.add(budgetAppropriationRegisterDisp.getBillAmount());
                    } else {

                        budgetAppropriationRegisterDisp.setBillAmount(budgetAppropriationRegisterDisp.getCreditAmount().multiply(
                                new BigDecimal("-1")));
                        totalCredit = totalCredit.add(budgetAppropriationRegisterDisp.getBillAmount().abs());
                    }
                if (BudgetingType.CREDIT.equals(budgetGroup.getBudgetingType()))
                    if (budgetAppropriationRegisterDisp.getCreditAmount() != null
                            && budgetAppropriationRegisterDisp.getCreditAmount().compareTo(BigDecimal.ZERO) == 1) {

                        budgetAppropriationRegisterDisp.setBillAmount(budgetAppropriationRegisterDisp.getCreditAmount());
                        totalCredit = totalCredit.add(budgetAppropriationRegisterDisp.getBillAmount());
                    } else {

                        budgetAppropriationRegisterDisp.setBillAmount(budgetAppropriationRegisterDisp.getDebitAmount().multiply(
                                new BigDecimal("-1")));
                        totalDebit = totalDebit.add(budgetAppropriationRegisterDisp.getBillAmount().abs());
                    }
                if (BudgetingType.ALL.equals(budgetGroup.getBudgetingType()))
                    if (budgetAppropriationRegisterDisp.getDebitAmount() != null
                            && budgetAppropriationRegisterDisp.getDebitAmount().compareTo(BigDecimal.ZERO) == 1)
                        budgetAppropriationRegisterDisp.setBillAmount(budgetAppropriationRegisterDisp.getDebitAmount());
                    else
                        budgetAppropriationRegisterDisp.setBillAmount(budgetAppropriationRegisterDisp.getCreditAmount().multiply(
                                new BigDecimal("-1")));
                if (cumulativeAmt == null) {
                    if (BudgetingType.ALL.equals(budgetGroup.getBudgetingType()))
                        cumulativeAmt = budgetAppropriationRegisterDisp.getBillAmount();
                    else if (BudgetingType.CREDIT.equals(budgetGroup.getBudgetingType()))
                        cumulativeAmt = totalCredit.subtract(totalDebit);
                    else if (BudgetingType.DEBIT.equals(budgetGroup.getBudgetingType()))
                        cumulativeAmt = totalDebit.subtract(totalCredit);
                    budgetAppropriationRegisterDisp.setCumulativeAmount(cumulativeAmt);
                } else // when budgeting type is 'ALL', to calculate the cumulative balance,
                       // if the debit amount>0, add the debit amount to cumulative amount
                       // if the credit amount>0, subtract the credit amount from the cumulative amount
                if (BudgetingType.ALL.equals(budgetGroup.getBudgetingType())) {
                    if (budgetAppropriationRegisterDisp.getDebitAmount() != null
                            && budgetAppropriationRegisterDisp.getDebitAmount().compareTo(BigDecimal.ZERO) == 1) {
                        cumulativeAmt = budgetAppropriationRegisterDisp.getBillAmount().abs().add(cumulativeAmt);
                        budgetAppropriationRegisterDisp.setCumulativeAmount(cumulativeAmt);
                    } else {
                        cumulativeAmt = cumulativeAmt.subtract(budgetAppropriationRegisterDisp.getBillAmount().abs());
                        budgetAppropriationRegisterDisp.setCumulativeAmount(cumulativeAmt);
                    }
                } else if (BudgetingType.CREDIT.equals(budgetGroup.getBudgetingType())) {
                    cumulativeAmt = cumulativeAmt.add(totalCredit.subtract(totalDebit));
                    budgetAppropriationRegisterDisp.setCumulativeAmount(cumulativeAmt);
                } else if (BudgetingType.DEBIT.equals(budgetGroup.getBudgetingType())) {
                    cumulativeAmt = cumulativeAmt.add(totalDebit.subtract(totalCredit));
                    budgetAppropriationRegisterDisp.setCumulativeAmount(cumulativeAmt);
                }
                // when budgeting type is 'ALL', to calculate the running balance,
                // if the debit amount>0, subtract the cumulative from running balance
                // if the credit amount>0, add the cumulative to running balance
                if (BudgetingType.ALL.equals(budgetGroup.getBudgetingType())) {
                    if (budgetAppropriationRegisterDisp.getDebitAmount() != null
                            && budgetAppropriationRegisterDisp.getDebitAmount().compareTo(BigDecimal.ZERO) == 1)
                        balanceAvailableAmt = totalGrant.subtract(budgetAppropriationRegisterDisp.getCumulativeAmount().abs());
                    else
                        balanceAvailableAmt = totalGrant.add(budgetAppropriationRegisterDisp.getCumulativeAmount());
                } else
                    balanceAvailableAmt = totalGrant.subtract(budgetAppropriationRegisterDisp.getCumulativeAmount());
                budgetAppropriationRegisterDisp.setBalanceAvailableAmount(balanceAvailableAmt);
                budgetAppropriationRegisterDisp.setSerailNumber(Integer.toString(iSerialNumber));
                updatedBdgtAppropriationRegisterList.add(budgetAppropriationRegisterDisp);
                totalCredit = BigDecimal.ZERO;
                totalDebit = BigDecimal.ZERO;
                iSerialNumber++;
            }
        }
    }

    private BigDecimal getBudgetBEorREAmt(final String type) {
        BigDecimal approvedAmount = new BigDecimal(0.0);
        try {
            CFinancialYear financialYr = new CFinancialYear();
            financialYr = financialYearDAO.getFinancialYearByDate(dtAsOnDate);
            final CFinancialYear financialYear = financialYearDAO.getFinancialYearById(Long.valueOf(financialYr.getId()));

            List<BudgetDetail> budgedDetailList = new ArrayList<BudgetDetail>();
            
            List<Object> params = new ArrayList<>();
            StringBuilder query = new StringBuilder(" from BudgetDetail bd where bd.budget.isbere=?")
                    .append(" and bd.budgetGroup.id=? and bd.budget.financialYear.id=?");
            params.add(type);
            params.add(Long.valueOf(budgetGroup.getId()));
            params.add(Long.valueOf(financialYear.getId()));
            if (department.getCode() != null && "-1".equals(department.getCode())) {
                query.append(" and bd.executingDepartment=?");
                params.add(department.getCode());
            }
            if (function.getId() != null && function.getId() != -1) {
                query.append(" and bd.function.id=?");
                params.add(Long.valueOf(function.getId()));
            }
            if (fund.getId() != null && fund.getId() != -1) {
                query.append(" and bd.fund.id=?");
                params.add(fund.getId());
            }
            budgedDetailList = persistenceService.findAllBy(query.toString(), params.toArray());
            if (budgedDetailList != null && budgedDetailList.size() > 0)
                for (final BudgetDetail bdetail : budgedDetailList) {
                    approvedAmount = approvedAmount.add(bdetail.getApprovedAmount());
                    if ("RE".equalsIgnoreCase(type) && !getConsiderReAppropriationAsSeperate()) {
                        approvedAmount = approvedAmount.add(bdetail.getApprovedReAppropriationsTotal());
                        continue;
                    } else if ("BE".equalsIgnoreCase(type))
                        addtionalAppropriationForBe = addtionalAppropriationForBe.add(bdetail
                                .getApprovedReAppropriationsTotal());
                    else {
                        shouldShowREAppropriations = true;
                        addtionalAppropriationForRe = addtionalAppropriationForRe.add(bdetail
                                .getApprovedReAppropriationsTotal());
                    }
                }
        } catch (final ValidationException e) {
            if (LOGGER.isInfoEnabled())
                LOGGER.info("ValidationException while fetching BudgetBEorREAmt :" + e.getMessage());
            return new BigDecimal(0.0);
        }
        return approvedAmount;
    }

    private Map<String, Object> getParamMapForReportFile() {
        final Map<String, Object> paramMapForReportFile = new HashMap<String, Object>();
        paramMapForReportFile.put("bgname", budgetHead);
        paramMapForReportFile.put("deptName", department.getName());
        paramMapForReportFile.put("function", function.getName());
        paramMapForReportFile.put("fund", fund.getName());

        final String rBEorREAmountForm = " - (" + finYearRange + ") (Rs.)  : ";
        paramMapForReportFile.put("rAsOnDate", strAsOnDate);
        if (isBeDefined) {
            paramMapForReportFile.put("rBE", rBEorREAmountForm + beAmount.toString());
            paramMapForReportFile.put("rAddiApprBe", addtionalAppropriationForBe.toString());
        } else {
            paramMapForReportFile.put("rBE", rBEorREAmountForm + "Budget Not Defined ");
            paramMapForReportFile.put("rAddiApprBe", "");
        }
        if (isReDefined) {
            paramMapForReportFile.put("rRE", rBEorREAmountForm + reAmount.toString());
            paramMapForReportFile.put("rAddiApprRe", addtionalAppropriationForRe.toString());
        } else {
            paramMapForReportFile.put("rRE", rBEorREAmountForm);
            paramMapForReportFile.put("rAddiApprRe", "");
        }
        paramMapForReportFile.put("showREAppr", shouldShowREAppropriations.toString());
        return paramMapForReportFile;
    }

    @Action(value = "/report/budgetAppropriationRegisterReport-generatePdf")
    public String generatePdf() throws JRException, IOException {
        updatedBdgtAppropriationRegisterList = new ArrayList<BudgetAppDisplay>();
        search();
        final List<Object> data = new ArrayList<Object>();
        data.addAll(getUpdatedBdgtAppropriationRegisterList());
        inputStream = reportHelper.exportPdf(getInputStream(), jasperpath, getParamMapForReportFile(), data);
        return "PDF";
    }

    @Action(value = "/report/budgetAppropriationRegisterReport-generateXls")
    public String generateXls() throws JRException, IOException {
        updatedBdgtAppropriationRegisterList = new ArrayList<BudgetAppDisplay>();
        search();
        final List<Object> data = new ArrayList<Object>();
        data.addAll(getUpdatedBdgtAppropriationRegisterList());
        inputStream = reportHelper.exportXls(getInputStream(), jasperpath, getParamMapForReportFile(), data);
        return "XLS";
    }
    
    private Query setParameterForBudgetAppDisplay(Query query ,Date asOnDate,Date startDate)
    {
        if (function.getId() != null && function.getId() != -1)
        {
            query.setParameter("functionId", function.getId(), LongType.INSTANCE);
        }
        if (department.getId() != null && department.getId() != -1)
        {
            query.setParameter("departmentcode", department.getCode(), StringType.INSTANCE);
        }
        if (fund.getId() != null && fund.getId() != -1)
        {
            query.setParameter("fundId", Long.valueOf(fund.getId()), LongType.INSTANCE);
        }
        if (budgetGroup.getMinCode().getId() != null )
        {
            query.setParameter("glCodeId", budgetGroup.getMinCode().getId(), LongType.INSTANCE);
        }
        if (asOnDate != null )
        {
            query.setParameter("strAODate", asOnDate, DateType.INSTANCE);
        }
        
        if (startDate != null )
        {
            query.setParameter("strStDate", startDate, DateType.INSTANCE);
        }
        return query;
    }
    
    public String getFormattedDate(final Date date) {
        final SimpleDateFormat formatter = Constants.DDMMYYYYFORMAT1;
        return formatter.format(date);
    }


    public void setReportHelper(final ReportHelper reportHelper) {
        this.reportHelper = reportHelper;
    }

    @Override
    public String execute() throws Exception {
        return "form";
    }

    @Override
    public Object getModel() {
        return null;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(final Department department) {
        this.department = department;
    }

    public CFunction getFunction() {
        return function;
    }

    public void setFunction(final CFunction function) {
        this.function = function;
    }

    public List<BudgetAppDisplay> getBudgetAppropriationRegisterList() {
        return budgetAppropriationRegisterList;
    }

    public void setBudgetAppropriationRegisterList(
            final List<BudgetAppDisplay> budgetAppropriationRegisterList) {
        this.budgetAppropriationRegisterList = budgetAppropriationRegisterList;
    }

    public String getBudgetHead() {
        return budgetHead;
    }

    public void setBudgetHead(final String budgetHead) {
        this.budgetHead = budgetHead;
    }

    public BigDecimal getTotalGrant() {
        return totalGrant;
    }

    public void setTotalGrant(final BigDecimal totalGrant) {
        this.totalGrant = totalGrant;
    }

    public BudgetDetailsDAO getBudgetDetailsDAO() {
        return budgetDetailsDAO;
    }

    public void setBudgetDetailsDAO(final BudgetDetailsDAO budgetDetailsDAO) {
        this.budgetDetailsDAO = budgetDetailsDAO;
    }

    public BudgetGroup getBudgetGroup() {
        return budgetGroup;
    }

    public void setBudgetGroup(final BudgetGroup budgetGroup) {
        this.budgetGroup = budgetGroup;
    }

    @Override
    public PersistenceService getPersistenceService() {
        return persistenceService;
    }

    public FinancialYearDAO getFinancialYearDAO() {
        return financialYearDAO;
    }

    public void setFinancialYearDAO(final FinancialYearDAO financialYearDAO) {
        this.financialYearDAO = financialYearDAO;
    }

    public List<BudgetAppDisplay> getUpdatedBdgtAppropriationRegisterList() {
        return updatedBdgtAppropriationRegisterList;
    }

    public void setUpdatedBdgtAppropriationRegisterList(
            final List<BudgetAppDisplay> updatedBdgtAppropriationRegisterList) {
        this.updatedBdgtAppropriationRegisterList = updatedBdgtAppropriationRegisterList;
    }

    public String getStrAsOnDate() {
        return strAsOnDate;
    }

    public void setStrAsOnDate(final String strAsOnDate) {
        this.strAsOnDate = strAsOnDate;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public BigDecimal getBeAmount() {
        return beAmount;
    }

    public void setBeAmount(final BigDecimal beAmount) {
        this.beAmount = beAmount;
    }

    public BigDecimal getReAmount() {
        return reAmount;
    }

    public void setReAmount(final BigDecimal reAmount) {
        this.reAmount = reAmount;
    }

    public String getFinYearRange() {
        return finYearRange;
    }

    public void setFinYearRange(final String finYearRange) {
        this.finYearRange = finYearRange;
    }

    public void setFund(final Fund fund) {
        this.fund = fund;
    }

    public Fund getFund() {
        return fund;
    }

    public void setBudgetService(final BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    public void setAddtionalAppropriationForBe(
            final BigDecimal addtionalAppropriationForBe) {
        this.addtionalAppropriationForBe = addtionalAppropriationForBe;
    }

    public BigDecimal getAddtionalAppropriationForBe() {
        return addtionalAppropriationForBe;
    }

    public void setAddtionalAppropriationForRe(
            final BigDecimal addtionalAppropriationForRe) {
        this.addtionalAppropriationForRe = addtionalAppropriationForRe;
    }

    public BigDecimal getAddtionalAppropriationForRe() {
        return addtionalAppropriationForRe;
    }

    public boolean getIsBeDefined() {
        return isBeDefined;
    }

    public boolean getIsReDefined() {
        return isReDefined;
    }

    private boolean getConsiderReAppropriationAsSeperate() {
        final List<AppConfigValues> appList = appConfigValuesService.getConfigValuesByModuleAndKey("EGF",
                "CONSIDER_RE_REAPPROPRIATION_AS_SEPARATE");
        String appValue = "-1";
        appValue = appList.get(0).getValue();
        return "Y".equalsIgnoreCase(appValue);
    }

    public void setShouldShowREAppropriations(final boolean shouldShowREAppropriations) {
        this.shouldShowREAppropriations = shouldShowREAppropriations;
    }

    public boolean getShouldShowREAppropriations() {
        return shouldShowREAppropriations;
    }

}