import axios, { AxiosResponse } from 'axios';

export default class SaveNetServices {
  public static getBlankSaveNetTemplate(): ISaveNet {
    const tempSaveNet: ISaveNet = {
      id: 0,
      userId: 0,
      saveName: '',
      netExport: '',
      public: false
    };
    return tempSaveNet;
  }

  public static async find(id: number): Promise<ISaveNet> {
    const token = localStorage.getItem('token');
    return (await axios.get<ISaveNet>('http://localhost:8081/api/saved_nets/' + id, {
      headers: {
        Authorization: `${token}`
      }
    })).data;
  }

  public static async fetchAll(): Promise<Array<ISaveNet>> {
    const token = localStorage.getItem('token');
    return (await axios.get<Array<ISaveNet>>('http://localhost:8081/api/saved_nets', {
      headers: {
        Authorization: `${token}`
      }
    })).data;
  }

  public static async create(saveNet: ISaveNet): Promise<ISaveNet> {
    const token = localStorage.getItem('token');
    return (await axios.post<ISaveNet>('http://localhost:8081/api/saved_nets', saveNet, {
      headers: {
        Authorization: `${token}`
      }
    })).data;
  }

  public static async update(saveNet: ISaveNet, id: number): Promise<ISaveNet> {
    const token = localStorage.getItem('token');
    return (await axios.put<ISaveNet>('http://localhost:8081/api/saved_nets?id=' + id, saveNet, {
      headers: {
        Authorization: `${token}`
      }
    })).data;
  }

  public static async delete(saveNetId: number): Promise<ISaveNet> {
    const token = localStorage.getItem('token');
    return (await axios.delete('http://localhost:8081/api/saved_nets/' + saveNetId, {
      headers: {
        Authorization: `${token}`
      }
    })).data;
  }

  public static async setPublic(saveNet: ISaveNet, id: number): Promise<ISaveNet> {
    const token = localStorage.getItem('token');
    return (await axios.put<ISaveNet>('http://localhost:8081/api/saved_nets/public?id=' + id, saveNet, {
      headers: {
        Authorization: `${token}`
      }
    })).data;
  }

  public static async findBySaveNameAndId(saveName: string): Promise<number> {
    const token = localStorage.getItem('token');
    return (await axios.get<number>('http://localhost:8081/api/saved_nets/find?saveName=' + saveName, {
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

  public static async fetchPublicNets(): Promise<Array<ISaveNet>> {
    return (await axios.get<Array<ISaveNet>>('http://localhost:8081/api/saved_nets/public')).data;
  }
}

export interface ISaveNet {
    id: number;
    userId: number;
    saveName: string;
    netExport: string;
    public: boolean;
}
