import { TaskItemStatus, TaskSection } from '@shared/task-list/task-list.interface';

import {
  NonComplianceApplicationSubmitRequestTaskPayload,
  NonComplianceCivilPenaltyRequestTaskPayload,
  NonComplianceDailyPenaltyNoticeRequestTaskPayload,
  NonComplianceFinalDeterminationRequestTaskPayload,
  NonComplianceNoticeOfIntentRequestTaskPayload,
} from 'pmrv-api';

export function getTaskStatusByTaskCompletionState(completionState?: boolean): TaskItemStatus {
  return completionState != null ? (completionState ? 'complete' : 'in progress') : 'not started';
}

export function getNonComplianceApplicationSections(
  payload: NonComplianceApplicationSubmitRequestTaskPayload,
): TaskSection<any>[] {
  return NON_COMPLIANCE_APPLICATION_TASKS.map((section) => {
    return {
      ...section,
      tasks: section.tasks.map((task) => {
        const status = getTaskStatusByTaskCompletionState(payload.sectionCompleted);

        const link =
          status === 'complete' ? 'non-compliance/submit/summary' : 'non-compliance/submit/details-of-breach';

        return {
          ...task,
          status,
          link,
        };
      }),
    };
  });
}

const NON_COMPLIANCE_APPLICATION_TASKS: TaskSection<any>[] = [
  {
    title: null,
    tasks: [
      {
        name: 'non-compliance',
        linkText: 'Provide details of breach',
        link: 'non-compliance/submit/details-of-breach',
      },
    ],
  },
];

export function getNonComplianceDailyPenaltySections(
  payload: NonComplianceDailyPenaltyNoticeRequestTaskPayload,
): TaskSection<any>[] {
  return NON_COMPLIANCE_DAILY_PENALTY_TASKS.map((section) => {
    return {
      ...section,
      tasks: section.tasks.map((task) => {
        const status = getTaskStatusByTaskCompletionState(payload.dailyPenaltyCompleted);

        const link =
          status === 'complete'
            ? 'non-compliance/daily-penalty-notice/summary'
            : 'non-compliance/daily-penalty-notice/upload-initial-notice';

        return {
          ...task,
          status,
          link,
        };
      }),
    };
  });
}

const NON_COMPLIANCE_DAILY_PENALTY_TASKS: TaskSection<any>[] = [
  {
    title: null,
    tasks: [
      {
        name: 'non-compliance',
        linkText: 'Upload daily penalty notice',
        link: 'non-compliance/daily-penalty-notice/upload-initial-notice',
      },
    ],
  },
];

export function getNonComplianceDailyPenaltyPeerReviewWaitSections(
  payload: NonComplianceDailyPenaltyNoticeRequestTaskPayload,
): TaskSection<any>[] {
  return NON_COMPLIANCE_DAILY_PENALTY_PEER_REVIEW_WAIT_TASKS.map((section) => {
    return {
      ...section,
      tasks: section.tasks.map((task) => {
        const status = getTaskStatusByTaskCompletionState(payload.dailyPenaltyCompleted);

        const link = 'non-compliance/daily-penalty-notice/summary';

        return {
          ...task,
          status,
          link,
        };
      }),
    };
  });
}

const NON_COMPLIANCE_DAILY_PENALTY_PEER_REVIEW_WAIT_TASKS: TaskSection<any>[] = [
  {
    title: null,
    tasks: [
      {
        name: 'non-compliance',
        linkText: 'Upload daily penalty notice',
        link: 'non-compliance/daily-penalty-notice/summary',
      },
    ],
  },
];

export function getNonComplianceDailyPenaltyPeerReviewSections(
  payload: NonComplianceDailyPenaltyNoticeRequestTaskPayload,
): TaskSection<any>[] {
  return NON_COMPLIANCE_DAILY_PENALTY_PEER_REVIEW_TASKS.map((section) => {
    return {
      ...section,
      tasks: section.tasks.map((task) => {
        const status = getTaskStatusByTaskCompletionState(payload.dailyPenaltyCompleted);

        const link = 'non-compliance/daily-penalty-notice/summary';

        return {
          ...task,
          status,
          link,
        };
      }),
    };
  });
}

const NON_COMPLIANCE_DAILY_PENALTY_PEER_REVIEW_TASKS: TaskSection<any>[] = [
  {
    title: null,
    tasks: [
      {
        name: 'non-compliance',
        linkText: 'Upload daily penalty notice',
        link: 'non-compliance/daily-penalty-notice/summary',
      },
    ],
  },
];

export function getNonComplianceNoticeOfIntentSections(
  payload: NonComplianceNoticeOfIntentRequestTaskPayload,
): TaskSection<any>[] {
  return NON_COMPLIANCE_NOTICE_OF_INTENT_TASKS.map((section) => {
    return {
      ...section,
      tasks: section.tasks.map((task) => {
        const status = getTaskStatusByTaskCompletionState(payload.noticeOfIntentCompleted);

        const link =
          status === 'complete'
            ? 'non-compliance/notice-of-intent/summary'
            : 'non-compliance/notice-of-intent/upload-notice-of-intent';

        return {
          ...task,
          status,
          link,
        };
      }),
    };
  });
}

const NON_COMPLIANCE_NOTICE_OF_INTENT_TASKS: TaskSection<any>[] = [
  {
    title: null,
    tasks: [
      {
        name: 'non-compliance',
        linkText: 'Upload daily penalty notice',
        link: 'non-compliance/notice-of-intent/upload-notice-of-intent',
      },
    ],
  },
];

