import { ChangeDetectionStrategy, Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { BackLinkService } from '@shared/back-link/back-link.service';
import { SharedModule } from '@shared/shared.module';

import { RequestTaskStore } from '../../../../../store';
import { TASK_FORM_PROVIDER } from '../../../../../task-form.provider';
import { AerEmissionsReductionClaimFormProvider } from '../emissions-reduction-claim-form.provider';

@Component({
  selector: 'app-emissions-reduction-claim-guard-page',
  standalone: true,
  imports: [SharedModule, ReturnToLinkComponent],
  templateUrl: './emissions-reduction-claim-guard-page.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EmissionsReductionClaimGuardPageComponent implements OnInit, OnDestroy {
  form = new FormGroup({
    exist: this.formProvider.existCtrl,
  });

  constructor(
    private store: RequestTaskStore,
    private router: Router,
    private route: ActivatedRoute,
    private backLinkService: BackLinkService,
    private pendingRequestService: PendingRequestService,
    @Inject(TASK_FORM_PROVIDER) protected readonly formProvider: AerEmissionsReductionClaimFormProvider,
  ) {}
  ngOnInit(): void {
    this.backLinkService.show();
  }

  ngOnDestroy(): void {
    this.backLinkService.hide();
  }

  onSubmit() {
    const formValue = this.formProvider.getFormValue();
    const value = formValue.exist ? formValue : { ...formValue, safDetails: null };
    this.store.aerDelegate
      .saveAer({ saf: value }, 'in progress')
      .pipe(this.pendingRequestService.trackRequest())
      .subscribe(() => {
        this.store.aerDelegate.setSaf(value);
        this.formProvider.setFormValue(value);

        let path =
          this.form.value.exist && this.formProvider.form.value.safDetails?.purchases?.length > 0
            ? 'list'
            : 'add-batch-item';
        if (!this.form.value.exist) path = 'summary';
        this.router.navigate([path], { relativeTo: this.route });
      });
  }
}
