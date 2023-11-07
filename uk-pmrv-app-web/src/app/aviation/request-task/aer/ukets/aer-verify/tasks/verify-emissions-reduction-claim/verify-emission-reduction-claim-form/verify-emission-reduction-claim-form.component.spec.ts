import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';

import { render } from '@testing-library/angular';

import { AviationAerEmissionsReductionClaimVerification } from 'pmrv-api';

import { VerifyEmissionReductionClaimFormComponent } from './verify-emission-reduction-claim-form.component';

describe('VerifyEmissionReductionClaimFormComponent', () => {
  let element: HTMLElement;

  const renderComponent = async () => {
    const { fixture, detectChanges } = await render(
      `<form [formGroup]="form">
            <app-verify-emission-reduction-claim-form></app-verify-emission-reduction-claim-form>
       </form>`,
      {
        imports: [ReactiveFormsModule, VerifyEmissionReductionClaimFormComponent],
        componentProperties: {
          form: new FormGroup({
            safBatchClaimsReviewed: new FormControl<
              AviationAerEmissionsReductionClaimVerification['safBatchClaimsReviewed'] | null
            >(null),

            batchReferencesNotReviewed: new FormControl<
              AviationAerEmissionsReductionClaimVerification['batchReferencesNotReviewed'] | null
            >(null),

            dataSampling: new FormControl<AviationAerEmissionsReductionClaimVerification['dataSampling'] | null>(null),

            reviewResults: new FormControl<AviationAerEmissionsReductionClaimVerification['reviewResults'] | null>(
              null,
            ),

            noDoubleCountingConfirmation: new FormControl<
              AviationAerEmissionsReductionClaimVerification['noDoubleCountingConfirmation'] | null
            >(null),

            evidenceAllCriteriaMetExist: new FormControl<
              AviationAerEmissionsReductionClaimVerification['evidenceAllCriteriaMetExist'] | null
            >(null),

            noCriteriaMetIssues: new FormControl<
              AviationAerEmissionsReductionClaimVerification['noCriteriaMetIssues'] | null
            >(null),

            complianceWithUkEtsRequirementsExist: new FormControl<
              AviationAerEmissionsReductionClaimVerification['complianceWithUkEtsRequirementsExist'] | null
            >(null),

            notCompliedWithUkEtsRequirementsAspects: new FormControl<
              AviationAerEmissionsReductionClaimVerification['notCompliedWithUkEtsRequirementsAspects'] | null
            >(null),
          }),
        },
      },
    );

    element = fixture.nativeElement;
    return { component: fixture.componentInstance, detectChanges };
  };

  it('should create', async () => {
    const { component } = await renderComponent();

    expect(component).toBeTruthy();
  });

  it('should render the verify emissions reduction claim groups', () => {
    expect(Array.from(element.querySelectorAll<HTMLHeadElement>('h2')).map((el) => el.textContent.trim())).toEqual([
      'Have you reviewed all the aircraft operator’s sustainable aviation fuel (SAF) batch claims?',
      'Describe the results of your review',
      'Confirmation of no double-counting',
      'Do all of the batch claims reviewed contain evidence that shows the sustainability, purchase and delivery criteria were met?',
      'Was the aircraft operator’s ERC compliant with their emissions monitoring plan, the legislation and regulator guidance?',
    ]);
  });
});
