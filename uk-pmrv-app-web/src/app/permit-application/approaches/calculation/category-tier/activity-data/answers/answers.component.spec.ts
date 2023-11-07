import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { CalculationOfCO2MonitoringApproach } from 'pmrv-api';

import { PermitIssuanceStore } from '../../../../../../permit-issuance/store/permit-issuance.store';
import { PermitApplicationState } from '../../../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../../../store/permit-application.store';
import { mockPermitApplyPayload } from '../../../../../testing/mock-permit-apply-action';
import { mockStateBuild } from '../../../../../testing/mock-state';
import { CalculationModule } from '../../../calculation.module';
import { AnswersComponent } from './answers.component';

describe('AnswersComponent', () => {
  let component: AnswersComponent;
  let fixture: ComponentFixture<AnswersComponent>;
  let store: PermitApplicationStore<PermitApplicationState>;

  const mockCalculationApproach = mockPermitApplyPayload.permit.monitoringApproaches
    .CALCULATION_CO2 as CalculationOfCO2MonitoringApproach;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CalculationModule, RouterTestingModule],
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
            CALCULATION_CO2: {
              ...mockCalculationApproach,
              sourceStreamCategoryAppliedTiers: [
                {
                  ...mockCalculationApproach.sourceStreamCategoryAppliedTiers[0],
                  activityData: {
                    ...mockCalculationApproach.sourceStreamCategoryAppliedTiers[0].activityData,
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
