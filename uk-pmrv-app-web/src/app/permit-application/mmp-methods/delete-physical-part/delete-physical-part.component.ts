import { ChangeDetectionStrategy, Component, computed } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { map } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';
import { SharedModule } from '@shared/shared.module';

import { DigitizedPlan } from 'pmrv-api';

@Component({
  selector: 'app-mmp-delete-physical-part',
  standalone: true,
  imports: [SharedModule, RouterLink],
  template: `
    <app-page-heading size="xl">
      Are you sure you want to delete physical part {{ physicalPartName() }}?
    </app-page-heading>

    <div class="govuk-button-group">
      <button type="button" appPendingButton (click)="delete()" govukWarnButton>Yes, delete</button>
      <a routerLink="../.." govukLink>Cancel</a>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DeletePhysicalPartComponent {
  physicalPartId = toSignal(this.route.paramMap.pipe(map((paramMap) => paramMap.get('id'))));
  permitTask = toSignal(this.route.data.pipe(map((x) => x?.permitTask)));
  state = toSignal(this.store.pipe());

  physicalPartName = computed(() => {
    const physicalParts = this.state().permit.monitoringMethodologyPlans.digitizedPlan.methodTask.connections;
    const physicalPartId = this.physicalPartId();

    return physicalParts.find((physicalPart) => physicalPart.itemId === physicalPartId).itemName;
  });

  constructor(
    private readonly pendingRequest: PendingRequestService,
    private readonly store: PermitApplicationStore<PermitApplicationState>,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
  ) {}

  delete() {
    const physicalPartId = this.physicalPartId();
    const permitTask = this.permitTask();
    const monitoringMethodologyPlans = this.state().permit.monitoringMethodologyPlans;

    this.store
      .patchTask(permitTask, {
        ...monitoringMethodologyPlans,
        digitizedPlan: {
          ...monitoringMethodologyPlans?.digitizedPlan,
          methodTask: {
            ...monitoringMethodologyPlans?.digitizedPlan.methodTask,
            connections: monitoringMethodologyPlans?.digitizedPlan.methodTask.connections.filter(
              (physicalPart) => physicalPart.itemId !== physicalPartId,
            ),
          },
        } as DigitizedPlan,
      })
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => this.router.navigate(['../../'], { relativeTo: this.route }));
  }
}
