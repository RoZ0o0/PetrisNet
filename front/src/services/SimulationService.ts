import axios, { AxiosResponse } from 'axios';

export default class SimulationServices {
  public static getBlankSimulationTemplate(): ISimulation {
    const tempElement: IElements = {
      name: '',
      type: '',
      x: 0,
      y: 0,
      weight: 0,
      tokens: 0,
      id: '',
      r: 0,
      width: 0,
      height: 0
    };

    const tempVertices: IVertices = {
      x: 0,
      y: 0
    };

    const tempConnection: IConnections = {
      source: '',
      target: '',
      weight: 0,
      vertices: [tempVertices]
    };

    const tempSimulation: ISimulation = {
      elements: [tempElement],
      connections: [tempConnection],
      changes: ['']
    };

    return tempSimulation;
  }

  public static async simulation(simulation: ISimulation): Promise<ISimulation> {
    return (await axios.post<ISimulation>('http://localhost:8081/api/simulation', simulation)).data;
  }

  public static async checkNet(simulation: ISimulation): Promise<boolean> {
    return (await axios.post<boolean>('http://localhost:8081/api/simulation/check', simulation)).data;
  }
}

export interface ISimulation {
    elements: Array<IElements>;
    connections: Array<IConnections>;
    changes: Array<string>;
}

export interface IElements {
    name: string;
    type: string;
    x: number;
    y: number;
    weight: number;
    tokens: number;
    id: string;
    r: number;
    width: number;
    height: number;
}

export interface IConnections {
    source: string;
    target: string;
    weight: number;
    vertices: Array<IVertices>;
}

export interface IVertices {
  x: number;
  y: number;
}
