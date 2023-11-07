import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedPermitModule } from '../../../permit-application/shared/shared-permit.module';
import { PermitApplicationStore } from '../../../permit-application/store/permit-application.store';
import { SharedModule } from '../../../shared/shared.module';
import { PermitVariationStore } from '../../store/permit-variation.store';
import { SummaryComponent } from './summary.component';
import { SummaryDetailsComponent } from './summary-details.component';

describe('SummaryComponent', () => {
  let component: SummaryComponent;
  let fixture: ComponentFixture<SummaryComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SummaryComponent, SummaryDetailsComponent],
      imports: [RouterTestingModule, SharedModule, SharedPermitModule],
      providers: [
        {
          provide: PermitApplicationStore,
          useExisting: PermitVariationStore,
        },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SummaryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
