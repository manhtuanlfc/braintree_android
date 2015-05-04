package com.braintreepayments.api;

import android.content.Intent;

import com.braintreepayments.api.models.ClientToken;
import com.braintreepayments.api.models.Configuration;
import com.google.android.gms.wallet.Cart;
import com.google.android.gms.wallet.FullWalletRequest;
import com.google.android.gms.wallet.MaskedWalletRequest;
import com.google.android.gms.wallet.PaymentMethodTokenizationParameters;
import com.google.android.gms.wallet.PaymentMethodTokenizationType;
import com.google.android.gms.wallet.WalletConstants;

public class AndroidPay {

    private ClientToken mClientToken;
    private Configuration mConfiguration;
    private Cart mCart;

    protected AndroidPay(ClientToken clientToken, Configuration configuration, Cart cart) {
        mClientToken = clientToken;
        mConfiguration = configuration;
        mCart = cart;
    }

    public static boolean isAndroidPayIntent(Intent intent) {
        return (intent.hasExtra(WalletConstants.EXTRA_MASKED_WALLET) ||
                intent.hasExtra(WalletConstants.EXTRA_FULL_WALLET));
    }

    protected PaymentMethodTokenizationParameters getTokenizationParameters() {
        return PaymentMethodTokenizationParameters.newBuilder()
                .setPaymentMethodTokenizationType(PaymentMethodTokenizationType.PAYMENT_GATEWAY)
                .addParameter("gateway", "braintree")
                .addParameter("braintree:merchantId", mConfiguration.getMerchantId())
                .addParameter("braintree:authorizationFingerprint",
                        mClientToken.getAuthorizationFingerprint())
                .addParameter("braintree:apiVersion", "v1")
                .addParameter("braintree:sdkVersion", BuildConfig.VERSION_NAME)
                .build();
    }

    protected MaskedWalletRequest getMaskedWalletRequest() {
        return MaskedWalletRequest.newBuilder()
                .setMerchantName(getMerchantName())
                .setCurrencyCode("USD")
                .setEstimatedTotalPrice(mCart.getTotalPrice())
                .setPaymentMethodTokenizationParameters(getTokenizationParameters())
                .build();
    }

    protected FullWalletRequest getFullWalletRequest(String googleTransactionId) {
        return FullWalletRequest.newBuilder()
                .setCart(mCart)
                .setGoogleTransactionId(googleTransactionId)
                .build();
    }

    private String getMerchantName() {
        if (mConfiguration.getAndroidPay().getDisplayName() != null) {
            return mConfiguration.getAndroidPay().getDisplayName();
        } else {
            return "";
        }
    }
}