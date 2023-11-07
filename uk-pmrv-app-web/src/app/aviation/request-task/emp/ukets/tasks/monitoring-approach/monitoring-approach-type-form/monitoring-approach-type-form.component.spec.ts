import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';

import { render } from '@testing-library/angular';

import { EmpEmissionsMonitoringApproach } from 'pmrv-api';

import { MonitoringApproachTypeFormComponent } from './monitoring-approach-type-form.component';

async function renderComponent() {
  const { fixture, detectChanges } = await render(
    `<form [formGroup]="form"><app-monitoring-approach-form></app-monitoring-approach-form></form>`,
    {
      imports: [ReactiveFormsModule, MonitoringApproachTypeFormComponent],
      componentProperties: {
        form: new FormGroup({
          monitoringApproachType: new FormControl<EmpEmissionsMonitoringApproach['monitoringApproachType'] | null>(
            null,
          ),
        }),
      },
    },
  );

  return { component: fixture.componentInstance, detectChanges };
}

describe('MonitoringApproachTypeFormComponent', () => {
  it('should create', async () => {
    const { component } = await renderComponent();
    expect(component).toBeTruthy();
  });
});
