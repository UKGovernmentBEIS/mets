import { Location } from '@angular/common';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { SharedStore } from '@shared/store/shared.store';

import { TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, mockClass } from '../../../../../testing';
import { SharedModule } from '../../../shared.module';
import { StoreContextResolver } from '../../../store-resolver/store-context.resolver';
import { AnswersComponent } from './answers.component';

describe('AnswersComponent', () => {
  let page: Page;
  let component: AnswersComponent;
  let fixture: ComponentFixture<AnswersComponent>;
  let router: Router;
  let store: SharedStore;
  let location: Location;

  const storeResolver = mockClass(StoreContextResolver);
  const tasksService = mockClass(TasksService);

  const activatedRouteStub = new ActivatedRouteStub({ taskId: '279' });

  class Page extends BasePage<AnswersComponent> {
    get answers() {
      return this.queryAll('dd').map((row) => row.textContent.trim());
    }

    get confirmButton() {
      return this.query<HTMLButtonElement>('.govuk-button');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, RouterTestingModule],
      providers: [
        { provide: ActivatedRoute, useValue: activatedRouteStub },
        { provide: TasksService, useValue: tasksService },
        { provide: StoreContextResolver, useValue: storeResolver },
      ],
    }).compileComponents();

    store = TestBed.inject(SharedStore);
    router = TestBed.inject(Router);
    location = TestBed.inject(Location);
  });

  beforeEach(() => {
    jest.spyOn(location, 'path').mockReturnValue('/permit-issuance/279/review/peer-review-decision/answers');
    storeResolver.getRequestTaskType.mockReturnValue(of('PERMIT_ISSUANCE_APPLICATION_PEER_REVIEW'));

    store.setState({
      ...store.getState(),
      peerReviewDecision: {
        type: 'AGREE',
        notes: 'I agree',
      },
    });
    fixture = TestBed.createComponent(AnswersComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the answers', () => {
    expect(page.answers).toEqual(['Agreed with the determination', 'I agree']);
  });

  it('should submit task status', () => {
    tasksService.processRequestTaskAction.mockReturnValueOnce(of({}));
    const navigateSpy = jest.spyOn(router, 'navigate');

    page.confirmButton.click();
    fixture.detectChanges();

    expect(tasksService.processRequestTaskAction).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskAction).toHaveBeenCalledWith({
      requestTaskActionType: 'PERMIT_ISSUANCE_REVIEW_SUBMIT_PEER_REVIEW_DECISION',
      requestTaskId: 279,
      requestTaskActionPayload: {
        payloadType: 'PERMIT_ISSUANCE_REVIEW_SUBMIT_PEER_REVIEW_DECISION_PAYLOAD',
        decision: {
          type: 'AGREE',
          notes: 'I agree',
        },
      },
    });
    expect(navigateSpy).toHaveBeenCalledWith(['../confirmation'], { relativeTo: TestBed.inject(ActivatedRoute) });
    expect(location.path).toHaveBeenCalled();
  });
});
