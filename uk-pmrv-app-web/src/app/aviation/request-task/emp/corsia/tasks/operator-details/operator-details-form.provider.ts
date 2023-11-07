import { inject, Injectable } from '@angular/core';
import { FormArray, FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';

import { Subject, takeUntil } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TaskFormProvider } from '@aviation/request-task/task-form.provider';
import { getRequestTaskAttachmentTypeForRequestTaskType, unparseCsv } from '@aviation/request-task/util';
import { getLocationOnShoreFormGroup } from '@aviation/shared/components/location-state-form/location-state-form.util';
import {
  flightIdentificationRegistrationMarkingsEmptyLineValidator,
  flightIdentificationRegistrationMarkingsValidator,
  organisationStructureCreatePartnerFormControl,
  organisationStructureCreatePartnersFormArray,
  organisationStructureDisableDifferentContactLocationControlByDifferentLocation,
  organisationStructureDisableDifferentContactLocationControlByLegalStatusType,
} from '@aviation/shared/components/operator-details/utils/operator-details-form.util';
import { EmpOperatorDetailsViewModel } from '@aviation/shared/types';
import { FileUpload } from '@shared/file-input/file-upload-event';
import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';

import { GovukValidators } from 'govuk-components';

import {
  ActivitiesDescription,
  EmpCorsiaOperatorDetails,
  EmpOperatorDetails,
  FlightIdentification,
  IndividualOrganisation,
  LimitedCompanyOrganisation,
  LocationOnShoreStateDTO,
  OrganisationStructure,
  PartnershipOrganisation,
} from 'pmrv-api';

export interface CorsiaOperatorDetailsFormModel {
  operatorName: FormControl<string>;
  flightIdentification: FormGroup<Record<keyof EmpCorsiaOperatorDetails['flightIdentification'], FormControl>>;
  airOperatingCertificate: FormGroup<Record<keyof EmpCorsiaOperatorDetails['airOperatingCertificate'], FormControl>>;
  organisationStructure: FormGroup<Record<keyof EmpCorsiaOperatorDetails['organisationStructure'], FormControl>>;
  activitiesDescription: FormGroup<Record<keyof EmpCorsiaOperatorDetails['activitiesDescription'], FormControl>>;
  subsidiaryCompanyExist: FormControl<boolean>;
  subsidiaryCompanies?: FormGroup<Record<keyof EmpCorsiaOperatorDetails['subsidiaryCompanies'], FormControl>>;
}

