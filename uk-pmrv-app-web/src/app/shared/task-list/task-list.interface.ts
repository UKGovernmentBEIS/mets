export interface TaskSection<T> {
  type?: T;
  title: string;
  tasks: TaskItem<any>[];
}

export interface TaskItem<T> {
  name?: string;
  type?: T;
  linkText: string;
  link: string;
  status?: TaskItemStatus;
  value?: unknown;
}

export type TaskItemStatus =
  | 'not started'
  | 'cannot start yet'
  | 'in progress'
  | 'incomplete'
  | 'complete'
  | 'needs review'
  | 'undecided'
  | 'accepted'
  | 'operator to amend'
  | 'rejected';
