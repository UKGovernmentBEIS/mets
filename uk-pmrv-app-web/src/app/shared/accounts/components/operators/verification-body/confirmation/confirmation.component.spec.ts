import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { BasePage } from '@testing';

import { GovukComponentsModule } from 'govuk-components';

import { ConfirmationComponent } from './confirmation.component';

describe('ConfirmationComponent', () => {
  let component: ConfirmationComponent;
  let fixture: ComponentFixture<ConfirmationComponent>;
  let page: Page;

  class Page extends BasePage<ConfirmationComponent> {
    get link() {
      return this.query<HTMLAnchorElement>('a');
    }
  }
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GovukComponentsModule, RouterTestingModule],
      declarations: [ConfirmationComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ConfirmationComponent);
    component = fixture.componentInstance;
    component.verificationAccount = 'Test account';
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the appointed verification account', () => {
    const element: HTMLElement = fixture.nativeElement;
    expect(element.querySelector('.govuk-panel__body').textContent).toEqual('Test account');
  });

  it('should back to the users list', () => {
    expect(page.link.textContent.trim()).toEqual(`Return to the users, contacts and verifiers page`);
    expect(page.link.href).toContain('/accounts');
  });
});