export function getNonComplianceNoticeOfIntentPeerReviewWaitSections(
  payload: NonComplianceNoticeOfIntentRequestTaskPayload,
): TaskSection<any>[] {
  return NON_COMPLIANCE_NOTICE_OF_INTENT_PEER_REVIEW_WAIT_TASKS.map((section) => {
    return {
      ...section,
      tasks: section.tasks.map((task) => {
        const status = getTaskStatusByTaskCompletionState(payload.noticeOfIntentCompleted);

        const link = 'non-compliance/notice-of-intent/summary';

        return {
          ...task,
          status,
          link,
        };
      }),
    };
  });
}

const NON_COMPLIANCE_NOTICE_OF_INTENT_PEER_REVIEW_WAIT_TASKS: TaskSection<any>[] = [
  {
    title: null,
    tasks: [
      {
        name: 'non-compliance',
        linkText: 'Upload notice of intent',
        link: 'non-compliance/notice-of-intent/summary',
      },
    ],
  },
];

export function getNonComplianceNoticeOfIntentPeerReviewSections(
  payload: NonComplianceNoticeOfIntentRequestTaskPayload,
): TaskSection<any>[] {
  return NON_COMPLIANCE_NOTICE_OF_INTENT_PEER_REVIEW_TASKS.map((section) => {
    return {
      ...section,
      tasks: section.tasks.map((task) => {
        const status = getTaskStatusByTaskCompletionState(payload.noticeOfIntentCompleted);

        const link = 'non-compliance/notice-of-intent/summary';

        return {
          ...task,
          status,
          link,
        };
      }),
    };
  });
}

const NON_COMPLIANCE_NOTICE_OF_INTENT_PEER_REVIEW_TASKS: TaskSection<any>[] = [
  {
    title: null,
    tasks: [
      {
        name: 'non-compliance',
        linkText: 'Upload notice of intent',
        link: 'non-compliance/notice-of-intent/summary',
      },
    ],
  },
];

export function getNonComplianceCivilPenaltytSections(
  payload: NonComplianceCivilPenaltyRequestTaskPayload,
): TaskSection<any>[] {
  return NON_COMPLIANCE_CIVIL_PENALTY_TASKS.map((section) => {
    return {
      ...section,
      tasks: section.tasks.map((task) => {
        const status = getTaskStatusByTaskCompletionState(payload.civilPenaltyCompleted);

        const link =
          status === 'complete'
            ? 'non-compliance/civil-penalty-notice/summary'
            : 'non-compliance/civil-penalty-notice/upload-penalty-notice';

        return {
          ...task,
          status,
          link,
        };
      }),
    };
  });
}

const NON_COMPLIANCE_CIVIL_PENALTY_TASKS: TaskSection<any>[] = [
  {
    title: null,
    tasks: [
      {
        name: 'non-compliance',
        linkText: 'Upload penalty notice',
        link: 'non-compliance/civil-penalty-notice/upload-penalty-notice',
      },
    ],
  },
];

export function getNonComplianceCivilPenaltytPeerReviewWaitSections(
  payload: NonComplianceCivilPenaltyRequestTaskPayload,
): TaskSection<any>[] {
  return NON_COMPLIANCE_CIVIL_PENALTY_PEER_REVIEW_WAIT_TASKS.map((section) => {
    return {
      ...section,
      tasks: section.tasks.map((task) => {
        const status = getTaskStatusByTaskCompletionState(payload.civilPenaltyCompleted);

        const link = 'non-compliance/civil-penalty-notice/summary';

        return {
          ...task,
          status,
          link,
        };
      }),
    };
  });
}

const NON_COMPLIANCE_CIVIL_PENALTY_PEER_REVIEW_WAIT_TASKS: TaskSection<any>[] = [
  {
    title: null,
    tasks: [
      {
        name: 'non-compliance',
        linkText: 'Upload penalty notice',
        link: 'non-compliance/civil-penalty-notice/summary',
      },
    ],
  },
];

export function getNonComplianceCivilPenaltytPeerReviewSections(
  payload: NonComplianceCivilPenaltyRequestTaskPayload,
): TaskSection<any>[] {
  return NON_COMPLIANCE_CIVIL_PENALTY_PEER_REVIEW_TASKS.map((section) => {
    return {
      ...section,
      tasks: section.tasks.map((task) => {
        const status = getTaskStatusByTaskCompletionState(payload.civilPenaltyCompleted);

        const link = 'non-compliance/civil-penalty-notice/summary';

        return {
          ...task,
          status,
          link,
        };
      }),
    };
  });
}

const NON_COMPLIANCE_CIVIL_PENALTY_PEER_REVIEW_TASKS: TaskSection<any>[] = [
  {
    title: null,
    tasks: [
      {
        name: 'non-compliance',
        linkText: 'Upload penalty notice',
        link: 'non-compliance/civil-penalty-notice/summary',
      },
    ],
  },
];

export function getNonComplianceFinalDeterminationSections(
  payload: NonComplianceFinalDeterminationRequestTaskPayload,
): TaskSection<any>[] {
  return NON_COMPLIANCE_FINAL_DETERMINATION_TASKS.map((section) => {
    return {
      ...section,
      tasks: section.tasks.map((task) => {
        const status = getTaskStatusByTaskCompletionState(payload.determinationCompleted);

        const link =
          status === 'complete' ? 'non-compliance/conclusion/summary' : 'non-compliance/conclusion/provide-conclusion';

        return {
          ...task,
          status,
          link,
        };
      }),
    };
  });
}

const NON_COMPLIANCE_FINAL_DETERMINATION_TASKS: TaskSection<any>[] = [
  {
    title: null,
    tasks: [
      {
        name: 'non-compliance',
        linkText: 'Provide conclusion of non-compliance',
        link: 'non-compliance/conclusion/provide-conclusion',
      },
    ],
  },
];
