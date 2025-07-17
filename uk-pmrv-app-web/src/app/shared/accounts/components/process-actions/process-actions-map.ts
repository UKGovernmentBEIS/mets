import { RequestCreateActionProcessDTO } from 'pmrv-api';

export interface WorkflowLabelProperties {
  button: string;
  type: RequestCreateActionProcessDTO['requestCreateActionType'];
  errors: string[];
}

export interface WorkflowLabel {
  title: string;
  properties: WorkflowLabelProperties[];
}

export type WorkflowArray = Array<Partial<WorkflowLabel>>;
