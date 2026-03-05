import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { filter, first, map, Observable, switchMap, tap } from 'rxjs';

import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchBadRequest, ErrorCodes, isBadRequest } from '@error/business-errors';
import { BackLinkService } from '@shared/back-link/back-link.service';
import { requiredFieldsValidator } from '@shared-user/utils/validators';

import { GovukValidators } from 'govuk-components';

import { CaExternalContactDTO, CaExternalContactsService } from 'pmrv-api';

import { saveNotFoundExternalContactError } from '../../errors/business-error';

@Component({
  selector: 'app-details',
  templateUrl: './details.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DetailsComponent implements OnInit {
  isSummaryDisplayed: boolean;
  form = this.fb.group(
    {
      name: [
        null,
        [
          GovukValidators.required(`Enter the external contact's displayed name`),
          GovukValidators.maxLength(
            100,
            `The external contact's displayed name should not be more than 100 characters`,
          ),
        ],
      ],
      email: [
        null,
        [
          GovukValidators.required(`Enter external contact's email address`),
          GovukValidators.email('Enter an email address in the correct format, like name@example.com'),
          GovukValidators.maxLength(255, 'Email should not be more than 255 characters'),
        ],
      ],
      description: [
        null,
        [
          GovukValidators.required(`Enter external contact's description`),
          GovukValidators.maxLength(100, 'Description should not be more than 100 characters'),
        ],
      ],
    },
    { validators: requiredFieldsValidator() },
  );

  userLoaded$: Observable<CaExternalContactDTO> = this.route.data.pipe(
    map((x) => x?.contact),
    filter((contact) => contact),
    tap((contact) => this.form.patchValue(contact)),
  );

  constructor(
    private readonly fb: UntypedFormBuilder,
    private readonly caExternalContactsService: CaExternalContactsService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly businessErrorService: BusinessErrorService,
    private readonly backLinkService: BackLinkService,
  ) {}

  ngOnInit(): void {
    this.backLinkService.show();
  }

  addExternalContact(): void {
    if (!this.form.valid) {
      this.form.markAllAsTouched();
      this.isSummaryDisplayed = true;
    } else {
      this.route.paramMap
        .pipe(
          first(),
          map((paramMap) => paramMap.get('userId')),
          switchMap((userId) =>
            userId
              ? this.caExternalContactsService.editCaExternalContact(Number(userId), this.form.value)
              : this.caExternalContactsService.createCaExternalContact(this.form.value),
          ),
          catchBadRequest(ErrorCodes.EXTCONTACT1000, () =>
            this.businessErrorService.showError(saveNotFoundExternalContactError),
          ),
        )
        .subscribe({
          next: () => this.router.navigate(['../..'], { relativeTo: this.route, fragment: 'external-contacts' }),
          error: (res: unknown) => this.handleError(res),
        });
    }
  }

  private handleError(res: unknown): void {
    if (isBadRequest(res)) {
      switch (res.error.code) {
        case ErrorCodes.EXTCONTACT1001:
          this.form.get('name').setErrors({ uniqueName: 'Enter a unique displayed name' });
          break;
        case ErrorCodes.EXTCONTACT1002:
          this.form.get('email').setErrors({ uniqueEmail: 'Email address already exists' });
          break;
        case ErrorCodes.EXTCONTACT1003:
          this.form.get('name').setErrors({ uniqueName: 'Enter a unique displayed name' });
          this.form.get('email').setErrors({ uniqueEmail: 'Email address already exists' });
          break;
        default:
          throw res;
      }
      this.isSummaryDisplayed = true;
    } else {
      throw res;
    }
  }
}
