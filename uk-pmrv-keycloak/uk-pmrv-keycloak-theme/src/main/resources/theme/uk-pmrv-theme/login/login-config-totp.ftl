<#import "template.ftl" as layout>
<@layout.registrationLayout displayRequiredFields=true displayMessage=!messagesPerField.existsError('totp') htmlPageTitle="${msg('loginTotpTitle')}"; section>

    <#if section = "header">
        ${msg("loginTotpTitle")}
    <#elseif section = "form">

        <p class="govuk-body">
            To access this service, you must download an authenticator app. This authenticator 
            will be used to generate a 6-digit verification code whenever you sign in to the UK 
            Emissions Trading Scheme reporting service
        </p>

        <br />
        
        <details class="govuk-details" data-module="govuk-details">
            <summary class="govuk-details__summary">
                <span class="govuk-details__summary-text">What is two-factor authentication?</span>
            </summary>
                <div class="govuk-details__text">
                    <p class="govuk-body">
                        Two-factor authentication, also known as 2FA, adds an additional level of security 
                        to online accounts. Authenticator apps generate a unique verification code that 
                        must be entered when you sign in to your account.
                    </p>
                </div>
        </details>

        <h3 class="govuk-body govuk-!-font-weight-bold">Download an authenticator</h3>
        <p class="govuk-body">You can use the following to sign in to the service:</p>

        <h3 class="govuk-body govuk-!-font-weight-bold">Microsoft Authenticator</h3>
        <p class="govuk-body">
            Microsoft Authenticator is available on Android smartphones and iPhones.
            You can download it by searching for ‘Microsoft Authenticator' in the Google Play 
            Store or the Apple App Store.
        </p>

        <h3 class="govuk-body govuk-!-font-weight-bold">Google Authenticator</h3>
        <p class="govuk-body">
            Google Authenticator is available on Android smartphones and iPhones.
            You can download it by searching for 'Google Authenticator' in the Google Play 
            Store or the Apple App Store.
        </p>

        <h3 class="govuk-body govuk-!-font-weight-bold">FreeOTP Authenticator</h3>
        <p class="govuk-body">
            FreeOTP Authenticator is available on Android smartphones and iPhones.
            You can download it by searching 'FreeOTP Authenticator' in the Google Play 
            Store or the Apple App Store.
        </p><br/>

        <p class="govuk-body">
            Open your authenticator app and scan the following QR code:
        </p>
        
        <img id="kc-totp-secret-qr-code" src="data:image/png;base64, ${totp.totpSecretQrCode}"
            alt="Scan this QR Code"><br/>
        
        <details class="govuk-details" data-module="govuk-details">
            <summary class="govuk-details__summary">
                <span class="govuk-details__summary-text">How do I scan the QR code?</span>
            </summary>
            <div class="govuk-details__text">
                <p class="govuk-body">
                    Use your device’s camera to scan the code with your authenticator app.
                </p>
                <p class="govuk-body">
                    <b>Microsoft Authenticator:</b> Select the + icon at the top of the screen, select the 
                    type of account you want to add, then select ‘Scan a QR code’.
                </p>
                <p class="govuk-body">
                    <b>Google Authenticator:</b> Select the + icon at the bottom of the screen, then select 
                    ‘Scan a QR code’.
                </p>
                <p class="govuk-body">
                    <b>FreeOTP Authenticator:</b> Select the QR code icon at the top of the screen.
                </p>
            </div>
        </details>

        <h3 class="govuk-body govuk-!-font-weight-bold">Enter the code shown in the authenticator</h3>

        <form action="${url.loginAction}" class="${properties.kcFormClass!}" id="kc-totp-settings-form" method="post">
            <#assign hasError = (message?has_content && message.summary?has_content && message.summary != msg("configureTotpMessage") && (message.type != 'warning' || !isAppInitiatedAction??))>
            <div class="${properties.kcFormGroupClass!} ${hasError?then('govuk-form-group--error', '')}">
                <#if hasError >
                    <span class="govuk-error-message" role="alert">
                    ${kcSanitize(message.summary)?no_esc}
                </span>
                </#if>
                <div id="totp-hint" class="govuk-hint">${msg("loginTotpAuthenticatorAppCode")}</div>
                <div class="${properties.kcInputWrapperClass!}">
                    <input type="text" id="otpCode" name="totp" autocomplete="off"
                           class="${properties.kcInputClass!} govuk-input--width-10" aria-describedby="totp-hint"/>
                </div>
                <input type="hidden" id="totpSecret" name="totpSecret" value="${totp.totpSecret}"/>

                <#if mode??><input type="hidden" id="mode" name="mode" value="${mode}"/></#if>
            </div>

            <#if isAppInitiatedAction??>
                <input type="submit"
                       class="${properties.kcButtonClass!} ${properties.kcButtonPrimaryClass!} ${properties.kcButtonLargeClass!}"
                       id="saveTOTPBtn" value="${msg("doSubmit")}"
                />
                <button type="submit"
                        class="${properties.kcButtonClass!} ${properties.kcButtonDefaultClass!} ${properties.kcButtonLargeClass!} ${properties.kcButtonLargeClass!}"
                        id="cancelTOTPBtn" name="cancel-aia" value="true" />${msg("doCancel")}
                </button>
            <#else>
                <input type="submit"
                       class="${properties.kcButtonClass!} ${properties.kcButtonPrimaryClass!} ${properties.kcButtonBlockClass!} ${properties.kcButtonLargeClass!}"
                       id="saveTOTPBtn" value="${msg("doContinue")}"
                />
            </#if>
        </form>
    </#if>
</@layout.registrationLayout>