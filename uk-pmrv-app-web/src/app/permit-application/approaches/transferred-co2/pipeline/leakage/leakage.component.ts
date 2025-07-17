import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, Observable, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';

import { PERMIT_TASK_FORM } from '../../../../shared/permit-task-form.token';
import { PermitApplicationState } from '../../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { headingMap } from '../../heading';
import { leakageFormProvider } from './leakage-form.provider';

@Component({
  selector: 'app-leakage',
  templateUrl: './leakage.component.html',
  providers: [leakageFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class LeakageComponent {
  taskKey$: Observable<string> = this.route.data.pipe(map((x) => x?.taskKey));
  headingMap = headingMap;

  constructor(
    @Inject(PERMIT_TASK_FORM) readonly form: UntypedFormGroup,
    readonly store: PermitApplicationStore<PermitApplicationState>,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly pendingRequest: PendingRequestService,
  ) {}

  onContinue(): void {
    this.route.data
      .pipe(
        first(),
        switchMap((data) => this.store.postTask(data.taskKey, this.form.value, false, data.statusKey)),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() => this.router.navigate(['../temperature'], { relativeTo: this.route }));
  }
}
