import { inject, Injectable } from '@angular/core';
import { FormArray, FormBuilder, FormControl, FormGroup } from '@angular/forms';

import { Subject, takeUntil } from 'rxjs';

import { AerRequestTaskPayload, RequestTaskStore } from '@aviation/request-task/store';
import { unparseCsv } from '@aviation/request-task/util';
import { getLocationOnShoreFormGroup } from '@aviation/shared/components/location-state-form/location-state-form.util';
import {
  flightIdentificationRegistrationMarkingsEmptyLineValidator,
  flightIdentificationRegistrationMarkingsValidator,
  organisationStructureCreatePartnerFormControl,
  organisationStructureCreatePartnersFormArray,
  organisationStructureDisableDifferentContactLocationControlByDifferentLocation,
  organisationStructureDisableDifferentContactLocationControlByLegalStatusType,
} from '@aviation/shared/components/operator-details/utils/operator-details-form.util';
import { FileUpload } from '@shared/file-input/file-upload-event';
import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';

import { GovukValidators } from 'govuk-components';

import {
  AviationCorsiaOperatorDetails,
  FlightIdentification,
  IndividualOrganisation,
  LimitedCompanyOrganisation,
  OrganisationStructure,
  PartnershipOrganisation,
} from 'pmrv-api';

import { TaskFormProvider } from '../../../../task-form.provider';
import { getRequestTaskAttachmentTypeForRequestTaskType } from '../../../../util';

export interface OperatorDetailsFormModel {
  operatorName: FormGroup<Record<keyof AviationCorsiaOperatorDetails, FormControl>>;
  flightIdentification: FormGroup<Record<keyof AviationCorsiaOperatorDetails, FormControl>>;
  airOperatingCertificate: FormGroup<Record<keyof AviationCorsiaOperatorDetails, FormControl>>;
  organisationStructure: FormGroup<Record<keyof AviationCorsiaOperatorDetails, FormControl>>;
}

