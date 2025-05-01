package uk.ac.aston.cs3mdd.mealmate.activity;


import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import uk.ac.aston.cs3mdd.mealmate.R;
import uk.ac.aston.cs3mdd.mealmate.adapter.RecipesAdapter;
import uk.ac.aston.cs3mdd.mealmate.model.Recipe;

public class SearchRecipeActivity extends AppCompatActivity {

    private EditText etSearchRecipe;
    private Button btnSearchRecipe;
    private RecyclerView rvSearchResults;
    private RecipesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_recipe);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Search Meal");

        }
        etSearchRecipe = findViewById(R.id.etSearchRecipe);
        btnSearchRecipe = findViewById(R.id.btnSearchRecipe);
        rvSearchResults = findViewById(R.id.rvSearchResults);

        adapter = new RecipesAdapter(new ArrayList<>(), this);
        rvSearchResults.setLayoutManager(new LinearLayoutManager(this));
        rvSearchResults.setAdapter(adapter);

        btnSearchRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchRecipes();
            }
        });
    }

    private void searchRecipes() {
        String searchQuery = etSearchRecipe.getText().toString().trim();

        if (!searchQuery.isEmpty()) {
            new SearchRecipesTask().execute(searchQuery);
        } else {
            Toast.makeText(this, "Please enter a meal name", Toast.LENGTH_SHORT).show();
        }
    }

    private class SearchRecipesTask extends AsyncTask<String, Void, List<Recipe>> {
        @Override
        protected List<Recipe> doInBackground(String... params) {
            String searchJson = makeHttpRequest("https://www.themealdb.com/api/json/v1/1/search.php?s=" + params[0]);
            return parseRecipes(searchJson);
        }

        @Override
        protected void onPostExecute(List<Recipe> recipes) {
            adapter.setRecipes(recipes);
        }
    }

    private String makeHttpRequest(String urlString) {
        String jsonResponse = "";

        try {
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {
                InputStream inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
            }
        } catch (MalformedURLException e) {

        } catch (IOException e) {
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

    private List<Recipe> parseRecipes(String json) {
        List<Recipe> recipes = new ArrayList<>();

        try {
            JSONObject root = new JSONObject(json);
            JSONArray mealsArray = root.getJSONArray("meals");

            for (int i = 0; i < mealsArray.length(); i++) {
                JSONObject currentRecipe = mealsArray.getJSONObject(i);

                String id = currentRecipe.getString("idMeal");
                String name = currentRecipe.getString("strMeal");
                String category = currentRecipe.getString("strCategory");
                String area = currentRecipe.getString("strArea");
                String instructions = currentRecipe.getString("strInstructions");
                String imageUrl = currentRecipe.getString("strMealThumb");

                Recipe recipe = new Recipe(id, name, imageUrl);
                recipes.add(recipe);
            }
        } catch (JSONException e) {
        }

        return recipes;
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