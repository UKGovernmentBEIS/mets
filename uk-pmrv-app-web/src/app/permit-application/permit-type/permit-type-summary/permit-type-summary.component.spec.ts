import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { PermitIssuanceStore } from '../../../permit-issuance/store/permit-issuance.store';
import { SharedModule } from '../../../shared/shared.module';
import { SharedPermitModule } from '../../shared/shared-permit.module';
import { PermitApplicationState } from '../../store/permit-application.state';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { mockState } from '../../testing/mock-state';
import { PermitTypeSummaryComponent } from './permit-type-summary.component';

describe('PermitTypeSummaryComponent', () => {
  let store: PermitApplicationStore<PermitApplicationState>;
  let component: PermitTypeSummaryComponent;
  let fixture: ComponentFixture<PermitTypeSummaryComponent>;

  const createComponent = () => {
    fixture = TestBed.createComponent(PermitTypeSummaryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [PermitTypeSummaryComponent],
      imports: [RouterTestingModule, SharedModule, SharedPermitModule],
      providers: [
        {
          provide: PermitApplicationStore,
          useExisting: PermitIssuanceStore,
        },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(PermitIssuanceStore);
    store.setState(mockState);

    createComponent();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
