import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, asyncData, MockType } from '@testing';

import { InstallationAccountsService, InstallationAccountUpdateService } from 'pmrv-api';

import { mockedAccountPermit } from '../../testing/mock-data';
import { NameComponent } from './name.component';

describe('NameComponent', () => {
  let component: NameComponent;
  let fixture: ComponentFixture<NameComponent>;
  let installationAccountsService: MockType<InstallationAccountsService>;
  let accountUpdateService: MockType<InstallationAccountUpdateService>;
  let route: ActivatedRouteStub;

  beforeEach(async () => {
    route = new ActivatedRouteStub(undefined, undefined, {
      accountPermit: mockedAccountPermit,
    });

    accountUpdateService = {
      updateInstallationName: jest.fn().mockReturnValue(asyncData(null)),
    };
    installationAccountsService = {
      isExistingAccountName: jest.fn().mockReturnValue(asyncData(false)),
    };

    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule],
      declarations: [NameComponent],
      providers: [
        { provide: InstallationAccountsService, useValue: installationAccountsService },
        { provide: InstallationAccountUpdateService, useValue: accountUpdateService },
        { provide: ActivatedRoute, useValue: route },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(NameComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should set isSummaryDisplayed$ to true when form is invalid', async () => {
    component.form.setValue({
      installationName: 'oldInstallationName',
    });
    component.form.invalid;

    component.onSubmit();

    expect(component.isSummaryDisplayed$.value).toBe(true);
  });

  it('should call updateInstallationName when form is dirty', () => {
    const accountId = 1;
    const installationName = 'new installation name';

    jest.spyOn(installationAccountsService, 'isExistingAccountName').mockReturnValue(of(false));

    jest.spyOn(accountUpdateService, 'updateInstallationName').mockReturnValue(of(null));

    component.form.patchValue({ installationName });
    component.form.markAsDirty();

    component.onSubmit();

    expect(installationAccountsService.isExistingAccountName).toHaveBeenCalledWith(installationName, accountId);
    expect(accountUpdateService.updateInstallationName).toHaveBeenCalledWith(accountId, { installationName });
  });

  it('should not call updateInstallationName if isExistingAccountName1 returns true', () => {
    jest.spyOn(installationAccountsService, 'isExistingAccountName').mockReturnValue(of(true));

    jest.spyOn(accountUpdateService, 'updateInstallationName');

    component.onSubmit();

    expect(accountUpdateService.updateInstallationName).not.toHaveBeenCalled();
  });
});
