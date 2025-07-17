import { InjectionToken } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';

import {
  InstallationAuditApplicationSaveRequestTaskActionPayload,
  InstallationAuditApplicationSubmitRequestTaskPayload,
  InstallationOnsiteInspectionApplicationSaveRequestTaskActionPayload,
  InstallationOnsiteInspectionApplicationSubmitRequestTaskPayload,
  RequestTaskActionProcessDTO,
  RequestTaskDTO,
} from 'pmrv-api';

export const INSPECTION_TASK_FORM = new InjectionToken<UntypedFormGroup>('Inspection task form');

export const notifyActionTypeMap: Partial<
  Record<RequestTaskDTO['type'], RequestTaskActionProcessDTO['requestTaskActionType']>
> = {
  INSTALLATION_AUDIT_APPLICATION_SUBMIT: 'INSTALLATION_AUDIT_SUBMIT_NOTIFY_OPERATOR',
  INSTALLATION_ONSITE_INSPECTION_APPLICATION_SUBMIT: 'INSTALLATION_ONSITE_INSPECTION_SUBMIT_NOTIFY_OPERATOR',
};

export const peerReviewActionTypeMap: Partial<
  Record<RequestTaskDTO['type'], RequestTaskActionProcessDTO['requestTaskActionType']>
> = {
  INSTALLATION_ONSITE_INSPECTION_APPLICATION_PEER_REVIEW: 'INSTALLATION_ONSITE_INSPECTION_SUBMIT_PEER_REVIEW_DECISION',
  INSTALLATION_AUDIT_APPLICATION_PEER_REVIEW: 'INSTALLATION_AUDIT_SUBMIT_PEER_REVIEW_DECISION',
};

export type InspectionType = 'audit' | 'onsite';

export type InspectionSaveRequestTaskActionPayload =
  | InstallationAuditApplicationSaveRequestTaskActionPayload
  | InstallationOnsiteInspectionApplicationSaveRequestTaskActionPayload;

export type InspectionSubmitRequestTaskPayload =
  | InstallationAuditApplicationSubmitRequestTaskPayload
  | InstallationOnsiteInspectionApplicationSubmitRequestTaskPayload;
