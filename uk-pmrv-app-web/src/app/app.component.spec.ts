import { APP_BASE_HREF } from '@angular/common';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { BehaviorSubject, of } from 'rxjs';

import { mockKeycloak, mockKeycloakEventSignal } from '@core/guards/mocks';
import { BREADCRUMB_ITEMS, BreadcrumbItem } from '@core/navigation/breadcrumbs';
import { AuthStore, LoginStatus } from '@core/store/auth';
import { SharedModule } from '@shared/shared.module';
import { BasePage, mockClass } from '@testing';
import { KEYCLOAK_EVENT_SIGNAL } from 'keycloak-angular';
import Keycloak from 'keycloak-js';

import { BulkDownloadService, UserStateDTO } from 'pmrv-api';

import { AppComponent } from './app.component';
import { TimeoutModule } from './timeout/timeout.module';

describe('AppComponent', () => {
  let component: AppComponent;
  let fixture: ComponentFixture<AppComponent>;
  let page: Page;
  let breadcrumbItem$: BehaviorSubject<BreadcrumbItem[]>;
  let authStore: AuthStore;

  const setUser: (roleType: UserStateDTO['roleType'], loginStatus?: LoginStatus) => void = (roleType, loginStatus?) => {
    authStore.setUserState({
      ...authStore.getState().userState,
      domainsLoginStatuses: { INSTALLATION: loginStatus ?? 'ENABLED' },
      roleType,
    });

    fixture.detectChanges();
  };

  function createComponent() {
    fixture = TestBed.createComponent(AppComponent);
    component = fixture.componentInstance;
    breadcrumbItem$ = TestBed.inject(BREADCRUMB_ITEMS);
    page = new Page(fixture);
    fixture.detectChanges();
  }

  class Page extends BasePage<AppComponent> {
    get footer() {
      return this.query<HTMLElement>('.govuk-footer');
    }

    get dashboardLink() {
      return this.query<HTMLAnchorElement>('a[href="/dashboard"]');
    }

    get regulatorsLink() {
      return this.query<HTMLAnchorElement>('a[href="/user/regulators"]');
    }

    get accountsLink() {
      return this.query<HTMLAnchorElement>('a[href="/accounts"]');
    }

    get templatesLink() {
      return this.query<HTMLAnchorElement>('a[href="/templates"]');
    }

    get batchVariationsLink() {
      return this.query<HTMLAnchorElement>('a[href="/workflows/batch-variations"]');
    }

    get bulkDownloadsLink() {
      return this.query<HTMLAnchorElement>('a[href="/bulk-downloads"]');
    }

    get navList() {
      return this.query<HTMLElement>('govuk-header-nav-list-legacy');
    }

    get breadcrumbs() {
      return this.queryAll<HTMLLIElement>('.govuk-breadcrumbs__list-item');
    }
  }

  const bulkDownloadService = mockClass(BulkDownloadService);

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule, TimeoutModule],
      declarations: [AppComponent],
      providers: [
        { provide: Keycloak, useValue: mockKeycloak },
        { provide: KEYCLOAK_EVENT_SIGNAL, useValue: mockKeycloakEventSignal },
        { provide: APP_BASE_HREF, useValue: '/installation-aviation/' },
        { provide: BulkDownloadService, useValue: bulkDownloadService },
      ],
    }).compileComponents();
    authStore = TestBed.inject(AuthStore);
    authStore.setIsLoggedIn(true);
    authStore.setCurrentDomain('INSTALLATION');
    authStore.setUserState({ roleType: 'OPERATOR', domainsLoginStatuses: { INSTALLATION: 'NO_AUTHORITY' } });

    bulkDownloadService.hasAccessBulkDownload.mockReturnValue(of(false));
  });

  beforeEach(() => {
    createComponent();
  });

  it('should create the app', () => {
    expect(component).toBeTruthy();
  });

  it('should render the footer', () => {
    expect(page.footer).toBeTruthy();
  });

  it('should not render the dashboard link for disabled users or an operator with no authority', () => {
    setUser('OPERATOR', 'NO_AUTHORITY');

    expect(page.dashboardLink).toBeFalsy();

    setUser('OPERATOR', 'ENABLED');

    expect(page.dashboardLink).toBeTruthy();

    setUser('REGULATOR', 'ENABLED');

    expect(page.dashboardLink).toBeTruthy();

    setUser('REGULATOR', 'DISABLED');

    expect(page.dashboardLink).toBeFalsy();
  });

  it('should render the regulators link only if the user is regulator', () => {
    setUser('OPERATOR', 'NO_AUTHORITY');

    expect(page.regulatorsLink).toBeFalsy();

    setUser('REGULATOR');

    expect(page.regulatorsLink).toBeTruthy();
  });

  it('should render the accounts link only if the user is regulator, verifier or authorized operator', () => {
    setUser('OPERATOR', 'NO_AUTHORITY');

    expect(page.accountsLink).toBeFalsy();

    setUser('VERIFIER');

    expect(page.accountsLink).toBeTruthy();

    setUser('REGULATOR');

    expect(page.accountsLink).toBeTruthy();

    setUser('OPERATOR', 'ENABLED');

    expect(page.accountsLink).toBeTruthy();
  });

  it('should render the templates link only if the user is a regulator', () => {
    setUser('OPERATOR');

    expect(page.templatesLink).toBeFalsy();

    setUser('VERIFIER');

    expect(page.templatesLink).toBeFalsy();

    setUser('REGULATOR');

    expect(page.templatesLink).toBeTruthy();
  });

  it('should not render the nav list if user is disabled', () => {
    setUser('REGULATOR', 'ENABLED');
    expect(page.navList).toBeTruthy();

    setUser('REGULATOR', 'DISABLED');
    fixture.detectChanges();

    expect(page.navList).toBeFalsy();

    setUser('VERIFIER', 'TEMP_DISABLED');
    fixture.detectChanges();

    expect(page.navList).toBeFalsy();
  });

  it('should not render the nav list if user is not logged in', () => {
    authStore.setIsLoggedIn(false);
    setUser('OPERATOR', 'NO_AUTHORITY');

    expect(page.navList).toBeFalsy();

    authStore.setIsLoggedIn(true);
    setUser('OPERATOR', 'ENABLED');

    expect(page.navList).toBeTruthy();

    authStore.setIsLoggedIn(false);
    fixture.detectChanges();

    expect(page.navList).toBeFalsy();
  });

  it('should display breadcrumbs', () => {
    expect(page.breadcrumbs).toEqual([]);

    breadcrumbItem$.next([{ text: 'Dashboard', link: ['/dashboard'] }, { text: 'Apply for a GHGE permit' }]);
    fixture.detectChanges();

    expect(Array.from(page.breadcrumbs).map((breacrumb) => breacrumb.textContent.trim())).toEqual([
      'Dashboard',
      'Apply for a GHGE permit',
    ]);

    expect(page.breadcrumbs[0].querySelector<HTMLAnchorElement>('a').href).toContain('/dashboard');
    expect(page.breadcrumbs[1].querySelector<HTMLAnchorElement>('a')).toBeFalsy();

    breadcrumbItem$.next(null);
    fixture.detectChanges();

    expect(page.breadcrumbs).toEqual([]);
  });

  it('should render the batch variations link only if the user is a regulator', () => {
    setUser('OPERATOR');
    expect(page.batchVariationsLink).toBeFalsy();

    setUser('VERIFIER');
    expect(page.batchVariationsLink).toBeFalsy();

    setUser('REGULATOR');
    expect(page.batchVariationsLink).toBeTruthy();
  });

  it('should not render the batch variations link if aviation', () => {
    authStore.setState({
      ...authStore.getState(),
      currentDomain: 'AVIATION',
    });

    setUser('REGULATOR');
    expect(page.batchVariationsLink).toBeFalsy();

    setUser('VERIFIER');
    expect(page.batchVariationsLink).toBeFalsy();

    setUser('OPERATOR');
    expect(page.batchVariationsLink).toBeFalsy();
  });

  it('should not render the bulk downloads link if the access api call returned false', () => {
    setUser('OPERATOR');
    expect(page.bulkDownloadsLink).toBeFalsy();

    setUser('VERIFIER');
    expect(page.bulkDownloadsLink).toBeFalsy();

    setUser('REGULATOR');
    expect(page.bulkDownloadsLink).toBeFalsy();
  });

  it('should render the bulk downloads link only if the user is a regulator', () => {
    bulkDownloadService.hasAccessBulkDownload.mockReturnValue(of(true));

    createComponent();

    setUser('OPERATOR');
    expect(page.bulkDownloadsLink).toBeFalsy();

    setUser('VERIFIER');
    expect(page.bulkDownloadsLink).toBeFalsy();

    setUser('REGULATOR');
    expect(page.bulkDownloadsLink).toBeTruthy();
  });

  it('should not render the bulk downloads link if aviation', () => {
    bulkDownloadService.hasAccessBulkDownload.mockReturnValue(of(true));

    createComponent();

    authStore.setState({
      ...authStore.getState(),
      currentDomain: 'AVIATION',
    });

    setUser('REGULATOR');
    expect(page.bulkDownloadsLink).toBeFalsy();

    setUser('VERIFIER');
    expect(page.bulkDownloadsLink).toBeFalsy();

    setUser('OPERATOR');
    expect(page.bulkDownloadsLink).toBeFalsy();
  });
});
