import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, mockClass } from '../../../../testing';
import { PermitIssuanceStore } from '../../../permit-issuance/store/permit-issuance.store';
import { SharedModule } from '../../../shared/shared.module';
import { SharedPermitModule } from '../../shared/shared-permit.module';
import { PermitApplicationState } from '../../store/permit-application.state';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { mockPermitApplyPayload } from '../../testing/mock-permit-apply-action';
import { mockPostBuild, mockState } from '../../testing/mock-state';
import { SourceStreamDeleteComponent } from './source-stream-delete.component';

describe('SourceStreamDeleteComponent', () => {
  let page: Page;
  let router: Router;
  let store: PermitApplicationStore<PermitApplicationState>;
  let component: SourceStreamDeleteComponent;
  let fixture: ComponentFixture<SourceStreamDeleteComponent>;

  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub({ streamId: mockPermitApplyPayload.permit.sourceStreams[0].id });

  class Page extends BasePage<SourceStreamDeleteComponent> {
    get header() {
      return this.query<HTMLHeadingElement>('h1');
    }

    get submitButton() {
      return this.queryAll<HTMLButtonElement>('button')[0];
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SourceStreamDeleteComponent],
      imports: [RouterTestingModule, SharedModule, SharedPermitModule],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        { provide: TasksService, useValue: tasksService },
        {
          provide: PermitApplicationStore,
          useExisting: PermitIssuanceStore,
        },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(PermitIssuanceStore);
    store.setState(mockState);
    fixture = TestBed.createComponent(SourceStreamDeleteComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the source stream name', () => {
    expect(page.header.textContent).toContain(mockPermitApplyPayload.permit.sourceStreams[0].reference);
  });

  it('should delete the source stream', () => {
    expect(store.permit.sourceStreams).toEqual(mockPermitApplyPayload.permit.sourceStreams);

    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
    const navigateSpy = jest.spyOn(router, 'navigate');
    const expectedSourceStreams = mockPermitApplyPayload.permit.sourceStreams.filter(
      (stream) => stream.id !== mockPermitApplyPayload.permit.sourceStreams[0].id,
    );

    page.submitButton.click();

    expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith(
      mockPostBuild({ sourceStreams: expectedSourceStreams }, { sourceStreams: [false] }),
    );
    expect(navigateSpy).toHaveBeenCalledTimes(1);
    expect(store.permit.sourceStreams).toEqual(expectedSourceStreams);
  });
});
