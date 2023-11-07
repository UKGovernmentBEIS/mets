import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, Observable, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { changeTypeLabelsMap, subInstallationNameLabelsMap } from '@shared/components/doal/activity-level-label.map';

import { DoalService } from '../../../../core/doal.service';
import { DOAL_TASK_FORM } from '../../../../core/doal-task-form.token';
import { activityLevelFormProvider } from './activity-level-form.provider';

@Component({
  selector: 'app-activity-level',
  templateUrl: './activity-level.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [activityLevelFormProvider],
})
export class ActivityLevelComponent {
  editable$: Observable<boolean> = this.doalService.isEditable$;
  index = this.route.snapshot.paramMap.get('index');
  createMode = this.index === null;

  readonly years = Array.from({ length: 2030 - 2021 + 1 }, (_, i) => 2021 + i).map((year) => ({
    text: `${year}`,
    value: `${year}`,
  }));

  subInstallationNameLabelsMap = subInstallationNameLabelsMap;
  changeTypeLabelsMap = changeTypeLabelsMap;

  constructor(
    @Inject(DOAL_TASK_FORM) readonly form: UntypedFormGroup,
    readonly doalService: DoalService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly pendingRequest: PendingRequestService,
  ) {}

  onSubmit(): void {
    if (!this.form.dirty) {
      this.router.navigate(['..'], { relativeTo: this.route });
    } else {
      this.doalService.payload$
        .pipe(
          first(),
          switchMap((payload) =>
            this.doalService.saveDoal(
              {
                activityLevelChangeInformation: {
                  ...payload.doal.activityLevelChangeInformation,
                  activityLevels: this.createMode
                    ? [...(payload.doal.activityLevelChangeInformation?.activityLevels ?? []), this.form.value]
                    : payload.doal.activityLevelChangeInformation?.activityLevels?.map((activityLevel, idx) =>
                        idx === Number(this.index) ? this.form.value : activityLevel,
                      ),
                },
              },
              this.route.snapshot.data.sectionKey,
              false,
            ),
          ),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() => this.router.navigate(['..'], { relativeTo: this.route }));
    }
  }
}
