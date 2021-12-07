package com.example.salestracker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.Menu;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

//Main activity adjusts the tab layout and its design.
public class MainActivity extends AppCompatActivity {

    //Activity components.
    private Toolbar toolbar;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    //Fragments.
    private SaleFragment saleFragment;
    private CustomerFragment customerFragment;
    private ProductFragment productFragment;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Get the activity components by their Id.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tab_layout);
        //Set the toolbar as a support action bar.
        setSupportActionBar(toolbar);
        //Initialize the fragments.
        saleFragment = new SaleFragment();
        customerFragment = new CustomerFragment();
        productFragment = new ProductFragment();
        tabLayout.setupWithViewPager(viewPager);

        //Design the tabs by using the adapter class below .
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), 0);
        viewPagerAdapter.addFragment(customerFragment, "Customers");
        viewPagerAdapter.addFragment(saleFragment, "Sales");
        viewPagerAdapter.addFragment(productFragment, "Products");
        viewPager.setAdapter(viewPagerAdapter);

        //Set icons.
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_person_black_24dp);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_local_atm_black_24dp);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_local_mall_black_24dp);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    //Adapter class for the ViewPager
    private class ViewPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragments = new ArrayList<>();
        private List<String> fragmentTitles = new ArrayList<>();


        public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        public void addFragment(Fragment fragment, String title){
            fragments.add(fragment);
            fragmentTitles.add(title);
        }
        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitles.get(position);
        }

    }
}