@Injectable()
export class OperatorDetailsFormProvider
  implements TaskFormProvider<AviationCorsiaOperatorDetails, OperatorDetailsFormModel>
{
  private fb = inject(FormBuilder);
  private _form: FormGroup;

  private destroy$ = new Subject<void>();

  constructor(private store: RequestTaskStore, private requestTaskFileService: RequestTaskFileService) {}

  get form(): FormGroup {
    if (!this._form) {
      this.buildForm();
    }

    this.disableFlightIdentificationFormGroupConditionally();
    this.disableAirOperatingCertificateFormGroupConditionally();
    this.disableOrganisationStructureFormGroupConditionally(this._form.controls.legalStatusType?.value);

    return this._form;
  }

  destroyForm() {
    this.destroy$.next();
    this.destroy$.complete();
    this._form = null;
  }

  setFormValue(formValues: AviationCorsiaOperatorDetails | undefined): void {
    if (formValues) {
      const aircraftRegistrationMarkings = formValues.flightIdentification?.aircraftRegistrationMarkings;
      const operatorName: Record<string, string> = { operatorName: formValues.operatorName };
      this.form.patchValue({
        ...formValues,
        operatorName,
        flightIdentification: {
          ...formValues.flightIdentification,
          aircraftRegistrationMarkings: aircraftRegistrationMarkings ? unparseCsv(aircraftRegistrationMarkings) : null,
        },
        airOperatingCertificate: {
          ...formValues?.airOperatingCertificate,
          certificateFiles:
            formValues?.airOperatingCertificate?.certificateFiles?.map((uuid) => ({
              file: { name: this.store.aerDelegate.payload.aerAttachments[uuid] } as File,
              uuid,
            })) ?? [],
        },
        organisationStructure: {
          ...formValues?.organisationStructure,
          evidenceFiles:
            (formValues?.organisationStructure as LimitedCompanyOrganisation)?.evidenceFiles?.map((uuid) => ({
              file: { name: this.store.aerDelegate.payload.aerAttachments[uuid] } as File,
              uuid,
            })) ?? [],
        },
      });
    }
  }

  private disableFlightIdentificationFormGroupConditionally() {
    const { flightIdentification } = this._form.value as AviationCorsiaOperatorDetails;
    const flightIdentificationControl = this._form.controls.flightIdentification as FormGroup;

    if (flightIdentification.flightIdentificationType === 'INTERNATIONAL_CIVIL_AVIATION_ORGANISATION') {
      flightIdentificationControl.controls.icaoDesignators.enable();
      flightIdentificationControl.controls.aircraftRegistrationMarkings.disable();
    } else {
      flightIdentificationControl.controls.aircraftRegistrationMarkings.enable();
      flightIdentificationControl.controls.icaoDesignators.disable();
    }
  }

  private disableAirOperatingCertificateFormGroupConditionally() {
    const { airOperatingCertificate } = this._form.value as AviationCorsiaOperatorDetails;
    const airOperatingCertificateControl = this._form.controls.airOperatingCertificate as FormGroup;

    if (airOperatingCertificate.certificateExist) {
      airOperatingCertificateControl.controls.certificateNumber.enable();
      airOperatingCertificateControl.controls.issuingAuthority.enable();
      airOperatingCertificateControl.controls.certificateFiles.enable();
    } else {
      airOperatingCertificateControl.controls.certificateNumber.disable();
      airOperatingCertificateControl.controls.issuingAuthority.disable();
      airOperatingCertificateControl.controls.certificateFiles.disable();
    }
  }

  private disableOrganisationStructureFormGroupConditionally(
    legalStatusType: OrganisationStructure['legalStatusType'],
  ) {
    const { organisationStructure } = this._form.value as AviationCorsiaOperatorDetails;
    const organisationStructureControl = this._form.controls.organisationStructure as FormGroup;

    switch (legalStatusType) {
      case 'LIMITED_COMPANY':
        organisationStructureControl.controls.registrationNumber.enable();
        organisationStructureControl.controls.evidenceFiles.enable();
        organisationStructureControl.controls.differentContactLocationExist.enable();
        (organisationStructure as LimitedCompanyOrganisation).differentContactLocationExist
          ? organisationStructureControl.controls.differentContactLocation.enable()
          : organisationStructureControl.controls.differentContactLocation.disable();
        organisationStructureControl.controls.fullName.disable();
        organisationStructureControl.controls.partnershipName.disable();
        organisationStructureControl.controls.partners.disable();
        break;

      case 'INDIVIDUAL':
        organisationStructureControl.controls.registrationNumber.disable();
        organisationStructureControl.controls.evidenceFiles.disable();
        organisationStructureControl.controls.differentContactLocationExist.disable();
        organisationStructureControl.controls.differentContactLocation.disable();
        organisationStructureControl.controls.fullName.enable();
        organisationStructureControl.controls.partnershipName.disable();
        organisationStructureControl.controls.partners.disable();
        break;

      case 'PARTNERSHIP':
        organisationStructureControl.controls.registrationNumber.disable();
        organisationStructureControl.controls.evidenceFiles.disable();
        organisationStructureControl.controls.differentContactLocationExist.disable();
        organisationStructureControl.controls.differentContactLocation.disable();
        organisationStructureControl.controls.fullName.disable();
        organisationStructureControl.controls.partnershipName.enable();
        organisationStructureControl.controls.partners.enable();
        break;
    }
  }

  private operatorNameCreateInitialForm() {
    return this.fb.group(
      {
        operatorName: new FormControl<AviationCorsiaOperatorDetails['operatorName']>(null, {
          validators: [
            GovukValidators.required('Enter an aeroplane or aircraft operator name'),
            GovukValidators.maxLength(255, 'Enter a valid aeroplane or aircraft operator name'),
          ],
        }),
      },
      { updateOn: 'change' },
    ) as unknown as FormGroup<Record<keyof AviationCorsiaOperatorDetails, FormControl>>;
  }

  private flightIdentificationCreateInitialForm() {
    return this.fb.group(
      {
        flightIdentificationType: new FormControl<FlightIdentification['flightIdentificationType']>(null, {
          validators: GovukValidators.required('Select the call sign identifier you use'),
        }),
        icaoDesignators: new FormControl<FlightIdentification['icaoDesignators']>(null, {
          validators: [
            GovukValidators.required('State which ICAO designators you are using'),
            GovukValidators.maxLength(100, 'The list of ICAO designators should not be more than 100 characters'),
          ],
        }),
        aircraftRegistrationMarkings: new FormControl<string>(null, {
          validators: [
            GovukValidators.required('State which aircraft registration markings you are using'),
            flightIdentificationRegistrationMarkingsValidator(),
            flightIdentificationRegistrationMarkingsEmptyLineValidator(),
          ],
        }),
      },
      { updateOn: 'change' },
    ) as unknown as FormGroup<Record<keyof AviationCorsiaOperatorDetails, FormControl>>;
  }

  private airOperatingCertificateCreateInitialForm() {
    const airOperatingCertificateFormGroup = this.fb.group(
      {
        certificateExist: new FormControl<AviationCorsiaOperatorDetails['airOperatingCertificate']['certificateExist']>(
          null,
          {
            validators: GovukValidators.required(
              'Select if you have an Air Operating Certificate or equivalent certification',
            ),
          },
        ),
        certificateNumber: new FormControl<
          AviationCorsiaOperatorDetails['airOperatingCertificate']['certificateNumber']
        >(null, {
          validators: [
            GovukValidators.required('Enter a certificate number'),
            GovukValidators.maxLength(255, 'Certificate number should not be more than 255 characters'),
          ],
        }),
        issuingAuthority: new FormControl<AviationCorsiaOperatorDetails['airOperatingCertificate']['issuingAuthority']>(
          null,
          {
            validators: GovukValidators.required('Select an issuing authority'),
          },
        ),
        restrictionsExist: new FormControl<
          keyof AviationCorsiaOperatorDetails['airOperatingCertificate']['restrictionsExist']
        >(null, {
          updateOn: 'change',
        }),
        restrictionsDetails: new FormControl<
          AviationCorsiaOperatorDetails['airOperatingCertificate']['restrictionsDetails']
        >(null),
        certificateFiles: this.requestTaskFileService.buildFormControl(
          this.store.requestTaskId,
          [],
          this.store.aerDelegate.payload.aerAttachments,
          getRequestTaskAttachmentTypeForRequestTaskType(this.store.getState().requestTaskItem?.requestTask?.type),
          true,
          !this.store.getState().isEditable,
        ) as FormControl<FileUpload[]>,
      },
      { updateOn: 'change' },
    ) as FormGroup<Record<keyof AviationCorsiaOperatorDetails['airOperatingCertificate'], FormControl>>;

    airOperatingCertificateFormGroup.controls.certificateExist.valueChanges
      .pipe(takeUntil(this.destroy$))
      .subscribe((value) => {
        if (value) {
          airOperatingCertificateFormGroup.controls.restrictionsExist.setValidators(
            GovukValidators.required('Select yes or no'),
          );
          airOperatingCertificateFormGroup.controls.restrictionsExist.updateValueAndValidity();
        } else {
          airOperatingCertificateFormGroup.controls.restrictionsExist.reset();
          airOperatingCertificateFormGroup.controls.restrictionsExist.clearValidators();
        }
      });

    airOperatingCertificateFormGroup.controls.restrictionsExist.valueChanges
      .pipe(takeUntil(this.destroy$))
      .subscribe((value) => {
        if (value) {
          airOperatingCertificateFormGroup.controls.restrictionsDetails.setValidators([
            GovukValidators.required('TBD'),
            GovukValidators.maxLength(500, 'TBD'),
          ]);
          airOperatingCertificateFormGroup.controls.restrictionsDetails.updateValueAndValidity();
        } else {
          airOperatingCertificateFormGroup.controls.restrictionsDetails.reset();
          airOperatingCertificateFormGroup.controls.restrictionsDetails.clearValidators();
        }
      });

    return airOperatingCertificateFormGroup;
  }

  private organisationStructureCreateInitialForm() {
    const partners = (
      (this.store.aerDelegate.payload as AerRequestTaskPayload).aer?.operatorDetails
        .organisationStructure as PartnershipOrganisation
    )?.partners;

    const organisationStructureFormGroup = this.fb.group(
      {
        legalStatusType: new FormControl<OrganisationStructure['legalStatusType']>(null, {
          validators: GovukValidators.required('Select the option which shows the legal status of your organisation'),
        }),
        organisationLocation: getLocationOnShoreFormGroup(),
        registrationNumber: new FormControl<LimitedCompanyOrganisation['registrationNumber']>(null, {
          validators: [
            GovukValidators.required('Enter the company registration number'),
            GovukValidators.maxLength(40, 'Registration number should not be more than 40 characters'),
          ],
        }),
        evidenceFiles: this.requestTaskFileService.buildFormControl(
          this.store.requestTaskId,
          [],
          this.store.aerDelegate.payload.aerAttachments,
          getRequestTaskAttachmentTypeForRequestTaskType(this.store.getState().requestTaskItem?.requestTask?.type),
          false,
          !this.store.getState().isEditable,
        ),
        differentContactLocationExist: new FormControl<LimitedCompanyOrganisation['differentContactLocationExist']>(
          null,
          {
            validators: GovukValidators.required('Say if you would like to enter a different contact address'),
          },
        ),
        differentContactLocation: getLocationOnShoreFormGroup(),
        fullName: new FormControl<IndividualOrganisation['fullName']>(null, {
          validators: [
            GovukValidators.required('Enter full name'),
            GovukValidators.maxLength(255, 'Full name should not be more than 255 characters'),
          ],
        }),
        partnershipName: new FormControl<PartnershipOrganisation['partnershipName']>(null),
        partners: this.fb.array(organisationStructureCreatePartnersFormArray(partners), {
          validators: GovukValidators.required('Enter the name of partner'),
        }),
      },
      { updateOn: 'change' },
    );

    if ((organisationStructureFormGroup.controls.partners.value as PartnershipOrganisation['partners']).length === 0) {
      (organisationStructureFormGroup.controls.partners as FormArray).push(
        organisationStructureCreatePartnerFormControl(),
      );
    }

    organisationStructureFormGroup.controls.legalStatusType.valueChanges
      .pipe(takeUntil(this.destroy$))
      .subscribe((legalStatusType) => {
        this.disableOrganisationStructureFormGroupConditionally(legalStatusType);

        organisationStructureDisableDifferentContactLocationControlByLegalStatusType(
          organisationStructureFormGroup,
          legalStatusType,
        );
      });

    organisationStructureFormGroup.controls.differentContactLocationExist.valueChanges
      .pipe(takeUntil(this.destroy$))
      .subscribe((differentContactLocationExist) =>
        organisationStructureDisableDifferentContactLocationControlByDifferentLocation(
          organisationStructureFormGroup,
          differentContactLocationExist,
        ),
      );

    return organisationStructureFormGroup;
  }

  private buildForm() {
    this._form = this.fb.group(
      {
        operatorName: this.operatorNameCreateInitialForm(),
        flightIdentification: this.flightIdentificationCreateInitialForm(),
        airOperatingCertificate: this.airOperatingCertificateCreateInitialForm(),
        organisationStructure: this.organisationStructureCreateInitialForm(),
      },
      { updateOn: 'change' },
    );
  }
}
