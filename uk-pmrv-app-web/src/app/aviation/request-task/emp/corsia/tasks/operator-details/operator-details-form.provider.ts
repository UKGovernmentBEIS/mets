import { inject, Injectable } from '@angular/core';
import { FormArray, FormBuilder, FormControl, FormGroup } from '@angular/forms';

import { Subject, takeUntil } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TaskFormProvider } from '@aviation/request-task/task-form.provider';
import { getRequestTaskAttachmentTypeForRequestTaskType, unparseCsv } from '@aviation/request-task/util';
import { getLocationOnShoreFormGroup } from '@aviation/shared/components/location-state-form/location-state-form.util';
import {
  activityDescriptionValidator,
  aircraftRegistrationMarkingsValidator,
  certificateExistValidator,
  certificateNumberValidator,
  cityValidator,
  countryValidator,
  differentContactLocationExistValidator,
  flightIdentificationTypeValidator,
  flightTypesValidator,
  fullNameValidator,
  icaoDesignatorsValidator,
  issuingAuthorityValidator,
  legalStatusTypeValidator,
  line1Validator,
  operatorNameValidator,
  operatorTypeValidator,
  organisationStructureCreatePartnerFormControl,
  organisationStructureCreatePartnersFormArray,
  organisationStructureDisableDifferentContactLocationControlByDifferentLocation,
  organisationStructureDisableDifferentContactLocationControlByLegalStatusType,
  partnersValidator,
  registrationNumberValidator,
  restrictionsDetailsValidator,
  restrictionsExistValidator,
  subsidiaryCompanyExistValidator,
} from '@aviation/shared/components/operator-details/utils/operator-details-form.util';
import { EmpOperatorDetailsViewModel } from '@aviation/shared/types';
import { FileUpload } from '@shared/file-input/file-upload-event';
import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';

import {
  ActivitiesDescription,
  AirOperatingCertificateCorsia,
  EmpCorsiaOperatorDetails,
  FlightIdentification,
  IndividualOrganisation,
  LimitedCompanyOrganisation,
  LocationOnShoreStateDTO,
  OrganisationStructure,
  PartnershipOrganisation,
  SubsidiaryCompanyCorsia,
} from 'pmrv-api';

export interface CorsiaOperatorDetailsFormModel {
  operatorName: FormControl<string>;
  flightIdentification: FormGroup<Record<keyof EmpCorsiaOperatorDetails['flightIdentification'], FormControl>>;
  airOperatingCertificate: FormGroup<Record<keyof EmpCorsiaOperatorDetails['airOperatingCertificate'], FormControl>>;
  organisationStructure: FormGroup<Record<keyof EmpCorsiaOperatorDetails['organisationStructure'], FormControl>>;
  activitiesDescription: FormGroup<Record<keyof EmpCorsiaOperatorDetails['activitiesDescription'], FormControl>>;
  subsidiaryCompanyExist: FormControl<boolean>;
  subsidiaryCompanies?: FormArray<FormGroup<Record<keyof SubsidiaryCompanyCorsia, FormGroup | FormControl>>>;
}

