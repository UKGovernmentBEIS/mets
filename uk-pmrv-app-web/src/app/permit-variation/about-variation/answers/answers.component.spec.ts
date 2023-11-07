import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedPermitModule } from '../../../permit-application/shared/shared-permit.module';
import { PermitApplicationStore } from '../../../permit-application/store/permit-application.store';
import { SharedModule } from '../../../shared/shared.module';
import { PermitVariationStore } from '../../store/permit-variation.store';
import { SummaryDetailsComponent } from '../summary/summary-details.component';
import { AnswersComponent } from './answers.component';

describe('AnswersComponent', () => {
  let component: AnswersComponent;
  let fixture: ComponentFixture<AnswersComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AnswersComponent, SummaryDetailsComponent],
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
    fixture = TestBed.createComponent(AnswersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
