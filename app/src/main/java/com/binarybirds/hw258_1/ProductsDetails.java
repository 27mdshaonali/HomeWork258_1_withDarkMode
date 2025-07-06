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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

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
                String discountedPrice = String.format("%.2f", mainPriceDouble - (mainPriceDouble * disDouble / 100));
                productOfferPrice.setText("Offer Price :  $" + discountedPrice + " (After " + disPercentage + "% Off)");
                productMainPrice.setText("Price :  $" + price);
                productMainPrice.setPaintFlags(productMainPrice.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);

                productRating.setText("Rating :  " + rating + " / 5");
                productStock.setText("Stock :  " + stock);
                productBrand.setText("Brand : " + brand);
                productCategory.setText("Category :  " + category);
                weight.setText("Weight :  " + product.get("weight"));

                // Image Slider
                List<SlideModel> slideModels = new ArrayList<>();
                for (String imageUrl : images) {
                    slideModels.add(new SlideModel(imageUrl.trim(), ScaleTypes.FIT));
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

                // Tags
                String tagsJson = product.get("tags");
                if (tagsJson != null && !tagsJson.isEmpty()) {
                    List<String> tagList = new ArrayList<>();
                    if (tagsJson.trim().startsWith("[")) {
                        JSONArray tagsArray = new JSONArray(tagsJson);
                        for (int i = 0; i < tagsArray.length(); i++) {
                            tagList.add(tagsArray.getString(i));
                        }
                    } else {
                        for (String tag : tagsJson.split(",")) {
                            tagList.add(tag.trim());
                        }
                    }
                    tagsTextView.setText("Tags: " + TextUtils.join(", ", tagList));
                } else {
                    tagsTextView.setText("Tags: None");
                }

                // Reviews
                String reviewsJson = product.get("reviews");
                if (reviewsJson != null && !reviewsJson.isEmpty()) {
                    JSONArray reviewsArray = new JSONArray(reviewsJson);
                    for (int i = 0; i < reviewsArray.length(); i++) {
                        JSONObject reviewObject = reviewsArray.getJSONObject(i);
                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("reviewerName", reviewObject.optString("reviewerName"));
                        hashMap.put("reviewerEmail", reviewObject.optString("reviewerEmail"));
                        hashMap.put("comment", reviewObject.optString("comment"));
                        hashMap.put("rating", reviewObject.optString("rating"));
                        hashMap.put("date", reviewObject.optString("date"));
                        arrayList.add(hashMap);
                    }
                    reviewsAdapter.notifyDataSetChanged();
                }
            } catch (Exception e) {
                Log.e("ProductsDetails", "Error: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private String formatDate(String isoDate) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            inputFormat.setTimeZone(TimeZone.getTimeZone("UTC")); // Make sure it's UTC if needed
            Date date = inputFormat.parse(isoDate);

            SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy, hh:mm a");
            outputFormat.setTimeZone(TimeZone.getDefault()); // Local timezone
            return outputFormat.format(date);
        } catch (Exception e) {
            return isoDate; // Fallback to raw if parsing fails
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

            String reviewerName = hashMap.get("reviewerName");
            String reviewerEmail = hashMap.get("reviewerEmail");
            String comment = hashMap.get("comment");
            String rating = hashMap.get("rating");
            String rawDate = hashMap.get("date");
            String formattedDate = formatDate(rawDate);

            holder.tvReviewerName.setText("Name: " + reviewerName);
            holder.tvReviewerEmail.setText("Email: " + reviewerEmail);
            holder.tvComment.setText(comment);
            holder.tvRating.setText("Rating: " + rating + " /5");
            holder.tvDate.setText("Date: " + formattedDate);


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
