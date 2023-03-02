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

  public static async fetchPaginated(page: number, size: number): Promise<Array<ISaveNet>> {
    const token = localStorage.getItem('token');
    return (await axios.get<Array<ISaveNet>>('http://localhost:8081/api/saved_nets/fetchAll?page=' + page + '&size=' + size, {
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

  public static async fetchSavedNetsPaginated(email: string, page: number, size: number): Promise<Array<ISaveNet>> {
    const token = localStorage.getItem('token');
    return (await axios.get<Array<ISaveNet>>('http://localhost:8081/api/saved_nets/user/fetchAll?email=' + email + '&page=' + page + '&size=' + size, {
      headers: {
        Authorization: `${token}`
      }
    })).data;
  }

  public static async fetchPublicNets(): Promise<Array<ISaveNet>> {
    return (await axios.get<Array<ISaveNet>>('http://localhost:8081/api/saved_nets/public')).data;
  }

  public static async fetchPublicNetsPaginated(page: number, size: number): Promise<Array<ISaveNet>> {
    return (await axios.get<Array<ISaveNet>>('http://localhost:8081/api/saved_nets/public/fetchAll?page=' + page + '&size=' + size)).data;
  }

  public static async searchPublicNetsPaginated(page: number, size: number, search: string): Promise<Array<ISaveNet>> {
    return (await axios.get<Array<ISaveNet>>('http://localhost:8081/api/saved_nets/public/search?page=' + page + '&size=' + size + '&search=' + search)).data;
  }

  public static async searchAllNetsPaginated(page: number, size: number, search: string): Promise<Array<ISaveNet>> {
    const token = localStorage.getItem('token');
    return (await axios.get<Array<ISaveNet>>('http://localhost:8081/api/saved_nets/public/searchAll?page=' + page + '&size=' + size + '&search=' + search, {
      headers: {
        Authorization: `${token}`
      }
    })).data;
  }

  public static async createRefLink(id: number): Promise<string> {
    const token = localStorage.getItem('token');
    return (await axios.get<string>('http://localhost:8081/api/saved_nets/ref?id=' + id, {
      headers: {
        Authorization: `${token}`
      }
    })).data;
  }

  public static async getByRef(ref: string): Promise<ISaveNet> {
    return (await axios.get<ISaveNet>('http://localhost:8081/api/saved_nets/ref/' + ref)).data;
  }
}

export interface ISaveNet {
    id: number;
    userId: number;
    saveName: string;
    netExport: string;
    public: boolean;
}
