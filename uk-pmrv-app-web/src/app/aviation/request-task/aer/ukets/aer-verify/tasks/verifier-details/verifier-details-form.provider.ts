import { inject, Injectable } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';

import { Subject } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TaskFormProvider } from '@aviation/request-task/task-form.provider';

import { GovukValidators } from 'govuk-components';

import {
  AviationAerUkEtsApplicationVerificationSubmitRequestTaskPayload,
  AviationAerUkEtsVerificationReport,
  AviationAerVerificationTeamDetails,
  AviationAerVerifierContact,
} from 'pmrv-api';

interface AviationAerUketsVerificationReportFormModel {
  name: FormControl<AviationAerVerifierContact['name'] | null>;
  email: FormControl<AviationAerVerifierContact['email'] | null>;
  phoneNumber: FormControl<AviationAerVerifierContact['phoneNumber'] | null>;

  leadEtsAuditor: FormControl<AviationAerVerificationTeamDetails['leadEtsAuditor'] | null>;
  etsAuditors: FormControl<AviationAerVerificationTeamDetails['etsAuditors'] | null>;
  etsTechnicalExperts: FormControl<AviationAerVerificationTeamDetails['etsTechnicalExperts'] | null>;
  independentReviewer: FormControl<AviationAerVerificationTeamDetails['independentReviewer'] | null>;
  technicalExperts: FormControl<AviationAerVerificationTeamDetails['technicalExperts'] | null>;
  authorisedSignatoryName: FormControl<AviationAerVerificationTeamDetails['authorisedSignatoryName'] | null>;
}

