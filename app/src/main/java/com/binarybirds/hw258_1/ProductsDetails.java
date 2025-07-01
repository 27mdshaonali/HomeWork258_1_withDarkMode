package com.binarybirds.hw258_1;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.AnimationTypes;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.interfaces.ItemClickListener;
import com.denzcoskun.imageslider.models.SlideModel;
import com.makeramen.roundedimageview.RoundedImageView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProductsDetails extends AppCompatActivity {

    RecyclerView reviewsRecyclerView;
    HashMap<String, String> hashMap;
    ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_details);

        HashMap<String, String> product = (HashMap<String, String>) getIntent().getSerializableExtra("product");

        ImageSlider imageSlider = findViewById(R.id.image_slider);
        TextView productTitle = findViewById(R.id.productTitle);
        TextView productDescription = findViewById(R.id.productDescription);
        TextView productOfferPrice = findViewById(R.id.productPrice);
        TextView productMainPrice = findViewById(R.id.productDiscount);
        TextView productRating = findViewById(R.id.productRating);
        TextView productStock = findViewById(R.id.productStock);
        TextView productBrand = findViewById(R.id.productBrand);
        TextView productCategory = findViewById(R.id.productCategory);

        Button addToCart = findViewById(R.id.addToCart);
        Button viewCart = findViewById(R.id.viewCart);
        CardView mainCard = findViewById(R.id.mainCard);
        LinearLayout linearLayout = findViewById(R.id.linearLayout);
        ConstraintLayout mainContainer = findViewById(R.id.mainContainer);

        reviewsRecyclerView = findViewById(R.id.reviewsRecyclerView);
        reviewsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        reviewsRecyclerView.setAdapter(new MyAdapter());

        if (product != null) {
            try {
                String title = product.get("title");
                String description = product.get("description");
                String price = product.get("price");
                String discount = product.get("discountPercentage");
                String rating = product.get("rating");
                String stock = product.get("stock");
                String brand = product.get("brand");
                String category = product.get("category");
                String[] images = product.get("images").split(",");

                productTitle.setText(title);
                productDescription.setText(description);

                double mainPriceDouble = Double.parseDouble(price);
                double disDouble = Double.parseDouble(discount);
                String disPercentage = String.format("%.2f", disDouble);
                String discountedPriceDouble = String.format("%.2f", mainPriceDouble - (mainPriceDouble * disDouble / 100));
                productOfferPrice.setText("Offer Price :  $" + discountedPriceDouble + " (After " + disPercentage + "% Off)");
                productMainPrice.setText("Price :  $" + price);
                productMainPrice.setPaintFlags(productMainPrice.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);

                productRating.setText("Rating :  " + rating + " / 5");

                int stockInt = Integer.parseInt(stock);
                productStock.setText("Stock :  " + stockInt);

                productBrand.setText("Brand : " + brand);
                productCategory.setText("Category :  " + category);

                List<SlideModel> slideModels = new ArrayList<>();

                if (images.length > 0) {
                    for (String imageUrl : images) {
                        String trimmedUrl = imageUrl.trim();
                        slideModels.add(new SlideModel(trimmedUrl, ScaleTypes.FIT));
                    }

                    imageSlider.setImageList(slideModels);
                    imageSlider.setSlideAnimation(AnimationTypes.TOSS);
                    imageSlider.startSliding(2000);

                    imageSlider.setItemClickListener(new ItemClickListener() {
                        @Override
                        public void doubleClick(int i) {
                        }

                        @Override
                        public void onItemSelected(int position) {
                            Toast.makeText(ProductsDetails.this, "Clicked image " + (position + 1), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                // Handle reviews safely
                String reviewsJson = product.get("reviews");
                Log.d("ProductsDetails", "Raw reviews JSON: " + reviewsJson);
                if (reviewsJson != null && !reviewsJson.isEmpty()) {
                    try {
                        JSONArray reviewsArray = new JSONArray(reviewsJson);
                        for (int i = 0; i < reviewsArray.length(); i++) {
                            JSONObject review = reviewsArray.getJSONObject(i);
                            HashMap<String, String> reviewMap = new HashMap<>();
                            reviewMap.put("reviewerName", review.optString("reviewerName"));
                            reviewMap.put("reviewerEmail", review.optString("reviewerEmail"));
                            reviewMap.put("comment", review.optString("comment"));
                            reviewMap.put("rating", String.valueOf(review.optDouble("rating")));
                            reviewMap.put("date", review.optString("date"));
                            arrayList.add(reviewMap);
                        }
                        reviewsRecyclerView.getAdapter().notifyDataSetChanged();
                    } catch (Exception e) {
                        Log.e("ProductsDetails", "Invalid JSON in reviews: " + e.getMessage());
                    }
                } else {
                    Log.w("ProductsDetails", "No reviews found for this product.");
                }

            } catch (Exception e) {
                Log.e("ProductsDetails", "Error setting product data: " + e.getMessage());
                e.printStackTrace();
                Toast.makeText(this, "Error loading product details", Toast.LENGTH_SHORT).show();
            }
        }

        try {
            addToCart.setOnClickListener(v -> {
                LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View productView = layoutInflater.inflate(R.layout.add_to_cart_product, linearLayout, false);

                linearLayout.removeAllViews();
                linearLayout.addView(productView);

                if (linearLayout.getVisibility() != View.VISIBLE) {
                    linearLayout.setVisibility(View.VISIBLE);
                    linearLayout.startAnimation(AnimationUtils.loadAnimation(ProductsDetails.this, R.anim.bottom_from_up));
                }

                RoundedImageView productImage = productView.findViewById(R.id.productImage);
                TextView qty = productView.findViewById(R.id.qty);
                RoundedImageView qtyMinus = productView.findViewById(R.id.qtyMinus);
                RoundedImageView qtyPlus = productView.findViewById(R.id.qtyPlus);
                Button btnSubmit = productView.findViewById(R.id.btnSubmit);

                qtyPlus.setOnClickListener(plusView -> {
                    int currentQty = Integer.parseInt(qty.getText().toString());
                    currentQty++;
                    qty.setText(String.valueOf(currentQty));
                });
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        viewCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ProductsDetails.this, "Opening Cart", Toast.LENGTH_SHORT).show();
                if (mainCard.getVisibility() == View.VISIBLE) {
                    mainCard.startAnimation(AnimationUtils.loadAnimation(ProductsDetails.this, R.anim.bottom_from_up));
                    mainCard.setVisibility(View.GONE);
                }
            }
        });
    }

    //============================ Recycler View Starts here ======================================

    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = getLayoutInflater();
            View myView = layoutInflater.inflate(R.layout.customers_reviews, parent, false);
            return new MyViewHolder(myView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            HashMap<String, String> review = arrayList.get(position);
            holder.customerName.setText(review.get("reviewerName"));
            holder.customerEmail.setText(review.get("reviewerEmail"));
            holder.customerReview.setText(review.get("comment"));
            holder.customerRating.setText("Rating: " + review.get("rating"));
            holder.reviewDate.setText(review.get("date"));
        }

        @Override
        public int getItemCount() {
            return arrayList.size();
        }

        private class MyViewHolder extends RecyclerView.ViewHolder {
            TextView customerName, customerEmail, customerReview, customerRating, reviewDate;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                customerName = itemView.findViewById(R.id.reviewerName);
                customerEmail = itemView.findViewById(R.id.reviewerEmail);
                customerReview = itemView.findViewById(R.id.comment);
                customerRating = itemView.findViewById(R.id.rating);
                reviewDate = itemView.findViewById(R.id.date);
            }
        }
    }

    //============================ Recycler View Ends here ======================================

}
