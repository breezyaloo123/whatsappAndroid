package com.example.whatsappandroid;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class TablayoutClass extends FragmentPagerAdapter
{

    public TablayoutClass(FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }



    @NonNull
    @Override
    public Fragment getItem(int position) {

        switch (position)
        {
            case 0:
                DiscFragment discFragment = new DiscFragment();
                return discFragment;
            case 1:
                GroupsFragment groupsFragment = new GroupsFragment();
                return groupsFragment;
            case 2:
                ContactsFragment contactsFragment = new ContactsFragment();
                return contactsFragment;
            case 3:
                RequestFragment requestFragment = new RequestFragment();
                return requestFragment;
            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return 4;
    }
//this method allow us to set the title of the Fragment
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position)
        {
            case 0:
                return "Disc";
            case 1:
                return "Groups";
            case 2:
                return "Contacts";
            case 3:
                return "Requests";
            default:
                return  null;
        }
    }
}
