import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '@shared/shared.module';
import { BasePage } from '@testing';

import { InstallationAccountApplicationStore } from '../store/installation-account-application.store';
import { ApplicationTypeComponent } from './application-type.component';

describe('ApplicationTypeComponent', () => {
  let component: ApplicationTypeComponent;
  let fixture: ComponentFixture<ApplicationTypeComponent>;
  let router: Router;
  let store: InstallationAccountApplicationStore;
  let page: Page;

  class Page extends BasePage<ApplicationTypeComponent> {
    get applicationTypes() {
      return this.queryAll<HTMLInputElement>('input[name="applicationType"]');
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, RouterTestingModule],
      declarations: [ApplicationTypeComponent],
      providers: [InstallationAccountApplicationStore],
    }).compileComponents();

    fixture = TestBed.createComponent(ApplicationTypeComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    store = TestBed.inject(InstallationAccountApplicationStore);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should not save value in store if form not valid', () => {
    const setStateSpy = jest.spyOn(store, 'setState');
    const navigateSpy = jest.spyOn(router, 'navigate').mockImplementation();

    page.submitButton.click();

    expect(setStateSpy).not.toHaveBeenCalled();
    expect(navigateSpy).not.toHaveBeenCalled();
  });

  it('should fill form from state', () => {
    component.form.patchValue({ applicationType: 'TRANSFER' });
    fixture.detectChanges();

    expect(page.applicationTypes[1].checked).toBeTruthy();
  });

  it('should save value in store', () => {
    page.applicationTypes[0].click();
    fixture.detectChanges();
    page.submitButton.click();

    expect(component.form.get('applicationType').value).toEqual('NEW_PERMIT');
  });
});