@Injectable()
export class OperatorDetailsCorsiaFormProvider
  implements TaskFormProvider<EmpCorsiaOperatorDetails, CorsiaOperatorDetailsFormModel>
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

  setFormValue(formValues: EmpCorsiaOperatorDetails | undefined): void {
    if (formValues) {
      const aircraftRegistrationMarkings = formValues.flightIdentification?.aircraftRegistrationMarkings;

      const patchValues = {
        ...formValues,
        operatorName: formValues.operatorName,
        flightIdentification: {
          ...formValues.flightIdentification,
          aircraftRegistrationMarkings: aircraftRegistrationMarkings ? unparseCsv(aircraftRegistrationMarkings) : null,
        },
        airOperatingCertificate: {
          ...formValues?.airOperatingCertificate,
          certificateFiles:
            formValues?.airOperatingCertificate?.certificateFiles?.map((uuid) => ({
              file: { name: this.store.empCorsiaDelegate.payload.empAttachments[uuid] } as File,
              uuid,
            })) ?? [],
        },
        organisationStructure: {
          ...formValues?.organisationStructure,
          evidenceFiles:
            (formValues?.organisationStructure as LimitedCompanyOrganisation)?.evidenceFiles?.map((uuid) => ({
              file: { name: this.store.empCorsiaDelegate.payload.empAttachments[uuid] } as File,
              uuid,
            })) ?? [],
        },
        subsidiaryCompanyExist: formValues?.subsidiaryCompanyExist ?? null,
      };
      this.form.addControl('subsidiaryCompanyExist', new FormControl(patchValues.subsidiaryCompanyExist ?? null));
      this.form.patchValue(patchValues);

      if (formValues?.subsidiaryCompanies) {
        this.addSubsidiaryCompanyControl(formValues.subsidiaryCompanies);
        patchValues.subsidiaryCompanies = this.getSubsidiaryFiles(formValues).subsidiaryCompanies;
        this.form.patchValue(patchValues);
      }
    }
  }

  getSubsidiaryFiles(operatorDetails) {
    if (operatorDetails?.subsidiaryCompanies) {
      for (const item of operatorDetails.subsidiaryCompanies) {
        if (item.airOperatingCertificate.certificateExist) {
          if (
            item.airOperatingCertificate.certificateFiles &&
            typeof item.airOperatingCertificate.certificateFiles[0] !== 'object'
          ) {
            item.airOperatingCertificate.certificateFiles = item.airOperatingCertificate.certificateFiles.map(
              (uuid) => ({
                file: { name: this.store.empDelegate.payload.empAttachments[uuid] } as File,
                uuid,
              }),
            );
          }
        }
      }
    }
    return operatorDetails;
  }

  public addSubsidiaryCompanyExistsControl(value): void {
    this._form.addControl('subsidiaryCompanyExist', new FormControl(value));
  }

  public removeSubsidiaryCompanyExistsControl(): void {
    if (this._form.get('subsidiaryCompanyExist')) {
      this._form.removeControl('subsidiaryCompanyExist');
    }
  }

  get subsidiaryCompaniesListForm(): FormGroup {
    return this.form.get('subsidiaryCompanies') as FormGroup;
  }

  public addSubsidiaryCompanyControl(values): void {
    const form = this.fb.array([], Validators.minLength(1));

    for (const value of values) {
      const control = this.fb.control(value);

      form.push(control);
    }

    form.patchValue([...values]);

    this._form.addControl('subsidiaryCompanies', form);
  }

  public removeSubsidiaryCompanyControl(): void {
    if (this._form.get('subsidiaryCompanies')) this._form.removeControl('subsidiaryCompanies');
  }

  public removeSubsidiaryCompanyItem(index: number): void {
    if (this._form.get('subsidiaryCompanies')) {
      (this.form.get('subsidiaryCompanies') as FormArray).removeAt(index);
      this.setFormValue(this.form.value);
    }
  }

  public addRestrictionDetailsControl() {
    this._form.addControl(
      'restrictionsDetails',
      new FormControl<EmpCorsiaOperatorDetails['airOperatingCertificate']['restrictionsDetails']>('', {
        validators: [GovukValidators.required('TBD'), GovukValidators.maxLength(500, 'TBD')],
      }),
    );
  }

  public removeRestrictionDetailsControl() {
    if (this._form.get('restrictionsDetails')) {
      this._form.addControl(
        'restrictionsDetails',
        new FormControl<EmpCorsiaOperatorDetails['airOperatingCertificate']['restrictionsDetails']>('', {
          validators: [GovukValidators.required('TBD'), GovukValidators.maxLength(500, 'TBD')],
        }),
      );
    }
  }

  private disableFlightIdentificationFormGroupConditionally() {
    const { flightIdentification } = this._form.value as EmpCorsiaOperatorDetails;
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
    const { airOperatingCertificate } = this._form.value as EmpCorsiaOperatorDetails;
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
    const { organisationStructure } = this._form.value as EmpOperatorDetails;
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
    ) as unknown as FormGroup<Record<keyof EmpCorsiaOperatorDetails, FormControl>>;
  }

  private airOperatingCertificateCreateInitialForm() {
    const form = this.fb.group(
      {
        certificateExist: new FormControl<EmpCorsiaOperatorDetails['airOperatingCertificate']['certificateExist']>(
          null,
          {
            validators: GovukValidators.required(
              'Select if you have an Air Operating Certificate or equivalent certification',
            ),
          },
        ),
        certificateNumber: new FormControl<EmpCorsiaOperatorDetails['airOperatingCertificate']['certificateNumber']>(
          null,
          {
            validators: [
              GovukValidators.required('Enter a certificate number'),
              GovukValidators.maxLength(255, 'Certificate number should not be more than 255 characters'),
            ],
          },
        ),
        issuingAuthority: new FormControl<EmpCorsiaOperatorDetails['airOperatingCertificate']['issuingAuthority']>(
          null,
          {
            validators: GovukValidators.required('Select an issuing authority'),
          },
        ),
        certificateFiles: this.requestTaskFileService.buildFormControl(
          this.store.requestTaskId,
          [],
          this.store.empCorsiaDelegate.payload.empAttachments,
          getRequestTaskAttachmentTypeForRequestTaskType(this.store.getState().requestTaskItem?.requestTask?.type),
          true,
          !this.store.getState().isEditable,
        ) as FormControl<FileUpload[]>,
      },
      { updateOn: 'change' },
    ) as unknown as FormGroup<Record<keyof EmpOperatorDetailsViewModel, FormControl>>;

    this.addRestrictionFormControls(form);
    return form;
  }

  private addRestrictionFormControls(form: FormGroup): void {
    (form as FormGroup).addControl(
      'restrictionsExist',
      new FormControl<keyof EmpCorsiaOperatorDetails['airOperatingCertificate']['restrictionsExist']>(null, {
        validators: GovukValidators.required('Select yes or no'),
      }),
    );
    (form as FormGroup).addControl(
      'restrictionsDetails',
      new FormControl<EmpCorsiaOperatorDetails['airOperatingCertificate']['restrictionsDetails']>('', {
        validators: [GovukValidators.required('TBD'), GovukValidators.maxLength(500, 'TBD')],
      }),
    );
  }

  private organisationStructureCreateInitialForm() {
    const partners = (
      this.store.empCorsiaDelegate.payload.emissionsMonitoringPlan.operatorDetails
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
          this.store.empCorsiaDelegate.payload.empAttachments,
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

  private activitiesDescriptionCreateInitialForm() {
    const form = this.fb.group(
      {
        operatorType: new FormControl<ActivitiesDescription['operatorType']>(null, {
          validators: GovukValidators.required('Select if you are a commercial or non-commercial operator'),
        }),
        flightTypes: new FormControl<ActivitiesDescription['flightTypes']>([], {
          validators: GovukValidators.required('Select if you operate scheduled or non-scheduled flights'),
        }),
        activityDescription: new FormControl<ActivitiesDescription['activityDescription']>(null, {
          validators: [
            GovukValidators.required('Describe what kind of activities you carry out'),
            GovukValidators.maxLength(10000, 'The activities you describe should not be more than 10000 characters'),
          ],
        }),
      },
      { updateOn: 'change' },
    ) as unknown as FormGroup<Record<keyof EmpCorsiaOperatorDetails, FormControl>>;
    return form;
  }

  private subsidiaryActivitiesDescriptionCreateInitialForm() {
    const form = this.fb.group(
      {
        flightTypes: new FormControl<ActivitiesDescription['flightTypes']>([], {
          validators: GovukValidators.required('Select if you operate scheduled or non-scheduled flights'),
        }),
        activityDescription: new FormControl<ActivitiesDescription['activityDescription']>(null, {
          validators: [
            GovukValidators.required('Describe what kind of activities you carry out'),
            GovukValidators.maxLength(10000, 'The activities you describe should not be more than 10000 characters'),
          ],
        }),
      },
      { updateOn: 'change' },
    ) as unknown as FormGroup<Record<keyof EmpCorsiaOperatorDetails, FormControl>>;
    return form;
  }

  public subsidiaryCompaniesForm() {
    return this.fb.group(
      {
        operatorName: this.fb.control(null, [
          GovukValidators.required('Enter an aeroplane or aircraft operator name'),
          GovukValidators.maxLength(255, 'Enter a valid aeroplane or aircraft operator name'),
        ]),
        flightIdentification: this.flightIdentificationCreateInitialForm(),
        airOperatingCertificate: this.airOperatingCertificateCreateInitialForm(),
        companyRegistrationNumber: new FormControl(null, [
          GovukValidators.required('Enter the company registration number'),
          GovukValidators.maxLength(40, 'Registration number should not be more than 40 characters'),
        ]),
        registeredLocation: new FormGroup({
          type: new FormControl<LocationOnShoreStateDTO['type']>('ONSHORE_STATE'),
          line1: new FormControl(null, {
            validators: GovukValidators.required('Enter the first line of your address'),
          }),
          line2: new FormControl(null),
          city: new FormControl(null, {
            validators: GovukValidators.required('Enter your town or city'),
          }),
          state: new FormControl(null),
          postcode: new FormControl(null),
          country: new FormControl(null, {
            validators: GovukValidators.required('Enter your country'),
          }),
        }),
        flightTypes: new FormControl([], {
          validators: GovukValidators.required('Select if you operate scheduled or non-scheduled flights'),
        }),
        activityDescription: new FormControl(null, {
          validators: [
            GovukValidators.required('Describe what kind of activities you carry out'),
            GovukValidators.maxLength(10000, 'The activities you describe should not be more than 10000 characters'),
          ],
        }),
      },
      { updateOn: 'change' },
    );
  }

  private buildForm() {
    this._form = this.fb.group(
      {
        operatorName: this.fb.control(null, [
          GovukValidators.required('Enter an aeroplane or aircraft operator name'),
          GovukValidators.maxLength(255, 'Enter a valid aeroplane or aircraft operator name'),
        ]),
        flightIdentification: this.flightIdentificationCreateInitialForm(),
        airOperatingCertificate: this.airOperatingCertificateCreateInitialForm(),
        organisationStructure: this.organisationStructureCreateInitialForm(),
        activitiesDescription: this.activitiesDescriptionCreateInitialForm(),
      },
      { updateOn: 'change' },
    );
  }
}
