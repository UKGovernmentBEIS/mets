import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';

import { render } from '@testing-library/angular';

import { ApplicationTimeframeApplyFormComponent } from './application-timeframe-apply-form.component';

async function renderComponent() {
  const { fixture, detectChanges } = await render(
    `<form [formGroup]="form"><app-application-timeframe-apply-form></app-application-timeframe-apply-form></form>`,
    {
      imports: [ReactiveFormsModule, ApplicationTimeframeApplyFormComponent],
      componentProperties: {
        form: new FormGroup({
          dateOfStart: new FormControl<string | null>(null),
          submittedOnTime: new FormControl<boolean | null>(null),
          reasonForLateSubmission: new FormControl<string | null>(null),
        }),
      },
    },
  );

  return { component: fixture.componentInstance, detectChanges };
}

describe('ApplicationTimeframeApplyFormComponent', () => {
  it('should create', async () => {
    const { component } = await renderComponent();
    expect(component).toBeTruthy();
  });
});
