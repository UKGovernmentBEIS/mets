import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { PermitIssuanceStore } from '../../../../../permit-issuance/store/permit-issuance.store';
import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { CalculationModule } from '../../calculation.module';
import { CalculationApproachHelpComponent } from './calculation-approach-help.component';

describe('CalculationApproachHelpComponent', () => {
  let component: CalculationApproachHelpComponent;
  let fixture: ComponentFixture<CalculationApproachHelpComponent>;

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
    fixture = TestBed.createComponent(CalculationApproachHelpComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
