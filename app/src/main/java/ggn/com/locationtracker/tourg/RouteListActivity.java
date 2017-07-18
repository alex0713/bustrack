package ggn.com.locationtracker.tourg;

import android.animation.Animator;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateInterpolator;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ggn.com.locationtracker.R;
import ggn.com.locationtracker.utills.SharedPrefHelper;
import ggn.com.locationtracker.utills.SuperAdapterG;
import ggn.com.locationtracker.utills.UtillG;

public class RouteListActivity extends AppCompatActivity
{
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_list);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            animate();
        }
        else
        {
            toolbar.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        }
        setUptoolBar();


        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(RouteListActivity.this));


        List<String> list = new ArrayList<>();
        for (int i = 0; i < SharedPrefHelper.getInstance(RouteListActivity.this).getrouteNumber(); i++)
        {
            list.add("Route " + i);
        }
        if (list.isEmpty())
        {
            UtillG.showToast("No routes saved yet..!", RouteListActivity.this, true);
        }

        recyclerView.setAdapter(new SuperAdapterG<String, viewHolder>(list, R.layout.routelist_inflator, viewHolder.class)
        {
            @Override
            protected void populateViewHolderG(viewHolder viewHolder, String model, final int position)
            {
                viewHolder.tvName.setText(model);


                viewHolder.view.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Intent intent = new Intent(RouteListActivity.this, ShowRouteActivity.class);
                        intent.putExtra("route", position+1);
                        startActivity(intent);
                    }
                });


            }
        });


//        getSupportFragmentManager().beginTransaction().replace(R.id.containerLayout, new MemoListFragment()).commit();
    }


    public static class viewHolder extends RecyclerView.ViewHolder
    {
        private TextView tvName;
        private View     view;

        public viewHolder(View itemView)
        {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tvName);

            view = itemView;
        }
    }


    private void animate()
    {
        getWindow().getEnterTransition().addListener(new Transition.TransitionListener()
        {
            @Override
            public void onTransitionStart(Transition transition)
            {
            }

            @Override
            public void onTransitionEnd(Transition transition)
            {
                toolbar.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                animateRevealShow(toolbar);
            }

            @Override
            public void onTransitionCancel(Transition transition)
            {
            }

            @Override
            public void onTransitionPause(Transition transition)
            {
            }

            @Override
            public void onTransitionResume(Transition transition)
            {
            }
        });

    }


    private void animateRevealShow(View viewRoot)
    {
        int cx          = (viewRoot.getLeft() + viewRoot.getRight()) / 2;
        int cy          = (viewRoot.getTop() + viewRoot.getBottom()) / 2;
        int finalRadius = Math.max(viewRoot.getWidth(), viewRoot.getHeight());

        Animator anim = ViewAnimationUtils.createCircularReveal(viewRoot, cx, cy, 0, finalRadius);
        anim.setDuration(700);
        anim.setInterpolator(new AccelerateInterpolator());
        anim.start();
    }

    private void setUptoolBar()
    {
//        TextView toolbar_title = (TextView) toolbar.findViewById(R.id.toolbar_title);
        try
        {
            toolbar.setTitleTextColor(getResources().getColor(R.color.white));
            toolbar.setTitle("Route List");


            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed()
    {
        finish();
    }
}
