import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { CommonActionsStore } from '../../store/common-actions.store';
import { NonComplianceModule } from '../non-compliance.module';
import { mockState } from '../testing/mock-non-compliance-submitted';
import { ClosedComponent } from './closed.component';

describe('ClosedComponent', () => {
  let store: CommonActionsStore;
  let component: ClosedComponent;
  let fixture: ComponentFixture<ClosedComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NonComplianceModule, RouterTestingModule],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(CommonActionsStore);
    store.setState(mockState);

    fixture = TestBed.createComponent(ClosedComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
