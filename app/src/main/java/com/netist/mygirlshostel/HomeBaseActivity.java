package com.netist.mygirlshostel;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.netist.mygirlshostel.adv.AdvertisementListActivity;
import com.netist.mygirlshostel.classes.ClassesBookingListActivity;
import com.netist.mygirlshostel.classes.ClassesListActivity;
import com.netist.mygirlshostel.constants.PrefManager;
import com.netist.mygirlshostel.facilities.FacilityListActivity;
import com.netist.mygirlshostel.hostel.HostelBookingListActivity;
import com.netist.mygirlshostel.hostel.HostelListActivity;
import com.netist.mygirlshostel.hostel.HostelListForDetailsActivity;
import com.netist.mygirlshostel.library.LibraryBookingListActivity;
import com.netist.mygirlshostel.library.LibraryListActivity;
import com.netist.mygirlshostel.mess.MessBookingListActivity;
import com.netist.mygirlshostel.mess.MessListActivity;
import com.netist.mygirlshostel.session_handler.SessionHelper;
import com.netist.mygirlshostel.utils.Utility;
import com.netist.mygirlshostel.view.ViewListActivity;

public class HomeBaseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    SessionHelper session;
    PrefManager preferenceManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        preferenceManager =new PrefManager(HomeBaseActivity.this);
        session = new SessionHelper(getApplicationContext());

    }

    @Override
    public void setContentView(int layoutResID)
    {
        DrawerLayout fullView = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_base, null);
        FrameLayout activityContainer = (FrameLayout) fullView.findViewById(R.id.activity_content);
        getLayoutInflater().inflate(layoutResID, activityContainer, true);
        super.setContentView(fullView);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (useToolbar())
        {
            setSupportActionBar(toolbar);
            setTitle("IYS");
        }
        else
        {
            toolbar.setVisibility(View.GONE);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if(session.getUserType().equals("adv"))
        {
            navigationView.setVisibility(View.GONE);

        }

        //TextView tv_username = (TextView) navigationView.findViewById(R.id.tv_username);
        //tv_username.setText(session.getUserType());

        if(session.getUserType().equals("user")) {

        //    navigationView.getMenu().add("Search Setting").setIcon(R.drawable.ic_menu_settings);

            navigationView.getMenu().add("Booking").setIcon(R.drawable.ic_menu_booking);

            //navigationView.getMenu().add("My Bookings").setIcon(R.drawable.ic_menu_booking);

            navigationView.getMenu().add("Views").setIcon(R.drawable.ic_menu_view);

            navigationView.getMenu().add("Facilities").setIcon(R.drawable.ic_menu_facility);

            navigationView.getMenu().add("Notice").setIcon(R.drawable.ic_menu_notice);

            //  navigationView.getMenu().add("Chat").setIcon(R.drawable.ic_menu_chat);

            //  navigationView.getMenu().add("Subscription Fee").setIcon(R.drawable.ic_menu_chat);
            navigationView.getMenu().add("Rental").setIcon(R.drawable.ic_menu_chat);

          //  navigationView.getMenu().add("Share").setIcon(R.drawable.ic_menu_share);

            navigationView.getMenu().add("Logout").setIcon(R.drawable.ic_menu_logout);
        }
        else if(session.getUserType().equals("hostel") ||
                session.getUserType().equals("mess") ||
                session.getUserType().equals("classes") ||
                session.getUserType().equals("library")) {

            navigationView.getMenu().add("Details").setIcon(R.drawable.ic_menu_hostel);

            navigationView.getMenu().add("Charges").setIcon(R.drawable.ic_menu_payment).setVisible(false);

            navigationView.getMenu().add("Accounts").setIcon(R.drawable.ic_menu_account);

            navigationView.getMenu().add("Bookings").setIcon(R.drawable.ic_menu_booking);

            navigationView.getMenu().add("Views").setIcon(R.drawable.ic_menu_view);

            navigationView.getMenu().add("Facilities").setIcon(R.drawable.ic_menu_facility);

            navigationView.getMenu().add("Notice").setIcon(R.drawable.ic_menu_notice);


            // navigationView.getMenu().add("Chat").setIcon(R.drawable.ic_menu_chat);

            navigationView.getMenu().add("Subscription Fee").setIcon(R.drawable.ic_menu_chat);
            navigationView.getMenu().add("Payment History").setIcon(R.drawable.ic_menu_chat);


           // navigationView.getMenu().add("Share").setIcon(R.drawable.ic_menu_share);

            navigationView.getMenu().add("Logout").setIcon(R.drawable.ic_menu_logout);
        }
        else if(session.getUserType().equals("admin")) {

          //  navigationView.getMenu().add("Search Setting").setIcon(R.drawable.ic_menu_settings);

            navigationView.getMenu().add("Service Type").setIcon(R.drawable.ic_menu_business);

            navigationView.getMenu().add("Subscription Charges").setIcon(R.drawable.ic_menu_payment);

         //   navigationView.getMenu().add("Accounts").setIcon(R.drawable.ic_menu_account);

            //navigationView.getMenu().add("My Bookings").setIcon(R.drawable.ic_menu_booking);

            navigationView.getMenu().add("Views").setIcon(R.drawable.ic_menu_view);

            navigationView.getMenu().add("Facilities").setIcon(R.drawable.ic_menu_facility);

            navigationView.getMenu().add("Notice").setIcon(R.drawable.ic_menu_notice);

            navigationView.getMenu().add("Advertisements").setIcon(R.drawable.ic_menu_chat);



            //  navigationView.getMenu().add("Subscription Fee").setIcon(R.drawable.ic_menu_chat);

           // navigationView.getMenu().add("Share").setIcon(R.drawable.ic_menu_share);

            navigationView.getMenu().add("Logout").setIcon(R.drawable.ic_menu_logout);
        }

        navigationView.setNavigationItemSelectedListener(this);

    }

    protected boolean useToolbar()
    {
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.homebase, menu);
        MenuItem item = menu.findItem(R.id.action_logout);
        if(session.getUserType().equals("guest"))
        {
            item.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder( HomeBaseActivity.this);

            // set title
            alertDialogBuilder.setTitle("Logout");

            // set dialog message
            alertDialogBuilder
                    .setMessage("Click yes to logout!")
                    .setCancelable(false)
                    .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {

                            SessionHelper session = new SessionHelper(getApplicationContext());
                            session.setLogin(false);
                            session.setUserType(null);
                            session.setUserID("");

                            finishAffinity();
                            Utility.launchActivity(HomeBaseActivity.this, SplashActivity.class,
                                    true);
                        }
                    })
                    .setNegativeButton("No",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            // if this button is clicked, just close
                            // the dialog box and do nothing
                            dialog.cancel();
                        }
                    });

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();


           /* SessionHelper session = new SessionHelper(getApplicationContext());
            session.setLogin(false);
            session.setUserType("");
            session.setUserID("");

            Intent intent = new Intent(getApplicationContext(), WelcomeActivity.class);
            startActivity(intent);
            this.finish();
            return true;*/

        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        String title = item.getTitle().toString();

        if (title.equals("Details")) {
            // Handle the camera action
            if(session.getUserType().equals("hostel"))
            {
                /*Intent intent = new Intent(getApplicationContext(),HostelDetailActivity.class);
                intent.putExtra("hostelId", session.getUserID() );
                startActivity(intent);*/

                preferenceManager.setAREA_SELECTED("details");
                //  Intent intent = new Intent(getApplicationContext(),HostelListActivity.class);
                Intent intent = new Intent(getApplicationContext(), HostelListForDetailsActivity.class);
                intent.putExtra("hostelId", session.getUserID() );

                startActivity(intent);

            }
            else if(session.getUserType().equals("mess"))
            {
                preferenceManager.setAREA_SELECTED("details");
                Intent intent = new Intent(getApplicationContext(),MessListActivity.class);
                intent.putExtra("messId", session.getUserID() );
                startActivity(intent);
            }
            else if(session.getUserType().equals("classes"))
            {
                preferenceManager.setAREA_SELECTED("details");
                Intent intent = new Intent(getApplicationContext(),ClassesListActivity.class);
                intent.putExtra("classesId", session.getUserID() );
                startActivity(intent);
            }
            else if(session.getUserType().equals("library"))
            {
                preferenceManager.setAREA_SELECTED("details");
                Intent intent = new Intent(getApplicationContext(),LibraryListActivity.class);
                intent.putExtra("libraryId", session.getUserID() );
                startActivity(intent);
            }
            else{
                Intent intent = new Intent(getApplicationContext(),UserRegistrationActivity.class);
                startActivity(intent);
            }
        }
        else if (title.equals("Booking"))
        {
//            Intent intent = new Intent(getApplicationContext(),HostelListActivity.class);
//            intent.putExtra("booking", "");
//            startActivity(intent);
            if(session.getUserType().equals("user")){
                Intent intent = new Intent(getApplicationContext(),BusinessListActivity.class);
                intent.putExtra("booking", "");
                startActivity(intent);
            }
            else if(session.getUserType().equals("hostel")){
                Intent intent = new Intent(getApplicationContext(),HostelListActivity.class);
                intent.putExtra("booking", "");
                startActivity(intent);
            }
            else if(session.getUserType().equals("mess")){
                Intent intent = new Intent(getApplicationContext(),MessListActivity.class);
                intent.putExtra("booking", "");
                startActivity(intent);
            }
            else if(session.getUserType().equals("classes")){
                Intent intent = new Intent(getApplicationContext(),ClassesListActivity.class);
                intent.putExtra("booking", "");
                startActivity(intent);
            }
            else if(session.getUserType().equals("library")){
                Intent intent = new Intent(getApplicationContext(),LibraryListActivity.class);
                intent.putExtra("booking", "");
                startActivity(intent);
            }
        }
        else if (title.equals("My Bookings"))
        {
            Intent intent = new Intent(getApplicationContext(),HostelBookingListActivity.class);
            intent.putExtra("userId",session.getUserID());
            startActivity(intent);
        }
        else if (title.equals("Views"))
        {
            if(session.getUserType().equals("admin")) {
                Intent intent = new Intent(getApplicationContext(),BusinessListActivity.class);
                intent.putExtra("views", "");
                startActivity(intent);
            }
            else if(session.getUserType().equals("hostel")){

                preferenceManager.setAREA_SELECTED("details");
                Intent intent = new Intent(getApplicationContext(),HostelListActivity.class);
                intent.putExtra("hostelId", session.getUserID() );
                intent.putExtra("views", "views" );
                startActivity(intent);

              /*  Intent intent = new Intent(getApplicationContext(),ViewListActivity.class);
                intent.putExtra("hostelId", session.getUserID());
                startActivity(intent);*/
            }
            else if(session.getUserType().equals("mess")){
                Intent intent = new Intent(getApplicationContext(),ViewListActivity.class);
                intent.putExtra("messId", session.getUserID());
                startActivity(intent);
            }
            else if(session.getUserType().equals("classes")){
                Intent intent = new Intent(getApplicationContext(),ViewListActivity.class);
                intent.putExtra("classesId", session.getUserID());
                startActivity(intent);
            }
            else if(session.getUserType().equals("library")){
                Intent intent = new Intent(getApplicationContext(),ViewListActivity.class);
                intent.putExtra("libraryId", session.getUserID());
                startActivity(intent);
            }


            else
            {
                Intent intent = new Intent(getApplicationContext(),BusinessListActivity.class);
                intent.putExtra("views", "");
                startActivity(intent);
            }
        }
        /*else if (title.equals("Contact"))
        {
            Intent intent = new Intent(getApplicationContext(),ContactUsActivity.class);
            startActivity(intent);
        }*/
        else if (title.equals("Facilities"))
        {
            if(session.getUserType().equals("admin")) {
                Intent intent = new Intent(getApplicationContext(),BusinessListActivity.class);
                intent.putExtra("facilities", "");
                startActivity(intent);
            }
            else if(session.getUserType().equals("hostel")){
               /* Intent intent = new Intent(getApplicationContext(),FacilityListActivity.class);
                intent.putExtra("hostelId", session.getUserID());
                startActivity(intent);*/

                preferenceManager.setAREA_SELECTED("details");
                Intent intent = new Intent(getApplicationContext(),HostelListActivity.class);
                intent.putExtra("hostelId", session.getUserID() );
                intent.putExtra("facilities", "facilities" );
                startActivity(intent);
            }
            else if(session.getUserType().equals("mess")){
                Intent intent = new Intent(getApplicationContext(),FacilityListActivity.class);
                intent.putExtra("messId", session.getUserID());
                startActivity(intent);
            }
            else if(session.getUserType().equals("classes")){
                Intent intent = new Intent(getApplicationContext(),FacilityListActivity.class);
                intent.putExtra("classesId", session.getUserID());
                startActivity(intent);
            }
            else if(session.getUserType().equals("library")){
                Intent intent = new Intent(getApplicationContext(),FacilityListActivity.class);
                intent.putExtra("libraryId", session.getUserID());
                startActivity(intent);
            }
            else
            {
                Intent intent = new Intent(getApplicationContext(),BusinessListActivity.class);
                intent.putExtra("facilities", "");
                startActivity(intent);
            }
        }
        else if (title.equals("Notice"))
        {
            if(session.getUserType().equals("admin")) {
                Intent intent = new Intent(getApplicationContext(),BusinessListActivity.class);
                intent.putExtra("notice", "");
                startActivity(intent);
            }
            else if(session.getUserType().equals("hostel"))
            {

                preferenceManager.setAREA_SELECTED("details");
                Intent intent = new Intent(getApplicationContext(),HostelListActivity.class);
                intent.putExtra("hostelId", session.getUserID() );
                intent.putExtra("notice", "notice" );
                startActivity(intent);
               /* Intent intent = new Intent(getApplicationContext(),NoticeListActivity.class);
                intent.putExtra("hostelId", session.getUserID());
                startActivity(intent);*/
            }
            else if(session.getUserType().equals("mess")){
                Intent intent = new Intent(getApplicationContext(),NoticeListActivity.class);
                intent.putExtra("messId", session.getUserID());
                startActivity(intent);
            }
            else if(session.getUserType().equals("classes")){
                Intent intent = new Intent(getApplicationContext(),NoticeListActivity.class);
                intent.putExtra("classesId", session.getUserID());
                startActivity(intent);
            }
            else if(session.getUserType().equals("library")){
                Intent intent = new Intent(getApplicationContext(),NoticeListActivity.class);
                intent.putExtra("libraryId", session.getUserID());
                startActivity(intent);
            }
            else
            {
                Intent intent = new Intent(getApplicationContext(),BusinessListActivity.class);
                intent.putExtra("notice", "");
                startActivity(intent);
            }
        }
       /* else if (title.equals("Chat"))
        {
            Intent intent = new Intent(getApplicationContext(),ChatUsersListActivity.class);
            startActivity(intent);
        }*/

        else if (title.equals("Subscription Fee"))
        {
            switch (session.getUserType())
            {
                case "admin":
                {
                    Intent intent = new Intent(getApplicationContext(),BusinessListActivity.class);
                    intent.putExtra("accounts", "");
                    startActivity(intent);
                }
                break;

                case "hostel":
                {
                    /*Intent intent = new Intent(getApplicationContext(),HostelListActivity.class);
                    intent.putExtra("accounts", "");
                    startActivity(intent);*/

                    preferenceManager.setAREA_SELECTED("details");
                    Intent intent = new Intent(getApplicationContext(),HostelListActivity.class);
                    intent.putExtra("hostelId", session.getUserID() );
                    intent.putExtra("payment", "payment" );
                    startActivity(intent);

                }
                break;

                case "mess":
                {
                    Intent intent = new Intent(getApplicationContext(),MessListActivity.class);
                    intent.putExtra("accounts", "");
                    startActivity(intent);
                }
                break;

                case "classes":
                {
                    Intent intent = new Intent(getApplicationContext(),ClassesListActivity.class);
                    intent.putExtra("accounts", "");
                    startActivity(intent);
                }
                break;

                case "library":
                {
                    Intent intent = new Intent(getApplicationContext(),LibraryListActivity.class);
                    intent.putExtra("accounts", "");
                    startActivity(intent);
                }
                break;
            }
           /* Intent intent = new Intent(getApplicationContext(),PaymentTezz.class);
            startActivity(intent);*/
        }

        else if (title.equals("Rental"))
        {
            // Intent intent = new Intent(getApplicationContext(),SubscriptionActivity.class);

            Intent intent = new Intent(getApplicationContext(),PaymentTezz.class);
            startActivity(intent);
        }


        else if (title.equals("Share"))
        {

          /*  Intent intent = new Intent(getApplicationContext(),Add_Data.class);
            startActivity(intent);*/

           /* Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "http://healtree.co.in/");
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
            return true;*/

            int applicationNameId =getApplicationInfo().labelRes;
            final String appPackageName =getPackageName();
            String text = "Install this application: ";
            String link = "https://play.google.com/store/apps/details?id=" +
                    appPackageName;

            Intent sendIntent = new Intent("android.intent.action.MAIN");
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.setType("text/plain");
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, "My Girls Hostel "+applicationNameId);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "" + text+ "  "+link);
            startActivity(Intent.createChooser(sendIntent, "Share App"));

        }

        else if (title.equals("Payment History"))
        {

            switch (session.getUserType()) {

                case "admin": {
                    Intent intent = new Intent(getApplicationContext(), BusinessListActivity.class);
                    intent.putExtra("accounts", "");
                    startActivity(intent);
                }
                break;

                case "hostel": {
                    /*Intent intent = new Intent(getApplicationContext(),HostelListActivity.class);
                    intent.putExtra("accounts", "");
                    startActivity(intent);*/

                    preferenceManager.setAREA_SELECTED("details");
                    Intent intent = new Intent(getApplicationContext(), HostelListActivity.class);
                    intent.putExtra("hostelId", session.getUserID());
                    intent.putExtra("paymenthistory", "paymenthistory");
                    startActivity(intent);

                }
                break;

                case "mess": {
                    Intent intent = new Intent(getApplicationContext(), MessListActivity.class);
                    intent.putExtra("accounts", "");
                    startActivity(intent);
                }
                break;

                case "classes": {
                    Intent intent = new Intent(getApplicationContext(), ClassesListActivity.class);
                    intent.putExtra("accounts", "");
                    startActivity(intent);
                }
                break;

                case "library": {
                    Intent intent = new Intent(getApplicationContext(), LibraryListActivity.class);
                    intent.putExtra("accounts", "");
                    startActivity(intent);
                }
                break;
           /* Intent intent = new Intent(getApplicationContext(), PaymentHistoryActivity.class);
            startActivity(intent);*/
            }
        }
        else if (title.equals("Logout"))
        {

            SessionHelper session = new SessionHelper(getApplicationContext());
            session.setLogin(false);
            session.setUserType(null);
           /* Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
            startActivity(intent);
            this.finish();*/
            Utility.launchActivity(HomeBaseActivity.this, SplashActivity.class, true);


        }

        else if (title.equals("Bookings"))
        {
//            Intent intent = new Intent(getApplicationContext(),HostelBookingListActivity.class);
//            intent.putExtra("hostelId",session.getUserID());
//            startActivity(intent);
            if(session.getUserType().equals("hostel")){
                Intent intent = new Intent(getApplicationContext(),HostelBookingListActivity.class);
                intent.putExtra("hostelId", session.getUserID());
                startActivity(intent);
            }
            else if(session.getUserType().equals("mess")){
                Intent intent = new Intent(getApplicationContext(),MessBookingListActivity.class);
                intent.putExtra("messId", session.getUserID());
                startActivity(intent);
            }
            else if(session.getUserType().equals("classes")){
                Intent intent = new Intent(getApplicationContext(),ClassesBookingListActivity.class);
                intent.putExtra("classesId", session.getUserID());
                startActivity(intent);
            }
            else if(session.getUserType().equals("library")){
                Intent intent = new Intent(getApplicationContext(),LibraryBookingListActivity.class);
                intent.putExtra("libraryId", session.getUserID());
                startActivity(intent);
            }
        }
        else if (title.equals("Service Type"))
        {
            Intent intent = new Intent(getApplicationContext(),BusinessListActivity.class);
            intent.putExtra("business", "");
            startActivity(intent);
        }

        else if (title.equals("Advertisements"))
        {
           Utility.launchActivity(HomeBaseActivity.this, AdvertisementListActivity.class,false);
        }
        else if (title.equals("Subscription Charges"))
        {
            switch (session.getUserType())
            {
                case "admin":
                {
                    Intent intent = new Intent(getApplicationContext(),BusinessListActivity.class);
                    intent.putExtra("charges", "adminSubCharges");
                    startActivity(intent);
                }
                break;
                case "hostel":
                {
                    preferenceManager.setAREA_SELECTED("details");
                    Intent intent = new Intent(getApplicationContext(),HostelListActivity.class);
                    intent.putExtra("hostelId", session.getUserID() );
                    intent.putExtra("charges", "charges" );
                    startActivity(intent);

                    /*Intent intent = new Intent(getApplicationContext(),HostelListActivity.class);
                    intent.putExtra("charges", "");
                    startActivity(intent);*/
                }
                break;
                case "mess":
                {
                    Intent intent = new Intent(getApplicationContext(),MessListActivity.class);
                    intent.putExtra("charges", "");
                    startActivity(intent);
                }
                break;
                case "classes":
                {
                    Intent intent = new Intent(getApplicationContext(),ClassesListActivity.class);
                    intent.putExtra("charges", "");
                    startActivity(intent);
                }
                break;
                case "library":
                    Intent intent = new Intent(getApplicationContext(),LibraryListActivity.class);
                    intent.putExtra("charges", "");
                    startActivity(intent);
                    break;
            }

        }

        else if (title.equals("Charges"))
        {
            switch (session.getUserType())
            {
                case "admin":
                {

                    Intent intent = new Intent(getApplicationContext(),BusinessListActivity.class);
                    intent.putExtra("charges", "adminSubCharges");
                    startActivity(intent);
                }
                break;
                case "hostel":
                {
                    preferenceManager.setAREA_SELECTED("details");
                    Intent intent = new Intent(getApplicationContext(),HostelListActivity.class);
                    intent.putExtra("charges", "");
                    startActivity(intent);
                }
                break;
                case "mess":
                {
                    Intent intent = new Intent(getApplicationContext(),MessListActivity.class);
                    intent.putExtra("charges", "");
                    startActivity(intent);
                }
                break;
                case "classes":
                {
                    Intent intent = new Intent(getApplicationContext(),ClassesListActivity.class);
                    intent.putExtra("charges", "");
                    startActivity(intent);
                }
                break;
                case "library":
                    Intent intent = new Intent(getApplicationContext(),LibraryListActivity.class);
                    intent.putExtra("charges", "");
                    startActivity(intent);
                    break;
            }

        }

        else if (title.equals("Accounts"))
        {
            switch (session.getUserType())
            {
                case "admin":
                {
                    Intent intent = new Intent(getApplicationContext(),BusinessListActivity.class);
                    intent.putExtra("accounts", "");
                    startActivity(intent);
                }
                break;

                case "hostel":
                {
                    /*Intent intent = new Intent(getApplicationContext(),HostelListActivity.class);
                    intent.putExtra("accounts", "");
                    startActivity(intent);*/


                    preferenceManager.setAREA_SELECTED("details");
                    Intent intent = new Intent(getApplicationContext(),HostelListActivity.class);
                    intent.putExtra("hostelId", session.getUserID() );
                    intent.putExtra("accounts", "accounts" );
                    startActivity(intent);
                }
                break;

                case "mess":
                {
                    Intent intent = new Intent(getApplicationContext(),MessListActivity.class);
                    intent.putExtra("accounts", "");
                    startActivity(intent);
                }
                break;

                case "classes":
                {
                    Intent intent = new Intent(getApplicationContext(),ClassesListActivity.class);
                    intent.putExtra("accounts", "");
                    startActivity(intent);
                }
                break;

                case "library":
                {
                    Intent intent = new Intent(getApplicationContext(),LibraryListActivity.class);
                    intent.putExtra("accounts", "");
                    startActivity(intent);
                }
                break;
            }

        }
        else if (title.equals("Search Setting"))
        {
            startActivity(new Intent(getApplicationContext(), SettingsActivity.class));

            //   startActivity(new Intent(getApplicationContext(), EditHostelRoom.class));

            //startActivity(new Intent(getApplicationContext(), demo.class));
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
