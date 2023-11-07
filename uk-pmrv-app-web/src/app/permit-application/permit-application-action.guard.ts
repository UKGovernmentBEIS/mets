import { RequestActionDTO } from 'pmrv-api';

export abstract class PermitApplicationActionGuard {
  protected buildPermitApplicationState(requestAction: RequestActionDTO, userState) {
    return {
      ...(requestAction.payload as any),
      isEditable: false,
      requestActionType: requestAction.type,
      requestId: requestAction.requestId,
      requestType: requestAction.requestType,
      competentAuthority: requestAction.competentAuthority,
      isRequestTask: false,
      assignable: false,
      requestActionCreationDate: requestAction.creationDate,
      userViewRole: userState.roleType,
      requestActionSubmitter: requestAction.submitter,
    };
  }
}
