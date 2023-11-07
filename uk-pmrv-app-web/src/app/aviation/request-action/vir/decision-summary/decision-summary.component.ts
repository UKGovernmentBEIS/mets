import { ChangeDetectionStrategy, Component } from '@angular/core';
import { RouterLink } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { RequestActionTaskComponent } from '@aviation/request-action/shared/components/request-action-task/request-action-task.component';
import { requestActionQuery, RequestActionStore } from '@aviation/request-action/store';
import { virQuery } from '@aviation/request-action/vir/vir.selectors';
import { ItemActionTypePipe } from '@shared/pipes/item-action-type.pipe';
import { SharedModule } from '@shared/shared.module';

import { AviationVirApplicationReviewedRequestActionPayload, FileInfoDTO } from 'pmrv-api';

interface ViewModel {
  pageHeader: string;
  creationDate: string;
  usersInfo: AviationVirApplicationReviewedRequestActionPayload['usersInfo'];
  officialNotice: FileInfoDTO;
  downloadUrl: string;
}

@Component({
  selector: 'app-decision-summary',
  standalone: true,
  imports: [SharedModule, RequestActionTaskComponent, RouterLink],
  templateUrl: './decision-summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DecisionSummaryComponent {
  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(virQuery.selectRequestActionPayload),
    this.store.pipe(requestActionQuery.selectCreationDate),
    this.store.pipe(requestActionQuery.selectRequestActionType),
  ]).pipe(
    map(
      ([payload, creationDate, requestActionType]) =>
        ({
          pageHeader: new ItemActionTypePipe().transform(requestActionType),
          creationDate,
          usersInfo: payload.usersInfo,
          officialNotice: payload.officialNotice,
          downloadUrl: this.store.virDelegate.baseFileDocumentDownloadUrl + '/',
        } as ViewModel),
    ),
  );

  signatory$ = this.store.pipe(
    virQuery.selectRequestActionPayload,
    map((payload) => payload.decisionNotification.signatory),
  );
  operators$ = this.store.pipe(
    virQuery.selectRequestActionPayload,
    map((payload) =>
      Object.keys(payload.usersInfo).filter((userId) => userId !== payload.decisionNotification.signatory),
    ),
  );

  constructor(public store: RequestActionStore) {}
}
