import { DoalAuthorityTaskSectionKey, DoalTaskSectionKey } from './doal-task.type';

export function resolveStatusBySectionsCompleted(
  doalSectionsCompleted: { [key: string]: boolean },
  section: DoalTaskSectionKey | DoalAuthorityTaskSectionKey,
) {
  return doalSectionsCompleted[section] === true
    ? 'complete'
    : doalSectionsCompleted[section] === false
      ? 'in progress'
      : 'not started';
}
