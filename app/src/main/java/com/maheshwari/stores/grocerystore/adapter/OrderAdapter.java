package com.maheshwari.stores.grocerystore.adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.maheshwari.stores.grocerystore.R;
import com.maheshwari.stores.grocerystore.customfonts.MyTextViewOleoScriptBold;
import com.maheshwari.stores.grocerystore.customfonts.MyTextViewSansBold;
import com.maheshwari.stores.grocerystore.model.Order;
import com.maheshwari.stores.grocerystore.model.OrderDetails;
import com.maheshwari.stores.grocerystore.util.Utils;
import com.maheshwari.stores.grocerystore.util.localstorage.LocalStorage;

import java.util.List;


public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.MyViewHolder> {

    List<Order> orderList;
    Context context;
    int pQuantity = 1;
    String _subtotal, _price, _quantity;
    LocalStorage localStorage;
    Gson gson;

    public OrderAdapter(List<Order> orderList, Context context) {
        this.orderList = orderList;
        this.context = context;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView;

        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_order, parent, false);


        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        final Order order = orderList.get(position);
        holder.orderId.setText("OrderID : #" + order.getInvoice_no());
        holder.date.setText("Date : " +order.getDate_ordered());
        holder.total.setText("Amount : ₹"+order.getFinal_cart_price());
        if(order.getOrder_status().equals(Utils.ORDER_STATUS_PENDING))
            holder.colorCardViewLL.setBackgroundColor(context.getResources().getColor(R.color.yellow));
        else if(order.getOrder_status().equals(Utils.ORDER_STATUS_CONFIRMED))
            holder.colorCardViewLL.setBackgroundColor(context.getResources().getColor(R.color.blue_200));
        else if(order.getOrder_status().equals(Utils.ORDER_STATUS_PAID))
            holder.colorCardViewLL.setBackgroundColor(context.getResources().getColor(R.color.green_200));
        else if(order.getOrder_status().equals(Utils.ORDER_STATUS_DELIVERED))
            holder.colorCardViewLL.setBackgroundColor(context.getResources().getColor(R.color.green_500));
        else if(order.getOrder_status().equals(Utils.ORDER_STATUS_CANCELLED))
            holder.colorCardViewLL.setBackgroundColor(context.getResources().getColor(R.color.red_500));
        holder.status.setText("Status : "+order.getOrder_status());

        holder.details_count.setText(order.getOrder_details().size() + " item(s)");

        holder.orderCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOrderDetailsDialog(context,order);
            }
        });


    }

    @Override
    public int getItemCount() {

        return orderList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView orderId, date, total, status,details_count;
        CardView orderCardView;
        LinearLayout colorCardViewLL;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            orderId = itemView.findViewById(R.id.order_id);
            date = itemView.findViewById(R.id.date);
            total = itemView.findViewById(R.id.total_amount);
            status = itemView.findViewById(R.id.status);
            details_count = itemView.findViewById(R.id.details_count);
            orderCardView = itemView.findViewById(R.id.order_card_view);
            colorCardViewLL = itemView.findViewById(R.id.colorCardView);

        }
    }

    private void showOrderDetailsDialog(Context context, Order order) {
        List<OrderDetails> orderDetailsList = order.getOrder_details();
        TableLayout.LayoutParams tableParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);
        TableRow.LayoutParams productNameParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT,7f);
        TableRow.LayoutParams productQtyParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT,1.5f);
        TableRow.LayoutParams productPriceParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT,1.5f);

        final Dialog dialog = new Dialog(context, android.R.style.Theme_Light_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.order_details_dialog);

        MyTextViewSansBold order_number = dialog.findViewById(R.id.order_number);
        order_number.setText(order.getInvoice_no());
        MyTextViewSansBold order_date = dialog.findViewById(R.id.order_date);
        order_date.setText(order.getDate_ordered());
        MyTextViewSansBold order_amount = dialog.findViewById(R.id.order_amount);
        order_amount.setText("₹"+order.getFinal_cart_price());

        LinearLayout parent = (LinearLayout)dialog.findViewById(R.id.parent_order_details_ll);
        TableLayout tableLayout = new TableLayout(context);
        tableLayout.setLayoutParams(parent.getLayoutParams());

        TableRow tableRow =  new TableRow(context);
        tableRow.setLayoutParams(tableParams);

        MyTextViewSansBold productNameTvHeader = new MyTextViewSansBold(context);
        productNameTvHeader.setLayoutParams(productNameParams);
        productNameTvHeader.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        productNameTvHeader.setPadding(8,2,8,2);
        productNameTvHeader.setTextSize(20);
        productNameTvHeader.setTypeface(Typeface.DEFAULT_BOLD);
        productNameTvHeader.setText("Product");

        MyTextViewSansBold productPriceTvHeader = new MyTextViewSansBold(context);
        productPriceTvHeader.setLayoutParams(productPriceParams);
        productPriceTvHeader.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        productPriceTvHeader.setPadding(2,2,2,2);
        productPriceTvHeader.setTextSize(20);
        productPriceTvHeader.setTypeface(Typeface.DEFAULT_BOLD);
        productPriceTvHeader.setText("Price");

        MyTextViewSansBold productQtyTvHeader = new MyTextViewSansBold(context);
        productQtyTvHeader.setLayoutParams(productQtyParams);
        productQtyTvHeader.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        productQtyTvHeader.setPadding(8,2,8,2);
        productQtyTvHeader.setTextSize(20);
        productQtyTvHeader.setTypeface(Typeface.DEFAULT_BOLD);
        productQtyTvHeader.setText("Qty");

        tableRow.addView(productNameTvHeader);
        tableRow.addView(productPriceTvHeader);
        tableRow.addView(productQtyTvHeader);

        tableLayout.setLayoutParams(tableParams);
        tableLayout.addView(tableRow);


        for (OrderDetails orderDetail: orderDetailsList) {
            TableRow llProductDetails = new TableRow(context);
            tableRow.setLayoutParams(tableParams);

            MyTextViewSansBold productNameTv = new MyTextViewSansBold(context);
            productNameTv.setLayoutParams(productNameParams);
            productNameTv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            productNameTv.setPadding(8,2,8,2);
            productNameTv.setTextSize(18);
            productNameTv.setText(orderDetail.getProduct());

            MyTextViewSansBold productPriceTv = new MyTextViewSansBold(context);
            productPriceTv.setLayoutParams(productPriceParams);
            productPriceTv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            productPriceTv.setPadding(2,2,2,2);
            productPriceTv.setTextSize(18);
            productPriceTv.setText("₹"+orderDetail.getDiscounted_price());

            MyTextViewSansBold productQtyTv = new MyTextViewSansBold(context);
            productQtyTv.setLayoutParams(productQtyParams);
            productQtyTv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            productQtyTv.setPadding(8,2,8,2);
            productQtyTv.setTextSize(18);
            productQtyTv.setText(orderDetail.getQuantity());

            llProductDetails.addView(productNameTv);
            llProductDetails.addView(productPriceTv);
            llProductDetails.addView(productQtyTv);
            tableLayout.addView(llProductDetails);

        }

        parent.addView(tableLayout);

        //exit button
        Button dialogButton = (Button) dialog.findViewById(R.id.ok_button);

        dialogButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }
}
