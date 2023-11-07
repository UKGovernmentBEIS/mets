import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '@shared/shared.module';
import { BasePage } from '@testing';

import { GovukComponentsModule } from 'govuk-components';

import { EmailLinkInvalidComponent } from './email-link-invalid.component';

describe('EmailLinkInvalidComponent', () => {
  let component: EmailLinkInvalidComponent;
  let fixture: ComponentFixture<EmailLinkInvalidComponent>;
  let page: Page;

  class Page extends BasePage<EmailLinkInvalidComponent> {
    get header() {
      return this.query<HTMLElement>('h1');
    }

    get link() {
      return this.query<HTMLAnchorElement>('a');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GovukComponentsModule, SharedModule, RouterTestingModule],
      declarations: [EmailLinkInvalidComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(EmailLinkInvalidComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display expired link message', () => {
    expect(page.header.textContent).toEqual('The password reset link has expired');
  });

  it('should display link to forgot-password page', () => {
    expect(page.link.href).toMatch(/\/forgot-password$/);
  });
});
