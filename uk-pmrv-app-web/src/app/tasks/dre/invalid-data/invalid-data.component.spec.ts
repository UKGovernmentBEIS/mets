import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { format, subDays } from 'date-fns';
import { KeycloakService } from 'keycloak-angular';

import { DreApplicationSubmitRequestTaskPayload } from 'pmrv-api';

import { SharedModule } from '../../../shared/shared.module';
import { CommonTasksStore } from '../../../tasks/store/common-tasks.store';
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
          payload: {
            dre: {
              fee: {
                feeDetails: {
                  dueDate: format(subDays(new Date(), 1), 'yyyy-MM-dd'),
                },
              },
            },
          } as DreApplicationSubmitRequestTaskPayload,
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
