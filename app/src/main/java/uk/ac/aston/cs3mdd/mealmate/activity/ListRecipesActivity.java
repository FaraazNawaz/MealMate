package uk.ac.aston.cs3mdd.mealmate.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import uk.ac.aston.cs3mdd.mealmate.adapter.RecipesAdapter;
import uk.ac.aston.cs3mdd.mealmate.model.Recipe;

public class ListRecipesActivity extends AppCompatActivity {
    private static final String TAG = ListRecipesActivity.class.getSimpleName();
    private RecyclerView recyclerView;
    private RecipesAdapter recipesAdapter;
    private List<Recipe> recipeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_recipes);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Category");
        }
        recyclerView = findViewById(R.id.rvRecipes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recipeList = new ArrayList<>();
        recipesAdapter = new RecipesAdapter(recipeList, this);
        recyclerView.setAdapter(recipesAdapter);


        String selectedCategory = getIntent().getStringExtra("selectedCategory");


        String categoryFilterUrl = "https://www.themealdb.com/api/json/v1/1/filter.php?c=" + selectedCategory;


        new FetchRecipesTask().execute(categoryFilterUrl);
    }

    private class FetchRecipesTask extends AsyncTask<String, Void, List<Recipe>> {
        @Override
        protected List<Recipe> doInBackground(String... params) {
            String recipesJson = null;
            try {
                recipesJson = makeHttpRequest(params[0]);
            } catch (IOException e) {
                Log.e(TAG, "Error making the request", e);
            }

            return parseRecipes(recipesJson);
        }

        @Override
        protected void onPostExecute(List<Recipe> recipes) {
            recipeList.addAll(recipes);
            recipesAdapter.notifyDataSetChanged();
        }

        private String makeHttpRequest(String urlString) throws IOException {
            String jsonResponse = "";
            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;

            try {
                URL url = new URL(urlString);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setConnectTimeout(10000);
                urlConnection.setReadTimeout(10000);
                urlConnection.connect();

                if (urlConnection.getResponseCode() == 200) {
                    inputStream = urlConnection.getInputStream();
                    jsonResponse = readFromStream(inputStream);
                } else {
                    Log.e(TAG, "Error response code: " + urlConnection.getResponseCode());
                }
            } catch (IOException e) {
                Log.e(TAG, "Problem making the request.", e);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            }

            return jsonResponse;
        }

        private String readFromStream(InputStream inputStream) throws IOException {
            StringBuilder output = new StringBuilder();
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null) {
                    output.append(line);
                    line = reader.readLine();
                }
            }
            return output.toString();
        }

        private List<Recipe> parseRecipes(String recipesJson) {
            List<Recipe> recipes = new ArrayList<>();
            try {
                JSONObject root = new JSONObject(recipesJson);
                JSONArray mealsArray = root.getJSONArray("meals");

                for (int i = 0; i < mealsArray.length(); i++) {
                    JSONObject recipeObject = mealsArray.getJSONObject(i);

                    String mealName = recipeObject.getString("strMeal");
                    String mealThumb = recipeObject.getString("strMealThumb");
                    String mealId = recipeObject.getString("idMeal");

                    Recipe recipe = new Recipe(mealId, mealName, mealThumb);
                    recipes.add(recipe);
                }
            } catch (JSONException e) {
                Log.e(TAG, "Error with JSON response", e);
            }
            return recipes;
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
