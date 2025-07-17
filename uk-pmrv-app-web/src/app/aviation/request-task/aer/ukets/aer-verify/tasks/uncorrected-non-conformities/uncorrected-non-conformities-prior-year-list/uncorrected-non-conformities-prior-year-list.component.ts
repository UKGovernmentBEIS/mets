import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { AerVerifyUkEtsStoreDelegate } from '@aviation/request-task/store/delegates/aer-verify-ukets/aer-verify-ukets-store-delegate';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { VerifierCommentGroupComponent } from '@aviation/shared/components/aer-verify/verifier-comment-group/verifier-comment-group.component';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { VerifierComment } from 'pmrv-api';

import { UncorrectedNonConformitiesFormProvider } from '../uncorrected-non-conformities-form.provider';

interface ViewModel {
  isEditable: boolean;
  verifierComments: VerifierComment[];
}

@Component({
  selector: 'app-uncorrected-non-conformities-prior-year-list',
  templateUrl: './uncorrected-non-conformities-prior-year-list.component.html',
  standalone: true,
  providers: [DestroySubject],
  imports: [GovukComponentsModule, SharedModule, RouterLink, VerifierCommentGroupComponent, ReturnToLinkComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class UncorrectedNonConformitiesPriorYearListComponent {
  vm$: Observable<ViewModel> = combineLatest([this.store.pipe(requestTaskQuery.selectIsEditable)]).pipe(
    map(([isEditable]) => ({
      isEditable,
      verifierComments: this.formProvider.priorYearIssuesGroup.value.priorYearIssues,
    })),
  );

  constructor(
    @Inject(TASK_FORM_PROVIDER) public formProvider: UncorrectedNonConformitiesFormProvider,
    readonly pendingRequestService: PendingRequestService,
    private router: Router,
    private route: ActivatedRoute,
    private store: RequestTaskStore,
  ) {}

  onSubmit() {
    (this.store.aerVerifyDelegate as AerVerifyUkEtsStoreDelegate)
      .saveAerVerify({ uncorrectedNonConformities: this.formProvider.getFormValue() }, 'in progress')
      .pipe(this.pendingRequestService.trackRequest())
      .subscribe(() => {
        (this.store.aerVerifyDelegate as AerVerifyUkEtsStoreDelegate).setUncorrectedNonConformities(
          this.formProvider.getFormValue(),
        );
        this.router.navigate(['..', 'summary'], { relativeTo: this.route });
      });
  }
}
