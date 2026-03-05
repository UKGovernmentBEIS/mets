import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { MaterialityLevelFormProvider } from '@aviation/request-task/aer/ukets/aer-verify/tasks/materiality-level/materiality-level-form.provider';
import { RequestTaskStore } from '@aviation/request-task/store';
import { AerVerifyUkEtsStoreDelegate } from '@aviation/request-task/store/delegates/aer-verify-ukets/aer-verify-ukets-store-delegate';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';

@Component({
  selector: 'app-materiality-level-page',
  templateUrl: './materiality-level-page.component.html',
  standalone: true,
  imports: [SharedModule, ReturnToLinkComponent],
  providers: [DestroySubject],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class MaterialityLevelPageComponent {
  protected form = new FormGroup({
    materialityDetails: this.formProvider.materialityDetailsCtrl,
  });

  constructor(
    @Inject(TASK_FORM_PROVIDER) public formProvider: MaterialityLevelFormProvider,
    private pendingRequestService: PendingRequestService,
    private router: Router,
    private route: ActivatedRoute,
    private store: RequestTaskStore,
  ) {}

  onSubmit() {
    if (this.form.invalid) return;

    (this.store.aerVerifyDelegate as AerVerifyUkEtsStoreDelegate)
      .saveAerVerify({ materialityLevel: this.formProvider.getFormValue() }, 'in progress')
      .pipe(this.pendingRequestService.trackRequest())
      .subscribe(() => {
        (this.store.aerVerifyDelegate as AerVerifyUkEtsStoreDelegate).setMaterialityLevel(
          this.formProvider.getFormValue(),
        );

        this.router.navigate(['reference-documents'], { relativeTo: this.route });
      });
  }
}
