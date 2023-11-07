import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '@shared/shared.module';
import { BasePage } from '@testing';

import { GovukComponentsModule } from 'govuk-components';

import { EmailSentComponent } from './email-sent.component';

describe('EmailSentComponent', () => {
  let component: EmailSentComponent;
  let fixture: ComponentFixture<EmailSentComponent>;
  let element: HTMLElement;
  let page: Page;

  class Page extends BasePage<EmailSentComponent> {
    get details() {
      return this.query<HTMLDetailsElement>('details.govuk-details');
    }

    get detailsSummary() {
      return this.query<HTMLSpanElement>('.govuk-details__summary-text');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [EmailSentComponent],
      imports: [GovukComponentsModule, SharedModule, RouterTestingModule],
    }).compileComponents();

    fixture = TestBed.createComponent(EmailSentComponent);
    page = new Page(fixture);
    component = fixture.componentInstance;
    element = fixture.nativeElement;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display appropriate title for password reset', () => {
    expect(element.querySelector('h2').textContent.trim()).toEqual('Password reset email sent');
  });

  it('should render details to reveal more information ', () => {
    expect(page.details).toBeTruthy();
    expect(page.detailsSummary.innerHTML.trim()).toEqual("I didn't get an email");
  });
});
