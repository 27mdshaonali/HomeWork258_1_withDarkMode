package com.binarybirds.hw258_1;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

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


                /*

                String sku = product.get("sku");
                String warrantyInformation = product.get("warrantyInformation");
                String shippingInformation = product.get("shippingInformation");
                String availabilityStatus = product.get("availabilityStatus");
                String returnPolicy = product.get("returnPolicy");
                int minimumOrderQuantity = Integer.parseInt(product.get("minimumOrderQuantity"));

                JSONObject dimensionsObject = new JSONObject(product.get("dimensions"));
                double width = dimensionsObject != null ? dimensionsObject.getDouble("width") : 0;
                double height = dimensionsObject != null ? dimensionsObject.getDouble("height") : 0;
                double depth = dimensionsObject != null ? dimensionsObject.getDouble("depth") : 0;

                JSONObject meta = new JSONObject(product.get("meta"));
                String createdAt = meta != null ? meta.getString("createdAt") : "";
                String updatedAt = meta != null ? meta.getString("updatedAt") : "";
                String barcode = meta != null ? meta.getString("barcode") : "";
                String qrCode = meta != null ? meta.getString("qrCode") : "";

                JSONArray tagsArray = new JSONArray(product.get("tags"));
                List<String> tags = new ArrayList<>();
                if (tagsArray != null) {
                    for (int i = 0; i < tagsArray.length(); i++) {
                        tags.add(tagsArray.getString(i));
                    }
                }

                JSONArray reviewsArray = new JSONArray(product.get("reviews"));
                StringBuilder reviewsInfo = new StringBuilder();
                for (int i = 0; i < reviewsArray.length(); i++) {
                    JSONObject review = reviewsArray.getJSONObject(i);
                    String reviewerName = review.optString("reviewerName");
                    double ratingProduct = review.optDouble("rating");
                    String comment = review.optString("comment");
                    reviewsInfo.append("Reviewer: ").append(reviewerName).append(", Rating: ").append(ratingProduct).append(", Comment: ").append(comment).append("\n");
                }

                Toast.makeText(this, reviewsInfo.toString(), Toast.LENGTH_LONG).show();


                 */

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
                        public void doubleClick(int i) {}

                        @Override
                        public void onItemSelected(int position) {
                            Toast.makeText(ProductsDetails.this, "Clicked image " + (position + 1), Toast.LENGTH_SHORT).show();
                        }
                    });
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
}
