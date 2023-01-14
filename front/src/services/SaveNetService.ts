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
    return (await axios.post<ISaveNet>('http://localhost:8081/api/saved_nets', saveNet)).data;
  }

  public static async fetchSavedNets(userId: number): Promise<Array<ISaveNet>> {
    return (await axios.get<Array<ISaveNet>>('http://localhost:8081/api/saved_nets/user?userId=' + userId)).data;
  }
}

export interface ISaveNet {
    id: number;
    userId: number;
    saveName: string;
    netExport: string;
}
