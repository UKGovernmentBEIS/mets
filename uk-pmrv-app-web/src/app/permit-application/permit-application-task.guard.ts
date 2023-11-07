export abstract class PermitApplicationTaskGuard {
  protected buildPermitApplicationState(requestTaskId, requestTaskItem, userState) {
    return {
      ...requestTaskItem.requestTask.payload,
      requestTaskId,
      requestId: requestTaskItem.requestInfo.id,
      requestType: requestTaskItem.requestInfo.type,
      isRequestTask: true,
      requestTaskType: requestTaskItem.requestTask.type,
      assignee: {
        assigneeUserId: requestTaskItem.requestTask.assigneeUserId,
        assigneeFullName: requestTaskItem.requestTask.assigneeFullName,
      },
      userAssignCapable: requestTaskItem.userAssignCapable,
      competentAuthority: requestTaskItem.requestInfo.competentAuthority,
      accountId: requestTaskItem.requestInfo.accountId,
      paymentCompleted: requestTaskItem.requestInfo.paymentCompleted,
      daysRemaining: requestTaskItem.requestTask.daysRemaining,
      assignable: requestTaskItem.requestTask.assignable,
      allowedRequestTaskActions: requestTaskItem.allowedRequestTaskActions,
      userViewRole: userState.roleType,
    };
  }
}
