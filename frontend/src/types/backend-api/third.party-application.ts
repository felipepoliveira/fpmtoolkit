export interface ThirdPartyApplicationModel {
  /**
   * Third party application id
   */
  id?: number;

  /**
   * A unique Client ID
   */
  clientId: string;

  /**
   * Application name
   */
  name: string;

  /**
   * Store all allowed redirect URIs for third party app
   */
  allowedRedirectUris: string[];

  /**
   * Store all allowed scopes for the client
   */
  grantedScopes: string[];
}
