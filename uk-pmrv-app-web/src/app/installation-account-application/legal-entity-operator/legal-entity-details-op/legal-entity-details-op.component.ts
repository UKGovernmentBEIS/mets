import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { legalEntityTypeMap } from '@shared/interfaces/legal-entity';
import { originalOrder } from '@shared/keyvalue-order';

import { LEGAL_ENTITY_FORM_OP } from '../../factories/legal-entity/legal-entity-form-op.factory';
import { ApplicationSectionType } from '../../store/installation-account-application.state';
import { InstallationAccountApplicationStore } from '../../store/installation-account-application.store';

@Component({
  selector: 'app-legal-entity-details-op',
  templateUrl: './legal-entity-details-op.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class LegalEntityDetailsOpComponent {
  form: UntypedFormGroup;
  readonly originalOrder = originalOrder;
  radioOptions = legalEntityTypeMap;

  constructor(
    public readonly store: InstallationAccountApplicationStore,
    private readonly route: ActivatedRoute,
    @Inject(LEGAL_ENTITY_FORM_OP) private readonly legalEntityOpForm: UntypedFormGroup,
  ) {
    this.form = this.legalEntityOpForm.get('detailsGroup') as UntypedFormGroup;
  }

  onSubmit(): void {
    const selectGroup = this.legalEntityOpForm.get('selectGroup');
    const detailsGroup = this.legalEntityOpForm.get('detailsGroup');
    const referenceNumberGroup = this.legalEntityOpForm.get('referenceNumberGroup');

    this.legalEntityOpForm.updateValueAndValidity();

    this.store.updateTask(
      ApplicationSectionType.legalEntity,
      {
        selectGroup: selectGroup.value,
        detailsGroup: {
          type: detailsGroup.get('type').value,
          name: detailsGroup.get('name').value,
          address: detailsGroup.get('address').value,
          belongsToHoldingCompany: detailsGroup.get('belongsToHoldingCompany').value,
          holdingCompanyGroup: detailsGroup.get('belongsToHoldingCompany').value
            ? detailsGroup.get('holdingCompanyGroup').value
            : null,

          referenceNumber: referenceNumberGroup.get('isEntityRegistered').value
            ? referenceNumberGroup.get('referenceNumber').value
            : null,
          noReferenceNumberReason: !referenceNumberGroup.get('isEntityRegistered').value
            ? referenceNumberGroup.get('noReferenceNumberReason').value
            : null,
        },
      },
      'complete',
    );

    this.store.nextStep('../..', this.route);
  }
}
