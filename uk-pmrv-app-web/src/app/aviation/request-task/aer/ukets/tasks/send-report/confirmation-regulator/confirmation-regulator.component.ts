import { ChangeDetectionStrategy, Component } from '@angular/core';
import { RouterLinkWithHref } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { BaseSuccessComponent } from '@shared/base-success/base-success.component';
import { SharedModule } from '@shared/shared.module';

export interface ConfirmationRegulatorViewModel {
  competentAuthority: 'ENGLAND' | 'NORTHERN_IRELAND' | 'OPRED' | 'SCOTLAND' | 'WALES';
  requestId: string;
  submitHidden: boolean;
}

@Component({
  selector: 'app-confirmation-regulator',
  templateUrl: './confirmation-regulator.component.html',
  imports: [SharedModule, RouterLinkWithHref, ReturnToLinkComponent],
  standalone: true,
  providers: [DestroySubject],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ConfirmationRegulatorComponent extends BaseSuccessComponent {
  vm$: Observable<ConfirmationRegulatorViewModel> = combineLatest([
    this.store.pipe(requestTaskQuery.selectRequestInfo),
    this.store.pipe(requestTaskQuery.selectCompetentAuthority),
    this.store.pipe(requestTaskQuery.selectIsEditable),
  ]).pipe(
    map(([requestInfo, authority, isEditable]) => {
      return {
        requestId: requestInfo.id,
        competentAuthority: authority,
        submitHidden: !isEditable,
      };
    }),
  );

  constructor(private readonly store: RequestTaskStore) {
    super();
  }
}
