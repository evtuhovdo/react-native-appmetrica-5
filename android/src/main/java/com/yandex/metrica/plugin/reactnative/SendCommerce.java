package com.yandex.metrica.plugin.reactnative; // replace com.your-app-name with your app’s name

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

public class SendEccomerce extends ReactContextBaseJavaModule {
  SendEccomerce(ReactApplicationContext context) {
    super(context);
  }

  @Override
  public String getName() {
    return "SendEccomerce";
  }

  @ReactMethod(isBlockingSynchronousMethod = true)
  public void changeCart(String eventName, int price, String id, String productName, String modificator) {
    // eventName = 'addToCart' | 'removeFromCart'
    // params = [price, id]
    if (System.getenv().containsKey("APPMETRICA_KEY")) {
      ECommerceScreen screen = new ECommerceScreen()
              .setName("ProductCardActivity");
      ECommercePrice originalPrice = new ECommercePrice(new ECommerceAmount(price, "RUB"));
      // модификаторы надо в actualPrice
      ECommercePrice actualPrice = new ECommercePrice(new ECommerceAmount(price, "RUB"));
      ECommerceProduct product = new ECommerceProduct(id)
              .setActualPrice(actualPrice)
              .setOriginalPrice(originalPrice)
              .setName(productName)
              .setCategoriesPath(Arrays.asList(modificator));
      ECommerceCartItem addedItem = new ECommerceCartItem(product, actualPrice, 1.0);
      ECommerceEvent changeCartEvent;
      if (eventName.equals("addToCart")) {
        changeCartEvent = ECommerceEvent.addCartItemEvent(addedItem);
      } else {
        changeCartEvent = ECommerceEvent.removeCartItemEvent(addedItem);
      }
      YandexMetrica.reportECommerce(changeCartEvent);
    }
  }
}
