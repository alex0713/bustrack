package ggn.com.locationtracker.utills;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Constructor;
import java.util.List;

/**
 * Created by GagaN "G-Expo" on 06-09-2016.
 */

public abstract class SuperAdapterG<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH>
{

    List<T> modelList;
    protected int mModelLayout;
    Class<VH> mViewHolderClass;

    public SuperAdapterG(List<T> modelClass, int modelLayout, Class<VH> viewHolderClass)
    {
        modelList = modelClass;
        mModelLayout = modelLayout;
        mViewHolderClass = viewHolderClass;
    }

    @Override
    public int getItemCount()
    {
        return modelList.size();
    }


    @Override
    public int getItemViewType(int position)
    {
        return position;
    }

    public T getItem(int position)
    {
        return modelList.get(position);
    }


    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType)
    {
        ViewGroup view = (ViewGroup) LayoutInflater.from(parent.getContext()).inflate(mModelLayout, parent, false);
        try
        {
            Constructor<VH> constructor = mViewHolderClass.getConstructor(View.class);
            return constructor.newInstance(view);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onBindViewHolder(VH viewHolder, int position)
    {
        T model = getItem(position);
        populateViewHolderG(viewHolder, model, position);
    }


    abstract protected void populateViewHolderG(VH viewHolder, T model, int position);
}
