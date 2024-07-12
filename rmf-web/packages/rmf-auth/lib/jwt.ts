import EventEmitter from 'eventemitter3';
import { Authenticator, AuthenticatorEventType } from './authenticator';
import axios, { AxiosInstance } from 'axios';

interface LoginUser {
  username: string;
  password: string;
}

export class JwtAuthenticator extends EventEmitter<AuthenticatorEventType> implements Authenticator {

  private axios: AxiosInstance;

  private _unAuthorizedUser?: LoginUser;

  user?: string;

  token?: string; // Not used

  constructor() {
    super();
    this.axios = axios.create({
      baseURL: process.env.REACT_APP_RMF_SERVER,
      withCredentials: true,
    });
    this._unAuthorizedUser = undefined;
  }

  async init(): Promise<void> {
    try {
      await this.refreshToken();
      await this.axios.get("/is_login");
      this.user = (await this.axios.get('/user')).data?.username;
    } catch (error) {
      this.user = undefined;
      console.log(error);
    }
    return Promise.resolve();
  }

  async login(successRedirectUri: string): Promise<never> {
    const formData = this.toFormData(this._unAuthorizedUser);
    const status = (await this.axios.post('/login', formData)).status;
    if (status === 200) {
      window.location.href = successRedirectUri;
    }
    throw new Error('login failed');
  }

  async loginWithPassword(): Promise<void> {
    const formData = this.toFormData(this._unAuthorizedUser);
    const status = (await this.axios.post('/login', formData)).status;
    if (status === 200) {
      window.location.href = '/';
      return;
    }
    throw new Error('login failed');
  }

  async logout(): Promise<never> {
    await this.axios.post('/logout');
    this.user = undefined;
    window.location.href = '/login';
    throw new Error('logout failed');
  }

  async refreshToken(): Promise<void> {
    await this.axios.post('/token/refresh');
    return Promise.resolve();
  }

  onChangeUser(username: string, password: string): void {
    this._unAuthorizedUser = { username, password };
  }

  private toFormData(unAuthorizedUser?: LoginUser): FormData {
    if (!unAuthorizedUser) {
      return new FormData();
    }
    const formData = new FormData();
    formData.append('username', unAuthorizedUser.username);
    formData.append('password', unAuthorizedUser.password);
    return formData;
  }
}

export default JwtAuthenticator;
