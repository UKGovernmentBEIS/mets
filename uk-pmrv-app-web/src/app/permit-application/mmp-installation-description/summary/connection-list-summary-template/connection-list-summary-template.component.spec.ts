import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedPermitModule } from '@permit-application/shared/shared-permit.module';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';
import { mockState } from '@permit-application/testing/mock-state';
import { PermitIssuanceStore } from '@permit-issuance/store/permit-issuance.store';
import { SharedModule } from '@shared/shared.module';

import { ConnectionListSummaryTemplateComponent } from './connection-list-summary-template.component';

describe('ConnectionListSummaryTemplateComponent', () => {
  let component: ConnectionListSummaryTemplateComponent;
  let fixture: ComponentFixture<ConnectionListSummaryTemplateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ConnectionListSummaryTemplateComponent],
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
    const store = TestBed.inject(PermitApplicationStore);
    store.setState(mockState);
    fixture = TestBed.createComponent(ConnectionListSummaryTemplateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
