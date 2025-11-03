import { Button } from "antd";
import Title from "antd/es/typography/Title";
import { useContext, useEffect, useState } from "react";
import ThirdPartyAppService from "../../../../../api/backend-api/third-party-app";
import { ThirdPartyApplicationModel } from "../../../../../types/backend-api/third.party-application";
import OAuthService from "../../../../../api/backend-api/oauth";
import UserSessionStore from "../../../../../store/user-session";
import { AuthenticatedAppContext } from "../../../../AuthenticatedApp";
import SessionCredentialsStore from "../../../../../store/session-credentials";
import { UserConsentModel } from "../../../../../types/backend-api/user-consent";

type ComponentState = 'fetching' | 'ready' | 'error';

export default function OAuth2OidcConsentPage() {

    const authUserToken = SessionCredentialsStore.get()
    const searchParams = new URLSearchParams(document.location.search);
    const clientId = searchParams.get('client_id');
    const [selectedThirdPartyApp, setSelectedThirdPartyApp] = useState<ThirdPartyApplicationModel | undefined>(undefined);
    const [userConsent, setUserConsent] = useState<UserConsentModel | undefined>(undefined);
    const [state, setState] = useState<ComponentState>('fetching')


    // fetch 'selectedThirdPartyApp' state
    useEffect(() => {
        // if the clientId is not given the page will be on error state
        if (!clientId) {
            setState('error');
            return;
        }

        ThirdPartyAppService.findByClientId(clientId)
            .then(app => { setSelectedThirdPartyApp(app); setState('ready'); })
            .catch(e => {
                console.error(e);
                setState('error');
            })

    }, [])

    useEffect(() => {
        // if there is not selected third party app ignore it
        if (!selectedThirdPartyApp) {
            return
        }

        ThirdPartyAppService.findConsent(selectedThirdPartyApp.clientId)
            .then(consent => setUserConsent(consent))

    }, [selectedThirdPartyApp])

    useEffect(() => {
        if (!userConsent) {
            return;
        }
        navigateToAuthorizationEndpoint();
    }, [userConsent])

    if (state === 'error') {
        return <div>Error loading third party application data.</div>
    }

    if (selectedThirdPartyApp === undefined) {
        return <div>Loading...</div>
    }

    async function consent() {

        // ignore if no third party app was fetched
        if (!selectedThirdPartyApp || !authUserToken) {
            return
        }


        try {
            await ThirdPartyAppService.registerConsent(selectedThirdPartyApp.clientId)
            navigateToAuthorizationEndpoint();
        } catch (e) {
            console.error(e);
        }

    }

    function navigateToAuthorizationEndpoint() {
        if (!selectedThirdPartyApp || !authUserToken) {
            return;
        }
        OAuthService.navigateToAuthorizationEndpoint({
            clientId: selectedThirdPartyApp.clientId,
            codeChallenge: searchParams.get('code_challenge') || '',
            codeChallengeMethod: searchParams.get('code_challenge_method') || '',
            responseType: searchParams.get('response_type') || '',
            scope: searchParams.get('scope') || '',
            redirectUri: searchParams.get('redirect_uri') || undefined,
            state: searchParams.get('state') || undefined,
        }, authUserToken)
    }

    return (
        <>
            <Title level={3}>{selectedThirdPartyApp.name} want to access you data</Title>
            <Button onClick={consent}>Eu aceito em nome da lei</Button>
        </>
    )
}