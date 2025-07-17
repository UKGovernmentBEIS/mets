import { Injectable } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot, UrlTree } from '@angular/router';

import { map, Observable, tap } from 'rxjs';

import { LegalEntitiesService, LegalEntityInfoDTO } from 'pmrv-api';

import { ApplicationSectionType } from '../store/installation-account-application.state';
import { InstallationAccountApplicationStore } from '../store/installation-account-application.store';

@Injectable()
export abstract class LegalEntitySelectGuard {
  private legalEntities: LegalEntityInfoDTO[];

  constructor(
    private readonly form: UntypedFormGroup,
    private readonly legalEntitiesService: LegalEntitiesService,
    private readonly router: Router,
    private readonly store: InstallationAccountApplicationStore,
  ) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot,
    redirectUrlPart: string,
  ): Observable<boolean | UrlTree> {
    return this.legalEntitiesService.getCurrentUserLegalEntities().pipe(
      tap((legalEntities) => (this.legalEntities = legalEntities)),
      map((legalEntities) => {
        if (legalEntities.length === 0) {
          this.form.get('selectGroup').get('isNew').setValue(true);
          this.store.updateTask(ApplicationSectionType.legalEntity, { selectGroup: { isNew: true } });

          return this.router.parseUrl(this.getRedirectUrl(route, state, redirectUrlPart));
        } else {
          return true;
        }
      }),
    );
  }

  resolve(): LegalEntityInfoDTO[] {
    return this.legalEntities;
  }

  protected getRedirectUrl(route: ActivatedRouteSnapshot, state: RouterStateSnapshot, redirectUrlPart: string): string {
    return state.url.slice(0, state.url.indexOf(route.url[route.url.length - 1].path)).concat(redirectUrlPart);
  }
}
