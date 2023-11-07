import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormArray, UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { AerService } from '@tasks/aer/core/aer.service';
import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { inPersonVisitFormProvider } from '@tasks/aer/verification-submit/opinion-statement/site-visits/in-person-visit/in-person-visit-form.provider';
import { createVisitDateControl } from '@tasks/aer/verification-submit/opinion-statement/utils/visit-dates.util';

import { InPersonSiteVisit, OpinionStatement } from 'pmrv-api';

@Component({
  selector: 'app-in-person-visit',
  templateUrl: './in-person-visit.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
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
  providers: [inPersonVisitFormProvider],
})
export class InPersonVisitComponent implements PendingRequest {
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
                    siteVisitType: 'IN_PERSON',
                    ...this.form.value,
                  } as InPersonSiteVisit,
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
