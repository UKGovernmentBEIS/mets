import { FormControl, FormGroup } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';

import { VirSharedModule } from '@shared/vir-shared/vir-shared.module';
import { render } from '@testing-library/angular';

describe('RespondItemFormComponent', () => {
  const renderComponent = async () => {
    const { fixture, detectChanges } = await render(
      `
      <app-respond-item-form
        [verificationDataItem]="verificationDataItem"
        [operatorImprovementResponse]="operatorImprovementResponse"
        [documentFiles]="[]"
        [regulatorImprovementResponse]=""
        [formGroup]="form"
        [isEditable]="true"
      ></app-respond-item-form>
      `,
      {
        imports: [RouterTestingModule, VirSharedModule],
        componentProperties: {
          verificationDataItem: {
            reference: 'B1',
            explanation: 'Test uncorrectedNonConformity',
            materialEffect: true,
          },
          operatorImprovementResponse: {
            isAddressed: false,
            uploadEvidence: false,
            addressedDescription: 'not to address',
          },
          regulatorImprovementResponse: {
            operatorActions: 'Actions B1',
            improvementDeadline: '2023-12-01',
            improvementRequired: true,
          },
          form: new FormGroup({
            improvementCompleted: new FormControl<boolean>(null),
            dateCompleted: new FormControl<string>(null),
            reason: new FormControl<string>(null),
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
