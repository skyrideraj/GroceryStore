package com.maheshwari.stores.grocerystore.fragment;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.maheshwari.stores.grocerystore.R;
import com.maheshwari.stores.grocerystore.activity.BaseActivity;
import com.maheshwari.stores.grocerystore.activity.CartActivity;
import com.maheshwari.stores.grocerystore.activity.MainActivity;
import com.maheshwari.stores.grocerystore.adapter.CheckoutCartAdapter;
import com.maheshwari.stores.grocerystore.api.clients.RestClient;
import com.maheshwari.stores.grocerystore.model.Cart;
import com.maheshwari.stores.grocerystore.model.Order;
import com.maheshwari.stores.grocerystore.model.OrderItem;
import com.maheshwari.stores.grocerystore.model.OrdersResult;
import com.maheshwari.stores.grocerystore.model.PlaceOrder;
import com.maheshwari.stores.grocerystore.model.User;
import com.maheshwari.stores.grocerystore.util.localstorage.LocalStorage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class ConfirmFragment extends Fragment {
    LocalStorage localStorage;
    List<Cart> cartList = new ArrayList<>();
    Gson gson;
    RecyclerView recyclerView;
    CheckoutCartAdapter adapter;
    RecyclerView.LayoutManager recyclerViewlayoutManager;
    TextView back, order;
    TextView total, shipping, totalAmount;
    Double _total, _shipping, _totalAmount;
    ProgressDialog progressDialog;
    List<Order> orderList = new ArrayList<>();
    List<OrderItem> orderItemList = new ArrayList<>();
    PlaceOrder confirmOrder;
    String orderNo;
    String id;
    OrderItem orderItem = new OrderItem();


    public ConfirmFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_confirm, container, false);
        localStorage = new LocalStorage(getContext());
        recyclerView = view.findViewById(R.id.cart_rv);
        totalAmount = view.findViewById(R.id.total_amount);
        total = view.findViewById(R.id.total);
        shipping = view.findViewById(R.id.shipping_amount);
        back = view.findViewById(R.id.back);
        order = view.findViewById(R.id.place_order);
        progressDialog = new ProgressDialog(getContext());
        gson = new Gson();
        orderList = ((BaseActivity) getActivity()).getOrderList();
        cartList = ((BaseActivity) getContext()).getCartList();
        User user = gson.fromJson(localStorage.getUserLogin(), User.class);


        for (int i = 0; i < cartList.size(); i++) {

            orderItem = new OrderItem(cartList.get(i).getId(), cartList.get(i).getQuantity(), cartList.get(i).getPrice(), cartList.get(i).getDiscounted_price(), cartList.get(i).getSubTotal());
            orderItemList.add(orderItem);
        }
        Double totalCartPrice_total = ((BaseActivity) getActivity()).getTotalCartPrice();
        _total = ((BaseActivity) getActivity()).getFinalCartPrice();
        confirmOrder = new PlaceOrder(user.getToken(), user.getFname(), user.getEmail(), user.getMobile(), user.getCity(), user.getAddress(), user.getUser_id(), orderItemList, String.valueOf(totalCartPrice_total),String.valueOf(_total),new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));




        _shipping = 0.0;
        _totalAmount = _total + _shipping;
        total.setText(cartList.get(0).getCurrency()+" "+_total + "");
        shipping.setText(cartList.get(0).getCurrency()+" "+_shipping + "");
        totalAmount.setText(cartList.get(0).getCurrency()+" "+_totalAmount);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), CartActivity.class));

                getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                getActivity().finish();
            }
        });

        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placeUserOrder();
            }
        });

        setUpCartRecyclerview();
        return view;
    }

    private void placeUserOrder() {
        progressDialog.setMessage("Confirming Order...");
        progressDialog.show();
        Log.d("Confirm Order==>", gson.toJson(confirmOrder));
        Call<OrdersResult> call = RestClient.getRestService(getContext()).confirmPlaceOrder(confirmOrder);
        call.enqueue(new Callback<OrdersResult>() {
            @Override
            public void onResponse(Call<OrdersResult> call, Response<OrdersResult> response) {
                Log.d("response==>", response.body().getCode() + "");

                OrdersResult ordersResult = response.body();

                if (ordersResult.getCode() == 200) {
                    localStorage.deleteCart();

                    showCustomDialog();
                }

                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<OrdersResult> call, Throwable t) {
                Log.e("Error response==>", t.getMessage() + "");
                progressDialog.dismiss();
            }
        });


    }


    private void showCustomDialog() {

        // Create custom dialog object
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
        // Include dialog.xml file
        dialog.setContentView(R.layout.success_dialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                Intent mainActivityIntent =new Intent(getContext(), MainActivity.class);
                mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mainActivityIntent);
                getActivity().finish();
            }
        });
        // Set dialog title

        dialog.show();
    }

    private void setUpCartRecyclerview() {
        recyclerView.setHasFixedSize(true);
        recyclerViewlayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(recyclerViewlayoutManager);
        adapter = new CheckoutCartAdapter(cartList, getContext());
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Confirm");
    }


}
