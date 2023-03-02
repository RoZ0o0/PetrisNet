import axios, { AxiosResponse } from 'axios';

export default class ExampleNetServices {
  public static getBlankExampleNetTemplate(): IExampleNet {
    const tempExampleNet: IExampleNet = {
      id: 0,
      netName: '',
      netExport: ''
    };
    return tempExampleNet;
  }

  public static async fetchAll(): Promise<Array<IExampleNet>> {
    return (await axios.get<Array<IExampleNet>>('http://localhost:8081/api/example_nets')).data;
  }

  public static async fetchPaginated(page: number, size: number): Promise<Array<IExampleNet>> {
    const token = localStorage.getItem('token');
    return (await axios.get<Array<IExampleNet>>('http://localhost:8081/api/example_nets/fetchAll?page=' + page + '&size=' + size, {
      headers: {
        Authorization: `${token}`
      }
    })).data;
  }

  public static async search(page: number, size: number, search: string): Promise<Array<IExampleNet>> {
    return (await axios.get<Array<IExampleNet>>('http://localhost:8081/api/example_nets/search?page=' + page + '&size=' + size + '&search=' + search)).data;
  }

  public static async fetchById(id: number): Promise<IExampleNet> {
    return (await axios.get<IExampleNet>('http://localhost:8081/api/example_nets/' + id)).data;
  }

  public static async findByNetName(netName: string): Promise<boolean> {
    const token = localStorage.getItem('token');
    return (await axios.get<boolean>('http://localhost:8081/api/example_nets/find?netName=' + netName, {
      headers: {
        Authorization: `${token}`
      }
    })).data;
  }

  public static async create(exampleNet: IExampleNet): Promise<IExampleNet> {
    const token = localStorage.getItem('token');
    return (await axios.post<IExampleNet>('http://localhost:8081/api/example_nets', exampleNet, {
      headers: {
        Authorization: `${token}`
      }
    })).data;
  }

  public static async update(exampleNet: IExampleNet, id: number): Promise<IExampleNet> {
    const token = localStorage.getItem('token');
    return (await axios.put<IExampleNet>('http://localhost:8081/api/example_nets?id=' + id, exampleNet, {
      headers: {
        Authorization: `${token}`
      }
    })).data;
  }

  public static async delete(exampleNetId: number): Promise<IExampleNet> {
    const token = localStorage.getItem('token');
    return (await axios.delete('http://localhost:8081/api/example_nets/' + exampleNetId, {
      headers: {
        Authorization: `${token}`
      }
    })).data;
  }
}

export interface IExampleNet {
    id: number;
    netName: string;
    netExport: string;
}
