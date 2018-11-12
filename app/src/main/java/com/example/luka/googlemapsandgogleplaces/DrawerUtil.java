package com.example.luka.googlemapsandgogleplaces;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import androidx.appcompat.widget.Toolbar;


public class DrawerUtil {
    public static void getDrawer(final Activity activity, Toolbar toolbar) {
        //if you want to update the items at a later time it is recommended to keep it in a variable
        PrimaryDrawerItem drawerEmptyItem= new PrimaryDrawerItem().withIdentifier(0).withName("");
        drawerEmptyItem.withEnabled(true);

            PrimaryDrawerItem drawerItemRunList = new PrimaryDrawerItem().withIdentifier(1)
                    .withName("List of runs").withIcon(R.drawable.ic_menu_manage);
            PrimaryDrawerItem drawerItemSettings = new PrimaryDrawerItem()
                    .withIdentifier(2).withName("Settings").withIcon(R.drawable.ic_menu_slideshow);

        if(activity instanceof ResultListActivity) {
            drawerItemRunList.withName("Home").withIcon(R.drawable.ic_menu_gallery);
        }





        //create the drawer and remember the `Drawer` result object
        Drawer result = new DrawerBuilder()
                .withActivity(activity)
                .withToolbar(toolbar)
                .withActionBarDrawerToggle(true)
                .withActionBarDrawerToggleAnimated(true)
                .withCloseOnClick(true)
                .withSelectedItem(-1)
                .addDrawerItems(
                        drawerEmptyItem,drawerEmptyItem,drawerEmptyItem,
                        drawerItemRunList,
                        drawerItemSettings,
                        new DividerDrawerItem()
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {

                        if (drawerItem.getIdentifier()==1 && !(activity instanceof ResultListActivity)) {

                            Intent intent = new Intent(activity, ResultListActivity.class);
                            view.getContext().startActivity(intent);
                        }

                        if (drawerItem.getIdentifier()==1 && !(activity instanceof MainActivity)) {

                            Intent intent = new Intent(activity, MainActivity.class);
                            view.getContext().startActivity(intent);
                        }
                        return true;
                    }
                })
                .build();
    }
}