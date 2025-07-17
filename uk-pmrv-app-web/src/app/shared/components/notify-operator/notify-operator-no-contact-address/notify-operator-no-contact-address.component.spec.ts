import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '@shared/shared.module';
import { BasePage } from '@testing';

import { NotifyOperatorNoContactAddressComponent } from './notify-operator-no-contact-address.component';

describe('NotifyOperatorNoContactAddressComponent', () => {
  let component: NotifyOperatorNoContactAddressComponent;
  let fixture: ComponentFixture<NotifyOperatorNoContactAddressComponent>;
  let page: Page;

  class Page extends BasePage<NotifyOperatorNoContactAddressComponent> {
    get message() {
      return this.query('p.govuk-body').innerHTML.trim();
    }

    get link() {
      return this.query<HTMLAnchorElement>('a[href="/aviation/accounts/100"]');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(NotifyOperatorNoContactAddressComponent);
    component = fixture.componentInstance;
    component.accountId = 100;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show appropriate message with link', () => {
    expect(page.link).toBeTruthy();
    expect(page.message).toContain('the aviation operator before you can issue the notice.');
  });
});
