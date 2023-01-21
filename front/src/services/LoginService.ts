import axios, { AxiosResponse, AxiosResponseHeaders } from 'axios';

export default class LoginServices {
  public static getBlankLoginTemplate(): ILogin {
    const tempLogin: ILogin = {
      id: 0,
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
    if ((responseRequest = await axios.post<ILogin>('http://localhost:8081/api/auth/login', login)) !== undefined) {
      localStorage.setItem('token', responseRequest.headers.authorization);
      return responseRequest;
    }
    return responseRequest;
  }

  public static async fetch(): Promise<ILogin> {
    const token = localStorage.getItem('token');
    return (await axios.get<ILogin>('http://localhost:8081/api/auth/user', {
      headers: {
        Authorization: `${token}`
      }
    })).data;
  }
}

export interface ILogin {
    id: number;
    firstName: string;
    lastName: string;
    email: string;
    password: string;
    role: string;
}
