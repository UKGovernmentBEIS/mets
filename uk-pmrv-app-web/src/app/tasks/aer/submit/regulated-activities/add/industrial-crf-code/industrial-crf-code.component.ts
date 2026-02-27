import { AfterViewInit, ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, takeUntil } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { AerService } from '@tasks/aer/core/aer.service';
import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { activityItemNameMap } from '@tasks/aer/submit/regulated-activities/add/crf-codes/crf-codes-item';
import { industrialCrfCodeFormProvider } from '@tasks/aer/submit/regulated-activities/add/industrial-crf-code/industrial-crf-code-form.provider';

@Component({
  selector: 'app-regulated-activity-industrial-crf-code',
  standalone: false,
  templateUrl: './industrial-crf-code.component.html',
  providers: [industrialCrfCodeFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class IndustrialCrfCodeComponent implements PendingRequest, AfterViewInit {
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
    if (this.form.get('industrialCrfCategory')) {
      this.form.get('industrialCrf').enable();
    }

    this.form
      .get('industrialCrfCategory')
      .valueChanges.pipe(takeUntil(this.destroy$))
      .subscribe(() => {
        if (this.form.get('industrialCrfCategory')) {
          this.form.get('industrialCrf').setValue(null);
          this.form.get('industrialCrf').enable();
        }
      });
  }

  onSubmit(): void {
    combineLatest([this.aerService.getTask('regulatedActivities'), this.route.paramMap])
      .pipe(
        first(),
        map(([regulatedActivities, paramMap]) => {
          const activity = regulatedActivities.find((activity) => activity.id === paramMap.get('activityId'));
          activity.industrialCrf = this.form.get('industrialCrf').value;

          return this.aerService
            .postTaskSave({ regulatedActivities: regulatedActivities }, {}, false, 'regulatedActivities')
            .pipe(this.pendingRequest.trackRequest())
            .subscribe(() => this.router.navigate(['../..'], { relativeTo: this.route }));
        }),
      )
      .subscribe();
  }
}
