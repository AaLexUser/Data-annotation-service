import type { AxiosError, AxiosResponse } from 'axios';
import axios from 'axios';
import _ from 'lodash';
import { message } from '@/components/StaticAnt';
import { EECode } from './errorCode';
import { goLogin } from '@/utils/sso';

export function successHandler(response: AxiosResponse<any>) {
  return response.data;
}

function errorHandler(error: AxiosError) {
  const errMsgFromServer = _.get(error, 'response.data.detail.message');
  const errCode = _.get(error, 'response.data.detail.code');
  // Special error codes that don't need global error reporting, should be handled in business logic
  if (Object.values(EECode).includes(errCode as any)) {
    return Promise.reject(_.get(error, 'response.data.detail'));
  }
  const errorText = (errCode && EECode[errCode]) || errMsgFromServer;
  if (errorText) {
    message.error(errorText);
  }

  return Promise.reject(error);
}

const authorizationBearerFailed = (error: any) => {
  // Redirect to login page on 401
  if (error?.response?.status === 401 && !import.meta.env.DEV) {
    goLogin();
  }

  // 422 Unprocessable Entity - https://developer.mozilla.org/ru/docs/Web/HTTP/Status/422
  if (error?.response?.status === 422) {
    message.error('Invalid data format');
  }

  return Promise.reject(error);
};

const requestConfig = {
  timeout: 2 * 60 * 1000,
  baseURL: '/api',
};

const request = axios.create(requestConfig);
export const plainRequest = axios.create(requestConfig);

request.interceptors.response.use(successHandler, errorHandler);
request.interceptors.response.use(undefined, authorizationBearerFailed);
plainRequest.interceptors.response.use(undefined, errorHandler);
plainRequest.interceptors.response.use(undefined, authorizationBearerFailed);

export default request;
