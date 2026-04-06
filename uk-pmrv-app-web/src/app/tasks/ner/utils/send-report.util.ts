import { RequestTaskDTO } from 'pmrv-api';

export const nerSendReportHeader: Partial<Record<RequestTaskDTO['type'], string>> = {
  NER_APPLICATION_SUBMIT: 'Send application for verification',
};

export const nerSendReportConfirmationTitle: Partial<Record<RequestTaskDTO['type'], string>> = {
  NER_APPLICATION_SUBMIT: 'Sent to verifier for review',
};

export const nerShowCurrentVerifierTypes: Array<RequestTaskDTO['type']> = ['NER_APPLICATION_SUBMIT'];
