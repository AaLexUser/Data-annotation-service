import request from './request';

export interface ICreate {
  username: string;
  password: string;
}

/**
 * Creates a new user with the specified parameters
 * @param {ICreate} params - The parameters required to create a user
 * @returns {Promise<void>} A promise that resolves when the user is created
 * @throws {Error} When the API request fails
 */
export const create = (params: ICreate): Promise<void> => {
  return request.post('/v1/user/create', params);
};

export enum EUserRole {
  admin = 'admin',
  user = 'user',
}

export interface IUserInfo {
  user_id: number;
  name: string;
  role: EUserRole;
  teams?: IUserInfo[];
}

/**
 * Logs in a user with provided credentials
 * @param {ICreate} params - The login credentials
 * @returns {Promise<IUserInfo>} A promise that resolves to user information
 * @throws {Error} If the login request fails
 */
export const login = (params: ICreate): Promise<IUserInfo> => {
  return request.post('/v1/user/login', params);
};

/**
 * Makes a POST request to log out the current user from the system.
 * @returns A Promise that resolves when the logout is successful
 */
export const logout = (): Promise<void> => {
  return request.post('/v1/user/logout');
};

/**
 * Retrieves the current user's information from the API.
 * 
 * @returns Promise containing the user information (IUserInfo)
 * @throws {Error} If the request fails or user is not authenticated
 */
export const getUserInfo = async (): Promise<IUserInfo> => {
  return request.get('/v1/user/me');
};

/**
 * Updates user information in the system
 * @param params - Object containing user update parameters
 * @param params.user_id - The ID of the user to update
 * @param params.role - The new role to assign to the user
 * @returns Promise containing the response from the API request
 */
export const updateUser = (params: Pick<IUserInfo, 'user_id' | 'role'>) => {
  return request.post('/v1/user/edit', params);
};
