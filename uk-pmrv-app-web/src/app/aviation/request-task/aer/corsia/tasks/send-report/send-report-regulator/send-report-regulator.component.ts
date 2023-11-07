import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';

import { BehaviorSubject, combineLatest, map, Observable } from 'rxjs';

import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { AerCorsiaStoreDelegate } from '@aviation/request-task/store/delegates/aer-corsia/aer-corsia-store-delegate';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';

import { RequestInfoDTO } from 'pmrv-api';

interface ViewModel {
  header: string;
  submittedHeader: string;
  competentAuthority: RequestInfoDTO['competentAuthority'];
  requestId: string;
  isEditable: boolean;
}

@Component({
  selector: 'app-send-report-regulator',
  templateUrl: './send-report-regulator.component.html',
  imports: [SharedModule, ReturnToLinkComponent, RouterLink],
  standalone: true,
  providers: [DestroySubject],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SendReportRegulatorComponent {
  private store = inject(RequestTaskStore);
  private pendingRequestService = inject(PendingRequestService);

  isSubmitted$: BehaviorSubject<boolean> = new BehaviorSubject(false);
  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(requestTaskQuery.selectCompetentAuthority),
    this.store.pipe(requestTaskQuery.selectRequestInfo),
    this.store.pipe(requestTaskQuery.selectIsEditable),
  ]).pipe(
    map(([competentAuthority, requestInfo, isEditable]) => {
      return {
        header: 'Send report to regulator',
        submittedHeader: 'Report sent to regulator',
        competentAuthority: competentAuthority,
        requestId: requestInfo.id,
        isEditable: isEditable,
      };
    }),
  );

  onSubmit() {
    return (this.store.aerDelegate as AerCorsiaStoreDelegate)
      .submitAer('AVIATION_AER_CORSIA_SUBMIT_APPLICATION')
      .pipe(this.pendingRequestService.trackRequest())
      .subscribe(() => {
        this.isSubmitted$.next(true);
      });
  }
}
