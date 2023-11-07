import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { CalculationOfPFCMonitoringApproach } from 'pmrv-api';

import { PermitIssuanceStore } from '../../../../../../permit-issuance/store/permit-issuance.store';
import { PermitApplicationState } from '../../../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../../../store/permit-application.store';
import { mockPermitApplyPayload } from '../../../../../testing/mock-permit-apply-action';
import { mockStateBuild } from '../../../../../testing/mock-state';
import { PFCModule } from '../../../pfc.module';
import { AnswersComponent } from './answers.component';

describe('AnswersComponent', () => {
  let component: AnswersComponent;
  let fixture: ComponentFixture<AnswersComponent>;
  let store: PermitApplicationStore<PermitApplicationState>;

  const mockPfcApproach = mockPermitApplyPayload.permit.monitoringApproaches
    .CALCULATION_PFC as CalculationOfPFCMonitoringApproach;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PFCModule, RouterTestingModule],
      providers: [
        {
          provide: PermitApplicationStore,
          useExisting: PermitIssuanceStore,
        },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AnswersComponent);
    component = fixture.componentInstance;
    store = TestBed.inject(PermitApplicationStore);
    store.setState(
      mockStateBuild(
        {
          monitoringApproaches: {
            CALCULATION_PFC: {
              ...mockPfcApproach,
              sourceStreamCategoryAppliedTiers: [
                {
                  ...mockPfcApproach.sourceStreamCategoryAppliedTiers[0],
                  activityData: {
                    ...mockPfcApproach.sourceStreamCategoryAppliedTiers[0].activityData,
                    isHighestRequiredTier: true,
                  },
                },
              ],
            },
          },
        },
        {},
      ),
    );
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
