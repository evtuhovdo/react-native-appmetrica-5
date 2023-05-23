/*
 * Version for React Native
 * © 2020 YANDEX
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * https://yandex.com/legal/appmetrica_sdk_agreement/
 */

package com.yandex.metrica.plugin.reactnative;

import android.app.Activity;
import android.util.Log;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReadableMap;

import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.yandex.metrica.YandexMetrica;
import com.yandex.metrica.ecommerce.ECommerceAmount;
import com.yandex.metrica.ecommerce.ECommerceCartItem;
import com.yandex.metrica.ecommerce.ECommerceEvent;
import com.yandex.metrica.ecommerce.ECommercePrice;
import com.yandex.metrica.ecommerce.ECommerceOrder;
import com.yandex.metrica.ecommerce.ECommerceProduct;
import com.yandex.metrica.ecommerce.ECommerceScreen;

import java.sql.Array;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

public class AppMetricaModule extends ReactContextBaseJavaModule {

        private static final String TAG = "AppMetricaModule";

        private final ReactApplicationContext reactContext;

        public AppMetricaModule(ReactApplicationContext reactContext) {
            super(reactContext);
            this.reactContext = reactContext;
        }

        @Override
        public String getName() {
            return "AppMetrica";
        }

        @ReactMethod
        public void activate(ReadableMap configMap) {
            YandexMetrica.activate(reactContext, Utils.toYandexMetricaConfig(configMap));
            enableActivityAutoTracking();
        }

        private void enableActivityAutoTracking() {
            Activity activity = getCurrentActivity();
            if (activity != null) { // TODO: check
                YandexMetrica.enableActivityAutoTracking(activity.getApplication());
            } else {
                Log.w(TAG, "Activity is not attached");
            }
        }

        @ReactMethod
        public void getLibraryApiLevel(Promise promise) {
            promise.resolve(YandexMetrica.getLibraryApiLevel());
        }

        @ReactMethod
        public void getLibraryVersion(Promise promise) {
            promise.resolve(YandexMetrica.getLibraryVersion());
        }

        @ReactMethod
        public void pauseSession() {
            YandexMetrica.pauseSession(getCurrentActivity());
        }

        @ReactMethod
        public void reportAppOpen(String deeplink) {
            YandexMetrica.reportAppOpen(deeplink);
        }

        @ReactMethod
        public void reportError(String message) {
            try {
                Integer.valueOf("00xffWr0ng");
            } catch (Throwable error) {
                YandexMetrica.reportError(message, error);
            }
        }

        @ReactMethod
        public void reportEvent(String eventName, ReadableMap attributes) {
            if (attributes == null) {
                YandexMetrica.reportEvent(eventName);
            } else {
                YandexMetrica.reportEvent(eventName, attributes.toHashMap());
            }
        }

        @ReactMethod
        public void reportReferralUrl(String referralUrl) {
            YandexMetrica.reportReferralUrl(referralUrl);
        }

        @ReactMethod
        public void requestAppMetricaDeviceID(Callback listener) {
            YandexMetrica.requestAppMetricaDeviceID(new ReactNativeAppMetricaDeviceIDListener(listener));
        }

        @ReactMethod
        public void resumeSession() {
            YandexMetrica.resumeSession(getCurrentActivity());
        }

        @ReactMethod
        public void sendEventsBuffer() {
            YandexMetrica.sendEventsBuffer();
        }

        @ReactMethod
        public void setLocation(ReadableMap locationMap) {
            YandexMetrica.setLocation(Utils.toLocation(locationMap));
        }

        @ReactMethod
        public void setLocationTracking(boolean enabled) {
            YandexMetrica.setLocationTracking(enabled);
        }

        @ReactMethod
        public void setStatisticsSending(boolean enabled) {
            YandexMetrica.setStatisticsSending(reactContext, enabled);
        }

        @ReactMethod
        public void setUserProfileID(String userProfileID) {
            YandexMetrica.setUserProfileID(userProfileID);
        }

