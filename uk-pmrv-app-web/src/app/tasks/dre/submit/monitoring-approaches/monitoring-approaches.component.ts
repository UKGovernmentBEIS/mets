import { ChangeDetectionStrategy, Component, Inject, OnInit } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, switchMap } from 'rxjs';

import { DRE_TASK_FORM } from '@tasks/dre/core/dre-task-form.token';

import { Dre } from 'pmrv-api';

import { PendingRequestService } from '../../../../core/guards/pending-request.service';
import { BackLinkService } from '../../../../shared/back-link/back-link.service';
import { DreService } from '../../core/dre.service';
import { monitoringApproachesFormProvider } from './monitoring-approaches-form.provider';

@Component({
  selector: 'app-monitoring-approaches',
  templateUrl: './monitoring-approaches.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [monitoringApproachesFormProvider],
})
export class MonitoringApproachesComponent implements OnInit {
  private readonly nextWizardStep = 'reportable-emissions';
  monitoringApproaches$ = this.dreService.dre$.pipe(map((dre) => dre.monitoringApproachReportingEmissions ?? {}));

  constructor(
    @Inject(DRE_TASK_FORM) readonly form: UntypedFormGroup,
    readonly dreService: DreService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly pendingRequest: PendingRequestService,
    private readonly backLinkService: BackLinkService,
  ) {}
  ngOnInit(): void {
    this.backLinkService.show();
  }

  onSubmit(): void {
    if (!this.form.dirty) {
      this.router.navigate(['..', this.nextWizardStep], { relativeTo: this.route });
    } else {
      this.dreService.dre$
        .pipe(
          first(),
          switchMap((dre) => {
            return this.dreService.saveDre(
              { monitoringApproachReportingEmissions: this.buildMonitoringApproaches(dre) },
              false,
            );
          }),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() => this.router.navigate(['..', this.nextWizardStep], { relativeTo: this.route }));
    }
  }

  buildMonitoringApproaches(dre: Dre) {
    const calculationCO2MonitoringApproach = this.buildMonitoringApproach(
      dre,
      'CALCULATION_CO2',
      'hasTransferCalculationCO2',
      'calculateTransferredCO2',
      'transferredCO2Emissions',
    );
    const measurementCO2MonitoringApproach = this.buildMonitoringApproach(
      dre,
      'MEASUREMENT_CO2',
      'hasTransferMeasurementCO2',
      'measureTransferredCO2',
      'transferredCO2Emissions',
    );
    const measurementN2OMonitoringApproach = this.buildMonitoringApproach(
      dre,
      'MEASUREMENT_N2O',
      'hasTransferMeasurementN2O',
      'measureTransferredN2O',
      'transferredN2OEmissions',
    );
    const calculationPFCMonitoringApproach = this.buildMonitoringApproach(dre, 'CALCULATION_PFC');
    const inherentCO2MonitoringApproach = this.buildMonitoringApproach(dre, 'INHERENT_CO2');
    const fallbackMonitoringApproach = this.buildMonitoringApproach(dre, 'FALLBACK');

    return {
      ...calculationCO2MonitoringApproach,
      ...measurementCO2MonitoringApproach,
      ...measurementN2OMonitoringApproach,
      ...calculationPFCMonitoringApproach,
      ...inherentCO2MonitoringApproach,
      ...fallbackMonitoringApproach,
    };
  }

  private buildMonitoringApproach(
    dre: Dre,
    type: string,
    hasTransferredFormKey?: string,
    hasTansferredPropertyKey?: string,
    transferredPropertyKey?: string,
  ) {
    const selectedMonitoringApproachTypes = this.form.value.monitoringApproaches as string[];
    const hasTransfer = this.form.value?.[hasTransferredFormKey];

    return !selectedMonitoringApproachTypes.includes(type)
      ? {}
      : ({
          [type]: {
            ...dre.monitoringApproachReportingEmissions?.[type],
            type,
            ...(hasTansferredPropertyKey ? { [hasTansferredPropertyKey]: hasTransfer } : {}),
            ...(transferredPropertyKey && !hasTransfer ? { [transferredPropertyKey]: undefined } : {}),
          },
        } as any);
  }
}
