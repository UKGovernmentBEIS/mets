import { ChangeDetectionStrategy, Component } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject } from 'rxjs';

import { GovukValidators } from 'govuk-components';

import { PendingRequestService } from '../../../../core/guards/pending-request.service';
import { AerAmendGroup } from '../../core/aer.amend.types';
import { AerService } from '../../core/aer.service';

@Component({
  selector: 'app-amend',
  templateUrl: './amend.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AmendComponent {
  section = this.route.snapshot.paramMap.get('section') as AerAmendGroup;

  form: UntypedFormGroup = this.fb.group({
    changes: [
      null,
      GovukValidators.required('Check the box to confirm you have made changes and want to mark as complete'),
    ],
  });

  displayErrorSummary$ = new BehaviorSubject<boolean>(false);
  constructor(
    readonly aerService: AerService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly fb: UntypedFormBuilder,
    readonly pendingRequest: PendingRequestService,
  ) {}

  confirm() {
    if (!this.form.valid) {
      this.displayErrorSummary$.next(true);
    } else {
      this.aerService
        .postTaskSave({}, {}, true, `AMEND_${this.section}`)
        .pipe(this.pendingRequest.trackRequest())
        .subscribe(() => this.router.navigate(['summary'], { relativeTo: this.route }));
    }
  }
}
