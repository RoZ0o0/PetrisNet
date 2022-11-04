import axios, { AxiosResponse, AxiosResponseHeaders } from 'axios';
import { IUser } from './UserService';

export default class LoginServices {
  public static getBlankLoginTemplate(): ILogin {
    const tempLogin: ILogin = {
      firstName: '',
      lastName: '',
      email: '',
      password: '',
      role: ''
    };
    return tempLogin;
  }

  public static async login(login: ILogin): Promise<AxiosResponse> {
    let responseRequest;
    if ((responseRequest = await axios.post<ILogin>('http://localhost:8081/api/users/login', login)) !== undefined) {
      localStorage.setItem('mail', login.email);
      return responseRequest;
    }
    return responseRequest;
  }

  //   public static async login(login: ILogin): Promise<AxiosResponse> {
  //     let responseRequest;
  //     if ((responseRequest = await axios.post<ILogin>('http://localhost:8081/api/users' + 'auth/login', login)) != undefined) {
  //       localStorage.setItem('token', responseRequest.headers.authorization);
  //       return responseRequest;
  //     }
  //     return responseRequest;
  //   }

  public static async fetch(): Promise<IUser> {
    const token = localStorage.getItem('mail');
    return (await axios.get<IUser>('http://localhost:8081/api/users/email?email=' + token)).data;
  }
}

export interface ILogin {
    firstName: string;
    lastName: string;
    email: string;
    password: string;
    role: string;
}
