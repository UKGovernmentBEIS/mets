import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestActionStore } from '@aviation/request-action/store';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { GovukDatePipe } from '@shared/pipes/govuk-date.pipe';
import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, BasePage } from '@testing';

import { CompletedComponent } from './completed.component';

describe('CompletedComponent', () => {
  let component: CompletedComponent;
  let fixture: ComponentFixture<CompletedComponent>;
  let store: RequestActionStore;

  let page: Page;

  const route = new ActivatedRouteStub();
  const currentDate = new Date().toISOString();

  class Page extends BasePage<CompletedComponent> {
    get heading() {
      return this.query<HTMLHeadingElement>('app-request-action-heading h1').textContent.trim();
    }

    get headingCaption() {
      return this.query<HTMLHeadingElement>('app-request-action-heading p.govuk-caption-m').textContent.trim();
    }

    get values() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CompletedComponent, SharedModule],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        { provide: TYPE_AWARE_STORE, useExisting: RequestActionStore },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestActionStore);
    store.setState({
      requestActionItem: {
        type: 'REISSUE_COMPLETED',
        creationDate: currentDate,
        payload: {
          payloadType: 'EMP_REISSUE_COMPLETED_PAYLOAD',
          submitter: 'Submitter1',
          signatory: 'ef8828e1-c5a2-4b5b-b4c4-5fc9d4f7b4f3',
          signatoryName: 'Signatory name',
          officialNotice: {
            name: 'Batch_variation_notice_UK_ETS.pdf',
            uuid: '8d3c40a1-739e-4e82-8d5f-9e672ccd002f',
          },
          document: {
            name: 'UK-E-AV-00061 v15.pdf',
            uuid: 'd95d2987-526d-49d3-b0c6-cbd5e8e58522',
          },
        } as any,
      },
      regulatorViewer: true,
    });

    fixture = TestBed.createComponent(CompletedComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display header', () => {
    expect(page.heading).toEqual('Batch variation completed');
  });

  it('should display heading caption', () => {
    expect(page.headingCaption).toEqual(new GovukDatePipe().transform(currentDate, 'datetime'));
  });

  it('should show filters', () => {
    expect(page.values).toEqual([
      ['Created by', 'Submitter1'],
      ['Date created', `${new GovukDatePipe().transform(currentDate)}`],
      ['Signatory', 'Signatory name'],
      ['Documents', 'Batch_variation_notice_UK_ETS.pdfUK-E-AV-00061 v15.pdf'],
    ]);
  });
});
