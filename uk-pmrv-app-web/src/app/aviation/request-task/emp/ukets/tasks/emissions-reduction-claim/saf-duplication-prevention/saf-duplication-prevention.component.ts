import { ChangeDetectionStrategy, Component, inject, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterLinkWithHref } from '@angular/router';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { BackLinkService } from '@shared/back-link/back-link.service';
import { SharedModule } from '@shared/shared.module';

import { RequestTaskStore } from '../../../../../store';
import { TASK_FORM_PROVIDER } from '../../../../../task-form.provider';
import { ProcedureFormStepComponent } from '../../../../shared/procedure-form-step';
import { EmissionsReductionClaimFormProvider } from '../emissions-reduction-claim-form.provider';

@Component({
  selector: 'app-saf-duplication-prevention',
  standalone: true,
  imports: [SharedModule, ProcedureFormStepComponent, RouterLinkWithHref],
  templateUrl: './saf-duplication-prevention.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class SafDuplicationPreventionComponent implements OnInit, OnDestroy {
  private backLinkService = inject(BackLinkService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private pendingRequestService = inject(PendingRequestService);
  private formProvider = inject<EmissionsReductionClaimFormProvider>(TASK_FORM_PROVIDER);
  private store = inject(RequestTaskStore);

  form = this.formProvider.safDuplicationPreventionCtrl;

  ngOnInit(): void {
    this.backLinkService.show();
  }

  ngOnDestroy(): void {
    this.backLinkService.hide();
  }

  onSubmit() {
    this.store.empUkEtsDelegate
      .saveEmp({ emissionsReductionClaim: this.formProvider.getFormValue() }, 'in progress')
      .pipe(this.pendingRequestService.trackRequest())
      .subscribe(() => {
        this.store.empUkEtsDelegate.setEmissionsReductionClaim(this.formProvider.getFormValue());
        this.router.navigate(['../summary'], {
          relativeTo: this.route,
        });
      });
  }
}
