import { Pipe, PipeTransform } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, Observable, of } from 'rxjs';

import { PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload } from 'pmrv-api';

import { ReviewGroupDecisionStatus } from '../../review/types/review.permit.type';
import { ReviewGroupStatusWrapperPipe } from './review-group-status-wrapper.pipe';

describe('ReviewGroupStatusWrapperPipe', () => {
  let pipe: ReviewGroupStatusWrapperPipe;

  @Pipe({
    name: 'mockPipe',
  })
  class MockPipe implements PipeTransform {
    transform(
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      key: PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload['group'],
    ): Observable<ReviewGroupDecisionStatus> {
      return of('needs review');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      declarations: [ReviewGroupStatusWrapperPipe, MockPipe],
    });
  });

  beforeEach(() => (pipe = new ReviewGroupStatusWrapperPipe()));

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should return value', async () => {
    await expect(firstValueFrom(pipe.transform('INSTALLATION_DETAILS', new MockPipe()))).resolves.toEqual(
      'needs review',
    );
  });
});
