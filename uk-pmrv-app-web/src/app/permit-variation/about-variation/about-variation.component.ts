import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, switchMap } from 'rxjs';

import { PendingRequestService } from '../../core/guards/pending-request.service';
import { PERMIT_TASK_FORM } from '../../permit-application/shared/permit-task-form.token';
import { PermitVariationStore } from '../store/permit-variation.store';
import { aboutVariationFormProvider } from './about-variation-form.provider';

@Component({
  selector: 'app-about-variation',
  templateUrl: './about-variation.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [aboutVariationFormProvider],
})
export class AboutVariationComponent {
  hintText = `This will help the ${
    this.store.isVariationRegulatorLedRequest ? 'operator' : 'regulator'
  } to understand the changes being proposed and the reasons for these changes. Include any effects of the changes on the capacity of the installation or of equipment within the installation.`;

  constructor(
    readonly store: PermitVariationStore,
    @Inject(PERMIT_TASK_FORM) readonly form: FormGroup,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly pendingRequest: PendingRequestService,
  ) {}

  onContinue(): void {
    this.store
      .pipe(
        first(),
        switchMap((state) => {
          return this.store.postVariationDetails({
            ...state.permitVariationDetails,
            reason: this.form.get('reason').value,
          });
        }),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() => this.router.navigate(['changes'], { relativeTo: this.route }));
  }
}
