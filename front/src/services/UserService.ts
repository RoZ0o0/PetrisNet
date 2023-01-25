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

  public static async fetchByEmail(email: string): Promise<IUser> {
    const token = localStorage.getItem('token');
    return (await axios.get<IUser>('http://localhost:8081/api/users/email?email=' + email, {
      headers: {
        Authorization: `${token}`
      }
    })).data;
  }

  public static async create(user: IUser): Promise<IUser> {
    return (await axios.post<IUser>('http://localhost:8081/api/users/register', user)).data;
  }

  public static async update(userId: number, user: IUser): Promise<IUser> {
    const token = localStorage.getItem('token');
    return (await axios.put<IUser>('http://localhost:8081/api/users/' + userId, user, {
      headers: {
        Authorization: `${token}`
      }
    })).data;
  }

  public static async delete(userId: number): Promise<IUser> {
    const token = localStorage.getItem('token');
    return (await axios.delete('http://localhost:8081/api/users/' + userId, {
      headers: {
        Authorization: `${token}`
      }
    })).data;
  }

  public static async editProfile(userId: number, user: IUser) {
    const token = localStorage.getItem('token');
    return (await axios.put<IUser>('http://localhost:8081/api/users/profile?userId=' + userId, user, {
      headers: {
        Authorization: `${token}`
      }
    })).data;
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
