package com.netist.mygirlshostel.constants;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.HashMap;


public class PrefManager {
    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    public static int PRIVATE_MODE = 0;

    // Shared preferences file name
    public static final String PREF_NAME = "healtree";

    // All Shared Preferences Keys

    private static final String AREA_SELECTED = "area_selected";

    private static final String MOBILE_SELECTED = "mobile_selected";

    private static final String HOSTELID_SELECTED = "hostelid_selected";

    private static final String LIBRARYID_SELECTED = "libraryid_selected";

    private static final String CLASSID_SELECTED = "classid_selected";

    private static final String MESSID_SELECTED = "messid_selected";

    private static final String HOSTEL_SERVICE = "add_hostel_service";

    private static final String RADIO_BUTTON = "radio_button";

    private static final String LIBRARY_LIST = "library_list";
    private static final String ROLE = "role";
    private static final String PAYMENT = "payment";
    private static final String TYPEID = "typeid";
    private static final String Lati = "lati";
    private static final String Longi = "longi";
    private static final String Distance = "dis";
    private static final String Type = "type";
    private static final String SearchType = "SearchType";
    private static final String hostelid = "hid";
    private static final String hname = "hname";
    private static final String hpicture = "hpicture";
    private static final String havailability = "havailability";

    private static final String latitude = "latitude";
    private static final String longitude = "longitude";


    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setAREA_SELECTED(String area) {
        editor.putString(AREA_SELECTED, area);
        editor.commit();
    }

    public String getAREA_SELECTED() {
        return pref.getString(AREA_SELECTED, null);
    }

    public void setMOBILE_SELECTED(String mobile) {
        editor.putString(MOBILE_SELECTED, mobile);
        editor.commit();
    }

    public String getMOBILE_SELECTED() {
        return pref.getString(MOBILE_SELECTED, null);
    }


//editrooms
    public void setHOSTELID_SELECTED(String id) {
        editor.putString(HOSTELID_SELECTED, id);
        editor.commit();
    }

    public String getHOSTELID_SELECTED() {
        return pref.getString(HOSTELID_SELECTED, null);
    }


    public void setLIBRARYID_SELECTED(String libraryid) {
        editor.putString(LIBRARYID_SELECTED, libraryid);
        editor.commit();
    }

    public String getLIBRARYID_SELECTED() {
        return pref.getString(LIBRARYID_SELECTED, null);
    }

    public void setCLASSID_SELECTED(String classid) {
        editor.putString(CLASSID_SELECTED, classid);
        editor.commit();
    }

    public String getCLASSID_SELECTED() {
        return pref.getString(CLASSID_SELECTED, null);
    }



    public void setMESSID_SELECTED(String messid) {
        editor.putString(MESSID_SELECTED, messid);
        editor.commit();
    }

    public String getMESSID_SELECTED() {
        return pref.getString(MESSID_SELECTED, null);
    }





    //end

    public void setHOSTEL_SERVICE(String hostel) {
        editor.putString(HOSTEL_SERVICE, hostel);
        editor.commit();
    }

    public String getHOSTEL_SERVICE() {
        return pref.getString(HOSTEL_SERVICE, null);
    }


    public void setRADIO_BUTTON(String button) {
        editor.putString(RADIO_BUTTON, button);
        editor.commit();
    }

    public String getRADIO_BUTTON() {
        return pref.getString(RADIO_BUTTON, null);
    }


    public void setLIBRARY_LIST(String library) {
        editor.putString(LIBRARY_LIST, library);
        editor.commit();
    }

    public String getLIBRARY_LIST() {
        return pref.getString(LIBRARY_LIST, null);
    }

     public void setROLE(String r) {
            editor.putString(ROLE, r);
            editor.commit();
        }

        public String getROLE() {
            return pref.getString(ROLE, null);
        }


     public void setPAYMENT(String r) {
                editor.putString(PAYMENT, r);
                editor.commit();
            }

     public String getPAYMENT() {
                return pref.getString(PAYMENT, null);
            }

     public void setTYPEID(String r) {
                editor.putString(TYPEID, r);
                editor.commit();
            }

     public String getTYPEID() {
                return pref.getString(TYPEID, null);
            }

      public void setLati(String r) {
                editor.putString(Lati, r);
                editor.commit();
            }

     public String getLati() {
                return pref.getString(Lati, null);
            }

      public void setLongi(String r) {
                    editor.putString(Longi, r);
                    editor.commit();
                }

      public String getLongi() {
                    return pref.getString(Longi, null);
                }

      public void setDistance(String r) {
                    editor.putString(Distance, r);
                    editor.commit();
                }

      public String getDistance() {
                    return pref.getString(Distance, null);
                }
     public void setType(String r) {
                        editor.putString(Type, r);
                        editor.commit();
                    }

     public String getType() {
         return pref.getString(Type, null);
     }

     public void setSearchType(String r) {
                        editor.putString(SearchType, r);
                        editor.commit();
                    }

     public String getSearchType() {
         return pref.getString(SearchType, null);
     }

     public void sethostelid(String r) {
                            editor.putString(hostelid, r);
                            editor.commit();
                        }

         public String gethostelid() {
             return pref.getString(hostelid, null);
         }


         public void sethname(String r) {
                            editor.putString(hname, r);
                            editor.commit();
                        }

         public String gethname() {
             return pref.getString(hname, null);
         }

         public void sethavailability(String r) {
                            editor.putString(havailability, r);
                            editor.commit();
                        }

         public String gethavailability() {
             return pref.getString(havailability, null);
         }

         public void sethpicture(String r) {
                            editor.putString(hpicture, r);
                            editor.commit();
                        }

         public String gethpicture() {
             return pref.getString(hpicture, null);
         }

         public void setlatitude(String r) {
         editor.putString(latitude, r);
         editor.commit();
         }

         public String getlatitude() {
         return pref.getString(latitude, null);
         }

          public void setlongitude(String r) {
         editor.putString(longitude, r);
         editor.commit();
         }

         public String getlongitude() {
         return pref.getString(longitude, null);
         }




}
