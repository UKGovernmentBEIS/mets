BEGIN;

DO $$
DECLARE
    v_rows_deleted BIGINT;
    v_rows_updated BIGINT;
BEGIN

    -- REQUEST / WORKFLOW DATA

    DELETE FROM request_task_visit;
    GET DIAGNOSTICS v_rows_deleted = ROW_COUNT;
    RAISE NOTICE 'Deleted % rows from ''request_task_visit''', v_rows_deleted;

    DELETE FROM request_task;
    GET DIAGNOSTICS v_rows_deleted = ROW_COUNT;
    RAISE NOTICE 'Deleted % rows from ''request_task''', v_rows_deleted;

    DELETE FROM request_task_history;
    GET DIAGNOSTICS v_rows_deleted = ROW_COUNT;
    RAISE NOTICE 'Deleted % rows from ''request_task_history''', v_rows_deleted;

    DELETE FROM request_action;
    GET DIAGNOSTICS v_rows_deleted = ROW_COUNT;
    RAISE NOTICE 'Deleted % rows from ''request_action''', v_rows_deleted;

    DELETE FROM request_note;
    GET DIAGNOSTICS v_rows_deleted = ROW_COUNT;
    RAISE NOTICE 'Deleted % rows from ''request_note''', v_rows_deleted;

    DELETE FROM request;
    GET DIAGNOSTICS v_rows_deleted = ROW_COUNT;
    RAISE NOTICE 'Deleted % rows from ''request''', v_rows_deleted;

    -- Keep the global SYSTEM_MESSAGE_NOTIFICATION entry (account_id = 0, not a real account)
    DELETE FROM request_sequence WHERE type != 'SYSTEM_MESSAGE_NOTIFICATION';
    GET DIAGNOSTICS v_rows_deleted = ROW_COUNT;
    RAISE NOTICE 'Deleted % rows from ''request_sequence'' (SYSTEM_MESSAGE_NOTIFICATION preserved)', v_rows_deleted;

    -- ACCOUNT CHILD TABLES (all FK -> account or account sub-tables)

    DELETE FROM account_contact;
    GET DIAGNOSTICS v_rows_deleted = ROW_COUNT;
    RAISE NOTICE 'Deleted % rows from ''account_contact''', v_rows_deleted;

    -- baseline_data_report_free_allocation FK -> account_installation, not account directly
    DELETE FROM baseline_data_report_free_allocation;
    GET DIAGNOSTICS v_rows_deleted = ROW_COUNT;
    RAISE NOTICE 'Deleted % rows from ''baseline_data_report_free_allocation''', v_rows_deleted;

    DELETE FROM account_installation;
    GET DIAGNOSTICS v_rows_deleted = ROW_COUNT;
    RAISE NOTICE 'Deleted % rows from ''account_installation''', v_rows_deleted;

    -- aviation status tables both FK -> account_aviation, must precede it
    DELETE FROM account_aviation_reporting_status_history;
    GET DIAGNOSTICS v_rows_deleted = ROW_COUNT;
    RAISE NOTICE 'Deleted % rows from ''account_aviation_reporting_status_history''', v_rows_deleted;

    DELETE FROM account_aviation_reporting_status;
    GET DIAGNOSTICS v_rows_deleted = ROW_COUNT;
    RAISE NOTICE 'Deleted % rows from ''account_aviation_reporting_status''', v_rows_deleted;

    DELETE FROM account_aviation;
    GET DIAGNOSTICS v_rows_deleted = ROW_COUNT;
    RAISE NOTICE 'Deleted % rows from ''account_aviation''', v_rows_deleted;

    DELETE FROM account_note;
    GET DIAGNOSTICS v_rows_deleted = ROW_COUNT;
    RAISE NOTICE 'Deleted % rows from ''account_note''', v_rows_deleted;

    DELETE FROM account_search_additional_keyword;
    GET DIAGNOSTICS v_rows_deleted = ROW_COUNT;
    RAISE NOTICE 'Deleted % rows from ''account_search_additional_keyword''', v_rows_deleted;

    DELETE FROM account_file_attachment;
    GET DIAGNOSTICS v_rows_deleted = ROW_COUNT;
    RAISE NOTICE 'Deleted % rows from ''account_file_attachment''', v_rows_deleted;

    DELETE FROM allowance_activity_level;
    GET DIAGNOSTICS v_rows_deleted = ROW_COUNT;
    RAISE NOTICE 'Deleted % rows from ''allowance_activity_level''', v_rows_deleted;

    DELETE FROM allowance_allocation;
    GET DIAGNOSTICS v_rows_deleted = ROW_COUNT;
    RAISE NOTICE 'Deleted % rows from ''allowance_allocation''', v_rows_deleted;

    -- OPERATOR AUTHORITY CLEANUP

    DELETE FROM au_authority_permission
    WHERE authority_id IN (
        SELECT id FROM au_authority
        WHERE type = 'OPERATOR'
           OR code IN ('operator', 'operator_admin', 'consultant_agent', 'emitter_contact')
    );
    GET DIAGNOSTICS v_rows_deleted = ROW_COUNT;
    RAISE NOTICE 'Deleted % rows from ''au_authority_permission'' (operator authorities)', v_rows_deleted;

    DELETE FROM au_authority
    WHERE type = 'OPERATOR'
       OR code IN ('operator', 'operator_admin', 'consultant_agent', 'emitter_contact');
    GET DIAGNOSTICS v_rows_deleted = ROW_COUNT;
    RAISE NOTICE 'Deleted % rows from ''au_authority'' (operator authorities)', v_rows_deleted;

    DELETE FROM au_user_role_type WHERE role_type = 'OPERATOR';
    GET DIAGNOSTICS v_rows_deleted = ROW_COUNT;
    RAISE NOTICE 'Deleted % rows from ''au_user_role_type'' (operator users)', v_rows_deleted;

    -- ACCOUNT AND ITS PARENT TABLES

    DELETE FROM account;
    GET DIAGNOSTICS v_rows_deleted = ROW_COUNT;
    RAISE NOTICE 'Deleted % rows from ''account''', v_rows_deleted;

    DELETE FROM account_legal_entity;
    GET DIAGNOSTICS v_rows_deleted = ROW_COUNT;
    RAISE NOTICE 'Deleted % rows from ''account_legal_entity''', v_rows_deleted;

    DELETE FROM account_holding_company;
    GET DIAGNOSTICS v_rows_deleted = ROW_COUNT;
    RAISE NOTICE 'Deleted % rows from ''account_holding_company''', v_rows_deleted;

    DELETE FROM account_location;
    GET DIAGNOSTICS v_rows_deleted = ROW_COUNT;
    RAISE NOTICE 'Deleted % rows from ''account_location''', v_rows_deleted;

    -- PERMIT / EMP / REPORTING DATA

    DELETE FROM permit;
    GET DIAGNOSTICS v_rows_deleted = ROW_COUNT;
    RAISE NOTICE 'Deleted % rows from ''permit''', v_rows_deleted;

    -- emp is the renamed emissions_monitoring_plan table
    DELETE FROM emp;
    GET DIAGNOSTICS v_rows_deleted = ROW_COUNT;
    RAISE NOTICE 'Deleted % rows from ''emp''', v_rows_deleted;

    DELETE FROM rpt_aer;
    GET DIAGNOSTICS v_rows_deleted = ROW_COUNT;
    RAISE NOTICE 'Deleted % rows from ''rpt_aer''', v_rows_deleted;

    DELETE FROM rpt_reportable_emissions;
    GET DIAGNOSTICS v_rows_deleted = ROW_COUNT;
    RAISE NOTICE 'Deleted % rows from ''rpt_reportable_emissions''', v_rows_deleted;

    DELETE FROM aviation_rpt_aer;
    GET DIAGNOSTICS v_rows_deleted = ROW_COUNT;
    RAISE NOTICE 'Deleted % rows from ''aviation_rpt_aer''', v_rows_deleted;

    DELETE FROM aviation_rpt_reportable_emissions;
    GET DIAGNOSTICS v_rows_deleted = ROW_COUNT;
    RAISE NOTICE 'Deleted % rows from ''aviation_rpt_reportable_emissions''', v_rows_deleted;

    -- FILE TABLES

    DELETE FROM file_attachment;
    GET DIAGNOSTICS v_rows_deleted = ROW_COUNT;
    RAISE NOTICE 'Deleted % rows from ''file_attachment''', v_rows_deleted;

    DELETE FROM file_document;
    GET DIAGNOSTICS v_rows_deleted = ROW_COUNT;
    RAISE NOTICE 'Deleted % rows from ''file_document''', v_rows_deleted;

    DELETE FROM file_note;
    GET DIAGNOSTICS v_rows_deleted = ROW_COUNT;
    RAISE NOTICE 'Deleted % rows from ''file_note''', v_rows_deleted;

    -- MIGRATION AND NOTIFICATION DATA

    DELETE FROM mmp_files_migration;
    GET DIAGNOSTICS v_rows_deleted = ROW_COUNT;
    RAISE NOTICE 'Deleted % rows from ''mmp_files_migration''', v_rows_deleted;

    DELETE FROM mmp_plans_removal_log;
    GET DIAGNOSTICS v_rows_deleted = ROW_COUNT;
    RAISE NOTICE 'Deleted % rows from ''mmp_plans_removal_log''', v_rows_deleted;

    DELETE FROM notification_alert;
    GET DIAGNOSTICS v_rows_deleted = ROW_COUNT;
    RAISE NOTICE 'Deleted % rows from ''notification_alert''', v_rows_deleted;

    -- USER LOGIN / TERMS CLEANUP (users no longer in au_authority)

    DELETE FROM terms_user_version
    WHERE id NOT IN (
        SELECT user_id FROM au_authority
    );
    GET DIAGNOSTICS v_rows_deleted = ROW_COUNT;
    RAISE NOTICE 'Deleted % rows from ''terms_user_version'' (users without remaining authorities)', v_rows_deleted;

    DELETE FROM user_login_domain
    WHERE user_id NOT IN (
        SELECT user_id FROM au_authority
    );
    GET DIAGNOSTICS v_rows_deleted = ROW_COUNT;
    RAISE NOTICE 'Deleted % rows from ''user_login_domain'' (users without remaining authorities)', v_rows_deleted;

    -- RESET ACCOUNT IDENTIFIER SEQUENCE

    UPDATE account_identifier SET account_id = 1 WHERE id = 1;
    GET DIAGNOSTICS v_rows_updated = ROW_COUNT;
    RAISE NOTICE 'Reset account_identifier: updated % row(s)', v_rows_updated;

    RAISE NOTICE '===== Cleanup completed successfully =====';

EXCEPTION WHEN OTHERS THEN
    RAISE EXCEPTION 'Cleanup failed: %', SQLERRM;
END $$;

ROLLBACK;
