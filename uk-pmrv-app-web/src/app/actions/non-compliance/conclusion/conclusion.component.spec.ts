import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { CommonActionsStore } from '../../store/common-actions.store';
import { NonComplianceModule } from '../non-compliance.module';
import { mockState } from '../testing/mock-non-compliance-submitted';
import { ConclusionComponent } from './conclusion.component';

describe('ConclusionComponent', () => {
  let store: CommonActionsStore;
  let component: ConclusionComponent;
  let fixture: ComponentFixture<ConclusionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NonComplianceModule, RouterTestingModule],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(CommonActionsStore);
    store.setState(mockState);

    fixture = TestBed.createComponent(ConclusionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
