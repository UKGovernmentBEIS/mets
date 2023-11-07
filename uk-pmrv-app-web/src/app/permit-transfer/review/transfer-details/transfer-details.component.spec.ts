import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedPermitModule } from '@permit-application/shared/shared-permit.module';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';
import { SharedModule } from '@shared/shared.module';
import { BasePage } from '@testing';

import { PermitTransferModule } from '../../permit-transfer.module';
import { PermitTransferStore } from '../../store/permit-transfer.store';
import {
  mockPermitTransferDetailsConfirmation,
  mockPermitTransferReviewPayload,
  mockPermitTransferSubmitPayload,
} from '../../testing/mock';
import { TransferDetailsReviewComponent } from './transfer-details.component';

describe('TransferDetailsComponent', () => {
  let component: TransferDetailsReviewComponent;
  let fixture: ComponentFixture<TransferDetailsReviewComponent>;
  let page: Page;
  let store: PermitTransferStore;

  class Page extends BasePage<TransferDetailsReviewComponent> {
    get answers() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim().replace('  ', ' ')));
    }

    get reviewGroupDecision() {
      return this.query('app-review-group-decision');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(TransferDetailsReviewComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TransferDetailsReviewComponent],
      imports: [PermitTransferModule, SharedPermitModule, RouterTestingModule, SharedModule],
      providers: [
        {
          provide: PermitApplicationStore,
          useExisting: PermitTransferStore,
        },
      ],
    }).compileComponents();
    store = TestBed.inject(PermitTransferStore);
    store.setState({
      ...mockPermitTransferReviewPayload,
      permitTransferDetails: mockPermitTransferSubmitPayload.permitTransferDetails,
      permitTransferDetailsConfirmation: mockPermitTransferDetailsConfirmation,
    });

    createComponent();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display transfer details confirmation summary list', () => {
    expect(page.answers).toEqual([
      ['Consent with the information provided', 'Yes'],
      ['Regulated activities in operation', 'Yes'],
      [
        'Consent that I will be the operator of the installation and transferres emissions equipment',
        'No Transfer rejected reason',
      ],
    ]);
  });

  it('should display review decision details', () => {
    expect(page.reviewGroupDecision.textContent.trim()).toBeTruthy();
  });
});
