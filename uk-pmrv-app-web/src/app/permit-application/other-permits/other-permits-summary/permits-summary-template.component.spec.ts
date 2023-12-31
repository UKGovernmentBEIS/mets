import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { PermitIssuanceStore } from '../../../permit-issuance/store/permit-issuance.store';
import { SharedModule } from '../../../shared/shared.module';
import { SharedPermitModule } from '../../shared/shared-permit.module';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { mockState } from '../../testing/mock-state';
import { PermitsSummaryTemplateComponent } from './permits-summary-template.component';

describe('PermitsSummaryTemplateComponent', () => {
  let component: PermitsSummaryTemplateComponent;
  let fixture: ComponentFixture<PermitsSummaryTemplateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [PermitsSummaryTemplateComponent],
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
    const store = TestBed.inject(PermitIssuanceStore);
    store.setState(mockState);
    fixture = TestBed.createComponent(PermitsSummaryTemplateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
