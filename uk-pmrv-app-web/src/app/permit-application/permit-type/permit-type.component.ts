import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, switchMap } from 'rxjs';

import { ConfigStore } from '@core/config/config.store';
import { PendingRequestService } from '@core/guards/pending-request.service';

import { PERMIT_TASK_FORM } from '../shared/permit-task-form.token';
import { PermitApplicationState } from '../store/permit-application.state';
import { PermitApplicationStore } from '../store/permit-application.store';
import { permitTypeFormProvider } from './permit-type-form.provider';

@Component({
  selector: 'app-permit-type',
  templateUrl: './permit-type.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [permitTypeFormProvider],
})
export class PermitTypeComponent {
  readonly wastePermitEnabled$ = this.configStore
    .asObservable()
    .pipe(map((state) => state.features?.wastePermitEnabled));

  constructor(
    @Inject(PERMIT_TASK_FORM) readonly form: UntypedFormGroup,
    readonly store: PermitApplicationStore<PermitApplicationState>,
    readonly pendingRequest: PendingRequestService,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly configStore: ConfigStore,
  ) {}

  onSubmit(): void {
    combineLatest([this.store, this.route.data])
      .pipe(
        first(),
        switchMap(([state, data]) =>
          (this.store as PermitApplicationStore<PermitApplicationState>).postCategoryTask(data.permitTask, {
            ...state,
            determination: state.determination?.type !== 'DEEMED_WITHDRAWN' ? undefined : state.determination,
            permitType: this.form.get('type').value,
          }),
        ),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() => this.router.navigate(['summary'], { relativeTo: this.route }));
  }
}
