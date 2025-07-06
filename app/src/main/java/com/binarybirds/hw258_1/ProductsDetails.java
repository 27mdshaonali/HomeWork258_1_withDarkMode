package com.binarybirds.hw258_1;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.AnimationTypes;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.interfaces.ItemClickListener;
import com.denzcoskun.imageslider.models.SlideModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProductsDetails extends AppCompatActivity {

    RecyclerView reviewsRecyclerView;
    ArrayList<HashMap<String, String>> arrayList;

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
        TextView weight = findViewById(R.id.weight);
        TextView tagsTextView = findViewById(R.id.productTags);


        LinearLayout linearLayout = findViewById(R.id.linearLayout);
        ConstraintLayout mainContainer = findViewById(R.id.mainContainer);

        reviewsRecyclerView = findViewById(R.id.reviewsRecyclerView);
        arrayList = new ArrayList<>();
        ReviewsAdapter reviewsAdapter = new ReviewsAdapter();
        reviewsRecyclerView.setAdapter(reviewsAdapter);
        reviewsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

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

                weight.setText("Weight :  " + product.get("weight"));




                /*
                if (stockInt == 0) {
                    linearLayout.setVisibility(View.GONE);
                    mainContainer.setVisibility(View.GONE);
                }

                 */

                String tagsJson = product.get("tags");
                if (tagsJson != null && !tagsJson.isEmpty()) {
                    try {
                        List<String> tagList = new ArrayList<>();

                        // Case 1: Valid JSON Array (e.g., ["beauty", "mascara"])
                        if (tagsJson.trim().startsWith("[")) {
                            JSONArray tagsArray = new JSONArray(tagsJson);
                            for (int i = 0; i < tagsArray.length(); i++) {
                                tagList.add(tagsArray.getString(i));
                            }
                        } else {
                            // Case 2: Comma-separated plain text (e.g., beauty,mascara)
                            String[] parts = tagsJson.split(",");
                            for (String part : parts) {
                                tagList.add(part.trim());
                            }
                        }

                        if (!tagList.isEmpty()) {
                            String tagsText = TextUtils.join(", ", tagList);
                            tagsTextView.setText("Tags: " + tagsText);
                        } else {
                            tagsTextView.setText("Tags: None");
                        }

                    } catch (Exception e) {
                        tagsTextView.setText("Tags: Invalid format");
                        Log.e("TAGS_PARSE", "Error parsing tags: " + e.getMessage());
                    }
                } else {
                    tagsTextView.setText("Tags: None");
                }



                //====================== Image Slider Start ======================

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
                //====================== Image Slider End ======================


                String reviewsJson = product.get("reviews");
                if (reviewsJson != null && !reviewsJson.isEmpty()) {
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
                    reviewsAdapter.notifyDataSetChanged();
                }
            } catch (Exception e) {
                Log.e("ProductsDetails", "Error setting product data: " + e.getMessage());
                e.printStackTrace();
            }
        }


    }

    private class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.UserReview> {

        @NonNull
        @Override
        public UserReview onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customers_reviews, parent, false);
            return new UserReview(view);
        }

        @Override
        public void onBindViewHolder(@NonNull UserReview holder, int position) {
            HashMap<String, String> hashMap = arrayList.get(position);

            holder.tvReviewerName.setText(hashMap.get("reviewerName"));
            holder.tvReviewerEmail.setText(hashMap.get("reviewerEmail"));
            holder.tvComment.setText(hashMap.get("comment"));
            holder.tvRating.setText(hashMap.get("rating"));
            holder.tvDate.setText(hashMap.get("date"));
        }

        @Override
        public int getItemCount() {
            return arrayList.size();
        }

        class UserReview extends RecyclerView.ViewHolder {
            TextView tvReviewerName, tvReviewerEmail, tvComment, tvRating, tvDate;

            public UserReview(@NonNull View itemView) {
                super(itemView);
                tvReviewerName = itemView.findViewById(R.id.reviewerName);
                tvReviewerEmail = itemView.findViewById(R.id.reviewerEmail);
                tvComment = itemView.findViewById(R.id.comment);
                tvRating = itemView.findViewById(R.id.rating);
                tvDate = itemView.findViewById(R.id.date);
            }
        }
    }
}
