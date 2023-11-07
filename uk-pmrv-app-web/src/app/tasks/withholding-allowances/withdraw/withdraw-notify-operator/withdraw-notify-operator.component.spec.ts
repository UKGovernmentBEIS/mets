import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { KeycloakService } from 'keycloak-angular';

import { SharedModule } from '../../../../shared/shared.module';
import { CommonTasksStore } from '../../../store/common-tasks.store';
import { mockState } from '../testing/mock-state';
import { WithdrawNotifyOperatorComponent } from './withdraw-notify-operator.component';

describe('WithdrawNotifyOperatorComponent', () => {
  let component: WithdrawNotifyOperatorComponent;
  let fixture: ComponentFixture<WithdrawNotifyOperatorComponent>;
  let store: CommonTasksStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule],
      declarations: [WithdrawNotifyOperatorComponent],
      providers: [KeycloakService, DestroySubject],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(CommonTasksStore);
    store.setState(mockState);

    fixture = TestBed.createComponent(WithdrawNotifyOperatorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
