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
/*
 * Created on Jul 6, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.exilant.eGov.src.common;


import com.exilant.exility.common.AbstractTask;
import com.exilant.exility.common.DataCollection;
import com.exilant.exility.common.TaskFailedException;
import org.apache.log4j.Logger;
import org.egov.infstr.services.PersistenceService;
import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.util.List;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
@Transactional(readOnly = true)
public class LoadSubLedgerSalaryData extends AbstractTask {
 @Autowired
 @Qualifier("persistenceService")
 private PersistenceService persistenceService;

    private final static Logger LOGGER = Logger.getLogger(LoadSubLedgerSalaryData.class);
    private static TaskFailedException taskExc;

    @Override
    public void execute(final String taskName,
            final String gridName, final DataCollection dc,
            final Connection con,
            final boolean errorOnNoData,
            final boolean gridHasColumnHeading, final String prefix) throws TaskFailedException
    {
        int noOfRec = 0;
        List<Object[]> rset = null;
        Query pst = null;
        final String cgn = dc.getValue("drillDownCgn");
        try {
            String mmonth = "", chequeId = "";

			StringBuilder sql = new StringBuilder(
					"select sbd.mmonth as \"salaryBillDetail_mmonth\" ,vh.fundid as \"fund_id\",").append(
							"vh.fundSourceid as \"fundSource_id\",sph.chequeid from salarybilldetail sbd,voucherheader  vh,")
							.append("subledgerpaymentheader sph ")
							.append(" where  sph.salarybillid=sbd.id and sph.voucherheaderid=vh.id and vh.cgn= :cgn");
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(sql);
			pst = persistenceService.getSession().createSQLQuery(sql.toString());
			pst.setParameter("cgn", cgn);
			rset = pst.list();
            for (final Object[] element : rset) {
                mmonth = element[0].toString();
                chequeId = element[3].toString();
            }
            if (chequeId == null || chequeId.equals("0"))
                dc.addValue("subLedgerPaymentHeader_typeOfPayment", "Cash");
            else
                dc.addValue("subLedgerPaymentHeader_typeOfPayment", "Cheque");
			sql = new StringBuilder("select  paidby as \"subLedgerPaymentHeader_paidBy\",bankaccountid as \"accId\", ")
					.append(" f.id as \"fund_id\", ").append(" fs.id as \"fundSource_id\" ,")
					.append(" paidto as \"chequeDetail_payTo\" ,").append(" from subledgerpaymentheader")
					.append(" sph,voucherheader  vh ,fund f,fundSource fs where ")
					.append(" sph.voucherheaderid=vh.id  and f.id=vh.fundid and fs.id=vh.fundSourceid and vh.cgn= :cgn");
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(sql);
			pst = persistenceService.getSession().createSQLQuery(sql.toString());
			pst.setParameter("cgn", cgn);
			rset = pst.list();
            for (final Object[] element : rset) {
                dc.addValue("subLedgerPaymentHeader_paidBy", element[0].toString());
                dc.addValue("accId", element[1].toString());
                dc.addValue("fund_id", element[2].toString());
                dc.addValue("fundSource_id", element[3].toString());
                dc.addValue("chequeDetail_payTo", element[4].toString());
            }

			sql = new StringBuilder(
					"select a.name as \"subLedgerPaymentHeader_paidBy\",c.glcode as \"billCollector_chequeInHandDesc\",")
							.append("b.glcode as \"billCollector_cashInHandDesc\" from billcollector a,chartofaccounts b,chartofaccounts c")
							.append(" where a.cashinhand=b.id and a.chequeinhand=c.id and b.id!=c.id and a.id= :paidBy");
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(sql);
			pst = persistenceService.getSession().createSQLQuery(sql.toString());
			pst.setParameter("paidBy", dc.getValue("subLedgerPaymentHeader_paidBy"));
			rset = pst.list();
            for (final Object[] element : rset) {
                dc.addValue("subLedgerPaymentHeader_paidBy", element[0].toString());
                dc.addValue("billCollector_chequeInHandDesc", element[1].toString());
                dc.addValue("billCollector_cashInHandDesc", element[2].toString());
            }

            dc.addValue("salaryBillDetail_mmonth", mmonth);

			sql = new StringBuilder("select  b.id as \"subLedgerPaymentHeader_bankId\"")
					.append(" from bank a ,bankbranch b, bankaccount c  where")
					.append(" a.id=b.bankid and b.id=c.branchid and c.id = :accId");
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(sql);
			pst = persistenceService.getSession().createSQLQuery(sql.toString());
			pst.setParameter("accId", dc.getValue("accId"));
			rset = pst.list();
            for (final Object[] element : rset)
                dc.addValue("subLedgerPaymentHeader_bankId", element[0].toString());
            dc.addValue("subLedgerPaymentHeader_branchAccountId", dc.getValue("accId"));

			sql = new StringBuilder(
					" select count(*) from salaryBillDetail s,voucherHeader v, subledgerpaymentheader sph ")
							.append(" where v.id=s.voucherHeaderId AND ")
							.append(" sph.salarybillid=s.id and sph.voucherheaderid in(select id from voucherheader where cgn = :cgn)");
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(sql);
			pst = persistenceService.getSession().createSQLQuery(sql.toString());
			pst.setParameter("cgn", cgn);
			rset = pst.list();
            for (final Object[] element : rset)
                noOfRec = Integer.parseInt(element[0].toString());

            if (noOfRec > 0) {
                final String[][] grid = new String[noOfRec + 1][7];
				sql = new StringBuilder("select s.id as \"salaryBillDetail_id\",")
						.append(" v.id as \"voucherHeader_id\",v.voucherNumber as \"voucherHeader_voucherNumber1\",")
						.append(" to_char(v.voucherdate,'dd-mon-yyyy') as \"voucherHeader_voucherDate1\",")
						.append("s.grossPay as \"salaryBillDetail_grossPay\",")
						.append(" s.totalDeductions as \"salaryBillDetail_totalDed\",s.netPay as \"salaryBillDetail_netPay\" ")
						.append(" from salaryBillDetail s,voucherHeader v, subledgerpaymentheader sph")
						.append(" where  v.id=s.voucherHeaderId AND sph.salarybillid=s.id")
						.append(" and sph.voucherheaderid in (select id from voucherheader where cgn = :cgn) ");
				if (LOGGER.isDebugEnabled())
					LOGGER.debug(sql);
				pst = persistenceService.getSession().createSQLQuery(sql.toString());
				pst.setParameter("cgn", cgn);
				rset = pst.list();

                for (final Object[] element : rset) {
                    grid[0][0] = element[0].toString();
                    grid[0][1] = element[1].toString();
                    grid[0][2] = element[2].toString();
                    grid[0][3] = element[3].toString();
                    grid[0][4] = element[4].toString();
                    grid[0][5] = element[5].toString();
                    grid[0][6] = element[6].toString();
                }
                int idx = 1;// grid[from 1][x] we start filling data
                for (final Object[] element : rset) {
                    grid[idx][0] = element[0].toString();
                    grid[idx][1] = element[1].toString();
                    grid[idx][2] = element[2].toString();
                    grid[idx][3] = element[3].toString();
                    grid[idx][4] = element[4].toString();
                    grid[idx][5] = element[5].toString();
                    grid[idx][6] = element[6].toString();
                    idx++;
                }
                // rset.close();
                dc.addGrid(gridName, grid);
            }
			sql = new StringBuilder(
					"select cgn as \"voucherHeader_cgn\",vouchernumber as \"voucherHeader_voucherNumber\",")
							.append("to_char(voucherdate,'dd-mon-yyyy') as \"voucherHeader_voucherDate\",")
							.append(" chequenumber as \"chequeDetail_chequeNumber\" ,to_char(chequedate,'dd-mon-yyyy')")
							.append(" as \"chequeDetail_chequeDate\",vh.description as \"narration\"")
							.append(" from voucherheader vh,subledgerpaymentheader sph,chequedetail cq where")
							.append(" sph.voucherheaderid=vh.id  and cq.id=sph.chequeid")
							.append(" and chequeid is not null and chequeid>0 and vh.cgn= :cgn").append(" union ")
							.append(" select cgn as \"voucherHeader_cgn\",vouchernumber as \"voucherHeader_voucherNumber\",")
							.append("to_char(voucherdate,'dd-mon-yyyy') as \"voucherHeader_voucherDate\",'','',vh.description as \"narration\"")
							.append(" from voucherheader vh,subledgerpaymentheader sph  where")
							.append(" sph.voucherheaderid=vh.id and (chequeid is  null or chequeid =0 )and vh.cgn = :cgn");
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(sql);
			pst = persistenceService.getSession().createSQLQuery(sql.toString());
			pst.setParameter("cgn", cgn);
			rset = pst.list();
            for (final Object[] element : rset) {
                dc.addValue("voucherHeader_cgn", element[0].toString());
                dc.addValue("voucherHeader_voucherNumber", element[1].toString());
                dc.addValue("voucherHeader_voucherDate", element[2].toString());
                dc.addValue("chequeDetail_chequeNumber", element[3].toString());
                dc.addValue("chequeDetail_chequeDate", element[4].toString());
                dc.addValue("subLedgerPaymentHeader_narration", element[5].toString());
            }

        } catch (final Exception e) {
            LOGGER.error("exilError" + e.getMessage());
            throw taskExc;
        }
    }
}