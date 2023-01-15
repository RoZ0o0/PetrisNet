import axios, { AxiosResponse } from 'axios';
import { IUser } from './UserService';

export default class SaveNetServices {
  public static getBlankSaveNetTemplate(): ISaveNet {
    const tempUser: ISaveNet = {
      id: 0,
      userId: 0,
      saveName: '',
      netExport: ''
    };
    return tempUser;
  }

  public static async create(saveNet: ISaveNet): Promise<ISaveNet> {
    const token = localStorage.getItem('token');
    return (await axios.post<ISaveNet>('http://localhost:8081/api/saved_nets', saveNet, {
      headers: {
        Authorization: `${token}`
      }
    })).data;
  }

  public static async fetchSavedNets(email: string): Promise<Array<ISaveNet>> {
    const token = localStorage.getItem('token');
    return (await axios.get<Array<ISaveNet>>('http://localhost:8081/api/saved_nets/user?email=' + email, {
      headers: {
        Authorization: `${token}`
      }
    })).data;
  }
}

export interface ISaveNet {
    id: number;
    userId: number;
    saveName: string;
    netExport: string;
}
