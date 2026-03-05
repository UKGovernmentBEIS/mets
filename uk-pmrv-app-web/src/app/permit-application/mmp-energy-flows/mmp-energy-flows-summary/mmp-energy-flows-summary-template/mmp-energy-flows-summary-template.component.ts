import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { SharedPermitModule } from '@permit-application/shared/shared-permit.module';
import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';
import { SharedModule } from '@shared/shared.module';

import { EnergyFlows } from 'pmrv-api';

interface ViewModel {
  fuelInputFlowsSupportingFiles: string[];
  measurableHeatFlowsSupportingFiles: string[];
  wasteGasFlowsSupportingFiles: string[];
  electricityFlowsSupportingFiles: string[];
}

@Component({
  selector: 'app-mmp-energy-flows-summary-template',
  templateUrl: './mmp-energy-flows-summary-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: true,
  imports: [SharedModule, SharedPermitModule, RouterModule],
})
export class MmpEnergyFlowsSummaryTemplateComponent {
  @Input() showOriginal = false;
  @Input() isPreview: boolean;
  @Input() hasBottomBorder = true;

  readonly vm$: Observable<ViewModel> = combineLatest([this.store, this.route.paramMap]).pipe(
    map(([state]) => {
      const originalPermitContainerEnergyFlows: EnergyFlows =
        (state as any)?.originalPermitContainer?.permit?.monitoringMethodologyPlans?.digitizedPlan?.energyFlows ?? null;

      const energyFlows = state.permit.monitoringMethodologyPlans?.digitizedPlan?.energyFlows ?? null;

      return this.showOriginal
        ? {
            fuelInputFlowsSupportingFiles: originalPermitContainerEnergyFlows?.fuelInputFlows?.supportingFiles ?? [],
            measurableHeatFlowsSupportingFiles:
              originalPermitContainerEnergyFlows?.measurableHeatFlows?.supportingFiles ?? [],
            wasteGasFlowsSupportingFiles: originalPermitContainerEnergyFlows?.wasteGasFlows?.supportingFiles ?? [],
            electricityFlowsSupportingFiles:
              originalPermitContainerEnergyFlows?.electricityFlows?.supportingFiles ?? [],
          }
        : {
            fuelInputFlowsSupportingFiles: energyFlows?.fuelInputFlows?.supportingFiles ?? [],
            measurableHeatFlowsSupportingFiles: energyFlows?.measurableHeatFlows?.supportingFiles ?? [],
            wasteGasFlowsSupportingFiles: energyFlows?.wasteGasFlows?.supportingFiles ?? [],
            electricityFlowsSupportingFiles: energyFlows?.electricityFlows?.supportingFiles ?? [],
          };
    }),
  );

  constructor(
    readonly store: PermitApplicationStore<PermitApplicationState>,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}
}
