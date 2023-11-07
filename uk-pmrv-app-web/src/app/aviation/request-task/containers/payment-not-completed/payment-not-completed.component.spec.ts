import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SharedModule } from '@shared/shared.module';
import { BasePage } from '@testing';

import { PaymentNotCompletedComponent } from './payment-not-completed.component';

describe('PaymentNotCompletedComponent', () => {
  let component: PaymentNotCompletedComponent;
  let fixture: ComponentFixture<PaymentNotCompletedComponent>;
  let page: Page;

  class Page extends BasePage<PaymentNotCompletedComponent> {
    get paymentNotCompleted(): HTMLElement {
      return this.query<HTMLElement>('app-payment-not-completed');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PaymentNotCompletedComponent, SharedModule],
    }).compileComponents();

    fixture = TestBed.createComponent(PaymentNotCompletedComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display all html elements', () => {
    expect(page.paymentNotCompleted).toBeTruthy();
  });
});
