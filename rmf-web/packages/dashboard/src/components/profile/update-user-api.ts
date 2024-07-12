import axios, { AxiosInstance } from 'axios';

export interface UpdatePasswordDto {
  currentPassword: string;
  newPassword: string;
  confirmPassword: string;
}

export default class UpdateUserApi {

  private readonly axios: AxiosInstance;

  constructor() {
    this.axios = axios.create({
      baseURL: process.env.REACT_APP_RMF_SERVER,
      withCredentials: true,
    });
  }

  updatePassword = async (updatePasswordDto: UpdatePasswordDto) => {
    const response = await this.axios.post('/user/password', updatePasswordDto);
    if (response.status === 204) return;
    throw new Error('Failed to update password');
  };
}
