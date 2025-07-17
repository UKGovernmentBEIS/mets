import { RequestTaskDTO } from 'pmrv-api';

export const permanentCessationHeader: Partial<Record<RequestTaskDTO['type'], string>> = {
  PERMANENT_CESSATION_APPLICATION_SUBMIT: 'Permanent cessation',
  PERMANENT_CESSATION_WAIT_FOR_PEER_REVIEW: 'Permanent cessation sent to peer reviewer',
  PERMANENT_CESSATION_APPLICATION_PEER_REVIEW: 'Peer review permanent cessation',
};

export const permanentCessationExpectedTaskType: Array<RequestTaskDTO['type']> = [
  'PERMANENT_CESSATION_APPLICATION_SUBMIT',
  'PERMANENT_CESSATION_WAIT_FOR_PEER_REVIEW',
  'PERMANENT_CESSATION_APPLICATION_PEER_REVIEW',
];

export const permanentCessationWaitTasks: Array<RequestTaskDTO['type']> = ['PERMANENT_CESSATION_WAIT_FOR_PEER_REVIEW'];
