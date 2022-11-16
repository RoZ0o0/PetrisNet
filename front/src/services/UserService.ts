import axios, { AxiosResponse } from 'axios';

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
}

export interface IUser {
    id: number;
    firstName: string;
    lastName: string;
    email: string;
    password: string;
}
