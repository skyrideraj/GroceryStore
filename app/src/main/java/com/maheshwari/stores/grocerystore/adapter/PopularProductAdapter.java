package com.maheshwari.stores.grocerystore.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.maheshwari.stores.grocerystore.R;
import com.maheshwari.stores.grocerystore.activity.BaseActivity;
import com.maheshwari.stores.grocerystore.activity.MainActivity;
import com.maheshwari.stores.grocerystore.activity.ProductActivity;
import com.maheshwari.stores.grocerystore.activity.ProductViewActivity;
import com.maheshwari.stores.grocerystore.fragment.PopularProductFragment;
import com.maheshwari.stores.grocerystore.interfaces.AddorRemoveCallbacks;
import com.maheshwari.stores.grocerystore.model.Cart;
import com.maheshwari.stores.grocerystore.model.Product;
import com.maheshwari.stores.grocerystore.util.Utils;
import com.maheshwari.stores.grocerystore.util.localstorage.LocalStorage;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class PopularProductAdapter extends RecyclerView.Adapter<PopularProductAdapter.MyViewHolder> {

    List<Product> productList;
    Context context;
    String Tag;
    LocalStorage localStorage;
    Gson gson;
    List<Cart> cartList = new ArrayList<>();
    String _quantity, _price, _attribute, _subtotal;

    public PopularProductAdapter(List<Product> productList, Context context) {
        this.productList = productList;
        this.context = context;
    }

    public PopularProductAdapter(List<Product> productList, Context context, String tag) {
        this.productList = productList;
        this.context = context;
        Tag = tag;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView;

        if (Tag.equalsIgnoreCase("Home")) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_popular_home_products, parent, false);
        } else {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_popuular_products, parent, false);
        }

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        final Product product = productList.get(position);
        localStorage = new LocalStorage(context);
        gson = new Gson();
        cartList = ((BaseActivity) context).getCartList();

        holder.title.setText(product.getName());

        double M = Double.parseDouble(product.getPrice());
        double S = Double.parseDouble(product.getDiscount());
        double discount = M - S;
        int disPercent = (int) Math.round((discount / M) * 100);
        holder.price.setText(Utils.getStrikeThroughTextForProductAdapters(product.getPrice(),product.getDiscount(),disPercent));
        if(disPercent>1){
            holder.discount.setVisibility(View.VISIBLE);
            holder.discount.setText(disPercent+"% OFF");
        }
        else
            holder.discount.setVisibility(View.INVISIBLE);
        holder.attribute.setText(product.getAttribute());
        Picasso.get().load(Utils.ProductImage + product.getImage()).error(R.drawable.no_image).into(holder.imageView, new Callback() {
            @Override
            public void onSuccess() {
                holder.progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(Exception e) {
                Log.d("Error : ", e.getMessage());
            }
        });


        if (!cartList.isEmpty()) {
            for (int i = 0; i < cartList.size(); i++) {
                if (cartList.get(i).getId().equalsIgnoreCase(product.getProduct_id())) {
                    holder.shopNow.setVisibility(View.GONE);
                    holder.quantity_ll.setVisibility(View.VISIBLE);
                    holder.quantity.setText(cartList.get(i).getQuantity());
                    Log.d("Tag : ", cartList.get(i).getId() + "-->" + product.getProduct_id());
                }
            }
        }


        holder.shopNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.shopNow.setVisibility(View.GONE);
                holder.quantity_ll.setVisibility(View.VISIBLE);
                _price = product.getPrice();
                holder.currency.setText(product.getCurrency());
                _quantity = holder.quantity.getText().toString();
                _attribute = product.getAttribute();
                _subtotal = String.valueOf(Double.parseDouble(product.getDiscount()) * Integer.parseInt(_quantity));

                if (context instanceof MainActivity) {
                    Cart cart = new Cart(product.getProduct_id(), product.getName(), product.getImage(), product.getCurrency(), _price, product.getDiscount(),_attribute, _quantity, _subtotal);
                    cartList = ((BaseActivity) context).getCartList();
                    cartList.add(cart);

                    String cartStr = gson.toJson(cartList);
                    //Log.d("CART", cartStr);
                    localStorage.setCart(cartStr);
                    ((AddorRemoveCallbacks) context).onAddProduct();
                    notifyItemChanged(position);
                }
            }
        });


        holder.plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (int i = 0; i < cartList.size(); i++) {
                    if (cartList.get(i).getId().equalsIgnoreCase(product.getProduct_id())) {
                        int total_item = Integer.parseInt(cartList.get(i).getQuantity());
                        total_item++;
                        Log.d("totalItem", total_item + "");
                        holder.quantity.setText(total_item + "");
                        _subtotal = String.valueOf(Double.parseDouble(product.getDiscount()) * total_item);
                        cartList.get(i).setQuantity(holder.quantity.getText().toString());
                        cartList.get(i).setSubTotal(_subtotal);
                        String cartStr = gson.toJson(cartList);
                        //Log.d("CART", cartStr);
                        localStorage.setCart(cartStr);
                    }
                }


            }
        });

        holder.minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Integer.parseInt(holder.quantity.getText().toString()) != 1) {
                    for (int i = 0; i < cartList.size(); i++) {
                        if (cartList.get(i).getId().equalsIgnoreCase(product.getProduct_id())) {
                            int total_item = Integer.parseInt(holder.quantity.getText().toString());

                            total_item--;
                            holder.quantity.setText(total_item + "");
                            Log.d("totalItem", total_item + "");

                            _subtotal = String.valueOf(Double.parseDouble(product.getDiscount()) * total_item);
                            cartList.get(i).setQuantity(holder.quantity.getText().toString());
                            cartList.get(i).setSubTotal(_subtotal);
                            String cartStr = gson.toJson(cartList);
                            //Log.d("CART", cartStr);
                            localStorage.setCart(cartStr);
                        }
                    }

                }


            }
        });

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ProductViewActivity.class);
                intent.putExtra("id", product.getProduct_id());
                intent.putExtra("title", product.getName());
                intent.putExtra("image", product.getImage());
                intent.putExtra("price", product.getPrice());
                intent.putExtra("currency", product.getCurrency());
                intent.putExtra("attribute", product.getAttribute());
                intent.putExtra("discount", product.getDiscount());
                intent.putExtra("description", product.getDescription());

                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {

        return productList.size();

    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView title, attribute, currency, price, shopNow,discount;
        ProgressBar progressBar;
        LinearLayout quantity_ll;
        TextView plus, minus, quantity;
        CardView cardView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.product_image);
            title = itemView.findViewById(R.id.product_title);
            attribute = itemView.findViewById(R.id.product_attribute);
            price = itemView.findViewById(R.id.product_price);
            discount = itemView.findViewById(R.id.product_discount);
            currency = itemView.findViewById(R.id.product_currency);
            shopNow = itemView.findViewById(R.id.shop_now);
            progressBar = itemView.findViewById(R.id.progressbar);
            quantity_ll = itemView.findViewById(R.id.quantity_ll);
            quantity = itemView.findViewById(R.id.quantity);
            plus = itemView.findViewById(R.id.quantity_plus);
            minus = itemView.findViewById(R.id.quantity_minus);
            cardView = itemView.findViewById(R.id.card_view);

        }
    }
}
