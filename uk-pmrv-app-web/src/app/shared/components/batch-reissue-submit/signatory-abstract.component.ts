import { Injectable } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { map, Observable, tap } from 'rxjs';

import { GovukSelectOption, GovukValidators } from 'govuk-components';

import { RegulatorUserAuthorityInfoDTO, RegulatorUsersAuthoritiesInfoDTO } from 'pmrv-api';

@Injectable()
export abstract class SignatoryAbstractComponent {
  regulators$: Observable<GovukSelectOption<string>[]>;

  form: UntypedFormGroup = this.formBuilder.group({
    signatory: [null, GovukValidators.required('Select a name to appear on the official notice document.')],
  });

  constructor(
    readonly formBuilder: UntypedFormBuilder,
    readonly router: Router,
    readonly route: ActivatedRoute,
  ) {}

  protected init(): void {
    this.regulators$ = this.route.data.pipe(
      map((data: { regulators: RegulatorUsersAuthoritiesInfoDTO }) =>
        data.regulators.caUsers.filter((caUser) => caUser.authorityStatus === 'ACTIVE'),
      ),
      tap((regulators) => {
        this.populateSignatoryFormControl(regulators);
      }),
      map((regulators) =>
        regulators.map((regulator) => ({
          text: `${regulator.firstName} ${regulator.lastName}`,
          value: regulator.userId,
        })),
      ),
    );
  }

  protected abstract patchStore(value): void;

  protected onSubmit(): void {
    if (this.form.dirty) {
      this.patchStore(this.form.value);
    }
    this.router.navigate(['..', 'summary'], { relativeTo: this.route });
  }

  protected abstract populateSignatoryFormControl(regulators: RegulatorUserAuthorityInfoDTO[]): void;
}
