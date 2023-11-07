import { ChangeDetectionStrategy, Component, Inject, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject, EMPTY, map, takeUntil } from 'rxjs';

import { HttpStatuses } from '@error/http-status';

import { VerificationBodiesService, VerificationBodyDTO } from 'pmrv-api';

import { DestroySubject } from '../../core/services/destroy-subject.service';
import { BusinessErrorService } from '../../error/business-error/business-error.service';
import { catchBadRequest, catchElseRethrow, ErrorCodes } from '../../error/business-errors';
import { BackLinkService } from '../../shared/back-link/back-link.service';
import { saveNotFoundVerificationBodyError } from '../errors/business-error';
import { VERIFICATION_BODY_FORM, verificationBodyFormFactory } from '../form/form.factory';

@Component({
  selector: 'app-details',
  templateUrl: './details.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject, verificationBodyFormFactory],
})
export class DetailsComponent implements OnInit {
  isSummaryDisplayed$ = new BehaviorSubject<boolean>(false);
  readonly verificationBodyId$ = this.route.paramMap.pipe(
    map((paramMap) => Number(paramMap.get('verificationBodyId'))),
  );

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
        map((data: { verificationBody: VerificationBodyDTO }) => data.verificationBody),
      )
      .subscribe((res) => {
        this.form.setValue({
          id: res.id,
          details: {
            name: res.name,
            accreditationRefNum: res.accreditationReferenceNumber,
            address: { ...res.address, line2: res.address.line2 ?? '' },
          },
          types: res.emissionTradingSchemes,
        });
      });
  }

  save(): void {
    if (this.form.valid) {
      this.verificationBodiesService
        .updateVerificationBody({
          name: this.form.get('details.name').value,
          accreditationReferenceNumber: this.form.get('details.accreditationRefNum').value,
          address: this.form.get('details.address').value,
          emissionTradingSchemes: this.form.get('types').value,
          id: this.form.get('id').value,
        })
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
}
