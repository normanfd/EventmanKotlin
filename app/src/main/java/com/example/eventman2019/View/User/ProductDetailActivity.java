package com.example.eventman2019.View.User;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.eventman2019.Model.Cart;
import com.example.eventman2019.Model.Konveksi;
import com.example.eventman2019.Model.Product;
import com.example.eventman2019.Prevalent.Prevalent;
import com.example.eventman2019.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ProductDetailActivity extends AppCompatActivity {
    private Button addToCartBtn;
    private ImageView product_Image;
    private ElegantNumberButton numberBtn;
    private TextView productPriceDetail, prouctDescriptionDetail, productNameDetail;
    private String productID="",category="",state = "Normal", stringketerangan="";
    private TextView keterangan;
    private EditText keteranganvalue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        productID =getIntent().getStringExtra("pid");
        category = getIntent().getStringExtra("category");

        numberBtn = (ElegantNumberButton)findViewById(R.id.number_btn);
        product_Image = (ImageView) findViewById(R.id.product_image_detail);
        productPriceDetail= (TextView) findViewById(R.id.product_price_detail);
        productNameDetail = (TextView) findViewById(R.id.product_name_detail);
        prouctDescriptionDetail = (TextView) findViewById(R.id.product_description_detail);
        addToCartBtn = (Button)findViewById(R.id.pd_add_to_cart_btn);
        keterangan = (TextView) findViewById(R.id.keterangan);
        keteranganvalue = (EditText) findViewById(R.id.keterangan_value);
        if (category.equals("konveksi")){
            keterangan.setVisibility(View.VISIBLE);
            keteranganvalue.setVisibility(View.VISIBLE);
        }

        getProductDetail(productID,category);

        addToCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(state.equals("Order placed") || state.equals("Order shipped")){
                    Toast.makeText(ProductDetailActivity.this, "you can add purchase products, once your order is shipped or confirmed", Toast.LENGTH_LONG ).show();
                }
                else {
                    addingToCartList();
                }
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        checkOrderState();
    }

    private void addingToCartList() {
        String saveCurrentTime, saveCurrentDate;
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calForDate.getTime());
        if (category.equals("konveksi")){
            stringketerangan = keteranganvalue.getText().toString();
        }
        final DatabaseReference cartListRef= FirebaseDatabase.getInstance().getReference().child("cart list");
        final Cart cartMap = new Cart(productID, productNameDetail.getText().toString(),
                productPriceDetail.getText().toString(), numberBtn.getNumber(),
                saveCurrentDate, saveCurrentTime,category, stringketerangan);

        cartListRef.child("User View").child(Prevalent.CurrentOnlineUser.getPhone())
                .child("Products").child(productID)
                .setValue(cartMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            cartListRef.child("Admin View").child(Prevalent.CurrentOnlineUser.getPhone())
                                    .child("Products").child(productID)
                                    .setValue(cartMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(ProductDetailActivity.this, "Added to cart list", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(ProductDetailActivity.this, HomeActivity.class);
                                                startActivity(intent);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void getProductDetail(String productID, String category) {
        DatabaseReference productsref = FirebaseDatabase.getInstance().getReference().child("Products").child(category);
        productsref.child(productID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    Product product = dataSnapshot.getValue(Product.class);
                    productNameDetail.setText(product.getProductname());
                    productPriceDetail.setText(product.getPrice());
                    prouctDescriptionDetail.setText(product.getDescription());
                    Picasso.get().load(product.getImage()).into(product_Image);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void checkOrderState(){
        DatabaseReference ordersRef;
        ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders").child(Prevalent.CurrentOnlineUser.getPhone());
        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String shippingState = dataSnapshot.child("state").getValue().toString();
                    if (shippingState.equals("shipped")){
                        state = "Order shipped";
                    }else if(shippingState.equals("not shipped")){
                        state = "Order placed";
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
