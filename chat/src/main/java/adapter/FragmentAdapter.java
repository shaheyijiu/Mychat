package adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

import fragment.Fragment_Find;
import fragment.Fragment_Friends;
import fragment.Fragment_Home;
import fragment.Fragment_Profile;

/**
 * Created by Administrator on 2015/12/20.
 */
public class FragmentAdapter extends FragmentPagerAdapter {
    protected static final String[] CONTENT = new String[] { "This", "Is", "A", "Test", };
    private int mCount = CONTENT.length;
    public static ArrayList<Fragment> fragments = new ArrayList<>();
    public FragmentAdapter(FragmentManager fm) {
        super(fm);
        initFragment();
    }

    public void initFragment(){
        fragments.add(new Fragment_Home());
        fragments.add(new Fragment_Friends());
        fragments.add(new Fragment_Find());
        fragments.add(new Fragment_Profile());
    }
    @Override
    public Fragment getItem(int position) {
            switch(position){
                case 0:
                   return fragments.get(0);
                case 1:
                    return fragments.get(1);
                case 2:
                    return fragments.get(2);
                case 3:
                    return fragments.get(3);
                default:
                    return null;
            }
    }

    @Override
    public int getCount() {
        return mCount;
    }
}
