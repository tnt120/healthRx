import { DrugPack } from "./drug-pack.model";

export interface DrugPacksResponse {
  drugId: number;
  drugPacks: DrugPack[];
}
