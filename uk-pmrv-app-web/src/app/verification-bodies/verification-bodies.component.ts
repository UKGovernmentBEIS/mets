import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { UntypedFormArray, UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { BehaviorSubject, merge, Observable, pluck, shareReplay, Subject, switchMapTo, tap } from 'rxjs';

import { GovukSelectOption, GovukTableColumn } from 'govuk-components';

import { VerificationBodiesService, VerificationBodyInfoDTO, VerificationBodyInfoResponseDTO } from 'pmrv-api';

import { BusinessErrorService } from '../error/business-error/business-error.service';
import { catchBadRequest, ErrorCodes } from '../error/business-errors';
import { savePartiallyNotFoundVerificationBodyError } from './errors/business-error';

type TableData = Pick<VerificationBodyInfoDTO, 'name' | 'status'> & { deleteBtn?: any };

@Component({
  selector: 'app-verification-bodies',
  templateUrl: './verification-bodies.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class VerificationBodiesComponent implements OnInit {
  verificationBodies$: Observable<VerificationBodyInfoDTO[]>;
  isEditable$: Observable<boolean>;
  isSummaryDisplayed$ = new BehaviorSubject<boolean>(false);

  verificationBodiesForm = this.fb.group({ verificationBodies: this.fb.array([]) });

  accountStatuses: GovukSelectOption<string>[] = [
    { text: 'Active', value: 'ACTIVE' },
    { text: 'Disabled', value: 'DISABLED' },
  ];

  editableCols: GovukTableColumn<TableData>[] = [
    { field: 'name', header: 'Verification body name' },
    { field: 'status', header: 'Account status' },
    { field: 'deleteBtn', header: undefined },
  ];

  nonEditableCols: GovukTableColumn<TableData>[] = [
    { field: 'name', header: 'Name', isSortable: false },
    { field: 'status', header: 'Account status' },
  ];

  refresh$ = new Subject<void>();

  constructor(
    private readonly fb: UntypedFormBuilder,
    private readonly route: ActivatedRoute,
    private readonly verificationBodiesService: VerificationBodiesService,
    private readonly businessErrorService: BusinessErrorService,
  ) {}

  get verificationBodies(): UntypedFormArray {
    return this.verificationBodiesForm.get('verificationBodies') as UntypedFormArray;
  }

  ngOnInit(): void {
    const initialVerificationBodies$ = (
      this.route.data as Observable<{
        verificationBodies: VerificationBodyInfoResponseDTO;
      }>
    ).pipe(pluck('verificationBodies'));

    const refreshedVerificationBodies$ = this.refresh$.pipe(
      switchMapTo(this.verificationBodiesService.getVerificationBodies()),
    );

    const verificationBodiesManagement$ = merge(initialVerificationBodies$, refreshedVerificationBodies$).pipe(
      shareReplay({ bufferSize: 1, refCount: true }),
    );

    this.isEditable$ = verificationBodiesManagement$.pipe(pluck('editable'));
    this.verificationBodies$ = verificationBodiesManagement$.pipe(
      pluck('verificationBodies'),
      tap((verificationBodies: VerificationBodyInfoDTO[]) =>
        this.verificationBodiesForm.setControl(
          'verificationBodies',
          this.fb.array(
            verificationBodies
              .sort((a, b) => a.name.localeCompare(b.name, 'en-GB', { sensitivity: 'base' }))
              .map(({ id, status }) => this.fb.group({ id, status })),
          ),
        ),
      ),
      tap(() => this.verificationBodiesForm.markAsPristine()),
    );
  }

  saveVerificationBodies(): void {
    if (!this.verificationBodiesForm.dirty) {
      return;
    }
    if (!this.verificationBodiesForm.valid) {
      this.isSummaryDisplayed$.next(true);
    } else {
      this.verificationBodiesService
        .updateVerificationBodiesStatus(
          this.verificationBodies.controls
            .filter((cοntrol) => cοntrol.dirty)
            .map((control) => ({
              status: control.value.status,
              id: control.value.id,
            })),
        )
        .pipe(
          catchBadRequest(ErrorCodes.VERBODY1002, () =>
            this.businessErrorService.showError(savePartiallyNotFoundVerificationBodyError),
          ),
          tap(() => this.verificationBodiesForm.markAsPristine()),
        )
        .subscribe(() => this.refresh$.next());
    }
  }
}
