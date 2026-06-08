import { RequestCreateActionProcessDTO } from 'pmrv-api';

export interface WorkflowLabelProperties {
  button: string;
  type: RequestCreateActionProcessDTO['requestCreateActionType'];
  errors: string[];
  content?: string;
  isHidden?: boolean;
}

export interface WorkflowLabel {
  title: string;
  properties: WorkflowLabelProperties[];
  isHidden?: boolean;
}

export type WorkflowArray = Array<Partial<WorkflowLabel>>;
