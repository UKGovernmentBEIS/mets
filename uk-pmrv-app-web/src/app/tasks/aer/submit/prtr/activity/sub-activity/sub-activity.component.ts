import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, Observable, switchMap } from 'rxjs';

import { AerService } from '@tasks/aer/core/aer.service';
import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';

import {
  activitiesChildSection,
  activityItemNameMap,
  activityItemTypeMap,
  intermediateStepsWithSubIntermediateSteps,
} from '../../activity-item';
import { subActivityFormProvider } from './sub-activity-form.provider';

@Component({
  selector: 'app-prtr-sub-activity',
  standalone: false,
  templateUrl: './sub-activity.component.html',
  providers: [subActivityFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SubActivityComponent {
  isEditable$ = this.aerService.isEditable$;
  activityItems$: Observable<string[]> = this.route.queryParams.pipe(
    map((params) => this.activitiesChildSection[params?.activityItem]),
  );
  hasSubActivities$: Observable<boolean> = this.route.queryParams.pipe(
    map((params) => intermediateStepsWithSubIntermediateSteps.includes(params?.activityItem) ?? false),
  );

  sector$: Observable<string> = this.route.queryParams.pipe(map((params) => params.activityItem));

  activityItemNameMap = activityItemNameMap;
  activitiesChildSection = activitiesChildSection;

  constructor(
    @Inject(AER_TASK_FORM) readonly form: UntypedFormGroup,
    private readonly aerService: AerService,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
  ) {}

  onSubmit(): void {
    const activityItem = [
      this.form.get('subActivity_2_C').value,
      this.form.get('subActivity_2_E').value,
      this.form.get('subActivity_3_C').value,
      this.form.get('subActivity_4_A').value,
      this.form.get('subActivity_4_B').value,
      this.form.get('subActivity_7_A').value,
      this.form.get('subActivity_8_B').value,
      this.form.get('activity').value,
    ].find((value) => !!value);

    combineLatest([this.route.paramMap, this.aerService.getTask('prtrCodes')])
      .pipe(
        first(),
        switchMap(([paramMap, activities]) => {
          const index = Number(paramMap.get('index'));
          const activity = activityItemTypeMap[activityItem];

          return this.aerService.postTaskSave(
            {
              prtrCodes: {
                exist: true,
                codes:
                  index === (activities?.codes?.length || 0)
                    ? [...(activities?.codes ?? []), activity]
                    : activities.codes.map((item, i) => (index === i ? activity : item)),
              },
            },
            undefined,
            false,
            'prtrCodes',
          );
        }),
        first(),
      )
      .subscribe(() => {
        return this.router.navigate(['../../../summary'], { relativeTo: this.route });
      });
  }
}
