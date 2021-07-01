package com.maheshwari.stores.grocerystore.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.maheshwari.stores.grocerystore.R;
import com.maheshwari.stores.grocerystore.activity.BaseActivity;
import com.maheshwari.stores.grocerystore.activity.ProductActivity;
import com.maheshwari.stores.grocerystore.activity.ProductViewActivity;
import com.maheshwari.stores.grocerystore.interfaces.AddorRemoveCallbacks;
import com.maheshwari.stores.grocerystore.model.Cart;
import com.maheshwari.stores.grocerystore.model.Product;
import com.maheshwari.stores.grocerystore.util.Utils;
import com.maheshwari.stores.grocerystore.util.localstorage.LocalStorage;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyViewHolder> {

    List<Product> productList;
    Context context;
    String Tag;
    int pQuantity = 1;
    LocalStorage localStorage;
    Gson gson;
    List<Cart> cartList = new ArrayList<>();
    String _quantity, _price, _attribute, _subtotal;

    public ProductAdapter(List<Product> productList, Context context) {
        this.productList = productList;
        this.context = context;
    }

    public ProductAdapter(List<Product> productList, Context context, String tag) {
        this.productList = productList;
        this.context = context;
        Tag = tag;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView;
        if (Tag.equalsIgnoreCase("List")) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_products, parent, false);
        } else {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_grid_products, parent, false);
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
        int disPercent=0;
        if (product.getPrice() != null && product.getPrice().length() != 0 && product.getDiscount() != null && product.getDiscount().length() != 0  ) {

            double M = Double.parseDouble(product.getPrice());
            double S = Double.parseDouble(product.getDiscount());
            double discount = M - S;

            disPercent = (int) Math.round((discount / M) * 100);

            if (disPercent > 1) {
                holder.offer.setText(disPercent + "% OFF");
            } else {
                holder.offer.setVisibility(View.GONE);
            }

        } else {
            holder.offer.setVisibility(View.GONE);
        }


        holder.attribute.setText(product.getAttribute());
        holder.currency.setText(product.getCurrency());
        holder.price.setText(Utils.getStrikeThroughTextForProductAdapters(product.getPrice(),product.getDiscount(),disPercent));

        Picasso.get()
                .load(Utils.ProductImage + product.getImage())
                .into(holder.imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        holder.progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.d("Error : ", e.getMessage());
                    }
                });


        if (product.getDiscount() == null || product.getDiscount().length() == 0) {
            holder.offer.setVisibility(View.GONE);
        }

        if (!cartList.isEmpty()) {
            boolean found = false;
            for (int i = 0; i < cartList.size(); i++) {
                if (cartList.get(i).getId().equalsIgnoreCase(product.getProduct_id())) {
                    holder.addToCart.setVisibility(View.GONE);
                    holder.subTotal.setVisibility(View.VISIBLE);
                    holder.plus.setVisibility(View.VISIBLE);
                    holder.minus.setVisibility(View.VISIBLE);
                    holder.quantity.setVisibility(View.VISIBLE);
                    holder.quantity.setText(cartList.get(i).getQuantity());
                    _quantity = cartList.get(i).getQuantity();
                    _price = product.getPrice();
                    _subtotal = String.valueOf(Double.parseDouble(product.getDiscount()) * Integer.parseInt(_quantity));
                    holder.subTotal.setText(product.getCurrency() + " " + _subtotal);
                    Log.d("Tag : ", cartList.get(i).getId() + "-->" + product.getProduct_id());
                    found = true;
                    break;
                }
            }
            if(!found){
                holder.addToCart.setVisibility(View.VISIBLE);
                holder.subTotal.setVisibility(View.GONE);
                holder.plus.setVisibility(View.GONE);
                holder.minus.setVisibility(View.GONE);
                holder.quantity.setVisibility(View.GONE);
            }
        } else {
            holder.addToCart.setVisibility(View.VISIBLE);
            holder.subTotal.setVisibility(View.GONE);
            holder.plus.setVisibility(View.GONE);
            holder.minus.setVisibility(View.GONE);
            holder.quantity.setVisibility(View.GONE);
            holder.quantity.setText("1");
        }

        holder.plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pQuantity = Integer.parseInt(holder.quantity.getText().toString());
                if (pQuantity >= 1) {
                    int total_item = Integer.parseInt(holder.quantity.getText().toString());
                    total_item++;
                    holder.quantity.setText(total_item + "");
                    for (int i = 0; i < cartList.size(); i++) {

                        if (cartList.get(i).getId().equalsIgnoreCase(product.getProduct_id())) {

                            // Log.d("totalItem", total_item + "");

                            _subtotal = String.valueOf(Double.parseDouble(product.getDiscount()) * total_item);
                            cartList.get(i).setQuantity(holder.quantity.getText().toString());
                            cartList.get(i).setSubTotal(_subtotal);
                            holder.subTotal.setText(product.getCurrency()+" " + _subtotal);
                            String cartStr = gson.toJson(cartList);
                            //Log.d("CART", cartStr);
                            localStorage.setCart(cartStr);
                            notifyItemChanged(position);
                        }
                    }
                }

            }
        });
        holder.minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pQuantity = Integer.parseInt(holder.quantity.getText().toString());
                if (pQuantity != 1) {
                    int total_item = Integer.parseInt(holder.quantity.getText().toString());
                    total_item--;
                    holder.quantity.setText(total_item + "");
                    for (int i = 0; i < cartList.size(); i++) {
                        if (cartList.get(i).getId().equalsIgnoreCase(product.getProduct_id())) {

                            //holder.quantity.setText(total_item + "");
                            //Log.d("totalItem", total_item + "");
                            _subtotal = String.valueOf(Double.parseDouble(product.getDiscount()) * total_item);
                            cartList.get(i).setQuantity(holder.quantity.getText().toString());
                            cartList.get(i).setSubTotal(_subtotal);
                            holder.subTotal.setText(product.getCurrency()+" " + _subtotal);
                            String cartStr = gson.toJson(cartList);
                            //Log.d("CART", cartStr);
                            localStorage.setCart(cartStr);
                            notifyItemChanged(position);
                        }
                    }

                }

            }
        });


        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent(context, ProductActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);*/
                Toast.makeText(context, "Product Clicked", Toast.LENGTH_LONG).show();
            }
        });

        holder.addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.plus.setVisibility(View.VISIBLE);
                holder.minus.setVisibility(View.VISIBLE);
                holder.quantity.setVisibility(View.VISIBLE);
                holder.addToCart.setVisibility(View.GONE);
                holder.subTotal.setVisibility(View.VISIBLE);


                _price = product.getPrice();
                _quantity = holder.quantity.getText().toString();
                _attribute = product.getAttribute();

                if (Integer.parseInt(_quantity) != 0) {
                    _subtotal = String.valueOf(Double.parseDouble(product.getDiscount()) * Integer.parseInt(_quantity));
                    holder.subTotal.setText(product.getCurrency()+" " + _subtotal);
                    if (context instanceof ProductActivity) {
                        Cart cart = new Cart(product.getProduct_id(), product.getName(), product.getImage(), product.getCurrency(), _price, product.getDiscount(),_attribute, _quantity, _subtotal);
                        cartList = ((BaseActivity) context).getCartList();
                        cartList.add(cart);
                        String cartStr = gson.toJson(cartList);
                        //Log.d("CART", cartStr);
                        localStorage.setCart(cartStr);
                        ((AddorRemoveCallbacks) context).onAddProduct();
                        notifyItemChanged(position);
                    }
                } else {
                    Toast.makeText(context, "Please Add Quantity", Toast.LENGTH_SHORT).show();
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
                intent.putExtra("category",((ProductActivity)context).getIntent().getStringExtra("category"));

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
        TextView title;
        ProgressBar progressBar;
        CardView cardView;
        TextView offer, currency, price, quantity, attribute, addToCart, subTotal;
        Button plus, minus;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.product_image);
            title = itemView.findViewById(R.id.product_title);
            progressBar = itemView.findViewById(R.id.progressbar);
            cardView = itemView.findViewById(R.id.card_view);
            offer = itemView.findViewById(R.id.product_discount);
            currency = itemView.findViewById(R.id.product_currency);
            price = itemView.findViewById(R.id.product_price);
            quantity = itemView.findViewById(R.id.quantity);
            addToCart = itemView.findViewById(R.id.add_to_cart);
            attribute = itemView.findViewById(R.id.product_attribute);
            plus = itemView.findViewById(R.id.quantity_plus);
            minus = itemView.findViewById(R.id.quantity_minus);
            subTotal = itemView.findViewById(R.id.sub_total);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
