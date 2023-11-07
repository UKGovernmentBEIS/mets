import { FormControl, FormGroup } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';

import { VirSharedModule } from '@shared/vir-shared/vir-shared.module';
import { render } from '@testing-library/angular';

describe('RecommendationResponseItemFormComponent', () => {
  const renderComponent = async () => {
    const { fixture, detectChanges } = await render(
      `
      <app-recommendation-response-item-form
        [verificationDataItem]="verificationDataItem"
        [operatorImprovementResponse]="operatorImprovementResponse"
        [attachedFiles]="attachedFiles"
        [formGroup]="form"
        [isEditable]="true"
      ></app-recommendation-response-item-form>
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
            addressedDescription: 'Not addressed',
            addressedDate: null,
            uploadEvidence: false,
            files: [],
          },
          attachedFiles: [],
          form: new FormGroup({
            improvementRequired: new FormControl<boolean>(null),
            improvementDeadline: new FormControl<string>(null),
            improvementComments: new FormControl<string>(null),
            operatorActions: new FormControl<string>(null),
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
