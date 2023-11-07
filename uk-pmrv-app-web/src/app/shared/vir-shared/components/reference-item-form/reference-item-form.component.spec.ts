import { FormControl, FormGroup } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';

import { VirSharedModule } from '@shared/vir-shared/vir-shared.module';
import { render } from '@testing-library/angular';

describe('ReferenceItemFormComponent', () => {
  const renderComponent = async () => {
    const { fixture, detectChanges } = await render(
      `
      <app-reference-item-form
        [verificationDataItem]="verificationDataItem"
        [formGroup]="form"
        [isEditable]="true"
      ></app-reference-item-form>
      `,
      {
        imports: [RouterTestingModule, VirSharedModule],
        componentProperties: {
          verificationDataItem: {
            reference: 'B1',
            explanation: 'Test uncorrectedNonConformity',
            materialEffect: true,
          },
          form: new FormGroup({
            isAddressed: new FormControl<boolean>(null),
            addressedDescription: new FormControl<string>(null),
            addressedDate: new FormControl<string>(null),
            addressedDescription2: new FormControl<string>(null),
          }),
        },
      },
    );

    return { component: fixture.componentInstance, detectChanges };
  };

  it('should create', async () => {
    const { component } = await renderComponent();
    expect(component).toBeTruthy();
  });
});
