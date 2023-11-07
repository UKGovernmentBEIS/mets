import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormArray, UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { AerService } from '@tasks/aer/core/aer.service';
import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { virtualVisitFormProvider } from '@tasks/aer/verification-submit/opinion-statement/site-visits/virtual-visit/virtual-visit-form.provider';
import { createVisitDateControl } from '@tasks/aer/verification-submit/opinion-statement/utils/visit-dates.util';

import { OpinionStatement, VirtualSiteVisit } from 'pmrv-api';

@Component({
  selector: 'app-virtual-visit',
  templateUrl: './virtual-visit.component.html',
  providers: [virtualVisitFormProvider],
  styles: [
    `
      .container {
        position: relative;
      }

      .float-right {
        float: right;
      }
    `,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class VirtualVisitComponent implements PendingRequest {
  isEditable$ = this.aerService.isEditable$;

  constructor(
    @Inject(AER_TASK_FORM) readonly form: UntypedFormGroup,
    readonly pendingRequest: PendingRequestService,
    private readonly aerService: AerService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  get visitDatesForms(): UntypedFormArray {
    return this.form.get('visitDates') as UntypedFormArray;
  }

  addVisitDate(isEditable: boolean) {
    this.visitDatesForms.push(createVisitDateControl(isEditable, null));
    this.form.markAsDirty();
    this.form.updateValueAndValidity();
  }

  removeVisitDate(index: number) {
    this.visitDatesForms.removeAt(index);
    this.form.markAsDirty();
    this.form.updateValueAndValidity();
  }

  onSubmit(): void {
    const nextRoute = '../../summary';
    if (!this.form.dirty) {
      this.router.navigate([nextRoute], { relativeTo: this.route }).then();
    } else {
      this.aerService
        .getMappedPayload<OpinionStatement>(['verificationReport', 'opinionStatement'])
        .pipe(
          switchMap((opinionStatement) =>
            this.aerService.postVerificationTaskSave(
              {
                opinionStatement: {
                  ...opinionStatement,
                  siteVisit: {
                    siteVisitType: 'VIRTUAL',
                    ...this.form.value,
                  } as VirtualSiteVisit,
                } as OpinionStatement,
              },
              false,
              'opinionStatement',
            ),
          ),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() => this.router.navigate([nextRoute], { relativeTo: this.route }));
    }
  }
}
