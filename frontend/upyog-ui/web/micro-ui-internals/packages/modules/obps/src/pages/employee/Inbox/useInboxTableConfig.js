import React, { Fragment, useMemo } from "react"
import { Link } from "react-router-dom";
import { format } from "date-fns";
import { useTranslation } from "react-i18next";

const useInboxTableConfig = ({ parentRoute, onPageSizeChange, formState, totalCount, table, dispatch, onSortingByData}) => {
    const GetCell = (value) => <span className="cell-text styled-cell">{value}</span>;
    const GetStatusCell = (value) => value === "CS_NA" ? t(value) : value === "Active" || value>0 ? <span className="sla-cell-success">{value}</span> : <span className="sla-cell-error">{value}</span> 
    const { t } = useTranslation()
    
    const tableColumnConfig = useMemo(() => {
        return [
        {
            Header: t("BPA_APPLICATION_NUMBER_LABEL"),
            accessor: "applicationNo",
            disableSortBy: true,
            Cell: ({ row }) => {
            return (
                <div>
                <Link to={window.location.href.includes("/citizen") ? `${parentRoute}/bpa/${row.original["applicationId"]}` : `${parentRoute}/inbox/bpa/${row.original["applicationId"]}`}>
                    <span className="link">{row.original["applicationId"]}</span>
                </Link>
                </div>
            );
            },
        },
        {
            Header: t("CS_APPLICATION_DETAILS_APPLICATION_DATE"),
            accessor: "applicationDate",
            Cell: ({row}) => row.original?.["date"] ? GetCell(format(new Date(row.original?.["date"]), 'dd/MM/yyyy')) : ""
            },
        {
            Header: t("ES_INBOX_LOCALITY"),
            accessor: (row) => t(row?.locality),
            disableSortBy: true,
        },
        {
            Header: t("EVENTS_STATUS_LABEL"),
            accessor: row => row?.state ? t(`WF_${row?.businessService}_${row?.state}`) : t(`WF_${row?.businessService}_${row?.status}`),
            disableSortBy: true,
        },
        {
            Header: t("WF_INBOX_HEADER_CURRENT_OWNER"),
            accessor: (row) => t(row?.owner),
            disableSortBy: true,
        },
        {
            Header: t("BPA_SEARCH_APPLICATION_TYPE_LABEL"),
            accessor: (row) => t(row?.applicationType),
            disableSortBy: true,
        },
        {
            Header: t("ES_INBOX_SLA_DAYS_REMAINING"),
            accessor: row => GetStatusCell(row?.sla),
        }
        ]
    })

    return {
        getCellProps: (cellInfo) => {
        return {
            style: {
            padding: "20px 18px",
            fontSize: "16px"
        }}},
        disableSort: false,
        autoSort:false,
        manualPagination:true,
        initSortId:"applicationDate",
        onPageSizeChange:onPageSizeChange,
        currentPage: formState.tableForm?.offset / formState.tableForm?.limit,
        onNextPage: () => dispatch({action: "mutateTableForm", data: {...formState.tableForm , offset: (parseInt(formState.tableForm?.offset) + parseInt(formState.tableForm?.limit)) }}),
        onPrevPage: () => dispatch({action: "mutateTableForm", data: {...formState.tableForm , offset: (parseInt(formState.tableForm?.offset) - parseInt(formState.tableForm?.limit)) }}),
        pageSizeLimit: formState.tableForm?.limit,
        onSort: onSortingByData,
        // sortParams: [{id: getValues("sortBy"), desc: getValues("sortOrder") === "DESC" ? true : false}],
        totalRecords: totalCount,
        onSearch: formState?.searchForm?.message,
        onLastPage: () => dispatch({action: "mutateTableForm", data: {...formState.tableForm , offset: (Math.ceil(totalCount / 10) * 10 - parseInt(formState.tableForm?.limit)) }}),
        onFirstPage: () => dispatch({action: "mutateTableForm", data: {...formState.tableForm , offset: 0 }}),
        // globalSearch: {searchForItemsInTable},
        // searchQueryForTable,
        data: table,
        columns: tableColumnConfig
    }
}

export default useInboxTableConfig