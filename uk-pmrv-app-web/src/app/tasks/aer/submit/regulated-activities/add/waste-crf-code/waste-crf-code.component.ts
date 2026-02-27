import { AfterViewInit, ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, takeUntil } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { AerService } from '@tasks/aer/core/aer.service';
import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';

import { activityItemNameMap } from '../crf-codes/crf-codes-item';
import { wasteCrfCodeFormProvider } from './waste-crf-code-form.provider';

@Component({
  selector: 'app-regulated-activity-waste-crf-code',
  standalone: false,
  templateUrl: './waste-crf-code.component.html',
  providers: [wasteCrfCodeFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class WasteCrfCodeComponent implements PendingRequest, AfterViewInit {
  caption$ = combineLatest([this.aerService.getTask('regulatedActivities'), this.route.paramMap]).pipe(
    map(
      ([regulatedActivities, paramMap]) =>
        regulatedActivities.find((activity) => activity.id === paramMap.get('activityId')).type,
    ),
  );
  activityItemNameMap = activityItemNameMap;

  constructor(
    @Inject(AER_TASK_FORM) readonly form: FormGroup,
    readonly aerService: AerService,
    readonly pendingRequest: PendingRequestService,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly destroy$: DestroySubject,
  ) {}

  ngAfterViewInit(): void {
    if (this.form.get('wasteCrfCategory')) {
      this.form.get('wasteCrf').enable();
    }

    this.form
      .get('wasteCrfCategory')
      .valueChanges.pipe(takeUntil(this.destroy$))
      .subscribe(() => {
        if (this.form.get('wasteCrfCategory')) {
          this.form.get('wasteCrf').setValue(null);
          this.form.get('wasteCrf').enable();
        }
      });
  }

  onSubmit(): void {
    combineLatest([this.aerService.getTask('regulatedActivities'), this.route.paramMap])
      .pipe(
        first(),
        map(([regulatedActivities, paramMap]) => {
          const activity = regulatedActivities.find((activity) => activity.id === paramMap.get('activityId'));
          activity.wasteCrf = this.form.get('wasteCrf').value;

          const nextStep = activity.hasEnergyCrf
            ? '../energy-crf-code'
            : activity.hasIndustrialCrf
              ? '../industrial-crf-code'
              : '../..';

          return this.aerService
            .postTaskSave({ regulatedActivities: regulatedActivities }, {}, false, 'regulatedActivities')
            .pipe(this.pendingRequest.trackRequest())
            .subscribe(() => this.router.navigate([nextStep], { relativeTo: this.route }));
        }),
      )
      .subscribe();
  }
}
