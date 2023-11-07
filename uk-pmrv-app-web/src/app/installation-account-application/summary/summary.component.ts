import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { filter, first, map, Observable, switchMap, takeWhile, tap } from 'rxjs';

import { RequestsService } from 'pmrv-api';

import { competentAuthorityMap } from '../../shared/interfaces/competent-authority';
import { LegalEntity, legalEntityTypeMap } from '../../shared/interfaces/legal-entity';
import { OffshoreDetails, OffshoreInstallation, OnshoreDetails } from '../installation-type/installation';
import { mapToSubmit } from '../pipes/action-pipes';
import { isOnShoreInstallation } from '../pipes/submit-application';
import {
  ApplicationSectionType,
  ApplicationTypeSection,
  CommencementSection,
  EtsSchemeSection,
  InstallationSection,
  LegalEntitySection,
  Section,
} from '../store/installation-account-application.state';
import { InstallationAccountApplicationStore } from '../store/installation-account-application.store';

@Component({
  selector: 'app-summary',
  templateUrl: './summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SummaryComponent {
  creationDate$ = this.store.select('requestActionCreationDate');
  actionType$ = this.route.data.pipe(map((data) => data.type));
  isSubmitDisabled: boolean;
  legalEntityTypeMap = legalEntityTypeMap;

  legalEntity$: Observable<LegalEntity> = this.getSectionData<LegalEntitySection>(ApplicationSectionType.legalEntity);

  installationType$: Observable<string> = this.getSectionData<InstallationSection>(
    ApplicationSectionType.installation,
  ).pipe(map((installation) => installation.installationTypeGroup.type));

  onshoreInstallation$: Observable<OnshoreDetails> = this.getSectionData<InstallationSection>(
    ApplicationSectionType.installation,
  ).pipe(
    filter(isOnShoreInstallation),
    map((installation) => installation.onshoreGroup),
  );

  offshoreInstallation$: Observable<OffshoreDetails> = this.getSectionData<InstallationSection>(
    ApplicationSectionType.installation,
  ).pipe(
    filter((installation) => !isOnShoreInstallation(installation)),
    map((installation: OffshoreInstallation) => installation.offshoreGroup),
  );

  installationEmissions$: Observable<string> = this.getSectionData<InstallationSection>(
    ApplicationSectionType.installation,
  ).pipe(
    filter(isOnShoreInstallation),
    map(
      (installation) =>
        Object.entries(competentAuthorityMap).find(([value]) => value === installation.locationGroup.location)[1],
    ),
  );

  etsScheme$: Observable<string> = this.getSectionData<EtsSchemeSection>(ApplicationSectionType.etsScheme).pipe(
    map((installation) => installation.etsSchemeType),
  );

  dateOfCommencement$: Observable<Date> = this.getSectionData<CommencementSection>(
    ApplicationSectionType.commencement,
  ).pipe(map((data) => data.commencementDate));

  applicationType$: Observable<string> = this.getSectionData<ApplicationTypeSection>(
    ApplicationSectionType.applicationType,
  ).pipe(map((installation) => installation.applicationType));

  constructor(
    private readonly store: InstallationAccountApplicationStore,
    private readonly requestsService: RequestsService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onApplicationSubmit(): void {
    this.isSubmitDisabled = true;
    this.store
      .select('tasks')
      .pipe(
        first(),
        mapToSubmit(),
        switchMap((payload) => this.requestsService.processRequestCreateAction(payload)),
        tap(() => this.store.reset()),
      )
      .subscribe(() => this.router.navigate(['../../submitted'], { relativeTo: this.route }));
  }

  private getSectionData<T extends Section>(section: T['type']): Observable<T['value']> {
    return this.store.getTask(section).pipe(
      filter((task) => !!task),
      map((task) => task.value),
      takeWhile(() => !this.isSubmitDisabled),
    );
  }
}
