ALTER TABLE EDCR_APPLICATION DROP COLUMN dcrnumber;

ALTER TABLE EDCR_DOCUMENT RENAME TO EDCR_APPLICATION_DETAIL;
ALTER TABLE EDCR_APPLICATION_DETAIL ADD COLUMN dcrnumber CHARACTER VARYING (128);

ALTER SEQUENCE SEQ_EDCR_DOCUMENT RENAME TO SEQ_EDCR_APPLICATION_DETAIL ;