import { ChangeDetectionStrategy, Component } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject } from 'rxjs';

import { GovukValidators } from 'govuk-components';

import { PendingRequestService } from '../../core/guards/pending-request.service';
import { PendingRequest } from '../../core/interfaces/pending-request.interface';
import { AmendGroup } from '../shared/types/amend.global.type';
import { PermitApplicationState } from '../store/permit-application.state';
import { PermitApplicationStore } from '../store/permit-application.store';

@Component({
  selector: 'app-amend',
  templateUrl: './amend.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AmendComponent implements PendingRequest {
  returnTo: string = this.store.getApplyForHeader();
  section = this.route.snapshot.paramMap.get('section') as AmendGroup;

  form: UntypedFormGroup = this.fb.group({
    changes: [
      null,
      GovukValidators.required('Check the box to confirm you have made changes and want to mark as complete'),
    ],
  });
  displayErrorSummary$ = new BehaviorSubject<boolean>(false);

  constructor(
    readonly pendingRequest: PendingRequestService,
    readonly store: PermitApplicationStore<PermitApplicationState>,
    private readonly fb: UntypedFormBuilder,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
  ) {}

  confirm() {
    if (!this.form.valid) {
      this.displayErrorSummary$.next(true);
    } else {
      this.store
        .postAmendStatus(this.section)
        .pipe(this.pendingRequest.trackRequest())
        .subscribe(() => this.router.navigate([`../../amend/${this.section}/summary`], { relativeTo: this.route }));
    }
  }
}
