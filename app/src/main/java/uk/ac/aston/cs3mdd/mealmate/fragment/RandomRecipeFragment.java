package uk.ac.aston.cs3mdd.mealmate.fragment;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import uk.ac.aston.cs3mdd.mealmate.R;
import uk.ac.aston.cs3mdd.mealmate.activity.SearchRecipeActivity;
import uk.ac.aston.cs3mdd.mealmate.adapter.CategoriesAdapter;
import uk.ac.aston.cs3mdd.mealmate.model.Category;
import uk.ac.aston.cs3mdd.mealmate.model.MealDetails;

public class RandomRecipeFragment extends Fragment {

    private TextView tvRandomName, tvInstruction;
    private RecyclerView rvCategories;
    private ImageView imgRandom;

    public RandomRecipeFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_recipes, container, false);

        view.findViewById(R.id.search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getActivity(), SearchRecipeActivity.class));
            }
        });

        imgRandom = view.findViewById(R.id.imageViewRandomRecipe);
        tvRandomName = view.findViewById(R.id.tvRecipeName);
        tvInstruction = view.findViewById(R.id.tvInstructions);

        rvCategories = view.findViewById(R.id.rvCategories);
        rvCategories.setLayoutManager(new LinearLayoutManager(requireContext()));

        new RandomMealTask().execute();

        new MealCategoriesTask().execute();

        return view;
    }

    public class RandomMealTask extends AsyncTask<Void, Void, String> {

        private static final String API_URL_RANDOM_MEAL = "https://www.themealdb.com/api/json/v1/1/random.php";

        @Override
        protected String doInBackground(Void... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String randomMealJson = null;

            try {

                URL url = new URL(API_URL_RANDOM_MEAL);


                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();


                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();

                if (inputStream == null) {
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }

                randomMealJson = buffer.toString();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }

                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            return randomMealJson;
        }

        @Override
        protected void onPostExecute(String randomMealJson) {

            MealDetails randomRecipeDetails = parseRandomRecipe(randomMealJson);


            Glide.with(requireActivity())
                    .load(randomRecipeDetails.getImageUrl())
                    .placeholder(R.drawable.meal_logo)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(imgRandom);
            tvRandomName.setText(randomRecipeDetails.getName());
             tvInstruction.setText(randomRecipeDetails.getInstructions());
        }

        private MealDetails parseRandomRecipe(String randomMealJson) {
            try {
                JSONObject jsonObject = new JSONObject(randomMealJson);
                JSONObject mealObject = jsonObject.getJSONArray("meals").getJSONObject(0);

                String name = mealObject.getString("strMeal");
                String category = mealObject.getString("strCategory");
                String area = mealObject.getString("strArea");
                String instructions = mealObject.getString("strInstructions");
                String imageUrl = mealObject.getString("strMealThumb");

                return new MealDetails(name, category, area, instructions, imageUrl);
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
    private class MealCategoriesTask extends AsyncTask<Void, Void, List<Category>> {
        @Override
        protected List<Category> doInBackground(Void... params) {
            String categoriesJson = makeHttpRequest("https://www.themealdb.com/api/json/v1/1/categories.php");
            return parseCategories(categoriesJson);
        }

        @Override
        protected void onPostExecute(List<Category> categories) {
            CategoriesAdapter adapter = new CategoriesAdapter(categories, requireContext());
            rvCategories.setAdapter(adapter);
        }
        private String makeHttpRequest(String url) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String jsonResponse = null;

            try {
                URL requestUrl = new URL(url);
                urlConnection = (HttpURLConnection) requestUrl.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder builder = new StringBuilder();

                if (inputStream != null) {
                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        builder.append(line);
                    }
                    jsonResponse = builder.toString();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            return jsonResponse;
        }
    }

    private List<Category> parseCategories(String categoriesJson) {
        List<Category> categories = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(categoriesJson);
            JSONArray categoriesArray = jsonObject.getJSONArray("categories");

            for (int i = 0; i < categoriesArray.length(); i++) {
                JSONObject categoryObject = categoriesArray.getJSONObject(i);
                String name = categoryObject.getString("strCategory");
                String image = categoryObject.getString("strCategoryThumb");
                categories.add(new Category(name, image));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return categories;
    }

}
