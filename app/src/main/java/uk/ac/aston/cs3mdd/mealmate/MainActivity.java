package uk.ac.aston.cs3mdd.mealmate;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;


public class MainActivity extends AppCompatActivity {

    private NavigationView navigationView;
    private NavController navController;
    private AppBarConfiguration appBarConfiguration;
    private static final int REQUEST_LOCATION_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();
        navController.navigate(R.id.homeFragment);




        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {

                updateActionBar(destination);
                if (destination.getId() == R.id.homeFragment) {
                    getSupportActionBar().setTitle("Home");
                } else if (destination.getId() == R.id.mealPlanFragment) {
                    getSupportActionBar().setTitle("Meal Plan");
                } else if (destination.getId() == R.id.randomRecipeFragment) {
                    getSupportActionBar().setTitle("Find Meals");
                } else if (destination.getId() == R.id.shoppingListFragment) {
                    getSupportActionBar().setTitle("Shopping List");
                } else if (destination.getId() == R.id.groceryStoreFragment) {
                    getSupportActionBar().setTitle("Find Grocery Stores");
                }
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {

            return navController.navigateUp() || super.onOptionsItemSelected(item);
        }

        return super.onOptionsItemSelected(item);
    }




    private void navigateToDestination(int itemId) {
        switch (itemId) {
            case R.id.nav_home:
                navController.navigate(R.id.homeFragment);
                break;
            case R.id.nav_profile:
                navController.navigate(R.id.mealPlanFragment);
                break;
        }
    }

    private void updateActionBar(NavDestination destination) {
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            if (destination.getId() == R.id.homeFragment) {

                actionBar.setDisplayHomeAsUpEnabled(false);
            } else {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp();
    }
}