@Injectable()
export class OperatorDetailsCorsiaFormProvider
  implements TaskFormProvider<EmpCorsiaOperatorDetails, CorsiaOperatorDetailsFormModel>
{
  private fb = inject(FormBuilder);
  private _form: FormGroup;

  private destroy$ = new Subject<void>();

  constructor(
    private store: RequestTaskStore,
    private requestTaskFileService: RequestTaskFileService,
  ) {}

  get form(): FormGroup {
    if (!this._form) {
      this.buildForm();
    }

    this.disableFlightIdentificationFormGroupConditionally();
    this.disableAirOperatingCertificateFormGroupConditionally();
    this.disableOrganisationStructureFormGroupConditionally(this._form.value.organisationStructure.legalStatusType);
    this.disableSubsidiaryCompaniesFormGroupConditionally();

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
        operatorName: formValues.operatorName ?? null,
        flightIdentification: {
          ...formValues.flightIdentification,
          aircraftRegistrationMarkings: aircraftRegistrationMarkings ? unparseCsv(aircraftRegistrationMarkings) : null,
        },
        airOperatingCertificate: {
          ...formValues?.airOperatingCertificate,
          certificateFiles: this.transformFiles(formValues?.airOperatingCertificate?.certificateFiles),
        },
        organisationStructure: {
          ...formValues?.organisationStructure,
          evidenceFiles: this.transformFiles(
            (formValues?.organisationStructure as LimitedCompanyOrganisation)?.evidenceFiles,
          ),
        },
        subsidiaryCompanyExist: formValues?.subsidiaryCompanyExist ?? null,
        subsidiaryCompanies: (formValues?.subsidiaryCompanies || []).map((subsidiaryCompany) => {
          const { flightIdentification, airOperatingCertificate } = subsidiaryCompany;
          const aircraftRegistrationMarkings = flightIdentification.aircraftRegistrationMarkings || [];

          return {
            ...subsidiaryCompany,
            flightIdentification:
              aircraftRegistrationMarkings.length > 0
                ? {
                    ...flightIdentification,
                    aircraftRegistrationMarkings: unparseCsv(flightIdentification.aircraftRegistrationMarkings),
                  }
                : flightIdentification,
            airOperatingCertificate: {
              ...airOperatingCertificate,
              certificateFiles: airOperatingCertificate.certificateExist
                ? this.transformFiles(airOperatingCertificate?.certificateFiles)
                : [],
            },
          };
        }),
      } as unknown as EmpCorsiaOperatorDetails;

      this.initiateSubsidiaryCompaniesForm(patchValues);

      this.form.patchValue(patchValues);
    }
  }

  public transformFiles(
    files: AirOperatingCertificateCorsia['certificateFiles'] | LimitedCompanyOrganisation['evidenceFiles'],
  ) {
    return (files || []).map((uuid) => ({
      file: { name: this.store.empCorsiaDelegate.payload.empAttachments[uuid] } as File,
      uuid,
    }));
  }

  public removeSubsidiaryCompanyItem(index: number): void {
    const subsidiaryComaniesForm = this._form.get('subsidiaryCompanies') as FormArray;

    if (subsidiaryComaniesForm) {
      subsidiaryComaniesForm.removeAt(index);

      const operatorDetails = this.form.value as EmpCorsiaOperatorDetails;

      if (operatorDetails.subsidiaryCompanies.length === 0) {
        this._form.get('subsidiaryCompanyExist').patchValue(false);
      }

      this._form.updateValueAndValidity();
    }
  }

  public addSubsidiaryCompany() {
    (this._form?.controls?.subsidiaryCompanies as FormArray)?.controls.push(this.subsidiaryCompaniesForm());
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
      airOperatingCertificateControl.controls.restrictionsExist.enable();

      if (airOperatingCertificate.restrictionsExist) {
        airOperatingCertificateControl.controls.restrictionsDetails.enable();
      } else {
        airOperatingCertificateControl.controls.restrictionsDetails.disable();
      }
    } else {
      airOperatingCertificateControl.controls.certificateNumber.disable();
      airOperatingCertificateControl.controls.issuingAuthority.disable();
      airOperatingCertificateControl.controls.certificateFiles.disable();
      airOperatingCertificateControl.controls.restrictionsDetails.disable();
      airOperatingCertificateControl.controls.restrictionsExist.disable();
    }
  }

  private disableOrganisationStructureFormGroupConditionally(
    legalStatusType: OrganisationStructure['legalStatusType'],
  ) {
    const { organisationStructure } = this._form.value as EmpCorsiaOperatorDetails;
    const organisationStructureControl = this._form.controls.organisationStructure as FormGroup;

    switch (legalStatusType) {
      case 'LIMITED_COMPANY':
        organisationStructureControl.controls.registrationNumber.enable();
        organisationStructureControl.controls.evidenceFiles.enable();
        organisationStructureControl.controls.differentContactLocationExist.enable();

        if ((organisationStructure as LimitedCompanyOrganisation).differentContactLocationExist) {
          organisationStructureControl.controls.differentContactLocation.enable();
        } else {
          organisationStructureControl.controls.differentContactLocation.disable();
        }

        organisationStructureControl.controls.fullName.disable();
        organisationStructureControl.controls.partnershipName.disable();
        organisationStructureControl.controls.partners.disable();

        break;

      case 'INDIVIDUAL':
        organisationStructureControl.controls.registrationNumber.disable();
        organisationStructureControl.controls.evidenceFiles.disable();
        organisationStructureControl.controls.differentContactLocationExist.disable();
        organisationStructureControl.controls.differentContactLocation.disable();
        organisationStructureControl.controls.partnershipName.disable();
        organisationStructureControl.controls.partners.disable();

        organisationStructureControl.controls.fullName.enable();

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

      default:
        organisationStructureControl.controls.organisationLocation.disable();
        organisationStructureControl.controls.registrationNumber.disable();
        organisationStructureControl.controls.evidenceFiles.disable();
        organisationStructureControl.controls.differentContactLocationExist.disable();
        organisationStructureControl.controls.differentContactLocation.disable();
        organisationStructureControl.controls.fullName.disable();
        organisationStructureControl.controls.partnershipName.disable();
        organisationStructureControl.controls.partners.disable();

        break;
    }
  }

  public initiateSubsidiaryCompaniesForm(operatorDetails?: EmpCorsiaOperatorDetails) {
    const form = this.form;
    operatorDetails = operatorDetails || form.value;

    if (operatorDetails?.subsidiaryCompanyExist) {
      if (operatorDetails?.subsidiaryCompanies?.length === 0) {
        this.addSubsidiaryCompany();
        operatorDetails.subsidiaryCompanies.push({} as any);
      } else {
        if (
          operatorDetails?.subsidiaryCompanies?.length !==
          (form?.controls?.subsidiaryCompanies as FormArray)?.controls.length
        ) {
          this.subsidiaryCompaniesCreateInitialForm(operatorDetails?.subsidiaryCompanies).forEach(
            (subsidiaryCompanyFormGrour) => {
              (form?.controls?.subsidiaryCompanies as FormArray)?.controls.push(subsidiaryCompanyFormGrour);
            },
          );
        }
      }
    }
  }

  private disableSubsidiaryCompaniesFormGroupConditionally() {
    const { subsidiaryCompanyExist } = this._form.value as EmpCorsiaOperatorDetails;
    const subsidiaryCompaniesFormArray = this._form.controls
      .subsidiaryCompanies as CorsiaOperatorDetailsFormModel['subsidiaryCompanies'];

    if (subsidiaryCompanyExist) {
      subsidiaryCompaniesFormArray.controls.forEach((subsidiaryCompanyControl) => {
        this.disableSubsidiaryCompanyAirOperatingCertificateControlsConditionally(
          subsidiaryCompanyControl.controls.airOperatingCertificate as FormGroup,
        );

        this.disableSubsidiaryCompanyFlightIdentificationControlsConditionally(
          subsidiaryCompanyControl.controls.flightIdentification as FormGroup,
        );
      });

      subsidiaryCompaniesFormArray.markAsDirty();
      subsidiaryCompaniesFormArray.updateValueAndValidity();
    }
  }

  public disableSubsidiaryCompanyAirOperatingCertificateControlsConditionally(airOperatingCertificate: FormGroup) {
    if (airOperatingCertificate.controls.certificateExist.value) {
      airOperatingCertificate.controls.certificateNumber.enable();
      airOperatingCertificate.controls.issuingAuthority.enable();
      airOperatingCertificate.controls.certificateFiles.enable();
      airOperatingCertificate.controls.restrictionsExist.enable();

      if (airOperatingCertificate.controls.restrictionsExist.value) {
        airOperatingCertificate.controls.restrictionsDetails.enable();
      } else {
        airOperatingCertificate.controls.restrictionsDetails.disable();
      }
    } else {
      airOperatingCertificate.controls.certificateNumber.disable();
      airOperatingCertificate.controls.issuingAuthority.disable();
      airOperatingCertificate.controls.certificateFiles.disable();
      airOperatingCertificate.controls.restrictionsExist.disable();
      airOperatingCertificate.controls.restrictionsDetails.disable();
    }
  }

  public disableSubsidiaryCompanyFlightIdentificationControlsConditionally(flightIdentification: FormGroup) {
    const flightIdentificationType = flightIdentification.controls.flightIdentificationType
      .value as FlightIdentification['flightIdentificationType'];

    if (flightIdentificationType === 'INTERNATIONAL_CIVIL_AVIATION_ORGANISATION') {
      flightIdentification.controls.icaoDesignators.enable();

      flightIdentification.controls.aircraftRegistrationMarkings.disable();
    } else {
      flightIdentification.controls.icaoDesignators.disable();

      flightIdentification.controls.aircraftRegistrationMarkings.enable();
    }
  }

  private flightIdentificationCreateInitialForm() {
    return this.fb.group(
      {
        flightIdentificationType: new FormControl<FlightIdentification['flightIdentificationType']>(null, {
          validators: flightIdentificationTypeValidator,
        }),
        icaoDesignators: new FormControl<FlightIdentification['icaoDesignators']>(null, {
          validators: icaoDesignatorsValidator,
        }),
        aircraftRegistrationMarkings: new FormControl<string>(null, {
          validators: aircraftRegistrationMarkingsValidator,
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
            validators: certificateExistValidator,
          },
        ),
        certificateNumber: new FormControl<EmpCorsiaOperatorDetails['airOperatingCertificate']['certificateNumber']>(
          null,
          {
            validators: certificateNumberValidator,
          },
        ),
        issuingAuthority: new FormControl<EmpCorsiaOperatorDetails['airOperatingCertificate']['issuingAuthority']>(
          null,
          {
            validators: issuingAuthorityValidator,
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
    form.addControl(
      'restrictionsExist',
      new FormControl<keyof EmpCorsiaOperatorDetails['airOperatingCertificate']['restrictionsExist']>(null, {
        validators: restrictionsExistValidator,
      }),
    );
    form.addControl(
      'restrictionsDetails',
      new FormControl<EmpCorsiaOperatorDetails['airOperatingCertificate']['restrictionsDetails']>(null, {
        validators: restrictionsDetailsValidator,
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
          validators: legalStatusTypeValidator,
        }),
        organisationLocation: getLocationOnShoreFormGroup(),
        registrationNumber: new FormControl<LimitedCompanyOrganisation['registrationNumber']>(null, {
          validators: registrationNumberValidator,
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
            validators: differentContactLocationExistValidator,
          },
        ),
        differentContactLocation: getLocationOnShoreFormGroup(),
        fullName: new FormControl<IndividualOrganisation['fullName']>(null, {
          validators: fullNameValidator,
        }),
        partnershipName: new FormControl<PartnershipOrganisation['partnershipName']>(null),
        partners: this.fb.array(organisationStructureCreatePartnersFormArray(partners), {
          validators: partnersValidator,
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
          validators: operatorTypeValidator,
        }),
        flightTypes: new FormControl<ActivitiesDescription['flightTypes']>([], {
          validators: flightTypesValidator,
        }),
        activityDescription: new FormControl<ActivitiesDescription['activityDescription']>(null, {
          validators: activityDescriptionValidator,
        }),
      },
      { updateOn: 'change' },
    ) as unknown as FormGroup<Record<keyof EmpCorsiaOperatorDetails, FormControl>>;
    return form;
  }

  public subsidiaryCompaniesForm(subsidiaryCompany = {} as SubsidiaryCompanyCorsia) {
    return this.fb.group(
      {
        operatorName: this.fb.control(subsidiaryCompany.operatorName || null, operatorNameValidator),
        flightIdentification: this.flightIdentificationCreateInitialForm(),
        airOperatingCertificate: this.airOperatingCertificateCreateInitialForm(),
        companyRegistrationNumber: new FormControl(subsidiaryCompany.companyRegistrationNumber || null, {
          validators: registrationNumberValidator,
        }),
        registeredLocation: new FormGroup({
          type: new FormControl<LocationOnShoreStateDTO['type']>('ONSHORE_STATE'),
          line1: new FormControl(subsidiaryCompany.registeredLocation?.line1 || null, {
            validators: line1Validator,
          }),
          line2: new FormControl(subsidiaryCompany.registeredLocation?.line2 || null),
          city: new FormControl(subsidiaryCompany.registeredLocation?.city || null, {
            validators: cityValidator,
          }),
          state: new FormControl(subsidiaryCompany.registeredLocation?.state || null),
          postcode: new FormControl(subsidiaryCompany.registeredLocation?.postcode || null),
          country: new FormControl(subsidiaryCompany.registeredLocation?.country || null, {
            validators: countryValidator,
          }),
        }),
        flightTypes: new FormControl(subsidiaryCompany.flightTypes || [], {
          validators: flightTypesValidator,
        }),
        activityDescription: new FormControl(subsidiaryCompany.activityDescription || null, {
          validators: activityDescriptionValidator,
        }),
      },
      { updateOn: 'change' },
    );
  }

  private subsidiaryCompaniesCreateInitialForm = (subsidiaryCompanies: SubsidiaryCompanyCorsia[]) => {
    return (subsidiaryCompanies || [])?.map((subsidiaryCompany) => this.subsidiaryCompaniesForm(subsidiaryCompany));
  };

  private buildForm() {
    this._form = this.fb.group(
      {
        operatorName: this.fb.control(null, operatorNameValidator),
        flightIdentification: this.flightIdentificationCreateInitialForm(),
        airOperatingCertificate: this.airOperatingCertificateCreateInitialForm(),
        organisationStructure: this.organisationStructureCreateInitialForm(),
        activitiesDescription: this.activitiesDescriptionCreateInitialForm(),
        subsidiaryCompanyExist: this.fb.control(null, subsidiaryCompanyExistValidator),
        subsidiaryCompanies: this.fb.array([]),
      },
      { updateOn: 'change' },
    );
  }
}
