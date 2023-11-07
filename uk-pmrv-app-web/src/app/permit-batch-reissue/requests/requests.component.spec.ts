import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { BatchReissuesResponseDTO, RequestsService } from 'pmrv-api';

import { BasePage, mockClass } from '../../../testing';
import { DestroySubject } from '../../core/services/destroy-subject.service';
import { mockPermitBatchReissues } from '../../shared/components/batch-reissue-requests/testing/mock-data';
import { SharedModule } from '../../shared/shared.module';
import { RequestsComponent } from './requests.component';

describe('RequestsComponent', () => {
  let component: RequestsComponent;
  let fixture: ComponentFixture<RequestsComponent>;
  let hostElement: HTMLElement;
  let page: Page;

  const requestsService = mockClass(RequestsService);

  class Page extends BasePage<RequestsComponent> {
    get startButton() {
      return this.query<HTMLButtonElement>('button[type="button"]');
    }

    get rowsCells() {
      return this.queryAll<HTMLTableRowElement>('table tr')
        .filter((row) => !row.querySelector('th'))
        .map((row) => Array.from(row.querySelectorAll('td')).map((td) => td.textContent.trim()));
    }
  }

  const createComponent = async () => {
    fixture = TestBed.createComponent(RequestsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    hostElement = fixture.nativeElement;
    fixture.detectChanges();
  };

  const createModule = async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule],
      declarations: [RequestsComponent],
      providers: [{ provide: RequestsService, useValue: requestsService }, DestroySubject],
    }).compileComponents();
  };

  afterEach(() => jest.clearAllMocks());

  describe('for empty results with no permission to start batch reissue', () => {
    beforeEach(async () => {
      requestsService.getBatchReissueRequests.mockReturnValue(
        of({
          canInitiateBatchReissue: false,
          requestDetails: [],
          total: 0,
        } as BatchReissuesResponseDTO),
      );
    });

    beforeEach(createModule);
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should render no results found', () => {
      expect(hostElement.textContent).toContain('There are no results to show');
      expect(requestsService.getBatchReissueRequests).toHaveBeenCalledTimes(1);
      expect(requestsService.getBatchReissueRequests).toHaveBeenCalledWith('INSTALLATION', 0, 30);
    });

    it('should not display start button', () => {
      expect(page.startButton).toBeNull();
    });
  });

  describe('for non empty results with permission to start batch reissue', () => {
    beforeEach(async () => {
      requestsService.getBatchReissueRequests.mockReturnValue(of(mockPermitBatchReissues));
    });

    beforeEach(createModule);
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should render the results', () => {
      expect(page.rowsCells).toEqual([
        ['requestId1', 'submitter_full_name1', '25 May 2023', '2'],
        ['requestId2', 'submitter_full_name2', '', ''],
      ]);
      expect(requestsService.getBatchReissueRequests).toHaveBeenCalledTimes(1);
    });

    it('should display start button', () => {
      expect(page.startButton).toBeTruthy();
    });
  });
});
