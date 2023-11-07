import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { aerVerifyCorsiaQuery } from '@aviation/request-task/aer/corsia/aer-verify/aer-verify-corsia.selector';
import { VerifiersConclusionsFormProvider } from '@aviation/request-task/aer/corsia/aer-verify/tasks/verifiers-conclusions/verifiers-conclusions-form.provider';
import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { AerVerifyCorsiaStoreDelegate } from '@aviation/request-task/store/delegates/aer-verify-corsia';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { VerifiersConclusionsCorsiaTemplateComponent } from '@aviation/shared/components/aer-verify/verifiers-conclusions-corsia-template/verifiers-conclusions-corsia-template.component';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';

import { AviationAerCorsiaVerifiersConclusions } from 'pmrv-api';

interface ViewModel {
  heading: string;
  verifiersConclusions: AviationAerCorsiaVerifiersConclusions;
  isEditable: boolean;
  hideSubmit: boolean;
}

@Component({
  selector: 'app-summary',
  standalone: true,
  templateUrl: './summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [SharedModule, ReturnToLinkComponent, VerifiersConclusionsCorsiaTemplateComponent],
})
export class SummaryComponent {
  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(requestTaskQuery.selectIsEditable),
    this.store.pipe(aerVerifyCorsiaQuery.selectStatusForTask('verifiersConclusions')),
  ]).pipe(
    map(([isEditable, taskStatus]) => {
      return {
        heading: 'Check your answers',
        verifiersConclusions: this.formProvider.getFormValue(),
        isEditable: isEditable,
        hideSubmit: !isEditable || ['complete', 'cannot start yet'].includes(taskStatus),
      };
    }),
  );

  constructor(
    @Inject(TASK_FORM_PROVIDER) private formProvider: VerifiersConclusionsFormProvider,
    private store: RequestTaskStore,
    private pendingRequestService: PendingRequestService,
    private router: Router,
    private route: ActivatedRoute,
  ) {}

  onSubmit() {
    (this.store.aerVerifyDelegate as AerVerifyCorsiaStoreDelegate)
      .saveAerVerify({ verifiersConclusions: this.formProvider.getFormValue() }, 'complete')
      .pipe(this.pendingRequestService.trackRequest())
      .subscribe(() =>
        this.router.navigate(['../../..'], {
          relativeTo: this.route,
        }),
      );
  }
}