        @ReactMethod(isBlockingSynchronousMethod = true)
        public void changeCart(
            boolean isCookery,
            String eventName,
            int price,
            String id,
            String productName,
            int amount,
            String productCategories
        ) {
            // eventName = 'addToCart' | 'removeFromCart'
            ECommerceScreen screen = new ECommerceScreen()
                .setCategoriesPath(Arrays.asList(
                    isCookery ? "Меню кулинарии" : "Меню предзаказа"
                ))
                .setName("ProductCardActivity");
            ECommercePrice originalPrice = new ECommercePrice(new ECommerceAmount(price, "RUB"));
            // модификаторы надо в actualPrice
            ECommercePrice actualPrice = new ECommercePrice(new ECommerceAmount(price, "RUB"));
            ECommerceProduct product = new ECommerceProduct(id)
                .setActualPrice(actualPrice)
                .setOriginalPrice(originalPrice)
                .setName(productName)
                .setCategoriesPath(Arrays.asList(productCategories));
            ECommerceCartItem addedItem = new ECommerceCartItem(product, actualPrice, amount);
            ECommerceEvent changeCartEvent;
            if (eventName.equals("addToCart")) {
                changeCartEvent = ECommerceEvent.addCartItemEvent(addedItem);
            } else {
                changeCartEvent = ECommerceEvent.removeCartItemEvent(addedItem);
            }
            YandexMetrica.reportECommerce(changeCartEvent);
        }

        @ReactMethod(isBlockingSynchronousMethod = true)
        public void reportProductViewEvent(
            boolean isCookery,
            int price,
            String id,
            String productName,
            String productCategories
        ) {
            ECommerceScreen screen = new ECommerceScreen()
                .setCategoriesPath(Arrays.asList(
                    isCookery ? "Меню кулинарии" : "Меню предзаказа"
                ))
                .setName("ProductCardActivity");

            ECommercePrice originalPrice = new ECommercePrice(new ECommerceAmount(price, "RUB"));
            ECommercePrice actualPrice = new ECommercePrice(new ECommerceAmount(price, "RUB"));
            ECommerceProduct product = new ECommerceProduct(id)
                  .setActualPrice(actualPrice)
                  .setOriginalPrice(originalPrice)
                  .setName(productName)
                  .setCategoriesPath(Arrays.asList(productCategories));
            ECommerceCartItem addedItem = new ECommerceCartItem(product, actualPrice, 1.0);
            ECommerceEvent showProductDetailsEvent = ECommerceEvent.showProductDetailsEvent(product, null);
            YandexMetrica.reportECommerce(showProductDetailsEvent);
        }

        @ReactMethod(isBlockingSynchronousMethod = true)
        public void reportPurchaseEvent(
            boolean isCookery,
            String orderId,
            String locationId,
            String locationName,
            ReadableArray orderItems
        ) {
            Map<String, String> payload = new HashMap<>();
            payload.put("locationId", locationId);
            payload.put("locationName", locationName);

            ECommerceScreen screen = new ECommerceScreen()
                .setCategoriesPath(Arrays.asList(
                    isCookery ? "Меню кулинарии" : "Меню предзаказа"
                ))
                .setName("ProductCardActivity");

            List<ECommerceCartItem> addedItems = new ArrayList();

            if (orderItems.size() > 0) {
                for (int i = 0; i < orderItems.size(); i++) {
                    ReadableMap currentItem = orderItems.getMap(i);
                    String id = currentItem.getString("id");
                    int _actualPrice = currentItem.getInt("actualPrice");
                    int _originalPrice = currentItem.getInt("originalPrice");
                    int amount = currentItem.getInt("amount");
                    String productName = currentItem.getString("productName");
                    String productCategories = currentItem.getString("productCategories");

                    ECommercePrice originalPrice = new ECommercePrice(new ECommerceAmount(_originalPrice, "RUB"));
                    ECommercePrice actualPrice = new ECommercePrice(new ECommerceAmount(_actualPrice, "RUB"));
                    ECommerceProduct product = new ECommerceProduct(id)
                          .setActualPrice(actualPrice)
                          .setOriginalPrice(originalPrice)
                          .setName(productName)
                          .setCategoriesPath(Arrays.asList(productCategories));
                    ECommerceCartItem addedItem = new ECommerceCartItem(product, actualPrice, amount);

                    addedItems.add(addedItem);
                }
            }

            ECommerceOrder order = new ECommerceOrder(orderId, addedItems)
                .setPayload(payload);
             ECommerceEvent purchaseEvent = ECommerceEvent.purchaseEvent(order);
             YandexMetrica.reportECommerce(purchaseEvent);
        }
}
