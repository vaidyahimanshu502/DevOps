package org.egov.nationaldashboardingest.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
@Component
public class ApplicationProperties {

    @Value("${egov.es.host}")
    private String elasticSearchHost;

    @Value("${master.data.index}")
    private String masterDataIndex;

    @Value("${ingest.data.key.persist.topic}")
    private String keyDataTopic;

    @Value("${ingest.error.queue}")
    private String ingestErrorQueue;

    @Value("#{${module.index.mapping}}")
    private Map<String, String> moduleIndexMapping;

    @Value("#{${module.fields.mapping}}")
    private Map<String, Map<String, String>> moduleFieldsMapping;

    @Value("#{${module.allowed.groupby.fields.mapping}}")
    private Map<String, List<String>> moduleAllowedGroupByFieldsMapping;

    @Value("#{${master.module.fields.mapping}}")
    private Map<String, Map<String, String>> masterModuleFieldsMapping;

    @Value("${max.data.list.size}")
    private Long maxDataListSize;
    
    @Value("${max.data.size.kafka}")
    private Integer maxDataSizeKafka;

    @Value("${adaptor.ingest.system.role}")
    private String adaptorIngestSystemRole;
    
    @Value("#{${national.dashboard.user}}")
    private Map<String, String> nationalDashboardUser;

    @Value("#{${national.dashboard.usageTypePT}}")
    private List<String> nationalDashboardUsageTypePT;

    @Value("#{${national.dashboard.usageTypeWS}}")
    private List<String> nationalDashboardUsageTypeWS;
    
    @Value("#{${national.dashboard.usageTypeFSM}}")
    private List<String> nationalDashboardUsageTypeFSM;

    @Value("#{${national.dashboard.usageTypeNOC}}")
    private List<String> nationalDashboardUsageTypeNOC;
}