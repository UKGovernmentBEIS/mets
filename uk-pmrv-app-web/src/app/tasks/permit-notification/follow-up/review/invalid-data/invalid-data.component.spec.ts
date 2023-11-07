import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { KeycloakService } from 'keycloak-angular';

import { SharedModule } from '../../../../../shared/shared.module';
import { CommonTasksStore } from '../../../../store/common-tasks.store';
import { InvalidDataComponent } from './invalid-data.component';

describe('InvalidDataComponent', () => {
  let component: InvalidDataComponent;
  let fixture: ComponentFixture<InvalidDataComponent>;
  let store: CommonTasksStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [InvalidDataComponent],
      providers: [KeycloakService],
      imports: [RouterTestingModule, SharedModule],
    }).compileComponents();

    store = TestBed.inject(CommonTasksStore);
    store.setState({
      ...store.getState(),
      storeInitialized: true,
      isEditable: true,
      requestTaskItem: {
        requestTask: {
          id: 1,
          type: 'PERMIT_NOTIFICATION_FOLLOW_UP_APPLICATION_REVIEW',
          payload: {
            payloadType: 'PERMIT_NOTIFICATION_FOLLOW_UP_APPLICATION_REVIEW_PAYLOAD',
          },
        },
        requestInfo: {
          accountId: 1,
        },
      },
    });

    fixture = TestBed.createComponent(InvalidDataComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
