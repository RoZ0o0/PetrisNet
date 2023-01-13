import axios, { AxiosResponse, AxiosResponseHeaders } from 'axios';

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
      localStorage.setItem('role', responseRequest.data.role);
      localStorage.setItem('email', responseRequest.data.email);
      localStorage.setItem('firstName', responseRequest.data.firstName);
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
}

export interface ILogin {
    firstName: string;
    lastName: string;
    email: string;
    password: string;
    role: string;
}
