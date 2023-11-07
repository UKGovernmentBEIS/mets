import { NgFor, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnDestroy, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';

import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { BaseMonitoringApproachComponent } from '../base-monitoring-approach.component';
import { MonitoringApproachTypeFormComponent } from '../monitoring-approach-type-form';

@Component({
  selector: 'app-monitoring-approach-type',
  standalone: true,
  imports: [
    GovukComponentsModule,
    NgIf,
    NgFor,
    SharedModule,
    ReturnToLinkComponent,
    MonitoringApproachTypeFormComponent,
  ],
  templateUrl: './monitoring-approach-type.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class MonitoringApproachTypeComponent extends BaseMonitoringApproachComponent implements OnInit, OnDestroy {
  form = new FormGroup({ monitoringApproachType: this.formProvider.monitoringApproachTypeCtrl });

  ngOnInit(): void {
    this.backLinkService.show();
  }

  onSubmit() {
    if (this.form.valid) {
      this.formProvider.form.updateValueAndValidity();
      const isFum = this.formProvider.getFormValue().monitoringApproachType === 'FUEL_USE_MONITORING';

      if (!isFum) {
        this.formProvider.addSimplifiedApproach();
      } else {
        this.formProvider.removeSimplifiedApproach();
      }

      const nextStepUrl = isFum ? 'summary' : 'simplified-approach';

      this.saveEmpAndNavigate(this.formProvider.getFormValue(), 'in progress', nextStepUrl);
    }
  }

  ngOnDestroy(): void {
    this.backLinkService.hide();
  }
}
