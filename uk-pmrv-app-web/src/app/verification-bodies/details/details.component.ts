import { ChangeDetectionStrategy, Component, Inject, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject, EMPTY, map, takeUntil } from 'rxjs';

import { HttpStatuses } from '@error/http-status';
import { vbTypes } from '@shared/pipes/verification-body-type.pipe';

import { GovukValidators } from 'govuk-components';

import { VerificationBodiesService, VerificationBodyEmissionSchemeDTO, VerificationBodyUpdateDTO } from 'pmrv-api';

import { DestroySubject } from '../../core/services/destroy-subject.service';
import { BusinessErrorService } from '../../error/business-error/business-error.service';
import { catchBadRequest, catchElseRethrow, ErrorCodes } from '../../error/business-errors';
import { BackLinkService } from '../../shared/back-link/back-link.service';
import { saveNotFoundVerificationBodyError } from '../errors/business-error';
import { VERIFICATION_BODY_FORM, verificationBodyFormFactory } from '../form/form.factory';

@Component({
  selector: 'app-details',
  standalone: false,
  templateUrl: './details.component.html',
  providers: [DestroySubject, verificationBodyFormFactory],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DetailsComponent implements OnInit {
  isSummaryDisplayed$ = new BehaviorSubject<boolean>(false);
  readonly verificationBodyId$ = this.route.paramMap.pipe(
    map((paramMap) => Number(paramMap.get('verificationBodyId'))),
  );
  typeOptions = vbTypes;

  constructor(
    @Inject(VERIFICATION_BODY_FORM) public readonly form: UntypedFormGroup,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly verificationBodiesService: VerificationBodiesService,
    private readonly fb: UntypedFormBuilder,
    private readonly destroy$: DestroySubject,
    private readonly businessErrorService: BusinessErrorService,
    private readonly backLinkService: BackLinkService,
  ) {
    form.addControl('id', this.fb.control(null));
  }

  ngOnInit(): void {
    this.backLinkService.show('verification-bodies');
    this.route.data
      .pipe(
        takeUntil(this.destroy$),
        map((data) => data.verificationBody as VerificationBodyUpdateDTO),
      )
      .subscribe((res) => {
        const flatTypes = res.verificationBodyEmissionSchemes ?? [];

        const accreditationRefNums = Object.fromEntries(
          flatTypes.map((t) => [
            `accreditationRefNum_${t.emissionTradingScheme}`,
            t.accreditationReferenceNumber ?? null,
          ]),
        );

        const accreditationNames = Object.fromEntries(
          flatTypes.map((t) => [`accreditationName_${t.emissionTradingScheme}`, t.accreditationName ?? null]),
        );

        this.form.patchValue({
          id: res.id,
          details: {
            name: res.name,
            address: { ...res.address, line2: res.address.line2 ?? '' },
          },
          types: flatTypes.map((t) => t.emissionTradingScheme),
          ...accreditationRefNums,
          ...accreditationNames,
        });
      });

    const typesControl = this.form.get('types');

    typesControl.valueChanges.pipe(takeUntil(this.destroy$)).subscribe((selectedValues: string[]) => {
      this.updateAccreditationValidators(selectedValues);
    });

    this.updateAccreditationValidators(typesControl.value ?? []);
  }

  save(): void {
    if (this.form.valid) {
      const selectedTypes: string[] = this.form.get('types').value;

      const verificationBodyEmissionSchemes: VerificationBodyEmissionSchemeDTO[] = selectedTypes.map(
        (emissionTradingScheme) => ({
          emissionTradingScheme: emissionTradingScheme as VerificationBodyEmissionSchemeDTO['emissionTradingScheme'],
          accreditationReferenceNumber: this.form.get(`accreditationRefNum_${emissionTradingScheme}`).value,
          accreditationName: this.form.get(`accreditationName_${emissionTradingScheme}`).value,
        }),
      );

      this.verificationBodiesService
        .updateVerificationBody({
          id: this.form.get('id').value,
          name: this.form.get('details.name').value,
          address: this.form.get('details.address').value,
          verificationBodyEmissionSchemes,
        } as VerificationBodyUpdateDTO)
        .pipe(
          catchElseRethrow(
            (res) => res.status === HttpStatuses.NotFound,
            () => this.businessErrorService.showError(saveNotFoundVerificationBodyError),
          ),
          catchBadRequest(ErrorCodes.VERBODY1001, () => {
            this.form.get('details.accreditationRefNum').setErrors({
              uniqueAccred: 'Enter a unique Accreditation reference number',
            });
            this.isSummaryDisplayed$.next(true);
            return EMPTY;
          }),
        )
        .subscribe(() => this.router.navigate(['..'], { relativeTo: this.route }));
    } else {
      this.isSummaryDisplayed$.next(true);
    }
  }

  private updateAccreditationValidators(selectedValues: string[]): void {
    this.typeOptions.forEach((option) => {
      const refNumControl = this.form.get(`accreditationRefNum_${option}`);
      const nameControl = this.form.get(`accreditationName_${option}`);

      if (selectedValues.includes(option)) {
        refNumControl.setValidators([
          GovukValidators.required('Enter the Accreditation reference number'),
          GovukValidators.maxLength(
            25,
            'Accreditation reference number must not be more than 25 characters long, including spaces',
          ),
        ]);
        nameControl.setValidators([
          GovukValidators.required('Enter the name of the National Accreditation Body'),
          GovukValidators.maxLength(255, 'Accreditation name must not be more than 255 characters long'),
        ]);
      } else {
        refNumControl.clearValidators();
        refNumControl.reset(null, { emitEvent: false });
        nameControl.clearValidators();
        nameControl.reset(null, { emitEvent: false });
      }

      refNumControl.updateValueAndValidity({ emitEvent: false });
      nameControl.updateValueAndValidity({ emitEvent: false });
    });
  }
}
