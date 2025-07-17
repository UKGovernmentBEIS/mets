import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { KeycloakService } from 'keycloak-angular';

import { CommonActionsStore } from '../../../../store/common-actions.store';
import { AerModule } from '../../../aer.module';
import { mockState } from '../../testing/mock-aer-submitted';
import { MonitoringPlanDetailsComponent } from './monitoring-plan-details.component';

describe('MonitoringPlanDetailsComponent', () => {
  let store: CommonActionsStore;
  let component: MonitoringPlanDetailsComponent;
  let fixture: ComponentFixture<MonitoringPlanDetailsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AerModule, RouterTestingModule],
      providers: [KeycloakService],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(CommonActionsStore);
    store.setState(mockState);

    fixture = TestBed.createComponent(MonitoringPlanDetailsComponent);
    component = fixture.componentInstance;

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
