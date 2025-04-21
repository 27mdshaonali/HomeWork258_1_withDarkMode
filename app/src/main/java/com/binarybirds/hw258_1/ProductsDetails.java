package com.binarybirds.hw258_1;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.AnimationTypes;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProductsDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_details);

        // Get the passed product data
        HashMap<String, String> product = (HashMap<String, String>) getIntent().getSerializableExtra("product");

        // Initialize ImageSlider
        ImageSlider imageSlider = findViewById(R.id.image_slider);

        // Initialize other views
        TextView productTitle = findViewById(R.id.productTitle);
        TextView productDescription = findViewById(R.id.productDescription);
        TextView productPrice = findViewById(R.id.productPrice);
        TextView productDiscount = findViewById(R.id.productDiscount);
        TextView productRating = findViewById(R.id.productRating);
        TextView productStock = findViewById(R.id.productStock);
        TextView productBrand = findViewById(R.id.productBrand);
        TextView productCategory = findViewById(R.id.productCategory);

        // Set up image slider
        List<SlideModel> slideModels = new ArrayList<>();

        // Check if images string exists and is not empty
        if (product.containsKey("images") && !product.get("images").isEmpty()) {
            // Split the images string into individual URLs
            String[] images = product.get("images").split(", ");

            // Add each image to the slider
            for (String imageUrl : images) {
                if (!imageUrl.isEmpty()) {
                    slideModels.add(new SlideModel(imageUrl, ScaleTypes.FIT));
//                    imageSlider.setSlideAnimation(AnimationTypes.TOSS);

                }
            }
        }

        // If no images were added, use the thumbnail as fallback
        if (slideModels.isEmpty() && product.containsKey("thumbnail") && !product.get("thumbnail").isEmpty()) {
            slideModels.add(new SlideModel(product.get("thumbnail"), ScaleTypes.FIT));

        }

        // Set the image list to the slider
        if (!slideModels.isEmpty()) {
            imageSlider.setImageList(slideModels, ScaleTypes.FIT);
            imageSlider.setSlideAnimation(AnimationTypes.TOSS);
        }

        // Set product data to TextViews
        if (product.containsKey("title")) {
            productTitle.setText(product.get("title"));
        }

        if (product.containsKey("description")) {
            productDescription.setText(product.get("description"));
        }

        // Calculate and display prices
        if (product.containsKey("price") && product.containsKey("discountPercentage")) {
            try {
                double price = Double.parseDouble(product.get("price"));
                double discount = Double.parseDouble(product.get("discountPercentage"));
                double discountedPrice = price - (price * discount / 100);

                productPrice.setText(String.format("Price: $%.2f (After %.0f%% discount)", discountedPrice, discount));
                productDiscount.setText(String.format("Original Price: $%.2f", price));
                productDiscount.setPaintFlags(productDiscount.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        // Set other product information
        if (product.containsKey("rating")) {
            productRating.setText(String.format("Rating: %s/5", product.get("rating")));
        }

        if (product.containsKey("stock")) {
            productStock.setText(String.format("Stock: %s", product.get("stock")));
        }

        if (product.containsKey("brand")) {
            productBrand.setText(String.format("Brand: %s", product.get("brand")));
        }

        if (product.containsKey("category")) {
            productCategory.setText(String.format("Category: %s", product.get("category")));
        }
    }
}