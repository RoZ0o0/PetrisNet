import axios, { AxiosResponse } from 'axios';

export default class UserServices {
  public static getBlankUserTemplate(): IUser {
    const tempUser: IUser = {
      id: 0,
      firstName: '',
      lastName: '',
      email: '',
      password: '',
      role: ''
    };
    return tempUser;
  }

  public static async fetch(): Promise<Array<IUser>> {
    const token = localStorage.getItem('token');
    return (await axios.get<Array<IUser>>('http://localhost:8081/api/users', {
      headers: {
        Authorization: `${token}`
      }
    })).data;
  }

  public static async create(user: IUser): Promise<IUser> {
    return (await axios.post<IUser>('http://localhost:8081/api/users/register', user)).data;
  }
}

export interface IUser {
    id: number;
    firstName: string;
    lastName: string;
    email: string;
    password: string;
    role: string;
}
