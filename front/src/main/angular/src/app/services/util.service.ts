import {HttpParams} from "@angular/common/http";
import {environment} from "../../environments/environment";

export class UtilService {

  public static getParams(rowLimit: number | null, daysFetch: number | null, sort: string | null): HttpParams {
    let queryParams = new HttpParams();
    if (daysFetch != null) {
      queryParams = queryParams.append(
        `${environment.endpointParamForPeriodInDays}`, daysFetch);
    }
    if (rowLimit != null) {
      queryParams = queryParams.append(
        `${environment.endpointParamForLimitOfRows}`, rowLimit);
    }
    if (sort != null) {
      queryParams = queryParams.append(
        `${environment.endpointParamForSort}`, sort);
    }
    return queryParams;
  }

  public static isStorageContainsValueByKey(keyName: string): boolean {
    let tmp = sessionStorage.getItem(keyName);
    return tmp != null;
  }

  public static getKey(endpoint: string, rowLimit: number, daysFetch: number, sort: string): string {
    return endpoint + "?" +
      this.getParams(rowLimit, daysFetch, sort).toString();
  }

}