@Injectable()
export class VerifierDetailsFormProvider
  implements TaskFormProvider<AviationAerUkEtsVerificationReport, AviationAerUketsVerificationReportFormModel>
{
  private store = inject(RequestTaskStore);
  private fb = inject(FormBuilder);
  private _form: FormGroup<AviationAerUketsVerificationReportFormModel>;
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

  setFormValue(aviationAerUkEtsVerificationReport: AviationAerUkEtsVerificationReport | undefined) {
    if (aviationAerUkEtsVerificationReport) {
      const value = {
        name: aviationAerUkEtsVerificationReport.verifierContact?.name ?? null,
        email: aviationAerUkEtsVerificationReport.verifierContact?.email ?? null,
        phoneNumber: aviationAerUkEtsVerificationReport.verifierContact?.phoneNumber ?? null,

        leadEtsAuditor: aviationAerUkEtsVerificationReport.verificationTeamDetails?.leadEtsAuditor ?? null,
        etsAuditors: aviationAerUkEtsVerificationReport.verificationTeamDetails?.etsAuditors ?? null,
        etsTechnicalExperts: aviationAerUkEtsVerificationReport.verificationTeamDetails?.etsTechnicalExperts ?? null,
        independentReviewer: aviationAerUkEtsVerificationReport.verificationTeamDetails?.independentReviewer ?? null,
        technicalExperts: aviationAerUkEtsVerificationReport.verificationTeamDetails?.technicalExperts ?? null,
        authorisedSignatoryName:
          aviationAerUkEtsVerificationReport.verificationTeamDetails?.authorisedSignatoryName ?? null,
      };

      this.form.setValue(value as any);
    }
  }

  getFormValue(): AviationAerUkEtsVerificationReport {
    const state = this.store.getState();

    const aviationAerUkEtsVerificationReport = (
      state.requestTaskItem.requestTask.payload as AviationAerUkEtsApplicationVerificationSubmitRequestTaskPayload
    ).verificationReport;

    const formValue = this.form.value;

    return {
      verificationReport: {
        ...aviationAerUkEtsVerificationReport,
        verifierContact: { name: formValue.name, email: formValue.email, phoneNumber: formValue.phoneNumber },
        verificationTeamDetails: {
          leadEtsAuditor: formValue.leadEtsAuditor,
          etsAuditors: formValue.etsAuditors,
          etsTechnicalExperts: formValue.etsTechnicalExperts,
          independentReviewer: formValue.independentReviewer,
          technicalExperts: formValue.technicalExperts,
          authorisedSignatoryName: formValue.authorisedSignatoryName,
        },
      },
    } as any;
  }

  private _buildForm() {
    this._form = this._createVerifierDetailsGroup();
  }

  private _createVerifierDetailsGroup(): FormGroup<AviationAerUketsVerificationReportFormModel> {
    const state = this.store.getState();

    const aviationAerUkEtsVerificationReport = (
      state.requestTaskItem.requestTask.payload as AviationAerUkEtsApplicationVerificationSubmitRequestTaskPayload
    ).verificationReport;

    return this.fb.group(
      {
        name: new FormControl<AviationAerVerifierContact['name'] | null>(
          {
            value: aviationAerUkEtsVerificationReport.verifierContact?.name ?? null,
            disabled: !state.isEditable,
          },
          {
            validators: [
              GovukValidators.required(`Enter the verifier's name`),
              GovukValidators.maxLength(500, `The verifier's name should not be more than 500 characters`),
            ],
          },
        ),
        email: new FormControl<AviationAerVerifierContact['email'] | null>(
          {
            value: aviationAerUkEtsVerificationReport.verifierContact?.email ?? null,
            disabled: !state.isEditable,
          },
          {
            validators: [
              GovukValidators.required(`Enter the verifier's email address`),
              GovukValidators.email('Enter an email address in the correct format, like name@example.com'),
              GovukValidators.maxLength(255, 'Email should not be more than 255 characters'),
            ],
          },
        ),
        phoneNumber: new FormControl<AviationAerVerifierContact['phoneNumber'] | null>(
          {
            value: aviationAerUkEtsVerificationReport.verifierContact?.phoneNumber ?? null,
            disabled: !state.isEditable,
          },
          {
            validators: [
              GovukValidators.required(`Enter the verifier's phone number`),
              GovukValidators.maxLength(255, `Verifier's phone number should not be more than 255 characters`),
            ],
          },
        ),
        leadEtsAuditor: new FormControl<AviationAerVerificationTeamDetails['leadEtsAuditor'] | null>(
          {
            value: aviationAerUkEtsVerificationReport.verificationTeamDetails?.leadEtsAuditor ?? null,
            disabled: !state.isEditable,
          },
          {
            validators: [
              GovukValidators.required('Enter the name of the lead ETS auditor'),
              GovukValidators.maxLength(500, 'The lead ETS auditor name should not be more than 500 characters'),
            ],
          },
        ),
        etsAuditors: new FormControl<AviationAerVerificationTeamDetails['etsAuditors'] | null>(
          {
            value: aviationAerUkEtsVerificationReport.verificationTeamDetails?.etsAuditors ?? null,
            disabled: !state.isEditable,
          },
          {
            validators: [
              GovukValidators.required('Enter the name of the ETS auditor'),
              GovukValidators.maxLength(10000, 'The ETS auditor name should not be more than 10.000 characters'),
            ],
          },
        ),
        etsTechnicalExperts: new FormControl<AviationAerVerificationTeamDetails['etsTechnicalExperts'] | null>(
          {
            value: aviationAerUkEtsVerificationReport.verificationTeamDetails?.etsTechnicalExperts ?? null,
            disabled: !state.isEditable,
          },
          {
            validators: [
              GovukValidators.required('Enter the name of the technical experts (ETS auditor)'),
              GovukValidators.maxLength(
                10000,
                'The technical experts (ETS auditor) name should not be more than 10.000 characters',
              ),
            ],
          },
        ),
        independentReviewer: new FormControl<AviationAerVerificationTeamDetails['independentReviewer'] | null>(
          {
            value: aviationAerUkEtsVerificationReport.verificationTeamDetails?.independentReviewer ?? null,
            disabled: !state.isEditable,
          },
          {
            validators: [
              GovukValidators.required('Enter the name of the Independent Reviewer'),
              GovukValidators.maxLength(
                10000,
                'The Independent Reviewer name should not be more than 10.000 characters',
              ),
            ],
          },
        ),
        technicalExperts: new FormControl<AviationAerVerificationTeamDetails['technicalExperts'] | null>(
          {
            value: aviationAerUkEtsVerificationReport.verificationTeamDetails?.technicalExperts ?? null,
            disabled: !state.isEditable,
          },
          {
            validators: [
              GovukValidators.required('Enter the names of the Technical Experts (Independent Review)'),
              GovukValidators.maxLength(
                10000,
                'The Technical Experts (Independent Review) names should not be more than 10.000 characters',
              ),
            ],
          },
        ),
        authorisedSignatoryName: new FormControl<AviationAerVerificationTeamDetails['authorisedSignatoryName'] | null>(
          {
            value: aviationAerUkEtsVerificationReport.verificationTeamDetails?.authorisedSignatoryName ?? null,
            disabled: !state.isEditable,
          },
          {
            validators: [
              GovukValidators.required('Enter the name of the authorised signatory'),
              GovukValidators.maxLength(500, 'The authorised signatory name should not be more than 500 characters'),
            ],
          },
        ),
      },
      { updateOn: 'change' },
    );
  }
}
