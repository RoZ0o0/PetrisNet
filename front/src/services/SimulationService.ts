import axios, { AxiosResponse } from 'axios';

export default class SimulationServices {
  public static getBlankSimulationTemplate(): ISimulation {
    const tempElement: IElements = {
      name: '',
      x: 0,
      y: 0,
      x2: 0,
      y2: 0
    };

    const tempConnection: IConnections = {
      name: '',
      ft: '',
      st: ''
    };

    const tempToken: ITokens = {
      name: '',
      object_name: '',
      label_name: '',
      circle: '',
      token_amount: 0
    };

    const tempSimulation: ISimulation = {
      elements: [tempElement],
      connections: [tempConnection],
      tokens: [tempToken],
      changes: ['']
    };

    return tempSimulation;
  }

  public static async simulation(simulation: ISimulation): Promise<ISimulation> {
    return (await axios.post<ISimulation>('http://localhost:8081/api/simulation', simulation)).data;
  }
}

export interface ISimulation {
    elements: Array<IElements>;
    connections: Array<IConnections>;
    tokens: Array<ITokens>;
    changes: Array<string>;
}

export interface IElements {
    name: string;
    x: number;
    y: number;
    x2: number;
    y2: number;
}

export interface IConnections {
    name: string;
    ft: string;
    st: string;
}

export interface ITokens {
    circle: string;
    label_name: string;
    name: string;
    object_name: string;
    token_amount: number;
}
