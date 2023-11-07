import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';

import { BehaviorSubject, combineLatest, map, Observable, switchMap } from 'rxjs';

import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { AerCorsiaStoreDelegate } from '@aviation/request-task/store/delegates/aer-corsia/aer-corsia-store-delegate';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';

import { AccountVerificationBodyService, RequestInfoDTO } from 'pmrv-api';

interface ViewModel {
  header: string;
  submittedHeader: string;
  assignedVerifier: string;
  competentAuthority: RequestInfoDTO['competentAuthority'];
  requestId: string;
  isEditable: boolean;
}

@Component({
  selector: 'app-send-report-verifier',
  templateUrl: './send-report-verifier.component.html',
  imports: [SharedModule, ReturnToLinkComponent, RouterLink],
  standalone: true,
  providers: [DestroySubject],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SendReportVerifierComponent {
  private store = inject(RequestTaskStore);
  private pendingRequestService = inject(PendingRequestService);
  private accountVerificationBodyService = inject(AccountVerificationBodyService);

  isSubmitted$: BehaviorSubject<boolean> = new BehaviorSubject(false);
  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(
      requestTaskQuery.selectRequestInfo,
      switchMap((requestInfo) =>
        this.accountVerificationBodyService.getVerificationBodyOfAccount(requestInfo.accountId),
      ),
      map((vb) => vb.name),
    ),
    this.store.pipe(requestTaskQuery.selectCompetentAuthority),
    this.store.pipe(requestTaskQuery.selectRequestInfo),
    this.store.pipe(requestTaskQuery.selectIsEditable),
  ]).pipe(
    map(([assignedVerifier, competentAuthority, requestInfo, isEditable]) => {
      return {
        header: 'Send report for verification',
        submittedHeader: 'Report sent for verification',
        assignedVerifier: assignedVerifier,
        competentAuthority: competentAuthority,
        requestId: requestInfo.id,
        isEditable: isEditable,
      };
    }),
  );

  onSubmit() {
    return (this.store.aerDelegate as AerCorsiaStoreDelegate)
      .submitAer('AVIATION_AER_CORSIA_REQUEST_VERIFICATION')
      .pipe(this.pendingRequestService.trackRequest())
      .subscribe(() => {
        this.isSubmitted$.next(true);
      });
  }
}
