import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, RouterLinkWithHref } from '@angular/router';

import { combineLatest, first, map, Observable, switchMap } from 'rxjs';

import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { AviationAccountViewService, RequestInfoDTO } from 'pmrv-api';

interface ViewModel {
  taskId: number;
  accountId: number;
  hasLocation: boolean;
  referenceCode: string;
}

@Component({
  selector: 'app-dre-notify-operator',
  templateUrl: './notify-operator.component.html',
  standalone: true,
  imports: [GovukComponentsModule, SharedModule, RouterLinkWithHref],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DreNotifyOperatorComponent {
  vm$: Observable<ViewModel> = combineLatest([
    this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('taskId')))),
    this.requestStore.pipe(map((state) => state?.requestTaskItem?.requestInfo?.accountId)),
    this.requestStore.pipe(requestTaskQuery.selectRequestInfo),
  ]).pipe(
    switchMap(([taskId, accountId, requestInfo]: [number, number, RequestInfoDTO]) =>
      this.accountViewService.getAviationAccountById(accountId).pipe(
        first(),
        map((account) => ({
          taskId,
          accountId,
          hasLocation: !!(account && account?.aviationAccount?.location),
          referenceCode: requestInfo.id,
        })),
      ),
    ),
  );

  readonly isForSubmission$ = this.requestStore.pipe(
    requestTaskQuery.selectRelatedActions,
    map((relatedActions) => relatedActions.includes('AVIATION_DRE_UKETS_SUBMIT_NOTIFY_OPERATOR')),
  );

  constructor(
    private readonly route: ActivatedRoute,
    private requestStore: RequestTaskStore,
    private readonly accountViewService: AviationAccountViewService,
  ) {}
}
