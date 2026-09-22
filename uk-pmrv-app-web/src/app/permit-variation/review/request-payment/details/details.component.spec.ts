import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '@shared/shared.module';
import { BasePage } from '@testing';

import { PermitVariationStore } from '../../../store/permit-variation.store';
import { mockPermitVariationReviewOperatorLedPayload } from '../../../testing/mock';
import { DetailsComponent } from './details.component';

describe('DetailsComponent', () => {
  let component: DetailsComponent;
  let fixture: ComponentFixture<DetailsComponent>;
  let store: PermitVariationStore;
  let page: Page;

  class Page extends BasePage<DetailsComponent> {
    get summaryListValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
  }

  async function createComponent(): Promise<void> {
    await TestBed.configureTestingModule({
      imports: [SharedModule, RouterTestingModule],
      declarations: [DetailsComponent],
    }).compileComponents();

    store = TestBed.inject(PermitVariationStore);
    store.setState({
      ...mockPermitVariationReviewOperatorLedPayload,
      requestActionSubmitter: 'Regulator Wales',
      requestActionCreationDate: '2026-07-24T10:00:00.000Z',
      paymentDetails: {
        amount: '1500',
        dueDate: '2027-03-03',
        notes: 'Lorem ipsum',
      },
    } as any);

    fixture = TestBed.createComponent(DetailsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  }

  it('should create', async () => {
    await createComponent();

    expect(component).toBeTruthy();
  });

  it('shows the response details', async () => {
    await createComponent();

    expect(page.summaryListValues).toEqual([
      ['Payment amount in pounds', '£1,500.00'],
      ['Due date for the payment', '3 Mar 2027'],
      ['Notes', 'Lorem ipsum'],
    ]);
  });
});
