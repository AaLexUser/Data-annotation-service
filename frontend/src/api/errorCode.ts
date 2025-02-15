// Special codes handled within business logic
export enum EECode {
  // Skip task code
  RELEASE_ERROR = 400305,
  // No permission
  USER_PERMISSION_DENIED = 403002,
  // Translation service unavailable
  TRANSLATE_SERVICE_UNAVAILABLE = 400801,
}

export const ECode = {
  500001: 'Server error',
  403002: 'Insufficient user permissions',
  403003: 'Invalid parameter format',
  4011001: 'Invalid Token',
  // User
  400201: 'This account does not exist, please ensure the account has logged into the platform',
  400202: 'This account is already an admin, addition failed',
  400203: 'This account is already an admin, addition failed',
  // Task
  400301: 'Task does not exist',
  400302: 'Task has ended, modifications not supported',
  400303: 'Task process does not exist',
  400304: 'This labeling task already has a corresponding review task, please select another task',
  // Data
  400401: 'Data does not exist',
  400402: 'Data does not belong to the user',
  400403: 'No more questions available for this task, please choose a different task',
  // Team
  400901: 'Team does not exist',
  400902: 'The default team cannot be removed',
  400903: 'User has not joined the team',
  400904: 'User has already joined the team',
  400905: 'Team member role is empty',
  400906: 'The team should have at least one super administrator',
  400951: 'Link does not exist',
} as Record<string, string>;
