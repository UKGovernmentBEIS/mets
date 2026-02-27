import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, Observable, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';

import { DoalService } from '../../../../core/doal.service';
import { DOAL_TASK_FORM } from '../../../../core/doal-task-form.token';
import { doalActivityLevelFormProvider } from './activity-level-form.provider';

@Component({
  selector: 'app-doal-activity-level',
  standalone: false,
  templateUrl: './activity-level.component.html',
  providers: [doalActivityLevelFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DoalActivityLevelComponent {
  editable$: Observable<boolean> = this.doalService.isEditable$;
  index = this.route.snapshot.paramMap.get('index');
  createMode = this.index === null;

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
