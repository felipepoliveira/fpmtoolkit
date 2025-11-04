import { toQueryParams } from "../../commons/object";
import BackendApi from "./backend-api";

export interface AuthorizationRequestParams {
  clientId: string;
  codeChallenge: string;
  codeChallengeMethod: string;
  responseType: string;
  scope: string;
  prompt?: string;
  redirectUri?: string;
  state?: string;
}

export interface AuthorizeResponse {
  /**
   * The redirect_uri of the OAuth flow
   */
  redirectUri: string;

  /**
   * Generated authorization code
   */
  code?: string;

  /**
   * The OAuth Client id
   */
  clientId?: string;

  /**
   * The code challenge
   */
  codeChallenge?: string;

  /**
   * Code challenge method
   */
  codeChallengeMethod?: string;

  /**
   * Scope provided by OAuth request
   */
  scope?: string;

  /**
   * State provided by OAuth request
   */
  state?: string;

  /**
   * Response type
   */
  responseType?: string;
}


const OAuthService = {
  navigateToRedirectUriOfAuthorizeRequest: async (params: AuthorizationRequestParams) => {
    const query = `client_id=${encodeURIComponent(params.clientId)}` +
      `&code_challenge=${encodeURIComponent(params.codeChallenge)}` +
      `&code_challenge_method=${encodeURIComponent(params.codeChallengeMethod)}` +
      `&response_type=${encodeURIComponent(params.responseType)}` +
      `&scope=${encodeURIComponent(params.scope)}` +
      `&redirect=false` +
      (params.prompt ? `&prompt=${encodeURIComponent(params.prompt)}` : '') +
      (params.redirectUri ? `&redirect_uri=${encodeURIComponent(params.redirectUri)}` : '') +
      (params.state ? `&state=${encodeURIComponent(params.state)}` : '')

      
    const response = (await (BackendApi.get(`/oauth/authorize?${query}`))).data as AuthorizeResponse;
    window.location.href = response.redirectUri
  }
}

export default OAuthService;