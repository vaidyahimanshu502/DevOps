insert into state.egdcr_layername(id,key,value,createdby,createddate,lastmodifiedby,lastmodifieddate,version) select nextval('state.seq_egdcr_layername'),'LAYER_NAME_VEHICLE_RAMPFLIGHT','BLK_%s_FLR_%s_VEHICLE_RAMP_%s_FLIGHT',1,now(),1,now(),0 where not exists(select key from state.egdcr_layername where key='LAYER_NAME_VEHICLE_RAMPFLIGHT');
insert into state.egdcr_layername(id,key,value,createdby,createddate,lastmodifiedby,lastmodifieddate,version) select nextval('state.seq_egdcr_layername'),'LAYER_NAME_VEHICLE_RAMPWIDTH','BLK_%s_FLR_%s_VEHICLE_RAMP_%s_WIDTH',1,now(),1,now(),0 where not exists(select key from state.egdcr_layername where key='LAYER_NAME_VEHICLE_RAMPWIDTH');