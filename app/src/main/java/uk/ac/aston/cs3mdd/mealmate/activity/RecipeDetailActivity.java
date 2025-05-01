package uk.ac.aston.cs3mdd.mealmate.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import uk.ac.aston.cs3mdd.mealmate.R;

public class RecipeDetailActivity extends AppCompatActivity {

    private ImageView ivRecipeImage;
    private TextView tvRecipeName, tvCategory, tvArea, tvInstructions, tvIngredients, tvYoutubeLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Meal Details");

        }
        ivRecipeImage = findViewById(R.id.ivRecipeImage);
        tvRecipeName = findViewById(R.id.tvRecipeName);
        tvCategory = findViewById(R.id.tvCategory);
        tvArea = findViewById(R.id.tvArea);
        tvInstructions = findViewById(R.id.tvInstructions);
        tvIngredients = findViewById(R.id.tvIngredients);
        tvYoutubeLink = findViewById(R.id.tvYoutubeLink);


        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("recipe_id")) {
            String recipeId = intent.getStringExtra("recipe_id");


            new LoadRecipeDetailsTask().execute(recipeId);
        } else {
            Toast.makeText(this, "Invalid recipe ID", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private class LoadRecipeDetailsTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }

            String recipeId = params[0];


            String apiUrl = "https://www.themealdb.com/api/json/v1/1/lookup.php?i="+recipeId;

            try {
                URL url = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                try {
                    InputStream inputStream = connection.getInputStream();
                    Scanner scanner = new Scanner(inputStream);
                    scanner.useDelimiter("\\A");

                    if (scanner.hasNext()) {
                        return scanner.next();
                    } else {
                        return null;
                    }
                } finally {
                    connection.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (!TextUtils.isEmpty(result)) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray mealsArray = jsonObject.getJSONArray("meals");
                    if (mealsArray.length() > 0) {
                        JSONObject mealObject = mealsArray.getJSONObject(0);

                        String recipeName = mealObject.optString("strMeal", "");
                        String category = mealObject.optString("strCategory", "");
                        String area = mealObject.optString("strArea", "");
                        String instructions = mealObject.optString("strInstructions", "");
                        String imageUrl = mealObject.optString("strMealThumb", "");
                        String youtubeLink = mealObject.optString("strYoutube", "");

                        tvRecipeName.setText(recipeName);
                        tvCategory.setText("Category: " + category);
                        tvArea.setText("Area: " + area);
                        tvInstructions.setText(instructions);


                        Glide.with(RecipeDetailActivity.this)
                                .load(imageUrl)
                                .placeholder(R.drawable.meal_logo)
                                .into(ivRecipeImage);


                        StringBuilder ingredientsBuilder = new StringBuilder();
                        for (int i = 1; i <= 20; i++) {
                            String ingredient = mealObject.optString("strIngredient" + i, "");
                            String measure = mealObject.optString("strMeasure" + i, "");

                            if (!TextUtils.isEmpty(ingredient) && !TextUtils.isEmpty(measure)) {
                                ingredientsBuilder.append("- ").append(measure).append(" ").append(ingredient).append("\n");
                            }
                        }
                        tvIngredients.setText(ingredientsBuilder.toString().trim());


                        if (!TextUtils.isEmpty(youtubeLink)) {
                            tvYoutubeLink.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(youtubeLink));
                                    startActivity(intent);
                                }
                            });
                        } else {
                            tvYoutubeLink.setVisibility(View.GONE);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}