import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedPermitModule } from '@permit-application/shared/shared-permit.module';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';
import { SharedModule } from '@shared/shared.module';

import { PermitTransferModule } from '../../permit-transfer.module';
import { PermitTransferStore } from '../../store/permit-transfer.store';
import { mockPermitTransferDetailsConfirmation, mockPermitTransferSubmitPayload } from '../../testing/mock';
import { TransferDetailsConfirmationSummaryComponent } from './summary.component';

describe('TransferDetailsConfirmationSummaryComponent', () => {
  let component: TransferDetailsConfirmationSummaryComponent;
  let fixture: ComponentFixture<TransferDetailsConfirmationSummaryComponent>;
  let store: PermitTransferStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TransferDetailsConfirmationSummaryComponent],
      imports: [RouterTestingModule, SharedModule, SharedPermitModule, PermitTransferModule],
      providers: [
        {
          provide: PermitApplicationStore,
          useExisting: PermitTransferStore,
        },
      ],
    }).compileComponents();

    store = TestBed.inject(PermitTransferStore);
    store.setState({
      ...mockPermitTransferSubmitPayload,
      permitTransferDetailsConfirmation: mockPermitTransferDetailsConfirmation,
      allowedRequestTaskActions: ['PERMIT_TRANSFER_B_SAVE_APPLICATION'],
    });
    fixture = TestBed.createComponent(TransferDetailsConfirmationSummaryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
