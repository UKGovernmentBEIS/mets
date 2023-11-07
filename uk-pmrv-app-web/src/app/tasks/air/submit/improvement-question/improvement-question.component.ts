import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { AirImprovementAll } from '@shared/air-shared/types/air-improvement-all.type';
import { AIR_TASK_FORM } from '@tasks/air/shared/air-task-form.token';
import { AirService } from '@tasks/air/shared/services/air.service';
import { AirImprovementResponseService } from '@tasks/air/shared/services/air-improvement-response.service';
import { improvementQuestionFormProvider } from '@tasks/air/submit/improvement-question/improvement-question-form.provider';

@Component({
  selector: 'app-improvement-question',
  templateUrl: './improvement-question.component.html',
  providers: [improvementQuestionFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ImprovementQuestionComponent {
  reference = this.route.snapshot.paramMap.get('id');
  airImprovement = this.route.snapshot.data.airImprovement as AirImprovementAll;
  isEditable$ = this.airService.isEditable$;

  constructor(
    @Inject(AIR_TASK_FORM) readonly form: UntypedFormGroup,
    readonly pendingRequest: PendingRequestService,
    private readonly airService: AirService,
    private readonly airImprovementResponseService: AirImprovementResponseService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onSubmit() {
    const nextRoute = '../' + this.airImprovementResponseService.mapResponseTypeToPath(this.form.get('type').value);

    if (!this.form.dirty) {
      this.router.navigate([nextRoute], { relativeTo: this.route }).then();
    } else {
      this.airService.payload$
        .pipe(
          first(),
          switchMap((payload) =>
            this.airService.postAirTaskSave({
              operatorImprovementResponses: {
                ...payload?.operatorImprovementResponses,
                [this.reference]: {
                  type: this.form.get('type').value,
                },
              },
              airSectionsCompleted: {
                ...payload?.airSectionsCompleted,
                [this.reference]: false,
              },
            }),
          ),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() => this.router.navigate([nextRoute], { relativeTo: this.route }));
    }
  }
}
