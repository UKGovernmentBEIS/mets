import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { SharedModule } from '@shared/shared.module';
import { BasePage, mockClass } from '@testing';

import { GovukComponentsModule } from 'govuk-components';

import { OperatorUsersService, RegulatorUsersService, VerifierUsersService } from 'pmrv-api';

import { SharedUserModule } from '../../shared-user/shared-user.module';
import { ResetTwoFaComponent } from './reset-two-fa.component';

describe('ResetTwoFaComponent', () => {
  let component: ResetTwoFaComponent;
  let fixture: ComponentFixture<ResetTwoFaComponent>;
  let page: Page;

  const regulatorUsersService = mockClass(RegulatorUsersService);
  const verifierUsersService = mockClass(VerifierUsersService);
  const operatorUsersService = mockClass(OperatorUsersService);

  class Page extends BasePage<ResetTwoFaComponent> {
    get heading() {
      return this.query<HTMLHeadingElement>('h1');
    }
    get submitButton() {
      return this.queryAll<HTMLButtonElement>('button')[0];
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GovukComponentsModule, SharedModule, RouterTestingModule, ReactiveFormsModule, SharedUserModule],
      declarations: [ResetTwoFaComponent],
      providers: [
        { provide: RegulatorUsersService, useValue: regulatorUsersService },
        { provide: VerifierUsersService, useValue: verifierUsersService },
        { provide: OperatorUsersService, useValue: operatorUsersService },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(ResetTwoFaComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should reset 2fa after clicking button', () => {
    jest.spyOn(fixture.componentInstance, 'reset');
    regulatorUsersService.resetRegulator2Fa.mockReturnValueOnce(of());
    verifierUsersService.resetVerifier2Fa.mockReturnValueOnce(of());
    operatorUsersService.resetOperator2Fa.mockReturnValueOnce(of());
    window.history.pushState({ userId: '1234', accountId: '1234', role: 'REGULATOR' }, 'yes');

    expect(page.heading).toBeTruthy();
    expect(page.heading.textContent.trim()).toContain('Are you sure you want to reset two-factor authentication');

    page.submitButton.click();
    fixture.detectChanges();

    expect(component.reset).toHaveBeenCalledTimes(1);
    expect(regulatorUsersService.resetRegulator2Fa).toHaveBeenCalledTimes(1);

    window.history.pushState({ role: 'VERIFIER' }, 'yes');
    page.submitButton.click();
    fixture.detectChanges();

    expect(component.reset).toHaveBeenCalledTimes(2);
    expect(verifierUsersService.resetVerifier2Fa).toHaveBeenCalledTimes(1);

    window.history.pushState({ role: 'OPERATOR' }, 'yes');
    page.submitButton.click();
    fixture.detectChanges();

    expect(component.reset).toHaveBeenCalledTimes(3);
    expect(operatorUsersService.resetOperator2Fa).toHaveBeenCalledTimes(1);
  });
});
