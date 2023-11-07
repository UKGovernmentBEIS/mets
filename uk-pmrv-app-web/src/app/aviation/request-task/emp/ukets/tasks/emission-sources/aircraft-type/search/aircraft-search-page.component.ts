import { CommonModule } from '@angular/common';
import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject, Subject, takeUntil, tap } from 'rxjs';

import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { AircraftTypeDescriptionPipe } from '@aviation/shared/pipes/aircraft-type-description.pipe';
import { BackLinkService } from '@shared/back-link/back-link.service';
import { SharedModule } from '@shared/shared.module';

import { GovukValidators } from 'govuk-components';

import { AircraftTypeDTO, AircraftTypeSearchCriteria, AircraftTypesService } from 'pmrv-api';

import { TASK_FORM_PROVIDER } from '../../../../../../task-form.provider';
import { EmissionSourcesFormModel } from '../../emission-sources-form.model';
import { EMP_AIRCRAFT_TYPE_FORM } from '../aircraft-type-form.provider';

type AircraftTypeSearchVm = {
  filters: { page: number; pageSize: number; totalResults: number };
  aircraftTypes: AircraftTypeDTO[];
};
const PAGE_SIZE = 30;
// eslint-disable-next-line @angular-eslint/prefer-on-push-component-change-detection
@Component({
  selector: 'app-aircraft-search',
  templateUrl: './aircraft-search-page.component.html',
  standalone: true,
  imports: [CommonModule, SharedModule, AircraftTypeDescriptionPipe, ReturnToLinkComponent],
})
export class AircraftSearchPageComponent implements OnInit, OnDestroy {
  // page size is static as described in respective the ticket.

  pageSize = PAGE_SIZE;
  aircraftTypesVm$ = new BehaviorSubject<AircraftTypeSearchVm | null>(null);
  readonly destroy$ = new Subject<void>();
  router = inject(Router);
  route = inject(ActivatedRoute);
  fb = inject(FormBuilder);
  apiClient = inject(AircraftTypesService);
  emissionSourcesForm = inject<FormGroup<EmissionSourcesFormModel>>(TASK_FORM_PROVIDER);
  aircraftTypeForm = inject(EMP_AIRCRAFT_TYPE_FORM).form;
  private backLinkService = inject(BackLinkService);
  showSearchError$ = new BehaviorSubject(false);
  noResultsError$ = new BehaviorSubject(false);
  selectedAircraftError$ = new BehaviorSubject(false);
  form = this.fb.group(
    {
      searchTerm: [''],
      selectedAircraftType: [null, GovukValidators.required('Select the aircraft type')],
    },
    { updateOn: 'change' },
  );
  ngOnInit(): void {
    this.backLinkService.show();
    this.route.queryParamMap.pipe(takeUntil(this.destroy$)).subscribe((queryParamsMap) => {
      const pageParam = queryParamsMap.get('page') ?? 1;
      const termParam = queryParamsMap.get('term');
      if (pageParam && typeof termParam === 'string') {
        const page = +pageParam;
        const searchCriteria = this.createSearchCriteria(page, termParam);
        this.apiClient
          .getAircraftTypes(searchCriteria)
          .pipe(
            tap((r) => {
              this.noResultsError$.next(r.total === 0);
              this.aircraftTypesVm$.next({
                aircraftTypes: r.aircraftTypes,
                filters: { page, pageSize: this.pageSize, totalResults: r.total },
              });
            }),
          )
          .subscribe();
      }
    });
  }
  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
  get editIndex(): number | null {
    const idx = this.route.snapshot.paramMap.get('aircraftTypeIndex');
    if (!idx) return null;
    return Number(idx);
  }
  private createSearchCriteria(page: number, term: string): AircraftTypeSearchCriteria | never {
    return {
      pageSize: this.pageSize,
      pageNumber: page - 1,
      term,
      excludedAircraftTypes: getExcludedAircraftTypes(this.emissionSourcesForm, this.editIndex),
    };
  }
  changePage(page: number) {
    const currentPage = this.aircraftTypesVm$.getValue()?.filters.page;
    // We subtract one because pages come indexed as 1 while the api needs them indexed 0
    if (this.form.valid && page != currentPage) {
      this.router.navigate([], {
        queryParams: {
          ...this.route.snapshot.queryParams,
          page,
        },
        relativeTo: this.route,
      });
    }
  }
  private navigateToAircraftForm() {
    const queryParams = { ...this.route.snapshot.queryParams, term: null };
    typeof this.editIndex === 'number'
      ? this.router.navigate(['../edit', this.editIndex], { relativeTo: this.route, queryParams })
      : this.router.navigate(['../add'], { relativeTo: this.route, queryParams });
  }
  onSearch() {
    const term = this.form.getRawValue().searchTerm;
    if (term.length > 0 && term.length < 3) {
      this.showSearchError$.next(true);
      return;
    } else {
      this.showSearchError$.next(false);
    }
    this.router.navigate([], {
      queryParams: {
        ...this.route.snapshot.queryParams,
        page: null,
        term: this.form.value.searchTerm || '',
      },
      relativeTo: this.route,
    });
  }
  onSubmit() {
    if (this.aircraftTypesVm$.getValue()) {
      const aircraftTypes = this.aircraftTypesVm$.getValue();
      if (aircraftTypes.aircraftTypes.length && !this.form.getRawValue().selectedAircraftType) {
        this.selectedAircraftError$.next(true);
        return;
      }
      this.aircraftTypeForm.patchValue({ aircraftTypeInfo: this.form.controls.selectedAircraftType.value });
      this.navigateToAircraftForm();
    }
  }
}
function getExcludedAircraftTypes(
  form: FormGroup<EmissionSourcesFormModel>,
  editIndex: number | null,
): AircraftTypeDTO[] {
  const excludedAircraftTypes = form.controls.aircraftTypes.value.map((at) => at.aircraftTypeInfo);
  return typeof editIndex === 'number' ? excludedAircraftTypes.splice(editIndex, 1) : excludedAircraftTypes;
}
