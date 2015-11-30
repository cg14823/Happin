package mosaic.happin;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;


public class PageAdapter extends FragmentPagerAdapter{
    final int PAGE_COUNT = 4;
    private String tabTitles[] = new String[] { "Main", "Profile", "Ranking","Game" };
    private Context context;

    public PageAdapter (FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new FragmentMain();
            case 1:
                return new Profile();
            case 2:
                return new Ranking();
            case 3:
                return new Game();
        }
        return FragmentMain.newInstance();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}
