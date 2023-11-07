import { Component, Input } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { SharedModule } from '@shared/shared.module';
import { BasePage } from '@testing';

import { GovukComponentsModule } from 'govuk-components';

import { ForgotPasswordService } from 'pmrv-api';

import { EmailSentComponent } from '../email-sent/email-sent.component';
import { SubmitEmailComponent } from './submit-email.component';

describe('SubmitEmailComponent', () => {
  let component: SubmitEmailComponent;
  let fixture: ComponentFixture<SubmitEmailComponent>;
  let element: HTMLElement;
  let page: Page;

  @Component({
    selector: 'app-email-sent',
    template: '<p>Mock template</p>',
  })
  class MockEmailSentComponent {
    @Input() email: string;
  }

  class Page extends BasePage<SubmitEmailComponent> {
    get inputError(): string[] {
      return this.queryAll<HTMLSpanElement>('.govuk-error-message').reduce(
        (result, element) => [
          ...result,
          ...Array.from(element.querySelectorAll('.govuk-\\!-display-block')).map((block) => block.textContent.trim()),
        ],
        [],
      );
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SubmitEmailComponent, MockEmailSentComponent],
      imports: [GovukComponentsModule, SharedModule, RouterTestingModule],
      providers: [
        {
          provide: ForgotPasswordService,
          useValue: { sendResetPasswordEmail: jest.fn((email) => of(email)) },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(SubmitEmailComponent);
    component = fixture.componentInstance;
    element = fixture.nativeElement;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should accept valid email address', () => {
    const emailInput = fixture.debugElement.query(By.css('input'));
    const button = element.querySelector<HTMLButtonElement>('button');
    const getEmailSent = () => fixture.debugElement.query(By.directive(EmailSentComponent));

    const setValueAndSubmit = (value) => {
      emailInput.triggerEventHandler('input', { target: { value } });
      button.click();
      fixture.detectChanges();
    };

    setValueAndSubmit('test');
    expect(page.inputError).toEqual(['Error: Enter an email address in the correct format, like name@example.com']);

    setValueAndSubmit('test@test.com');
    expect(getEmailSent());
  });
});
