import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';

import { render } from '@testing-library/angular';

import { SimplifiedApproachFormComponent } from './simplified-approach-form.component';

async function renderComponent() {
  const vm = {
    downloadUrl: '/aviation/tasks/27/file-download/attachment/',
  };
  const { fixture, detectChanges } = await render(
    `<form [formGroup]="form"><app-simplified-approaches-form [vm]="vm"></app-simplified-approaches-form></form>`,
    {
      imports: [ReactiveFormsModule, SimplifiedApproachFormComponent],
      componentProperties: {
        form: new FormGroup({
          explanation: new FormControl<string | null>(null),
          supportingEvidenceFiles: new FormControl<any>(null),
        }),
        vm,
      },
    },
  );

  return { component: fixture.componentInstance, detectChanges };
}

describe('SimplifiedApproachFormComponent', () => {
  it('should create', async () => {
    const { component } = await renderComponent();
    expect(component).toBeTruthy();
  });
});
