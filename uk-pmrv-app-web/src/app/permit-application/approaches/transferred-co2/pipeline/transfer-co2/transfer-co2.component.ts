import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, Observable, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';

import { PERMIT_TASK_FORM } from '../../../../shared/permit-task-form.token';
import { PermitApplicationState } from '../../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { headingMap } from '../../heading';
import { transferFormProvider } from './transfer-co2-form.provider';

@Component({
  selector: 'app-transfer-co2',
  templateUrl: './transfer-co2.component.html',
  providers: [transferFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TransferCo2Component {
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
        switchMap((data) =>
          this.store.postTask(data.taskKey, this.form.value.proceduresForTransferredCO2AndN2O, false, data.statusKey),
        ),
        this.pendingRequest.trackRequest(),
        switchMap(() => this.store),
        first(),
      )
      .subscribe(() => this.router.navigate(['../answers'], { relativeTo: this.route }));
  }
}
