import { inject, Injectable } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';

import { Subject } from 'rxjs';

import { TaskFormProvider } from '@aviation/request-task/task-form.provider';

import { AviationAerEtsComplianceRules } from 'pmrv-api';

import { AviationAerEtsComplianceRulesFormModel } from './ets-compliance-rules.interface';
import {
  createControlActivitiesForm,
  createDataVerificationCompletedForm,
  createDetailsSourceDataVerifiedForm,
  createFlightsCompletenessComparedWithAirTrafficDataForm,
  createFuelConsistencyChecksPerformedForm,
  createMissingDataMethodsAppliedForm,
  createMonitoringApproachAppliedCorrectlyForm,
  createMonitoringPlanRequirementsForm,
  createNonConformitiesForm,
  createProceduresMonitoringPlanForm,
  createRegulatorGuidanceMetForm,
  createReportedDataConsistencyChecksPerformedForm,
  createUkEtsOrderRequirementsForm,
} from './util/form-helpers';

@Injectable()
export class EtsComplianceRulesFormProvider
  implements TaskFormProvider<AviationAerEtsComplianceRules, AviationAerEtsComplianceRulesFormModel>
{
  private fb = inject(FormBuilder);
  private _form: FormGroup;
  private destroy$ = new Subject<void>();

  get form() {
    if (!this._form) {
      this._buildForm();
    }

    return this._form;
  }

  destroyForm() {
    this.destroy$.next();
    this.destroy$.complete();
    this._form = null;
  }

  setFormValue(etsComplianceRules: AviationAerEtsComplianceRules | undefined) {
    if (etsComplianceRules) {
      const value: AviationAerEtsComplianceRules = {
        monitoringPlanRequirementsMet: etsComplianceRules.monitoringPlanRequirementsMet ?? null,
        monitoringPlanRequirementsNotMetReason: etsComplianceRules.monitoringPlanRequirementsNotMetReason ?? null,
        ukEtsOrderRequirementsMet: etsComplianceRules.ukEtsOrderRequirementsMet ?? null,
        ukEtsOrderRequirementsNotMetReason: etsComplianceRules.ukEtsOrderRequirementsNotMetReason ?? null,
        detailSourceDataVerified: etsComplianceRules.detailSourceDataVerified ?? null,
        detailSourceDataNotVerifiedReason: etsComplianceRules.detailSourceDataNotVerifiedReason ?? null,
        partOfSiteVerification: etsComplianceRules.partOfSiteVerification ?? null,
        controlActivitiesDocumented: etsComplianceRules.controlActivitiesDocumented ?? null,
        controlActivitiesNotDocumentedReason: etsComplianceRules.controlActivitiesNotDocumentedReason ?? null,
        proceduresMonitoringPlanDocumented: etsComplianceRules.proceduresMonitoringPlanDocumented ?? null,
        proceduresMonitoringPlanNotDocumentedReason:
          etsComplianceRules.proceduresMonitoringPlanNotDocumentedReason ?? null,
        dataVerificationCompleted: etsComplianceRules.dataVerificationCompleted ?? null,
        dataVerificationNotCompletedReason: etsComplianceRules.dataVerificationNotCompletedReason ?? null,
        monitoringApproachAppliedCorrectly: etsComplianceRules.monitoringApproachAppliedCorrectly ?? null,
        monitoringApproachNotAppliedCorrectlyReason:
          etsComplianceRules.monitoringApproachNotAppliedCorrectlyReason ?? null,
        flightsCompletenessComparedWithAirTrafficData:
          etsComplianceRules.flightsCompletenessComparedWithAirTrafficData ?? null,
        flightsCompletenessNotComparedWithAirTrafficDataReason:
          etsComplianceRules.flightsCompletenessNotComparedWithAirTrafficDataReason ?? null,
        reportedDataConsistencyChecksPerformed: etsComplianceRules.reportedDataConsistencyChecksPerformed ?? null,
        reportedDataConsistencyChecksNotPerformedReason:
          etsComplianceRules.reportedDataConsistencyChecksNotPerformedReason ?? null,
        fuelConsistencyChecksPerformed: etsComplianceRules.fuelConsistencyChecksPerformed ?? null,
        fuelConsistencyChecksNotPerformedReason: etsComplianceRules.fuelConsistencyChecksNotPerformedReason ?? null,
        missingDataMethodsApplied: etsComplianceRules.missingDataMethodsApplied ?? null,
        missingDataMethodsNotAppliedReason: etsComplianceRules.missingDataMethodsNotAppliedReason ?? null,
        regulatorGuidanceMet: etsComplianceRules.regulatorGuidanceMet ?? null,
        regulatorGuidanceNotMetReason: etsComplianceRules.regulatorGuidanceNotMetReason ?? null,
        nonConformities: etsComplianceRules.nonConformities ?? null,
      };

      this.form.setValue(value);
    }
  }

  getFormValue(): AviationAerEtsComplianceRules {
    return this.form.value;
  }

  private _buildForm() {
    this._form = this.fb.group(
      {
        ...createMonitoringPlanRequirementsForm(this.destroy$),
        ...createUkEtsOrderRequirementsForm(this.destroy$),
        ...createDetailsSourceDataVerifiedForm(this.destroy$),
        ...createControlActivitiesForm(this.destroy$),
        ...createProceduresMonitoringPlanForm(this.destroy$),
        ...createDataVerificationCompletedForm(this.destroy$),
        ...createMonitoringApproachAppliedCorrectlyForm(this.destroy$),
        ...createFlightsCompletenessComparedWithAirTrafficDataForm(this.destroy$),
        ...createReportedDataConsistencyChecksPerformedForm(this.destroy$),
        ...createFuelConsistencyChecksPerformedForm(this.destroy$),
        ...createMissingDataMethodsAppliedForm(this.destroy$),
        ...createRegulatorGuidanceMetForm(this.destroy$),
        ...createNonConformitiesForm(),
      },
      { updateOn: 'change' },
    );
  }
}
