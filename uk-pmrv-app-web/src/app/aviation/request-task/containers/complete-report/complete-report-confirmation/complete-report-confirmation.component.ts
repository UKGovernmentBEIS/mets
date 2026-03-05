import { ChangeDetectionStrategy, Component } from '@angular/core';
import { RouterLinkWithHref } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { BaseSuccessComponent } from '@shared/base-success/base-success.component';
import { SharedModule } from '@shared/shared.module';

export interface ConfirmationViewModel {
  requestId: string;
}

@Component({
  selector: 'app-complete-report-confirmation',
  templateUrl: './complete-report-confirmation.component.html',
  imports: [SharedModule, RouterLinkWithHref, ReturnToLinkComponent],
  standalone: true,
  providers: [DestroySubject],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CompleteReportConfirmationComponent extends BaseSuccessComponent {
  vm$: Observable<ConfirmationViewModel> = combineLatest([this.store.pipe(requestTaskQuery.selectRequestInfo)]).pipe(
    map(([requestInfo]) => {
      return {
        requestId: requestInfo.id,
      };
    }),
  );

  constructor(private readonly store: RequestTaskStore) {
    super();
  }
}
