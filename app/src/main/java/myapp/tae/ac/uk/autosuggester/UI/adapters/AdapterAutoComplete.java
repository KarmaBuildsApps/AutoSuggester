package myapp.tae.ac.uk.autosuggester.UI.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Karma on 10/06/16.
 */
public class AdapterAutoComplete extends BaseAdapter implements Filterable {
    private Context context;
    private ArrayList<String> autoSuggestions;
    private static final String TAG = "AdapterAutoComplete";
    private boolean isValueChange = false;

    public AdapterAutoComplete(Context context, ArrayList<String> autoSuggestions) {
        this.context = context;
        this.autoSuggestions = autoSuggestions;
    }

    @Override
    public int getCount() {
        return autoSuggestions.size();
    }

    @Override
    public String getItem(int position) {
        return autoSuggestions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        TextView tvListItem;
        return convertView;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                if (constraint == null) {
                    return null;
                }
                FilterResults results = new FilterResults();
                results.values = autoSuggestions;
                results.count = autoSuggestions.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if(constraint!=null&& results.count>0){

                }
            }
        };
        return filter;
    }

}
