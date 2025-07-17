import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { SourceStreamsTableComponent } from '@shared/components/source-streams/source-streams-table/source-streams-table.component';

import { BasePage } from '../../../../testing';
import { PermitIssuanceStore } from '../../../permit-issuance/store/permit-issuance.store';
import { SharedModule } from '../../../shared/shared.module';
import { SharedPermitModule } from '../../shared/shared-permit.module';
import { PermitApplicationState } from '../../store/permit-application.state';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { sourceStreamsPayload } from '../../testing/mock-permit-apply-action';
import { SourceStreamsSummaryComponent } from './source-streams-summary.component';

describe('SourceStreamsSummaryComponent', () => {
  let component: SourceStreamsSummaryComponent;
  let fixture: ComponentFixture<SourceStreamsSummaryComponent>;
  let page: Page;
  let store: PermitApplicationStore<PermitApplicationState>;

  class Page extends BasePage<SourceStreamsSummaryComponent> {
    get sourceStreams() {
      return this.queryAll<HTMLDListElement>('tr').map((sourceStream) =>
        Array.from(sourceStream.querySelectorAll('td')).map((dd) => dd.textContent.trim()),
      );
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SourceStreamsSummaryComponent],
      imports: [RouterTestingModule, SharedModule, SharedPermitModule, SourceStreamsTableComponent],
      providers: [
        {
          provide: PermitApplicationStore,
          useExisting: PermitIssuanceStore,
        },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(PermitIssuanceStore);

    store.setState({
      ...store.getState(),
      permit: {
        ...store.permit,
        sourceStreams: sourceStreamsPayload,
      },
      requestTaskId: 279,
    });
    fixture = TestBed.createComponent(SourceStreamsSummaryComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the list of data', () => {
    expect(page.sourceStreams).toEqual([
      [],
      ['13123124', 'White Spirit & SBP', 'Refineries: Hydrogen production', '', ''],
      ['33334', 'Lignite', 'Refineries: Catalytic cracker regeneration', '', ''],
      ['33334', 'Other Description', 'Refineries: Catalytic cracker regeneration', '', ''],
    ]);
  });
});
