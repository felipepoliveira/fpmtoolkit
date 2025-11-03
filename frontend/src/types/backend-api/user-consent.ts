import { ThirdPartyApplicationModel } from "./third.party-application";
import { UserModel } from "./user";

export interface UserConsentModel {
  /**
   * Unique ID for user consent
   */
  id?: number;

  /**
   * The user that made the consent
   */
  user: UserModel;

  /**
   * The client that received the consent
   */
  client: ThirdPartyApplicationModel;

  /**
   * When the user has consented
   */
  consentedAt: string; // ISO-8601 datetime string (e.g., "2025-11-03T10:15:30Z")

  /**
   * Store all allowed redirect URI for third party app
   */
  grants: string[];
}
