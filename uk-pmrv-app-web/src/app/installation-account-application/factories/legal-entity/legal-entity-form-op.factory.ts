import { InjectionToken } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';

import { AddressInputComponent } from '@shared/address-input/address-input.component';
import { HoldingCompanyFormComponent } from '@shared/holding-company-form';
import { LegalEntity } from '@shared/interfaces/legal-entity';
import { RecursivePartial } from '@shared/types/recursive-partial.type';

import { GovukValidators } from 'govuk-components';

import { LegalEntitiesService } from 'pmrv-api';

import { legalEntityNameNotExists } from './legal-entity';

export const LEGAL_ENTITY_FORM_OP = new InjectionToken<UntypedFormGroup>('Legal entity operator form');

export const legalEntityFormOpFactory = {
  provide: LEGAL_ENTITY_FORM_OP,
  useFactory: (fb: UntypedFormBuilder, legalEntitiesService: LegalEntitiesService) =>
    fb.group({
      selectGroup: fb.group({
        isNew: [null],
        id: [null, GovukValidators.required('Select an existing legal entity or create a new one')],
      }),
      referenceNumberGroup: fb.group({
        isEntityRegistered: [null, [GovukValidators.required('Select whether your organization is registered')]],
        referenceNumber: [
          null,
          [
            GovukValidators.required('Enter your company registration number'),
            GovukValidators.maxLength(15, 'Company registration number must be 15 characters or less'),
          ],
        ],
        noReferenceNumberReason: [
          null,
          [
            GovukValidators.required('Explain why you do not have a company registration number'),
            GovukValidators.maxLength(500, 'Your explanation should not be more than 500 characters'),
          ],
        ],
      }),
      detailsGroup: fb.group({
        type: [null, GovukValidators.required('Select the operator type')],
        name: [
          null,
          [
            GovukValidators.required('Enter the operator name'),
            GovukValidators.maxLength(255, 'The legal entity name should not be more than 255 characters'),
          ],
          [legalEntityNameNotExists(legalEntitiesService)],
        ],
        address: fb.group(AddressInputComponent.controlsFactory(null)),
        belongsToHoldingCompany: [false, { updateOn: 'change' }],
        holdingCompanyGroup: fb.group(HoldingCompanyFormComponent.controlsFactory()),
      }),
    }),
  deps: [UntypedFormBuilder, LegalEntitiesService],
};

export const legalEntityInitialValue: RecursivePartial<LegalEntity> = {
  selectGroup: { isNew: false },
  detailsGroup: {
    belongsToHoldingCompany: false,
  },
};
