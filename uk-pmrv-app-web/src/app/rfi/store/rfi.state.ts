import {
  FileInfoDTO,
  RequestActionDTO,
  RequestActionUserInfo,
  RequestInfoDTO,
  RequestTaskDTO,
  RequestTaskItemDTO,
  RfiQuestionPayload,
  RfiResponsePayload,
  RfiSubmitPayload,
} from 'pmrv-api';

export interface RfiState {
  requestTaskId: number;
  requestTaskType: RequestTaskDTO['type'];
  assignee: Pick<RequestTaskDTO, 'assigneeUserId' | 'assigneeFullName'>;
  actionId: number;
  actionType: RequestActionDTO['type'];
  accountId: number;
  paymentCompleted?: boolean;
  rfiAttachments?: { [key: string]: string };
  officialDocument?: FileInfoDTO;
  isEditable: boolean;
  assignable?: boolean;
  usersInfo?: { [key: string]: RequestActionUserInfo };
  rfiSubmitPayload: RfiSubmitPayload;
  rfiQuestionPayload: RfiQuestionPayload;
  rfiResponsePayload: RfiResponsePayload;
  daysRemaining: number;
  requestId: string;
  requestType: RequestInfoDTO['type'];
  competentAuthority: RequestInfoDTO['competentAuthority'];
  allowedRequestTaskActions?: RequestTaskItemDTO['allowedRequestTaskActions'];
  requestActionCreationDate?: string;
  userAssignCapable?: boolean;
}

export const initialState: RfiState = {
  requestId: undefined,
  requestTaskType: undefined,
  requestType: undefined,
  competentAuthority: undefined,
  requestTaskId: undefined,
  assignee: undefined,
  actionId: undefined,
  actionType: undefined,
  accountId: undefined,
  rfiAttachments: {},
  isEditable: true,
  usersInfo: undefined,
  rfiSubmitPayload: undefined,
  rfiQuestionPayload: undefined,
  rfiResponsePayload: undefined,
  daysRemaining: undefined,
};
