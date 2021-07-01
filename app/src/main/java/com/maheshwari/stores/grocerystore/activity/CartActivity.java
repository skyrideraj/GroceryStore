package com.maheshwari.stores.grocerystore.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.maheshwari.stores.grocerystore.R;
import com.maheshwari.stores.grocerystore.adapter.CartAdapter;
import com.maheshwari.stores.grocerystore.fragment.PopularProductFragment;
import com.maheshwari.stores.grocerystore.model.Cart;
import com.maheshwari.stores.grocerystore.util.localstorage.LocalStorage;

import java.util.ArrayList;
import java.util.List;


public class CartActivity extends BaseActivity {
    LocalStorage localStorage;
    List<Cart> cartList = new ArrayList<>();
    Gson gson;
    RecyclerView recyclerView;
    CartAdapter adapter;
    RecyclerView.LayoutManager recyclerViewlayoutManager;
    ImageView emptyCart;
    LinearLayout checkoutLL;
    TextView totalPrice,savingsText;
    private String mState = "SHOW_MENU";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFF")));
        changeActionBarTitle(getSupportActionBar());
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp);
        //upArrow.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        localStorage = new LocalStorage(getApplicationContext());
        gson = new Gson();
        emptyCart = findViewById(R.id.empty_cart_img);
        checkoutLL = findViewById(R.id.checkout_LL);
        totalPrice = findViewById(R.id.total_price);
        savingsText = findViewById(R.id.savings_text);
        totalPrice.setText("₹ " + getFinalCartPrice() + "");
        savingsText.setText("Saved ₹ " + String.format("%.1f",(getTotalCartPrice() - getFinalCartPrice())) + "");
        setUpCartRecyclerview();


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.cart_menu, menu);
        MenuItem item = menu.findItem(R.id.cart_delete);
        if (mState.equalsIgnoreCase("HIDE_MENU")) {
            item.setVisible(false);
        } else {
            item.setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.cart_delete:

                AlertDialog diaBox = showDeleteDialog();
                diaBox.show();

                return true;

            case android.R.id.home:
                // todo: goto back activity from here

                Intent intent = new Intent(CartActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private AlertDialog showDeleteDialog() {
        AlertDialog myQuittingDialogBox = new AlertDialog.Builder(this)

                //set message, title, and icon
                .setTitle("Clear")
                .setMessage("Do you want to clear shopping cart?")
                .setIcon(R.drawable.ic_baseline_remove_shopping_cart_24)

                .setPositiveButton("Clear", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        localStorage.deleteCart();
                        adapter.notifyDataSetChanged();
                        emptyCart.setVisibility(View.VISIBLE);
                        mState = "HIDE_MENU";
                        invalidateOptionsMenu();
                        dialog.dismiss();
                    }

                })

                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                    }
                })
                .create();
        return myQuittingDialogBox;
    }

    @Override
    public void onBackPressed() {
        Intent originalIntent = getIntent();
        if(originalIntent.hasExtra("category")){
            Intent intent = null;
            if(originalIntent.getStringExtra("activity_tag").equals(ProductActivity.class.getSimpleName())) {
                intent = new Intent(CartActivity.this, ProductActivity.class);
                intent.putExtra("category",originalIntent.getStringExtra("category"));
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            }
            else if(originalIntent.getStringExtra("activity_tag").equals(ProductViewActivity.class.getSimpleName())) {
                intent = new Intent(CartActivity.this, ProductViewActivity.class);
                intent.putExtra("category",originalIntent.getStringExtra("category"));
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            }
            else if(originalIntent.getStringExtra("activity_tag").equals(PopularProductFragment.class.getSimpleName())) {
                intent = new Intent(CartActivity.this, MainActivity.class);
                intent.putExtra("category",PopularProductFragment.class.getSimpleName());
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            }

            startActivity(intent);
            overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
        }
        else{
            Intent intent = new Intent(CartActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
        }
        finish();
    }

    private void changeActionBarTitle(ActionBar actionBar) {
        // Create a LayoutParams for TextView
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, // Width of TextView
                RelativeLayout.LayoutParams.WRAP_CONTENT); // Height of TextView
        TextView tv = new TextView(getApplicationContext());
        // Apply the layout parameters to TextView widget
        tv.setLayoutParams(lp);
        tv.setGravity(Gravity.CENTER);
        tv.setTypeface(null, Typeface.BOLD);
        // Set text to display in TextView
        tv.setText("Cart"); // ActionBar title text
        tv.setTextSize(20);

        // Set the text color of TextView to red
        // This line change the ActionBar title text color
        tv.setTextColor(getResources().getColor(R.color.colorPrimaryDark));

        // Set the ActionBar display option
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        // Finally, set the newly created TextView as ActionBar custom view
        actionBar.setCustomView(tv);
    }


    private void setUpCartRecyclerview() {
        cartList = new ArrayList<>();
        cartList = getCartList();
        if (cartList.isEmpty()) {
            mState = "HIDE_MENU";
            invalidateOptionsMenu();
            emptyCart.setVisibility(View.VISIBLE);
            checkoutLL.setVisibility(View.GONE);
        }
        recyclerView = findViewById(R.id.cart_rv);
        recyclerView.setHasFixedSize(true);
        recyclerViewlayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(recyclerViewlayoutManager);
        adapter = new CartAdapter(cartList, CartActivity.this);
        recyclerView.setAdapter(adapter);
    }


    public void onCheckoutClicked(View view) {
        startActivity(new Intent(getApplicationContext(), CheckoutActivity.class).putExtras(getIntent().getExtras()));
        finish();
    }


    @Override
    public void updateTotalPrice() {

        totalPrice.setText("Rs. " + getFinalCartPrice() + "");
        if (getFinalCartPrice() == 0.0) {
            mState = "HIDE_MENU";
            invalidateOptionsMenu();
            emptyCart.setVisibility(View.VISIBLE);
            checkoutLL.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpCartRecyclerview();

        invalidateOptionsMenu();
    }
}
