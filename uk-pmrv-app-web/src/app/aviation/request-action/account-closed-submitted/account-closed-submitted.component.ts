import { ChangeDetectionStrategy, Component, OnDestroy, OnInit } from '@angular/core';

import { combineLatest, map, Observable } from 'rxjs';

import { BackLinkService } from '@shared/back-link/back-link.service';
import { SharedModule } from '@shared/shared.module';

import { AviationAccountClosure, RequestActionDTO } from 'pmrv-api';

import { RequestActionTaskComponent } from '../shared/components/request-action-task/request-action-task.component';
import { AccountClosureRequestActionPayload, requestActionQuery, RequestActionStore } from '../store';
import { getRequestActionHeader } from '../util';

interface ViewModel {
  requestActionType: RequestActionDTO['type'];
  pageHeader: string;
  creationDate: string;
  data: AviationAccountClosure;
  submitter: string;
}

@Component({
  selector: 'app-account-closed-submitted',
  standalone: true,
  imports: [RequestActionTaskComponent, SharedModule],
  templateUrl: './account-closed-submitted.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AccountClosedSubmittedComponent implements OnInit, OnDestroy {
  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(requestActionQuery.selectRequestActionItem),
    this.store.pipe(requestActionQuery.selectCreationDate),
    this.store.pipe(requestActionQuery.selectRequestActionType),
  ]).pipe(
    map(([requestAction, creationDate, requestActionType]) => ({
      requestActionType: requestActionType,
      pageHeader: getRequestActionHeader(requestAction.type),
      creationDate: creationDate,
      submitter: requestAction.submitter,
      data: (requestAction.payload as AccountClosureRequestActionPayload).aviationAccountClosure,
    })),
  );

  constructor(private readonly store: RequestActionStore, private readonly backLinkService: BackLinkService) {}

  ngOnInit(): void {
    this.backLinkService.show();
  }

  ngOnDestroy(): void {
    this.backLinkService.hide();
  }
}
