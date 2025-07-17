import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { UntypedFormArray, UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { BehaviorSubject, map, merge, Observable, shareReplay, Subject, switchMap, takeUntil, tap } from 'rxjs';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { AuthStore, selectUserId } from '@core/store/auth';
import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchBadRequest, ErrorCodes } from '@error/business-errors';

import { GovukSelectOption, GovukTableColumn } from 'govuk-components';

import { RegulatorAuthoritiesService, RegulatorUserAuthorityInfoDTO, RegulatorUsersAuthoritiesInfoDTO } from 'pmrv-api';

import { savePartiallyNotFoundRegulatorError } from './errors/business-error';

@Component({
  selector: 'app-regulators',
  templateUrl: './regulators.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class RegulatorsComponent implements OnInit {
  regulators$: Observable<RegulatorUserAuthorityInfoDTO[]>;
  isEditable$: Observable<boolean>;
  isSummaryDisplayed$ = new BehaviorSubject<boolean>(false);
  authorityStatuses: GovukSelectOption<string>[] = [
    { text: 'Active', value: 'ACTIVE' },
    { text: 'Disabled', value: 'DISABLED' },
  ];
  authorityStatusesAccepted: GovukSelectOption<string>[] = [
    { text: 'Accepted', value: 'ACCEPTED' },
    { text: 'Active', value: 'ACTIVE' },
  ];
  editableCols: GovukTableColumn[] = [
    { field: 'name', header: 'Name', isSortable: true },
    { field: 'jobTitle', header: 'Job title' },
    { field: 'authorityStatus', header: 'Account status' },
    { field: 'deleteBtn', header: undefined },
  ];
  nonEditableCols: GovukTableColumn[] = this.editableCols.slice(0, 2);
  regulatorsForm = this.fb.group({ regulatorsArray: this.fb.array([]) });
  userId$ = this.authStore.pipe(selectUserId);
  refresh$ = new Subject<void>();

  constructor(
    readonly authStore: AuthStore,
    private readonly fb: UntypedFormBuilder,
    private readonly regulatorAuthoritiesService: RegulatorAuthoritiesService,
    private readonly route: ActivatedRoute,
    private readonly destroy$: DestroySubject,
    private readonly businessErrorService: BusinessErrorService,
  ) {}

  get regulatorsArray(): UntypedFormArray {
    return this.regulatorsForm.get('regulatorsArray') as UntypedFormArray;
  }

  ngOnInit(): void {
    const regulatorsManagement$ = merge(
      this.route.data.pipe(map((data: { regulators: RegulatorUsersAuthoritiesInfoDTO }) => data.regulators)),
      this.refresh$.pipe(switchMap(() => this.regulatorAuthoritiesService.getCaRegulators())),
    ).pipe(takeUntil(this.destroy$), shareReplay({ bufferSize: 1, refCount: false }));
    this.regulators$ = regulatorsManagement$.pipe(map((authoritiesInfoDTO) => authoritiesInfoDTO?.caUsers));
    this.isEditable$ = regulatorsManagement$.pipe(map((authoritiesInfoDTO) => authoritiesInfoDTO?.editable));
  }

  saveRegulators(): void {
    if (!this.regulatorsForm.dirty) {
      return;
    }
    if (!this.regulatorsForm.valid) {
      this.isSummaryDisplayed$.next(true);
    } else {
      this.regulatorAuthoritiesService
        .updateCompetentAuthorityRegulatorUsersStatus(
          this.regulatorsArray.controls
            .filter((control) => control.dirty)
            .map((control) => ({
              authorityStatus: control.value.authorityStatus,
              userId: control.value.userId,
            })),
        )
        .pipe(
          catchBadRequest(ErrorCodes.AUTHORITY1003, () =>
            this.businessErrorService.showError(savePartiallyNotFoundRegulatorError),
          ),
          tap(() => this.regulatorsForm.markAsPristine()),
        )
        .subscribe(() => this.refresh$.next());
    }
  }
}
