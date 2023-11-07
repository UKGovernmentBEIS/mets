import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { PermitTransferAModule } from '../permit-transfer-a.module';
import { SubmitContainerComponent } from './submit-container.component';

describe('SubmitComponent', () => {
  let component: SubmitContainerComponent;
  let fixture: ComponentFixture<SubmitContainerComponent>;
  let page: Page;
  let store: CommonTasksStore;

  class Page extends BasePage<SubmitContainerComponent> {
    get sections(): HTMLUListElement[] {
      return Array.from(this.queryAll<HTMLUListElement>('.app-task-list__item > .app-task-list__task-name'));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SubmitContainerComponent],
      providers: [KeycloakService],
      imports: [PermitTransferAModule, RouterTestingModule],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(CommonTasksStore);
    jest.spyOn(store, 'storeInitialized$', 'get').mockReturnValue(of(true));
    jest.spyOn(store, 'requestTaskItem$', 'get').mockReturnValue(
      of({
        requestTask: {
          id: 1,
          type: 'PERMIT_TRANSFER_A_APPLICATION_SUBMIT',
          payload: {
            payloadType: 'PERMIT_TRANSFER_A_APPLICATION_SUBMIT_PAYLOAD',
            reasonAttachments: [],
            transferAttachments: {},
          },
        },
        allowedRequestTaskActions: [],
      }),
    );

    fixture = TestBed.createComponent(SubmitContainerComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the content', () => {
    expect(page.sections.map((el) => el.textContent.trim())).toEqual([
      'Provide transfer details',
      'Send to the new operator',
    ]);
  });
});
