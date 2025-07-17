import { ChangeDetectionStrategy, Component } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import {
  BehaviorSubject,
  combineLatest,
  distinctUntilChanged,
  filter,
  map,
  Observable,
  shareReplay,
  switchMap,
  takeUntil,
  tap,
  withLatestFrom,
} from 'rxjs';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { AuthStore, selectCurrentDomain } from '@core/store/auth';

import { GovukTableColumn, GovukValidators } from 'govuk-components';

import { DocumentTemplatesService, NotificationTemplatesService, TemplateSearchResults } from 'pmrv-api';

@Component({
  selector: 'app-templates',
  templateUrl: './templates.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class TemplatesComponent {
  readonly pageSize = 30;
  private term$ = new BehaviorSubject<string>(null);
  private readonly currentDomain$ = this.authStore.pipe(selectCurrentDomain, takeUntil(this.destroy$));

  operatorTabCols: GovukTableColumn[] = [
    { field: 'name', header: 'Template name' },
    { field: 'workflow', header: 'Workflow' },
    { field: 'lastUpdatedDate', header: 'Last changed' },
  ];
  regulatorTabCols: GovukTableColumn[] = this.operatorTabCols.filter((col) => col.field !== 'operatorType');
  searchForm: UntypedFormGroup = this.fb.group({
    term: [
      null,
      {
        validators: [
          GovukValidators.minLength(3, 'Enter at least 3 characters'),
          GovukValidators.maxLength(256, 'Enter up to 256 characters'),
        ],
      },
    ],
  });

  currentPage$ = new BehaviorSubject<number>(1);
  currentFragment$ = this.route.fragment;
  templateSearchResults$: Observable<TemplateSearchResults> = combineLatest([
    combineLatest([
      this.currentPage$.pipe(distinctUntilChanged()),
      this.currentFragment$.pipe(
        distinctUntilChanged(),
        tap(() => this.resetSearchForm()),
        tap(() =>
          this.router.navigate(['.'], {
            preserveFragment: true,
            relativeTo: this.route,
          }),
        ),
      ),
    ]).pipe(
      filter(([page]) => page === 1),
      map(([, fragment]) => fragment),
      distinctUntilChanged(),
    ),
    this.currentPage$.pipe(distinctUntilChanged()),
    this.term$.pipe(
      tap(() =>
        this.router.navigate(['.'], {
          preserveFragment: true,
          relativeTo: this.route,
          queryParams: {
            page: 1,
          },
        }),
      ),
    ),
  ]).pipe(
    withLatestFrom(this.currentDomain$),
    switchMap(([[fragment, currentPage, term], currentDomain]) => {
      switch (fragment) {
        case 'operator-documents':
          return this.documentTemplatesService.getCurrentUserDocumentTemplates(
            currentDomain,
            currentPage - 1,
            this.pageSize,
            term ? term : null,
          );
        case 'regulator-emails':
          return this.notificationTemplatesService.getCurrentUserNotificationTemplates(
            currentDomain,
            'REGULATOR',
            currentPage - 1,
            this.pageSize,
            term ? term : null,
          );
        case 'operator-emails':
        default:
          return this.notificationTemplatesService.getCurrentUserNotificationTemplates(
            currentDomain,
            'OPERATOR',
            currentPage - 1,
            this.pageSize,
            term ? term : null,
          );
      }
    }),
    shareReplay({ bufferSize: 1, refCount: true }),
  );
  templates$ = this.templateSearchResults$.pipe(map((results) => results?.templates));
  totalPages$ = this.templateSearchResults$.pipe(map((results) => results?.total));

  constructor(
    private readonly fb: UntypedFormBuilder,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly notificationTemplatesService: NotificationTemplatesService,
    private readonly documentTemplatesService: DocumentTemplatesService,
    private readonly authStore: AuthStore,
    private readonly destroy$: DestroySubject,
  ) {}

  onSearch() {
    if (this.searchForm.valid) {
      this.term$.next(this.searchForm.get('term').value);
    }
  }

  navigateToTemplate(templateId: number): void {
    const fragment = this.route.snapshot.fragment;
    this.router.navigate(fragment === 'operator-documents' ? ['document', templateId] : ['email', templateId], {
      relativeTo: this.route,
    });
  }

  private resetSearchForm(): void {
    this.searchForm.reset();
    this.term$.next(null);
  }
}
