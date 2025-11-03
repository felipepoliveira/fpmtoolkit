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

const OAuthService = {
    navigateToAuthorizationEndpoint: (params: AuthorizationRequestParams, clientCredentials: string) => {
        window.location.href = BackendApi.defaults.baseURL + '/oauth/authorize?' +
            `client_id=${encodeURIComponent(params.clientId)}` +
            `&code_challenge=${encodeURIComponent(params.codeChallenge)}` +
            `&code_challenge_method=${encodeURIComponent(params.codeChallengeMethod)}` +
            `&response_type=${encodeURIComponent(params.responseType)}` +
            `&scope=${encodeURIComponent(params.scope)}` +
            `&clientToken=${encodeURIComponent(clientCredentials)}` +
            (params.prompt ? `&prompt=${encodeURIComponent(params.prompt)}` : '') +
            (params.redirectUri ? `&redirect_uri=${encodeURIComponent(params.redirectUri)}` : '') +
            (params.state ? `&state=${encodeURIComponent(params.state)}` : '')
    }
}

export default OAuthService;