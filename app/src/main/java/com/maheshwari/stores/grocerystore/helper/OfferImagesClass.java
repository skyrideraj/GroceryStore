package com.maheshwari.stores.grocerystore.helper;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.maheshwari.stores.grocerystore.model.Offer;
import com.maheshwari.stores.grocerystore.util.GroceryApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class OfferImagesClass {

    List<Offer> offerList = new ArrayList<>();


    public List<Offer> getOfferList() {
        //TODO Implement dynamic offers/images to be fetched from server
        JSONArray offersJsonArray = null;
        String offer_pages = FirebaseRemoteConfig.getInstance().getString("offer_pages");
        if(offer_pages!=null) {
            try {
                offersJsonArray = new JSONArray(offer_pages);
                for (int i=0;i<offersJsonArray.length();i++){
                    offerList.add(new Offer(offersJsonArray.getString(i)));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return offerList;
    }
}
