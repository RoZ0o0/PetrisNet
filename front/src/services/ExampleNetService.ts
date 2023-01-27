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

  public static async fetchById(id: number): Promise<IExampleNet> {
    return (await axios.get<IExampleNet>('http://localhost:8081/api/example_nets/' + id)).data;
  }
}

export interface IExampleNet {
    id: number;
    netName: string;
    netExport: string;
}
