import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '@shared/shared.module';
import { BasePage } from '@testing';

import { GovukComponentsModule } from 'govuk-components';

import { RequestTwoFaResetComponent } from './request-two-fa-reset.component';

describe('RequestTwoFaResetComponent', () => {
  let component: RequestTwoFaResetComponent;
  let fixture: ComponentFixture<RequestTwoFaResetComponent>;
  let page: Page;

  class Page extends BasePage<RequestTwoFaResetComponent> {
    get heading() {
      return this.query<HTMLElement>('app-page-heading h1.govuk-heading-l');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [RequestTwoFaResetComponent],
      imports: [GovukComponentsModule, SharedModule, RouterTestingModule],
    }).compileComponents();

    fixture = TestBed.createComponent(RequestTwoFaResetComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display appropriate title for reset 2FA', () => {
    expect(page.heading.textContent.trim()).toEqual('Request two-factor authentication reset');
  });
});
