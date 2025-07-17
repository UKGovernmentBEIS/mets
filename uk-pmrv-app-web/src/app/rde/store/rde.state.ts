import {
  FileInfoDTO,
  RdeForceDecisionPayload,
  RdePayload,
  RdeResponsePayload,
  RequestActionDTO,
  RequestActionUserInfo,
  RequestInfoDTO,
  RequestTaskDTO,
  RequestTaskItemDTO,
} from 'pmrv-api';

export interface RdeState {
  requestTaskId: number;
  assignee: Pick<RequestTaskDTO, 'assigneeUserId' | 'assigneeFullName'>;
  actionId: number;
  accountId: number;
  requestId: string;
  paymentCompleted?: boolean;
  rdeAttachments?: { [key: string]: string };

  rdePayload: RdePayload;
  rdeResponsePayload: RdeResponsePayload;
  reason?: string;
  rdeForceDecisionPayload: RdeForceDecisionPayload;

  usersInfo?: { [key: string]: RequestActionUserInfo };
  officialDocument?: FileInfoDTO;

  daysRemaining: number;
  requestType: RequestInfoDTO['type'];
  actionType: RequestActionDTO['type'];
  competentAuthority: RequestInfoDTO['competentAuthority'];
  isEditable: boolean;
  requestActionCreationDate?: string;
  allowedRequestTaskActions?: RequestTaskItemDTO['allowedRequestTaskActions'];
  assignable?: boolean;
  userAssignCapable?: boolean;
}

export const initialState: RdeState = {
  requestId: undefined,
  requestType: undefined,
  actionType: undefined,
  competentAuthority: undefined,
  requestTaskId: undefined,
  assignee: undefined,
  actionId: undefined,
  accountId: undefined,
  rdeAttachments: {},

  rdePayload: undefined,
  rdeResponsePayload: undefined,
  rdeForceDecisionPayload: undefined,

  usersInfo: undefined,

  daysRemaining: undefined,
  isEditable: true,
};
