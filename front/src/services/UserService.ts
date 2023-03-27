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

  public static async fetchPaginated(page: number, size: number): Promise<Array<IUser>> {
    const token = localStorage.getItem('token');
    return (await axios.get<Array<IUser>>('http://localhost:8081/api/users/fetchAll?page=' + page + '&size=' + size, {
      headers: {
        Authorization: `${token}`
      }
    })).data;
  }

  public static async searchPaginated(page: number, size: number, search: string): Promise<Array<IUser>> {
    const token = localStorage.getItem('token');
    return (await axios.get<Array<IUser>>('http://localhost:8081/api/users/search?page=' + page + '&size=' + size + '&search=' + search, {
      headers: {
        Authorization: `${token}`
      }
    })).data;
  }

  public static async findById(id: number): Promise<IUser> {
    const token = localStorage.getItem('token');
    return (await axios.get<IUser>('http://localhost:8081/api/users/' + id, {
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

  public static async verify(code: string) {
    return (await axios.post<boolean>('http://localhost:8081/api/users/verify?code=' + code)).data;
  }

  public static async resetMail(email: string) {
    await (await axios.post<void>('http://localhost:8081/api/users/resetMail?email=' + email)).data;
  }

  public static async resetPassword(code: string, password: string) {
    return (await axios.post<boolean>('http://localhost:8081/api/users/reset?code=' + code + '&password=' + password)).data;
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
