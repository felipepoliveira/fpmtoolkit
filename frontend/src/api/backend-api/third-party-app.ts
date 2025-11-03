import { ThirdPartyApplicationModel } from "../../types/backend-api/third.party-application";
import { UserConsentModel } from "../../types/backend-api/user-consent";
import BackendApi from "./backend-api";

const ThirdPartyAppService = {
    /**
     * Return an ThirdPartyApplication by its client ID
     * @param clientId 
     * @returns 
     */
    findByClientId: async (clientId: string): Promise<ThirdPartyApplicationModel> => {
        return (await BackendApi.get(`/api/third-party-apps/public/${clientId}`)).data;
    },

    /**
     * Find the user consent for the authenticated user and the given third-party app
     * @param appId 
     * @returns 
     */
    findConsent: async (appId: string): Promise<UserConsentModel> => {
        return await BackendApi.get(`/api/third-party-apps/${appId}/consent`);
    },
    /**
     * Register the authenticated user consent to a third-party app
     * @param appId 
     * @returns 
     */
    registerConsent: async (appId: string): Promise<UserConsentModel> => {
        return await BackendApi.post(`/api/third-party-apps/${appId}/consent`);
    }
}

export default ThirdPartyAppService;