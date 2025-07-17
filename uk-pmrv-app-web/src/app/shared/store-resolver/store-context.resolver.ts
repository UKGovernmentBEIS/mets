import { Injectable, Injector } from '@angular/core';

import { map, Observable } from 'rxjs';

import { CommonActionsStore } from '@actions/store/common-actions.store';
import { RequestActionStore } from '@aviation/request-action/store';
import { RequestTaskStore } from '@aviation/request-task/store';
import { Store } from '@core/store/store';
import { PermitRevocationStore } from '@permit-revocation/store/permit-revocation-store';
import { UrlRequestType } from '@shared/types/url-request-type';

import { RequestTaskActionProcessDTO, RequestTaskDTO } from 'pmrv-api';

import { PermitIssuanceStore } from '../../permit-issuance/store/permit-issuance.store';
import { PermitSurrenderStore } from '../../permit-surrender/store/permit-surrender.store';
import { PermitTransferStore } from '../../permit-transfer/store/permit-transfer.store';
import { PermitVariationStore } from '../../permit-variation/store/permit-variation.store';
import { RdeStore } from '../../rde/store/rde.store';
import { RfiStore } from '../../rfi/store/rfi.store';
import { CommonTasksStore } from '../../tasks/store/common-tasks.store';

@Injectable({
  providedIn: 'root',
})
export class StoreContextResolver {
  constructor(private injector: Injector) {}

  getStore(path: UrlRequestType, isAction?: boolean): Store<any> {
    switch (path) {
      case 'rde':
        return <RdeStore>this.injector.get(RdeStore);
      case 'rfi':
        return <RfiStore>this.injector.get(RfiStore);
      case 'permit-issuance':
        return <PermitIssuanceStore>this.injector.get(PermitIssuanceStore);
      case 'permit-variation':
        return <PermitVariationStore>this.injector.get(PermitVariationStore);
      case 'permit-surrender':
        return <PermitSurrenderStore>this.injector.get(PermitSurrenderStore);
      case 'permit-revocation':
        return <PermitRevocationStore>this.injector.get(PermitRevocationStore);
      case 'permit-transfer':
        return <PermitTransferStore>this.injector.get(PermitTransferStore);
      case 'aviation':
        return isAction
          ? <RequestActionStore>this.injector.get(RequestActionStore)
          : <RequestTaskStore>this.injector.get(RequestTaskStore);
      default:
        return isAction
          ? <CommonActionsStore>this.injector.get(CommonActionsStore)
          : <CommonTasksStore>this.injector.get(CommonTasksStore);
    }
  }

  getRequestTaskType(path: UrlRequestType): Observable<RequestTaskDTO['type']> {
    switch (path) {
      case 'permit-issuance':
        return (<PermitIssuanceStore>this.injector.get(PermitIssuanceStore)).pipe(
          map((state) => state.requestTaskType),
        );
      case 'permit-variation':
        return (<PermitVariationStore>this.injector.get(PermitVariationStore)).pipe(
          map((state) => state.requestTaskType),
        );
      case 'permit-surrender':
        return (<PermitSurrenderStore>this.injector.get(PermitSurrenderStore)).pipe(
          map((state) => state.requestTaskType),
        );
      case 'permit-revocation':
        return (<PermitRevocationStore>this.injector.get(PermitRevocationStore)).pipe(
          map((state) => state.requestTaskType),
        );
      case 'permit-transfer':
        return (<PermitTransferStore>this.injector.get(PermitTransferStore)).pipe(
          map((state) => state.requestTaskType),
        );
      case 'aviation':
        return (<RequestTaskStore>this.injector.get(RequestTaskStore)).pipe(
          map((state) => state.requestTaskItem.requestTask.type),
        );
      default:
        return (<CommonTasksStore>this.injector.get(CommonTasksStore)).pipe(
          map((state) => state.requestTaskItem.requestTask.type),
        );
    }
  }

  getRequestId(path: UrlRequestType): Observable<string> {
    switch (path) {
      case 'permit-issuance':
        return (<PermitIssuanceStore>this.injector.get(PermitIssuanceStore)).pipe(map((state) => state.requestId));
      case 'permit-variation':
        return (<PermitVariationStore>this.injector.get(PermitVariationStore)).pipe(map((state) => state.requestId));
      case 'permit-surrender':
        return (<PermitSurrenderStore>this.injector.get(PermitSurrenderStore)).pipe(map((state) => state.requestId));
      case 'permit-revocation':
        return (<PermitRevocationStore>this.injector.get(PermitRevocationStore)).pipe(map((state) => state.requestId));
      case 'permit-transfer':
        return (<PermitTransferStore>this.injector.get(PermitTransferStore)).pipe(map((state) => state.requestId));
      case 'aviation':
        return (<RequestTaskStore>this.injector.get(RequestTaskStore)).pipe(
          map((state) => state.requestTaskItem.requestInfo.id),
        );
      default:
        return (<CommonTasksStore>this.injector.get(CommonTasksStore)).pipe(
          map((state) => state.requestTaskItem.requestInfo.id),
        );
    }
  }

  getAllowedRequestTaskActions(
    path: UrlRequestType,
  ): Observable<RequestTaskActionProcessDTO['requestTaskActionType'][]> {
    switch (path) {
      case 'permit-issuance':
        return (<PermitIssuanceStore>this.injector.get(PermitIssuanceStore)).pipe(
          map((state) => state.allowedRequestTaskActions),
        );
      case 'permit-variation':
        return (<PermitVariationStore>this.injector.get(PermitVariationStore)).pipe(
          map((state) => state.allowedRequestTaskActions),
        );
      case 'permit-surrender':
        return (<PermitSurrenderStore>this.injector.get(PermitSurrenderStore)).pipe(
          map((state) => state.allowedRequestTaskActions),
        );
      case 'permit-revocation':
        return (<PermitRevocationStore>this.injector.get(PermitRevocationStore)).pipe(
          map((state) => state.allowedRequestTaskActions),
        );
      case 'permit-transfer':
        return (<PermitTransferStore>this.injector.get(PermitTransferStore)).pipe(
          map((state) => state.allowedRequestTaskActions),
        );
      case 'aviation':
        return (<RequestTaskStore>this.injector.get(RequestTaskStore)).pipe(
          map((state) => state.requestTaskItem.allowedRequestTaskActions),
        );
      default:
        return (<CommonTasksStore>this.injector.get(CommonTasksStore)).pipe(
          map((state) => state.requestTaskItem.allowedRequestTaskActions),
        );
    }
  }
}
