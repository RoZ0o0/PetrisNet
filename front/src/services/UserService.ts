import axios from 'axios';
import { ILogin } from './LoginService';

export default class UserServices {
  public static getBlankUserTemplate(): IUser {
    const tempUser: IUser = {
      id: 0,
      firstName: '',
      lastName: '',
      email: '',
      password: ''
    };
    return tempUser;
  }

  public static async fetch(): Promise<Array<IUser>> {
    return (await axios.get<Array<IUser>>('http://localhost:8081/api/users')).data;
  }

  public static async findByEmail(email: string): Promise<IUser> {
    return (await axios.get<IUser>('http://localhost:8081/api/users/email?email=' + email)).data;
  }
}

export interface IUser {
    id: number;
    firstName: string;
    lastName: string;
    email: string;
    password: string;
}
