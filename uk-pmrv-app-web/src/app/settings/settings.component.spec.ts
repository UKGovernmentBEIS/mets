import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { of } from 'rxjs';

import { AuthStore } from '@core/store';
import { BasePage } from '@testing';

import { SettingsService } from 'pmrv-api';

import { SettingsComponent } from './settings.component';

describe('SettingsComponent', () => {
  let fixture: ComponentFixture<SettingsComponent>;
  let component: SettingsComponent;
  let authStore: AuthStore;
  let page: Page;

  const settingsService = {
    getAccessibleSections: jest.fn().mockReturnValue(of(['EMISSION_FACTORS', 'FEES', 'GLOBAL_WARMING_POTENTIALS'])),
  };

  class Page extends BasePage<SettingsComponent> {
    get heading() {
      return this.query<HTMLHeadingElement>('h1');
    }
    get linkTexts() {
      return this.queryAll<HTMLAnchorElement>('.app-settings-list a').map((a) => a.textContent?.trim());
    }
    get feesLink() {
      return this.queryAll<HTMLAnchorElement>('.app-settings-list a').find((a) => a.textContent?.trim() === 'Fees');
    }
  }

  beforeEach(async () => {
    settingsService.getAccessibleSections.mockReturnValue(
      of(['EMISSION_FACTORS', 'FEES', 'GLOBAL_WARMING_POTENTIALS']),
    );

    await TestBed.configureTestingModule({
      imports: [SettingsComponent],
      providers: [provideRouter([]), { provide: SettingsService, useValue: settingsService }],
    }).compileComponents();

    authStore = TestBed.inject(AuthStore);
  });

  function createComponent() {
    fixture = TestBed.createComponent(SettingsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
  }

  it('should create', () => {
    authStore.setCurrentDomain('INSTALLATION');
    createComponent();
    fixture.detectChanges();

    expect(component).toBeTruthy();
  });

  it('renders the Installation heading and intro when domain is INSTALLATION', () => {
    authStore.setCurrentDomain('INSTALLATION');
    createComponent();
    fixture.detectChanges();

    expect(page.heading.textContent.trim()).toEqual('Installation settings');
  });

  it('renders the Aviation heading and intro when domain is AVIATION', () => {
    authStore.setCurrentDomain('AVIATION');
    createComponent();
    fixture.detectChanges();

    expect(page.heading.textContent.trim()).toEqual('Aviation settings');
  });

  it('requests the accessible sections for the current domain', () => {
    authStore.setCurrentDomain('AVIATION');
    createComponent();
    fixture.detectChanges();

    expect(settingsService.getAccessibleSections).toHaveBeenCalledWith('AVIATION');
  });

  it('renders a link for every section returned by the API', () => {
    authStore.setCurrentDomain('INSTALLATION');
    createComponent();
    fixture.detectChanges();

    expect(page.linkTexts).toEqual(['Emission factors', 'Fees', 'Global warming potentials']);
  });

  it('only renders links for sections the user has access to', () => {
    settingsService.getAccessibleSections.mockReturnValue(of(['FEES']));
    authStore.setCurrentDomain('INSTALLATION');
    createComponent();
    fixture.detectChanges();

    expect(page.linkTexts).toEqual(['Fees']);
  });

  it('links the Fees item to the fees page for Installation', () => {
    authStore.setCurrentDomain('INSTALLATION');
    createComponent();
    fixture.detectChanges();

    expect(page.feesLink.getAttribute('href')).toEqual('/fees');
  });

  it('links the Fees item to the fees page for Aviation', () => {
    authStore.setCurrentDomain('AVIATION');
    createComponent();
    fixture.detectChanges();

    expect(page.feesLink.getAttribute('href')).toEqual('/fees');
  });
});
