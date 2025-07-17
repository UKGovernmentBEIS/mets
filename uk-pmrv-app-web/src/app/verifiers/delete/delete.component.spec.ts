import { Location } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of, throwError } from 'rxjs';

import { AuthService } from '@core/services/auth.service';
import { AuthStore } from '@core/store/auth';
import { BusinessTestingModule, expectBusinessErrorToBe } from '@error/testing/business-error';
import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, BasePage, RouterStubComponent } from '@testing';

import { UserDTO, VerifierAuthoritiesService } from 'pmrv-api';

import { saveNotFoundVerifierError } from '../errors/business-error';
import { DeleteComponent } from './delete.component';

describe('VerifierDeleteComponent', () => {
  let component: DeleteComponent;
  let fixture: ComponentFixture<DeleteComponent>;
  let page: Page;
  let location: Location;
  let authStore: AuthStore;

  class Page extends BasePage<DeleteComponent> {
    get header() {
      return this.query<HTMLHeadingElement>('h2');
    }

    get cancelLink() {
      return this.queryAll<HTMLAnchorElement>('a').find((element) => element.textContent.trim() === 'Cancel');
    }

    get submitButton() {
      return this.queryAll<HTMLButtonElement>('button')[0];
    }

    get panelTitle() {
      return this.query<HTMLDivElement>('.govuk-panel__title');
    }

    get returnLink() {
      return this.query<HTMLAnchorElement>('a');
    }
  }

  const user: UserDTO = {
    email: 'alfyn-octo@pmrv.uk',
    firstName: 'Alfyn',
    lastName: 'Octo',
  };

  let verifierAuthoritiesService: Partial<jest.Mocked<VerifierAuthoritiesService>>;
  let authService: Partial<jest.Mocked<AuthService>>;
  let activatedRoute: ActivatedRouteStub;

  beforeEach(async () => {
    activatedRoute = new ActivatedRouteStub({ userId: '1reg' }, null, { user });
    authService = {
      logout: jest.fn(),
    };
    verifierAuthoritiesService = {
      deleteVerifierAuthority: jest.fn().mockReturnValue(of(null)),
      deleteCurrentVerifierAuthority: jest.fn().mockReturnValue(of(null)),
    };

    await TestBed.configureTestingModule({
      declarations: [DeleteComponent, RouterStubComponent],
      imports: [BusinessTestingModule, SharedModule, RouterTestingModule],
      providers: [
        { provide: VerifierAuthoritiesService, useValue: verifierAuthoritiesService },
        { provide: AuthService, useValue: authService },
        { provide: ActivatedRoute, useValue: activatedRoute },
      ],
    }).compileComponents();

    authStore = TestBed.inject(AuthStore);
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DeleteComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    location = TestBed.inject(Location);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should contain a heading with regulator name', () => {
    expect(page.header.textContent).toContain(`${user.firstName} ${user.lastName}`);
  });

  it('should contain cancel and delete buttons', () => {
    expect(page.cancelLink.textContent.trim()).toEqual('Cancel');
    expect(page.submitButton.textContent.trim()).toEqual('Confirm delete');
  });

  it('should return without reload on cancel click', () => {
    const locationSpy = jest.spyOn(location, 'back');
    page.cancelLink.click();

    expect(locationSpy).toHaveBeenCalled();
  });

  it('should delete regulator and logout if current user', () => {
    authStore.setUserState({ userId: '1reg' });

    page.submitButton.click();

    expect(verifierAuthoritiesService.deleteCurrentVerifierAuthority).toHaveBeenCalled();
    expect(verifierAuthoritiesService.deleteVerifierAuthority).not.toHaveBeenCalled();
    expect(authService.logout).toHaveBeenCalled();
  });

  it('should delete regulator on confirm delete click', () => {
    authStore.setUserState({ userId: '1' });

    page.submitButton.click();

    expect(verifierAuthoritiesService.deleteCurrentVerifierAuthority).not.toHaveBeenCalled();
    expect(verifierAuthoritiesService.deleteVerifierAuthority).toHaveBeenCalledWith('1reg');
  });

  it('should show confirmation screen on delete', () => {
    authStore.setUserState({ userId: '1' });

    page.submitButton.click();
    fixture.detectChanges();

    expect(page.panelTitle.textContent).toContain(user.firstName);
    expect(page.panelTitle.textContent).toContain(user.lastName);

    const locationSpy = jest.spyOn(location, 'back');
    page.returnLink.click();

    expect(locationSpy).toHaveBeenCalled();
  });

  it('should dismiss with a message if error', async () => {
    authStore.setUserState({ userId: '1' });
    verifierAuthoritiesService.deleteVerifierAuthority.mockReturnValue(
      throwError(() => new HttpErrorResponse({ error: { code: 'AUTHORITY1006' }, status: 400 })),
    );

    page.submitButton.click();

    await expectBusinessErrorToBe(saveNotFoundVerifierError);
  });
});
