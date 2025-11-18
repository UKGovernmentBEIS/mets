import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, Observable, of, switchMap } from 'rxjs';

import { AerService } from '@tasks/aer/core/aer.service';
import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { activityFormProvider } from '@tasks/aer/submit/prtr/activity/activity-form.provider';
import { activitiesChildSection, activityItemNameMap, activitySections } from '@tasks/aer/submit/prtr/activity-item';

@Component({
  selector: 'app-prtr-activity',
  templateUrl: './activity.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [activityFormProvider],
})
export class ActivityComponent {
  isEditable$ = this.aerService.isEditable$;
  activityItems$: Observable<string[]> = of(activitySections);

  activityItemNameMap = activityItemNameMap;
  activitiesChildSection = activitiesChildSection;

  constructor(
    @Inject(AER_TASK_FORM) readonly form: UntypedFormGroup,
    private readonly aerService: AerService,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
  ) {}

  onSubmit(): void {
    this.route.paramMap
      .pipe(
        first(),
        switchMap((paramMap) => {
          return of({ index: Number(paramMap.get('index')) });
        }),
        first(),
      )
      .subscribe((res) => {
        return this.router.navigate(['../../activity', res.index, 'subActivity'], {
          relativeTo: this.route,
          queryParams: { activityItem: this.form.get('activity').value },
        });
      });
  }
}
