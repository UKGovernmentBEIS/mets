import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';

import { AviationAerCorsiaEmissionsReductionClaim } from 'pmrv-api';

import { EmissionsReductionClaimFormProvider } from '../emissions-reduction-claim-form.provider';
import { EmissionsReductionClaimFormComponent } from '../emissions-reduction-claim-form/emissions-reduction-claim-form.component';

@Component({
  selector: 'app-emissions-reduction-claim-page',
  standalone: true,
  imports: [SharedModule, EmissionsReductionClaimFormComponent, ReturnToLinkComponent],
  templateUrl: './emissions-reduction-claim-page.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class EmissionsReductionClaimPageComponent {
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private pendingRequestService = inject(PendingRequestService);
  private formProvider = inject<EmissionsReductionClaimFormProvider>(TASK_FORM_PROVIDER);
  private store = inject(RequestTaskStore);

  form = new FormGroup({
    exist: this.formProvider.existCtrl,
  });

  onSubmit() {
    const emissionsReductionClaim = this.getFormData();

    this.store.aerDelegate
      .saveAer({ emissionsReductionClaim: emissionsReductionClaim }, 'in progress')
      .pipe(this.pendingRequestService.trackRequest())
      .subscribe(() => {
        this.store.aerDelegate.setSaf(emissionsReductionClaim);
        this.router.navigate([this.form.value.exist ? 'upload-template' : 'summary'], {
          relativeTo: this.route,
        });
      });
  }

  getFormData() {
    const formValue = this.formProvider.getFormValue();

    if (!this.form.controls.exist.value) {
      return {
        exist: this.form.controls.exist.value,
      };
    } else {
      return {
        ...formValue,
        exist: this.form.controls.exist.value,
      } as AviationAerCorsiaEmissionsReductionClaim;
    }
  }
}
