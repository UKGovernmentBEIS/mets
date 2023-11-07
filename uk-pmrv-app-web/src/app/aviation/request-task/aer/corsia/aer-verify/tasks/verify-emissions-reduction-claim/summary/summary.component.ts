import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { aerVerifyCorsiaQuery } from '@aviation/request-task/aer/corsia/aer-verify/aer-verify-corsia.selector';
import { EmissionsReductionClaimFormProvider } from '@aviation/request-task/aer/corsia/aer-verify/tasks/verify-emissions-reduction-claim/emissions-reduction-claim-form.provider';
import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { AerVerifyCorsiaStoreDelegate } from '@aviation/request-task/store/delegates/aer-verify-corsia';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { EmissionsReductionClaimCorsiaTemplateComponent } from '@aviation/shared/components/aer-verify/emissions-reduction-claim-corsia-template/emissions-reduction-claim-corsia-template.component';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';

import { AviationAerCorsiaEmissionsReductionClaimVerification } from 'pmrv-api';

interface ViewModel {
  heading: string;
  emissionsReductionClaimVerification: AviationAerCorsiaEmissionsReductionClaimVerification;
  isEditable: boolean;
  hideSubmit: boolean;
}

@Component({
  selector: 'app-summary',
  standalone: true,
  templateUrl: './summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [SharedModule, ReturnToLinkComponent, EmissionsReductionClaimCorsiaTemplateComponent],
})
export class SummaryComponent {
  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(requestTaskQuery.selectIsEditable),
    this.store.pipe(aerVerifyCorsiaQuery.selectStatusForTask('emissionsReductionClaimVerification')),
  ]).pipe(
    map(([isEditable, taskStatus]) => {
      return {
        heading: 'Check your answers',
        emissionsReductionClaimVerification: this.formProvider.getFormValue(),
        isEditable: isEditable,
        hideSubmit: !isEditable || ['complete', 'cannot start yet'].includes(taskStatus),
      };
    }),
  );

  constructor(
    @Inject(TASK_FORM_PROVIDER) private formProvider: EmissionsReductionClaimFormProvider,
    private store: RequestTaskStore,
    private pendingRequestService: PendingRequestService,
    private router: Router,
    private route: ActivatedRoute,
  ) {}

  onSubmit() {
    (this.store.aerVerifyDelegate as AerVerifyCorsiaStoreDelegate)
      .saveAerVerify({ emissionsReductionClaimVerification: this.formProvider.getFormValue() }, 'complete')
      .pipe(this.pendingRequestService.trackRequest())
      .subscribe(() =>
        this.router.navigate(['../../..'], {
          relativeTo: this.route,
        }),
      );
  }
}
