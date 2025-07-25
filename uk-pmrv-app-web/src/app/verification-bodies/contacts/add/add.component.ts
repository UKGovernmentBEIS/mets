import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { BehaviorSubject, EMPTY, first, switchMap, takeUntil } from 'rxjs';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchBadRequest, catchElseRethrow, ErrorCodes } from '@error/business-errors';
import { HttpStatuses } from '@error/http-status';
import { BackLinkService } from '@shared/back-link/back-link.service';

import { GovukValidators } from 'govuk-components';

import { VerifierUsersInvitationService } from 'pmrv-api';

import { disabledVerificationBodyError } from '../../errors/business-error';

@Component({
  selector: 'app-add',
  templateUrl: './add.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class AddComponent implements OnInit {
  readonly isSummaryDisplayed$ = new BehaviorSubject<boolean>(false);
  readonly confirmedAddedVerifier$ = new BehaviorSubject<string>(null);
  readonly form = this.fb.group({
    firstName: [
      null,
      [
        GovukValidators.required('Enter user first name'),
        GovukValidators.maxLength(255, 'The first name should not be larger than 255 characters'),
      ],
    ],
    lastName: [
      null,
      [
        GovukValidators.required('Enter user last name'),
        GovukValidators.maxLength(255, 'The last name should not be larger than 255 characters'),
      ],
    ],
    phoneNumber: [
      null,
      [
        GovukValidators.required('Enter user telephone number'),
        GovukValidators.maxLength(255, 'The telephone number should not be larger than 255 characters'),
      ],
    ],
    mobileNumber: [],
    email: [
      null,
      [
        GovukValidators.required('Enter user email address'),
        GovukValidators.email('Enter an email address in the correct format, like name@example.com'),
        GovukValidators.maxLength(255, 'The email address should not be larger than 255 characters'),
      ],
    ],
  });

  constructor(
    private readonly fb: UntypedFormBuilder,
    private readonly verifierUsersInvitationService: VerifierUsersInvitationService,
    private readonly route: ActivatedRoute,
    private readonly businessErrorService: BusinessErrorService,
    private readonly destroy$: DestroySubject,
    private readonly backLinkService: BackLinkService,
  ) {}

  ngOnInit(): void {
    this.confirmedAddedVerifier$.pipe(takeUntil(this.destroy$)).subscribe((res) => {
      if (res) {
        this.backLinkService.hide();
      } else {
        this.backLinkService.show();
      }
    });
  }

  onSubmit(): void {
    this.isSummaryDisplayed$.next(true);

    if (this.form.valid) {
      this.route.paramMap
        .pipe(
          first(),
          switchMap((paramMap) =>
            this.verifierUsersInvitationService.inviteVerifierAdminUserByVerificationBodyId(
              Number(paramMap.get('verificationBodyId')),
              this.form.value,
            ),
          ),
          catchBadRequest([ErrorCodes.USER1001, ErrorCodes.AUTHORITY1005, ErrorCodes.AUTHORITY1015], () => {
            this.form.get('email').setErrors({ unique: 'This user email already exists in the service' });

            return EMPTY;
          }),
          catchElseRethrow(
            (error) => error.status === HttpStatuses.NotFound,
            () => this.businessErrorService.showError(disabledVerificationBodyError),
          ),
        )
        .subscribe(() => this.confirmedAddedVerifier$.next(this.form.get('email').value));
    }
  }
}
